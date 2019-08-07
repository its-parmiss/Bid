package rahnema.tumaj.bid.backend.services.auction;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.domains.auction.AuctionInputDTO;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Category;
import rahnema.tumaj.bid.backend.repositories.AuctionRepository;
import rahnema.tumaj.bid.backend.repositories.CategoryRepository;
import rahnema.tumaj.bid.backend.utils.exceptions.AuctionNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.CategoryNotFoundException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuctionServiceImpl implements AuctionService {

    private final AuctionRepository repository;
    private final CategoryRepository categoryRepository;
    public AuctionServiceImpl(AuctionRepository repository, CategoryRepository categoryRepository) {
        this.repository = repository;
        this.categoryRepository = categoryRepository;
    }
    @Override
    public Auction addAuction(AuctionInputDTO auctionInput) {
        Auction auction = auctionInput.toModel();
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
        return repository.findAllAuctionsHottest(PageRequest.of(page,limit));
    }

    @Override
    public Optional<Auction> getOne(Long id) {
        return this.repository.findById(id);

    }

    @Override
    public List<Auction> findByTitle(String title,Integer page,Integer limit) {
        return this.repository.findByTitleContaining(title,PageRequest.of(page,limit));
    }
}
