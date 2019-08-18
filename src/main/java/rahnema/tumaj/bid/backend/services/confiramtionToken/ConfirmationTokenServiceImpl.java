package rahnema.tumaj.bid.backend.services.confiramtionToken;

import net.bytebuddy.implementation.auxiliary.AuxiliaryType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.models.ConfirmationToken;
import rahnema.tumaj.bid.backend.repositories.ConfirmationTokenRepository;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;

import java.util.Optional;

@Service
public class ConfirmationTokenServiceImpl implements ConfirmationTokenService {

    @Autowired
    private ConfirmationTokenRepository repository;

    @Override
    public ConfirmationToken save(ConfirmationToken confirmationToken) {
        return repository.save(confirmationToken);
    }

    @Override
    public Optional<ConfirmationToken> findByConfirmationToken(String confirmationToken) {
        return repository.findByConfirmationToken(confirmationToken);
    }

}
