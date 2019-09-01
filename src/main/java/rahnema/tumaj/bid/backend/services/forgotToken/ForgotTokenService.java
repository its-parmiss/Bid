package rahnema.tumaj.bid.backend.services.forgotToken;

import rahnema.tumaj.bid.backend.models.ForgotToken;

import java.util.Optional;

public interface ForgotTokenService {
    ForgotToken save(ForgotToken forgotToken);
    Optional<ForgotToken> findByForgotToken(String forgotToken);
}