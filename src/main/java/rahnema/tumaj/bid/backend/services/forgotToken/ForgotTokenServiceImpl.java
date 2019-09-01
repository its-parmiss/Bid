package rahnema.tumaj.bid.backend.services.forgotToken;

import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.models.ForgotToken;
import rahnema.tumaj.bid.backend.repositories.ForgotTokenRepository;

import java.util.Optional;

@Service
public class ForgotTokenServiceImpl implements ForgotTokenService {

    private final ForgotTokenRepository forgotTokenRepository;

    public ForgotTokenServiceImpl(ForgotTokenRepository forgotTokenRepository) {
        this.forgotTokenRepository = forgotTokenRepository;
    }

    @Override
    public ForgotToken save(ForgotToken forgotToken) {
        return forgotTokenRepository.save(forgotToken);
    }

    @Override
    public Optional<ForgotToken> findByForgotToken(String forgotToken) {
        return forgotTokenRepository.findByForgotToken(forgotToken);
    }
}
