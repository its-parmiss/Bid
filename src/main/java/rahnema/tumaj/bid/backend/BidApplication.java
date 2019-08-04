package rahnema.tumaj.bid.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;

@SpringBootApplication(exclude = {JacksonAutoConfiguration.class})
public class BidApplication {
    public static void main(String[] args){
        SpringApplication.run(BidApplication.class, args);
    }
}
