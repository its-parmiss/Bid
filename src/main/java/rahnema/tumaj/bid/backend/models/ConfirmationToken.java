package rahnema.tumaj.bid.backend.models;

import lombok.Data;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="ConfirmationTokens")
@Data
public class ConfirmationToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name="confirmation_token")
    String confirmationToken;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
//    @JoinColumn(nullable = false, name = "user_id")
    User user;

    public ConfirmationToken() {
        super();
        confirmationToken = UUID.randomUUID().toString();
    }
}

