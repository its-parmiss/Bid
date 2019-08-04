package rahnema.tumaj.bid.backend.models;

import lombok.Data;

import javax.persistence.*;
//import java.util.Date;
@Entity
@Table(name="Images")
@Data
public class Images {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    @Column(nullable = false,name="url",unique = true)
    String url;
    @Column(name = "created_at",columnDefinition="DATE DEFAULT CURRENT_DATE")
    java.sql.Date created_at;
    @ManyToOne
    Auction auction;
}
