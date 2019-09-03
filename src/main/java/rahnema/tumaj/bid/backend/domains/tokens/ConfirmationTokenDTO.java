package rahnema.tumaj.bid.backend.domains.tokens;

import lombok.Data;

@Data
public class ConfirmationTokenDTO {
    String confirmationToken;
    String userEmail;
}
