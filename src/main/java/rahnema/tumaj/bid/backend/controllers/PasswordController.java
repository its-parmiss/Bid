package rahnema.tumaj.bid.backend.controllers;

import org.springframework.mail.SimpleMailMessage;
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
        String userEmail = params.get("email");
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));

        user.setResetToken(UUID.randomUUID().toString());
        userService.saveUser(user);

        sendPasswordRecoveryEmailToUser(user);
    }

    private void sendPasswordRecoveryEmailToUser(User user) {
        String to = user.getEmail();
        String subject = "Tumaj Password Recovery";
        String message = "Click this link below to reset your password:\n" +
                "http://192.168.11.191/forgot?token=" +
                user.getResetToken();

        SimpleMailMessage mail = createMail(to, subject, message);
        emailService.sendSimpleEmail(mail);
    }

    private SimpleMailMessage createMail(String to, String subject, String message) {
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject(subject);
        email.setText(message);
        return email;
    }

    @PostMapping("/reset")
    public void reset(@RequestParam Map<String, String> params) {
        User user = userService.findByResetToken(params.get("token"))
                .orElseThrow(TokenNotFoundException::new);

        String newPassword = params.get("password");
        if (userValidator.isUserPasswordValid(newPassword, ValidatorConstants.PASSWORD)) {
            changeUserPassword(newPassword, user);
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