package rahnema.tumaj.bid.backend.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import rahnema.tumaj.bid.backend.domains.user.UserEmailDTO;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalUserInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.UserNotFoundException;
import rahnema.tumaj.bid.backend.utils.validators.UserValidator;
import rahnema.tumaj.bid.backend.utils.validators.ValidatorConstants;

import java.util.Map;

@RestController
public class PasswordController {
    private final UserService userService;
    private final SecurityController securityController;
    private final UserValidator userValidator;
    private final RestTemplate restTemplate;

    public PasswordController(UserService userService,
                              SecurityController securityController,
                              UserValidator userValidator) {
        this.userService = userService;
        this.securityController = securityController;
        this.userValidator = userValidator;
        this.restTemplate = new RestTemplate();
    }

    @PostMapping("/forgot")
    public void resetPasswordViaEmail(@RequestBody Map<String, String> params) {
        String userEmail = params.get("email");
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));
        restTemplate.postForEntity("http://localhost:8701/forgot", UserEmailDTO.fromModel(user), String.class);
    }

    @PostMapping("/reset")
    public String reset(@RequestParam Map<String, String> params) {
        try {
            String token = params.get("token");
            String newPassword = params.get("password");

            UserEmailDTO userDTO = restTemplate
                    .postForObject("http://localhost:8701/reset", token, UserEmailDTO.class);

            User user = userService.findByEmail(userDTO.getEmail())
                    .orElseThrow(() -> new UserNotFoundException(userDTO.getEmail()));

            if (userValidator.isUserPasswordValid(newPassword, ValidatorConstants.PASSWORD)) {
                changeUserPassword(newPassword, user);
                return "Your password changed.";
            } else {
                throw new IllegalUserInputException();
            }
        } catch (HttpClientErrorException ex) {
            throw new TokenNotFoundException();
        }
    }

    private void changeUserPassword(String password, User user) {
        user.setPassword(
                securityController.bCryptPasswordEncoder
                        .encode(password)
        );



        userService.saveUser(user);
    }

}