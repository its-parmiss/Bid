package rahnema.tumaj.bid.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="ConfirmationTokens")
@Data
@EqualsAndHashCode(exclude={"user"})

public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name="confirmation_token")
    String confirmationToken;
    @JsonBackReference
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
//    @JoinColumn(nullable = false, name = "user_id")
    User user;

    public ConfirmationToken() {
        super();
        confirmationToken = UUID.randomUUID().toString();
    }
}

