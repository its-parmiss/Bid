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
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.repositories.AuctionRepository;
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

    public static void main(String[] args) {
        SpringApplication.run(BidApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    /*
        auctionRepository.deleteAll();
        userRepository.deleteAll();
        List<User> users = new ArrayList<>();
        List<Auction> auctions = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            User user = new User();
            user.setFirst_name(String.valueOf(i));
            user.setEmail(String.valueOf(i));
            user.setPassword(String.valueOf(i));
            user.setAuctions(new HashSet<>());
            user.setMyAuctions(new HashSet<>());
            users.add(user);
        }
        for (int j = 0; j < 50; j++) {
            Auction auction = new Auction();
            auction.setTitle(String.valueOf(j));
            auction.setBase_price((long) j);
            auction.setUsers(new HashSet<>());
            auctions.add(auction);
        }

        for (int i = 0; i < 50; i++) {
            int random = (int) (Math.random() * 50);
            for (int j = 0; j < random; j++) {
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
            auctionRepository.save(auction);
        }
        List<Auction> auctionss = (auctionRepository.findAllAuctionsHottest(PageRequest.of(0, 5)));
        for (Auction auc:auctionss
             ) {
            System.out.println(auc.getUsers().size());
        }
    */
    }
}
