package rahnema.tumaj.bid.backend.storage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService {

    private final Path rootLocationForProfilePics;
    private final Path rootLocationForAuctionPics;

    @Autowired
    public StorageServiceImpl(StorageProperties properties) {
        this.rootLocationForAuctionPics = Paths.get(properties.getAuctionLocation());
        this.rootLocationForProfilePics = Paths.get(properties.getProfilePicLocation());

    }

    @Override

    public String store(MultipartFile file, String storage) {
        String filename = StringUtils.cleanPath(new Date().hashCode() + file.getOriginalFilename());
        Path savePath = evaluatePath(storage);
        handleFileAndExceptions(file, filename, savePath);
        return filename;
    }

    private Path evaluatePath(String storage) {
        Path savePath;
        if (storage.equals("profilePicture"))
            savePath = this.rootLocationForProfilePics;
        else
            savePath = this.rootLocationForAuctionPics;
        return savePath;
    }

    private void handleFileAndExceptions(MultipartFile file, String filename, Path savePath) {
        try {
            checkIfValidAndSave(file, filename, savePath);
        } catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    private void checkIfValidAndSave(MultipartFile file, String filename, Path savePath) throws IOException {
        if (file.isEmpty())
            throw new StorageException("Failed to store empty file " + filename);

        if (filename.contains(".."))
            throw new StorageException("Cannot store file with relative path outside current directory " + filename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, savePath.resolve(filename), StandardCopyOption.REPLACE_EXISTING);
        }
    }


    @Override
    public Path load(String filename, String storage) {
        if (storage.equals("profilePicture"))
            return rootLocationForProfilePics.resolve(filename);
        else if (storage.equals("auctionPicture"))
            return rootLocationForAuctionPics.resolve(filename);
        else return null;
    }

    @Override
    public Resource loadAsResource(String filename, String storage) {
        try {
            Path file = load(filename, storage);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable())
                return resource;
            else
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

        } catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

}
