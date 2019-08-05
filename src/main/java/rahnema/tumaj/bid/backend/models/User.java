package rahnema.tumaj.bid.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
//import java.awt.print.Book;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name="Users")
@Data
@EqualsAndHashCode(exclude="auctions")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    @Column(nullable = false,name="first_name")
    String first_name;
    @Column(name = "last_name")
    String last_name;
    @Column(nullable = false,name = "email",unique = true)
    String email;
    @Column(nullable = false,name = "password")
    String password;
    @Column(name = "profile_picture")
    String profile_picture;
    @Column(name = "created_at")
    Date created_at;
    @Column(name = "reset_token")
    String resetToken;

    @JsonManagedReference
  @ManyToMany (cascade=CascadeType.ALL)
  @JoinTable(
          name = "Bookmarks",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "auction_id"))
    Set<Auction> auctions;

    @OneToMany(mappedBy = "bidder")
    Set<Bid> bids;
}