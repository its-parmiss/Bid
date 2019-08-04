package rahnema.tumaj.bid.backend.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name="Auctions")
@Data
public class Auction {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    @Column(nullable = false,name="title")
    String title;
    @Column(name="description")
    String description;
    @Column(name = "start_date",columnDefinition="DATE DEFAULT CURRENT_DATE")
    Date start_date;
    @Column(name = "last_bid")
    Long last_bid;
    @Column(name = "active_bidders_limit",columnDefinition="int DEFAULT '0'")
    Integer active_bidders_limit;
    @Column(name = "is_active",columnDefinition="boolean DEFAULT true")
    Boolean is_active;
    @Column(name = "expire_date")
    Date expire_date;
    @Column(name = "created_at",columnDefinition="DATE DEFAULT CURRENT_DATE")
    Date created_at;
    @ManyToOne
    Category category;
    @ManyToOne
    User user;
    @OneToMany(mappedBy = "auction")
    Set<Images> images;




}
