package rahnema.tumaj.bid.backend.services.auction;

import org.springframework.data.domain.PageRequest;
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
import java.util.*;

@Service
public class AuctionServiceImpl implements AuctionService {

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
        Auction auction = auctionInput.toModel();

        for(String url:auctionInput.getImageUrls()){
            ImageInputDTO imageInputDTO=new ImageInputDTO();
            imageInputDTO.setUrl(url);
            imageInputDTO.setAuction(auction);
            Images img=imageService.addOne(imageInputDTO);
            auction.addImage(img);
        }

        System.out.println(auction.getImages().size()+ "sout in service");
        System.out.println(auction.getTitle());
        setAuctionCategoryById(auctionInput, auction);
        parseDateAndHandleException(auctionInput, auction);
        return this.repository.save(auction);
    }

    private void parseDateAndHandleException(AuctionInputDTO auctionInput, Auction auction) {
        try {
            parseInputDate(auctionInput, auction);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void setAuctionCategoryById(AuctionInputDTO auctionInput, Auction auction) {
        Category cat = categoryRepository.findById(auctionInput.getCategoryId()).orElseThrow(()-> new CategoryNotFoundException(auctionInput.getCategoryId()));
        auction.setCategory(cat);
    }

    private void parseInputDate(AuctionInputDTO auctionInput, Auction auction) throws ParseException {
        Date startDate;
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        startDate = df.parse(auctionInput.getStartDate());
        auction.setStart_date(startDate);
    }

    @Override
    public void deleteAuction(Long id) {

    }

    @Override
    public List<Auction> getAll(Integer page, Integer limit) {
//        return repository.findAllAuctionsHottest(PageRequest.of(page,limit));
        return repository.findAll(PageRequest.of(page,limit)).getContent();
    }

    @Override
    public Optional<Auction> getOne(Long id) {
        return this.repository.findById(id);

    }

    @Override
    public List<Auction> findByTitle(String title,Integer page,Integer limit) {
        return this.repository.findByTitleContaining(title,PageRequest.of(page,limit));
    }

    @Override
    public List<Auction> findByTitleAndCategory(String title, Long categoryId, Integer page, Integer limit) {
        Category category=categoryRepository.findById(categoryId).orElseThrow(() -> new CategoryNotFoundException(categoryId));
        return repository.findByTitleContainingAndCategory(title,category,PageRequest.of(page,limit));
    }
}
