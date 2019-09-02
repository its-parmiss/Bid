package rahnema.tumaj.bid.backend.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rahnema.tumaj.bid.backend.domains.tokens.ConfirmationTokenDTO;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.UserNotFoundException;

@Controller
public class TokenConfirmationController {
    private final UserService userService;
    private final RestTemplate restTemplate;

    public TokenConfirmationController(UserService userService) {
        this.userService = userService;
        this.restTemplate = new RestTemplate();
    }

    @GetMapping(path = "/confirmAccount")
    public String confirmAccount(@RequestParam("token") String token) {
        try {
            ConfirmationTokenDTO confirmationToken = restTemplate
                    .postForObject("http://localhost:8701/confirmAccount", token, ConfirmationTokenDTO.class);
            User user = userService.findByEmail(confirmationToken.getUserEmail())
                    .orElseThrow(() -> new UserNotFoundException(confirmationToken.getUserEmail()));
            if(user.isEnabled())
                throw new TokenNotFoundException();
            user.setEnabled(true);
            userService.saveUser(user);
        } catch (HttpClientErrorException ex) {
            throw new TokenNotFoundException();
        }
        return "errors/accountConfirmed";
    }
}
