package rahnema.tumaj.bid.backend.models;

import lombok.Data;

import javax.persistence.*;
//import java.util.Date;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name="Categories")
@Data
public class Category {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    @Column(nullable = false,name="title",unique = true)
    String title;
    @Column(name = "created_at")
    Date created_at;
    @OneToMany(fetch=FetchType.EAGER,mappedBy = "category")
    Set<Auction> auctions;

    @PrePersist
    protected void onCreate() {
        this.created_at = new Date();
    }
}
