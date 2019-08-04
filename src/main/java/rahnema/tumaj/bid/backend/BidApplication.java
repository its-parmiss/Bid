package rahnema.tumaj.bid.backend;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import rahnema.tumaj.bid.backend.services.auction.AuctionService;


@SpringBootApplication(exclude = {JacksonAutoConfiguration.class})
public class BidApplication implements CommandLineRunner {
    AuctionService service;

    BidApplication(AuctionService service){
        this.service = service;
    }
    public static void main(String[] args){

        SpringApplication.run(BidApplication.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
//        Auction auction = new Auction();
//        auction.setTitle("GFG");
//        service.addAuction(auction);
    }
}
