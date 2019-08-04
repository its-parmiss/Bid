package rahnema.tumaj.bid.backend.services;

import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.domains.user.UserInputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service("userService")
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> getOne(Long id) {
        return this.userRepository.findById(id);
    }

    public List<User> getAll() {
        Iterable<User> userIterable = this.userRepository.findAll();
        List<User> userList = new ArrayList<>();
        userIterable.forEach(userList::add);
        return userList;
    }

    public UserOutputDTO addOne(UserInputDTO user) {
        User userModel = UserInputDTO.toModel(user);
        userRepository.save(userModel);
        return UserOutputDTO.fromModel(userModel);
    }
}
