package rahnema.tumaj.bid.backend.services.auction;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.datetime.DateFormatter;
import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.domains.Image.ImageInputDTO;
import rahnema.tumaj.bid.backend.domains.auction.AuctionInputDTO;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Category;
import rahnema.tumaj.bid.backend.models.Images;
import rahnema.tumaj.bid.backend.repositories.AuctionRepository;
import rahnema.tumaj.bid.backend.repositories.CategoryRepository;
import rahnema.tumaj.bid.backend.services.Images.ImageService;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.CategoryNotFoundException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashSet;
import java.util.Optional;

@Service
public class AuctionServiceImpl implements AuctionService {

    private final String dateFormat = "yyyy-MM-dd";
    private final AuctionRepository repository;
    private final CategoryRepository categoryRepository;
    private final ImageService imageService;

    public AuctionServiceImpl(AuctionRepository repository,
                              CategoryRepository categoryRepository,
                              ImageService imageService) {

        this.repository = repository;
        this.categoryRepository = categoryRepository;
        this.imageService=imageService;
    }

    @Override
    public Auction addAuction(AuctionInputDTO auctionInput) {

        Auction auction = this.repository.save(auctionInput.toModel());

        for(String url:auctionInput.getImageUrls())
            saveImageToRepo(auction, url);

        setAuctionCategoryById(auctionInput, auction);
        return this.repository.save(auction);
    }

    private void saveImageToRepo(Auction auction, String url) {
        ImageInputDTO imageInputDTO=new ImageInputDTO();
        imageInputDTO.setUrl(url);
        imageInputDTO.setAuction(auction);
        Images img=imageService.addOne(imageInputDTO);
        auction.addImage(img);
    }


    private void setAuctionCategoryById(AuctionInputDTO auctionInput, Auction auction) {
        Category cat = categoryRepository.findById(auctionInput.getCategoryId()).orElseThrow(()-> new CategoryNotFoundException(auctionInput.getCategoryId()));
        auction.setCategory(cat);
    }


    @Override
    public Page<Auction> getAll(Integer page, Integer limit) {
        Long lastPage =repository.countByFinished(false);
        return new PageImpl<>(repository.getHottestPage(PageRequest.of(page,limit)),PageRequest.of(page,limit),lastPage);
    }

    @Override
    public Optional<Auction> getOne(Long id) {
        return this.repository.findById(id);

    }

    @Override
    public Page<Auction> findByTitle(String title,Integer page,Integer limit) {
        return this.repository.findByFinishedAndTitleContainingOrderByCreatedAtDesc(false,title,PageRequest.of(page,limit));
    }

    @Override
    public Page<Auction> findByTitleAndCategory(String title, Long categoryId, Integer page, Integer limit) {
        Category category=categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
        return repository.findByFinishedAndTitleContainingAndCategoryOrderByCreatedAtDesc(false,title,category,PageRequest.of(page,limit));
    }

    @Override
    public Page<Auction> findByCategory(Long categoryId, Integer page, Integer limit) {
        Category category=categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
        return repository.findByFinishedAndCategoryOrderByCreatedAtDesc(false,category,PageRequest.of(page, limit));
    }
}
