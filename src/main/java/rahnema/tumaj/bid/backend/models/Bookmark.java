package rahnema.tumaj.bid.backend.models;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name="Bookmarks")
@Data
public class Bookmark {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    Long id;
    @Column(name = "created_at",columnDefinition="DATE DEFAULT CURRENT_DATE")
    Date created_at;

}
