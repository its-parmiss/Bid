package rahnema.tumaj.bid.backend.models;

import lombok.Data;
import lombok.EqualsAndHashCode;

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
    @Column(name = "created_at")
    Date created_at;
    @ManyToOne(fetch=FetchType.EAGER)
    Auction related_auction;
    @ManyToOne(fetch=FetchType.EAGER)
    User bidder;
}
