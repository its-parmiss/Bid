package rahnema.tumaj.bid.backend.services;

import java.util.ArrayList;

import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import lombok.Getter;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.repositories.UserRepository;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.UserNotFoundException;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	@Getter
	private User user;

	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException(email));
		if (!user.isEnabled()) {
			throw new DisabledException("This user is not activated");
		}

		this.user = user;
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
	}

}
