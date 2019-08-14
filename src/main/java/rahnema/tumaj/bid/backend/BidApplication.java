package rahnema.tumaj.bid.backend;

import org.modelmapper.internal.util.Iterables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Category;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.repositories.AuctionRepository;
import rahnema.tumaj.bid.backend.repositories.CategoryRepository;
import rahnema.tumaj.bid.backend.repositories.UserRepository;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@SpringBootApplication(exclude = {JacksonAutoConfiguration.class})
public class BidApplication implements CommandLineRunner {
    @Autowired
    AuctionRepository auctionRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CategoryRepository categoryRepository;

    public static void main(String[] args) {
        SpringApplication.run(BidApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

//       auctionRepository.deleteAll();
       /*
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        List<User> users = new ArrayList<>();

        List<Auction> auctions = new ArrayList<>();
        Category digitalCat = new Category("FaridCategory");
        Category mamad = new Category("mamad");
        categoryRepository.save(digitalCat);
        categoryRepository.save(mamad);
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setFirst_name(String.valueOf(i));
            user.setEmail(String.valueOf(i*300+"a"));
            user.setPassword(String.valueOf(i));
            user.setAuctions(new HashSet<>());
            user.setMyAuctions(new HashSet<>());
            users.add(user);
        }
        for (int j = 0; j < 30; j++) {
            Auction auction = new Auction();
            auction.setTitle(String.valueOf(j));
            auction.setBase_price((long) j);
            auction.setCategory(digitalCat);
            if (j< 15)
                auction.setFinished(true);
            else
                auction.setFinished(false);
            auction.setUsers(new HashSet<>());
            auctions.add(auction);
        }

        for (int i = 0; i < 30; i++) {
            for (int k = 0; k< 3; k++) {
                int j = (int) (Math.random() * 10);
                auctions.get(i).getUsers().add(users.get(j));
                users.get(j).getAuctions().add(auctions.get(i));

                auctions.get(i).setUser(users.get(j));
                users.get(j).getMyAuctions().add(auctions.get(i));
            }
        }

        for (User user : users) {
            userRepository.save(user);
        }
        for (Auction auction : auctions) {
            System.out.println(auctionRepository.save(auction).isFinished());

        }*/
//        System.out.println(userRepository.findById((long)2256));

        /*List<Auction> auctionss = (auctionRepository.findAllAuctionsHottest(PageRequest.of(0, 50)));
        for (Auction auc:auctionss
             ) {
            System.out.println(auc.getId()+ " " + auc.getUsers().size());
        }*/
    }
}
