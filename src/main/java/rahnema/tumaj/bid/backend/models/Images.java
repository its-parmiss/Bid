package rahnema.tumaj.bid.backend.models;

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
    @Column(nullable = false,name="url",unique = true)
    String url;
    @Column(name = "created_at")
    Date created_at;
    @ManyToOne (fetch=FetchType.EAGER)
    Auction auction;

    @PrePersist
    protected void onCreate() {
        this.created_at = new Date();
    }
}
