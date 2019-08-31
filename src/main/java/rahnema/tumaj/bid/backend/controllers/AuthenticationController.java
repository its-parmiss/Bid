package rahnema.tumaj.bid.backend.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import rahnema.tumaj.bid.backend.domains.authentication.AuthenticationRequest;
import rahnema.tumaj.bid.backend.domains.authentication.AuthenticationResponse;
import rahnema.tumaj.bid.backend.services.UserDetailsServiceImpl;
import rahnema.tumaj.bid.backend.utils.athentication.JwtTokenUtil;
import rahnema.tumaj.bid.backend.utils.athentication.TokenUtil;

@RestController
public class AuthenticationController {

	private AuthenticationManager authenticationManager;
	private TokenUtil tokenUtil;
	private UserDetailsService userDetailsService;

	@Autowired
	public AuthenticationController(AuthenticationManager authenticationManager,
                                    JwtTokenUtil tokenUtil,
                                    UserDetailsServiceImpl userDetailService) {

		this.authenticationManager = authenticationManager;
		this.tokenUtil = tokenUtil;
		this.userDetailsService = userDetailService;
	}

	@PostMapping("/authenticate")
	public AuthenticationResponse createAuthenticationToken(
			@RequestBody AuthenticationRequest authenticationRequest) throws Exception {

		authenticate(authenticationRequest.getEmail(), authenticationRequest.getPassword());
		final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
		final String token = tokenUtil.generateToken(userDetails);
		return new AuthenticationResponse(token);
	}

	private void authenticate(String username, String password) throws Exception {
		try {
			authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		}
	}

}
