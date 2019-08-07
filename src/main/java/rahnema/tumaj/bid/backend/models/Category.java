package rahnema.tumaj.bid.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
//import java.util.Date;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name="Categories")
@Data @NoArgsConstructor @EqualsAndHashCode(exclude = "auctions")
public class Category {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    @Column(nullable = false,name="title",unique = true)
    String title;
    @Column(name = "created_at")
    Date created_at;
    @OneToMany(fetch=FetchType.EAGER,mappedBy = "category")
    @JsonBackReference
    Set<Auction> auctions;

    public Category(String title){
        this.title = title;
    }

    @PrePersist
    protected void onCreate() {
        this.created_at = new Date();
    }
}
