package rahnema.tumaj.bid.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rahnema.tumaj.bid.backend.models.ConfirmationToken;

import java.util.Optional;

@Repository
public interface ConfirmationTokenRepository extends CrudRepository<ConfirmationToken, Long> {
    Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken);
}