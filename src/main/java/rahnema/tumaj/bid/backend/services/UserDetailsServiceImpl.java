package rahnema.tumaj.bid.backend.services;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
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

	@Autowired
	UserRepository userRepository;

	@Getter
	private User user;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email).orElseThrow(()-> new UserNotFoundException(email));
		if (user == null)
			throw new UsernameNotFoundException("User not found with username: " + email);
		this.user = user;
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), new ArrayList<>());
	}

}
