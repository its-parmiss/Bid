package rahnema.tumaj.bid.backend.services;

import org.springframework.stereotype.Service;
import rahnema.tumaj.bid.backend.repositories.UserRepository;

@Service("userService")
public class UserService {
    final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }



}
