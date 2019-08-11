package rahnema.tumaj.bid.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
//import java.util.Date;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name="Categories")
@ToString(exclude = "auctions")
@Data @NoArgsConstructor @EqualsAndHashCode(exclude = "auctions")
public class Category {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    @Column(nullable = false,name="title",unique = true)
    String title;
    @Column(name = "created_at")
    Date createdAt;
    @OneToMany(fetch=FetchType.EAGER,mappedBy = "category")
    @JsonBackReference
    Set<Auction> auctions;

    public Category(String title){
        this.title = title;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}
