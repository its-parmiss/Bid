package rahnema.tumaj.bid.backend.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name="Users")
@Data
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
    String profile_picture;
    @Column(name = "created_at",columnDefinition="DATE DEFAULT CURRENT_DATE")
    Date created_at;

    @OneToMany(mappedBy="user")
    Set<Auction> auctions;
}

//
