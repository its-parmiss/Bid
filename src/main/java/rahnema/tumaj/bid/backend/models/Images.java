package rahnema.tumaj.bid.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name="Images")
@Data
public class Images {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    @Column(nullable = false,name="url")
    String url;
    @Column(name = "created_at")
    Date createdAt;
    @ManyToOne (fetch=FetchType.EAGER)
    @JsonBackReference
    Auction auction;

    @PrePersist
    protected void onCreate() {
        this.createdAt = new Date();
    }
}
