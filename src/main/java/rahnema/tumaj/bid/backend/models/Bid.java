package rahnema.tumaj.bid.backend.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="Bids")
@Data
public class Bid {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    @Column(nullable = false)
    String title;
    @Column(name = "created_at",columnDefinition="DATE DEFAULT CURRENT_DATE")
    Date created_at;
    @ManyToOne
    Auction related_auction;
    @ManyToOne
    User bidder;
}
