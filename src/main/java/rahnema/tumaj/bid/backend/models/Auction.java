package rahnema.tumaj.bid.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.awt.print.Book;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name="Auctions")
@Data
public class Auction {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;
    @Column(nullable = false, name = "title")
    String title;
    @Column(name = "description")
    String description;
    @Column(name = "start_date")
    Date start_date;
    @Column(name = "last_bid")
    Long last_bid;
    @Column(name = "active_bidders_limit", columnDefinition = "int DEFAULT '0'")
    int active_bidders_limit;
    @Column(name = "is_active", columnDefinition = "boolean DEFAULT true")
    boolean is_active;
    @Column(name = "expire_date")
    Date expire_date;
    @Column(name = "created_at")
    Date created_at;
    @ManyToOne
    Category category;
    @ManyToOne
    User user;
    @OneToMany(fetch=FetchType.EAGER,mappedBy = "auction")
    Set<Images> images;
    @OneToMany(fetch=FetchType.EAGER,mappedBy = "related_auction")
    Set<Bid> bids;
    @JsonBackReference
    @ManyToMany(fetch=FetchType.EAGER,mappedBy = "auctions",cascade=CascadeType.ALL) //bookmarks
    Set<User> users;
}