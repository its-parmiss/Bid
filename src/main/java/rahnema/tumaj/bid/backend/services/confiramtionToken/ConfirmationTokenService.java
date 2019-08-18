package rahnema.tumaj.bid.backend.services.confiramtionToken;

import rahnema.tumaj.bid.backend.models.ConfirmationToken;

import java.util.Optional;

public interface ConfirmationTokenService {
    ConfirmationToken save(ConfirmationToken confirmationToken);
    Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken);
}
