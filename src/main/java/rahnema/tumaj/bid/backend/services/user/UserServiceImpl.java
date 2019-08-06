package rahnema.tumaj.bid.backend.services.user;

import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.controllers.SecurityController;
import rahnema.tumaj.bid.backend.domains.user.UserInputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final SecurityController securityController;

    public UserServiceImpl(UserRepository userRepository,
                           SecurityController securityController) {
        this.userRepository = userRepository;
        this.securityController = securityController;
    }

    @Override
    public Optional<User> getOne(Long id) {
        return this.userRepository.findById(id);
    }

    @Override
    public List<User> getAll() {
        Iterable<User> userIterable = this.userRepository.findAll();
        List<User> userList = new ArrayList<>();
        userIterable.forEach(userList::add);
        return userList;
    }

    @Override
    public UserOutputDTO addOne(UserInputDTO user) {
        User userModel = UserInputDTO.toModel(user);
        userModel.setPassword(
            this.securityController.bCryptPasswordEncoder
                    .encode(userModel.getPassword())
        );
        return UserOutputDTO.fromModel(userRepository.save(userModel));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Override
    public Optional<User> findByResetToken(String token) {
        return userRepository.findByResetToken(token);
    }
}
