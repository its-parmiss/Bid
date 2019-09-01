package rahnema.tumaj.bid.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name="Users")
@Data
@ToString(exclude = {"auctions", "myAuctions","confirmationToken"})
@EqualsAndHashCode(exclude={"confirmationToken", "bids"})
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    @Column(nullable = false,name="first_name")
    String firstName;
    @Column(name = "last_name")
    String lastName;
    @Column(nullable = false,name = "email",unique = true)
    String email;
    @Column(nullable = false,name = "password")
    String password;
    @Column(name = "profile_picture")
    String profilePicture;
    @Column(name = "created_at")
    Date createdAt;
    @Column(name = "enabled")
    boolean enabled;

    public User() {
        super();
        this.enabled = false;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }

    @JsonManagedReference
    @ManyToMany (cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    @JoinTable(
          name = "Bookmarks",
          joinColumns = @JoinColumn(name = "user_id"),
          inverseJoinColumns = @JoinColumn(name = "auction_id"))
    Set<Auction> auctions;

    @OneToMany(fetch=FetchType.EAGER,mappedBy = "user")
    @JsonManagedReference
    Set<Auction> myAuctions;

    @JsonBackReference
    @OneToMany(fetch=FetchType.EAGER,mappedBy = "bidder")
    Set<Bid> bids;

    @JsonBackReference
    @OneToOne(mappedBy = "user")
    ConfirmationToken confirmationToken;

    @JsonBackReference
    @OneToOne(mappedBy = "user")
    ForgotToken forgotToken;

}