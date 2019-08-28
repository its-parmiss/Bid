package rahnema.tumaj.bid.backend.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="Bids")
@ToString(exclude = {"bidder", "auction"})
@Data
public class Bid {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    @Column(name = "created_at")
    Date createdAt;
    @JsonManagedReference
    @ManyToOne
    Auction auction;
    @JsonManagedReference
    @ManyToOne
    User bidder;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}
