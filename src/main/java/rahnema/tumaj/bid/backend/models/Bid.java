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
    Date createdAt;
    @ManyToOne(fetch=FetchType.EAGER)
    Auction relatedAuction;
    @ManyToOne(fetch=FetchType.EAGER)
    User bidder;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}
