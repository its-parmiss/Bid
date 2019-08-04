package rahnema.tumaj.bid.backend.models;

import javax.persistence.*;
import java.util.Date;

public class Images {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    @Column(nullable = false,name="url",unique = true)
    String url;
    @Column(name = "created_at",columnDefinition="DATE DEFAULT CURRENT_DATE")
    Date created_at;
    @ManyToOne
    Auction auction;

}
