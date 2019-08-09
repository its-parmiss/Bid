package rahnema.tumaj.bid.backend.domains;

import lombok.Data;

@Data
public class AuthenticationRequest {

	private String email;
	private String password;

}
