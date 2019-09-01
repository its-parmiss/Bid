package rahnema.tumaj.bid.backend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name="ForgotTokens")
@Data
@EqualsAndHashCode(exclude={"user"})
public class ForgotToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    Long id;

    @Column(name="forgot_token")
    String forgotToken;

    @JsonBackReference
    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    User user;

    public ForgotToken() {
        super();
        forgotToken = UUID.randomUUID().toString();
    }
}