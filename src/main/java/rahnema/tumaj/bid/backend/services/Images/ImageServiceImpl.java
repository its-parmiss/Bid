package rahnema.tumaj.bid.backend.services.Images;

import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.domains.Image.ImageInputDTO;
import rahnema.tumaj.bid.backend.domains.Image.ImageOutputDTO;
import rahnema.tumaj.bid.backend.models.Images;
import rahnema.tumaj.bid.backend.repositories.ImageRepository;
@Service
public class ImageServiceImpl implements ImageService{
    private final ImageRepository repository;

    public ImageServiceImpl(ImageRepository repository) {
        this.repository = repository;
    }
    @Override
    public Images addOne(ImageInputDTO image) {
        Images imageModel = ImageInputDTO.toModel(image);
        return repository.save(imageModel);
    }
}
