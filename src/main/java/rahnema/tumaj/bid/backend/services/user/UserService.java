package rahnema.tumaj.bid.backend.services.user;

import rahnema.tumaj.bid.backend.domains.user.UserInputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.User;

import java.util.List;
import java.util.Optional;

public interface UserService {
    Optional<User> getOne(Long id);
    List<User> getAll();
    UserOutputDTO addOne(UserInputDTO user);
}
