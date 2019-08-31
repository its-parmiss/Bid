package rahnema.tumaj.bid.backend.domains.authentication;

import lombok.Data;

@Data
public class AuthenticationRequest {

	private String email;
	private String password;

}
