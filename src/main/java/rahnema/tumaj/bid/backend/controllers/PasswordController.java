package rahnema.tumaj.bid.backend.controllers;

import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.email.EmailService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.assemblers.UserResourceAssembler;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalUserInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.UserNotFoundException;
import rahnema.tumaj.bid.backend.utils.validators.UserValidator;
import rahnema.tumaj.bid.backend.utils.validators.UserValidatorImpl;
import rahnema.tumaj.bid.backend.utils.validators.ValidatorConstants;

import java.util.Map;
import java.util.UUID;

@RestController
public class PasswordController {
    private final UserService userService;
    private final EmailService emailService;
    private final SecurityController securityController;
    private final UserValidator userValidator;

    public PasswordController(UserService userService,
                              UserResourceAssembler assembler,
                              EmailService emailService,
                              SecurityController securityController,
                              UserValidator userValidator) {
        this.userService = userService;
        this.emailService = emailService;
        this.securityController = securityController;
        this.userValidator = userValidator;
    }

    @PostMapping("/forgot")
    public void resetPasswordViaEmail(@RequestBody Map<String, String> params) {
        String email = params.get("email");
        User user = userService.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(email));

        user.setResetToken(UUID.randomUUID().toString());
        userService.saveUser(user);

        String message = "Click this link below to reset your password:\n" +
                "http://localhost:8080/forgot?token=" +
                user.getResetToken();

        sendEmail(user, message);
    }

    private void sendEmail(User user, String message) {
        emailService.sendSimpleEmail(user.getEmail(), "Tumaj Password Recovery", message);
    }

    @PostMapping("/reset")
    public void reset(@RequestParam Map<String, String> params) {
        User user = userService.findByResetToken(params.get("token"))
                .orElseThrow(TokenNotFoundException::new);

        if (userValidator.isUserPasswordValid(user.getPassword(), ValidatorConstants.PASSWORD)) {
            changeUserPassword(params.get("password"), user);
        } else {
            throw new IllegalUserInputException();
        }
    }

    public void changeUserPassword(String password, User user) {
        user.setPassword(
                securityController.bCryptPasswordEncoder
                        .encode(password)
        );
        user.setResetToken(null);
        userService.saveUser(user);
    }
}