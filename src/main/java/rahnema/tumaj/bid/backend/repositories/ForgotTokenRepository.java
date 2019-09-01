package rahnema.tumaj.bid.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rahnema.tumaj.bid.backend.models.ForgotToken;
import rahnema.tumaj.bid.backend.models.User;

import java.util.Optional;

@Repository
public interface ForgotTokenRepository extends CrudRepository<ForgotToken, Long> {
    Optional<ForgotToken> findByForgotToken(String forgotToken);
    Optional<ForgotToken> findByUser(User user);
}
