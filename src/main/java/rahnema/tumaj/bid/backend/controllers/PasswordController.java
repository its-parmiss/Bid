package rahnema.tumaj.bid.backend.controllers;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.models.ForgotToken;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.email.EmailService;
import rahnema.tumaj.bid.backend.services.forgotToken.ForgotTokenService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.assemblers.UserResourceAssembler;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalUserInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.UserNotFoundException;
import rahnema.tumaj.bid.backend.utils.validators.UserValidator;
import rahnema.tumaj.bid.backend.utils.validators.ValidatorConstants;

import java.util.Map;
import java.util.UUID;

@RestController
public class PasswordController {
    private final UserService userService;
    private final EmailService emailService;
    private final SecurityController securityController;
    private final UserValidator userValidator;
    private final ForgotTokenService forgotTokenService;

    public PasswordController(UserService userService,
                              EmailService emailService,
                              SecurityController securityController,
                              UserValidator userValidator,
                              ForgotTokenService forgotTokenService) {
        this.userService = userService;
        this.emailService = emailService;
        this.securityController = securityController;
        this.userValidator = userValidator;
        this.forgotTokenService = forgotTokenService;
    }

    @PostMapping("/forgot")
    public void resetPasswordViaEmail(@RequestBody Map<String, String> params) {
        String userEmail = params.get("email");
        User user = userService.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(userEmail));

        ForgotToken forgotToken = new ForgotToken();
        forgotToken.setUser(user);
        forgotTokenService.save(forgotToken);

        sendPasswordRecoveryEmailToUser(user, forgotToken);
    }

    private void sendPasswordRecoveryEmailToUser(User user, ForgotToken forgotToken) {
        String to = user.getEmail();
        String subject = "Tumaj Password Recovery";
        String message = "Click this link below to reset your password:\n" +
                "http://192.168.11.191/forgot?token=" +
                forgotToken.getForgotToken();

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
    public String reset(@RequestParam Map<String, String> params) {

        ForgotToken forgotToken = forgotTokenService.findByForgotToken(params.get("token"))
                .orElseThrow(TokenNotFoundException::new);

        String newPassword = params.get("password");
        if (userValidator.isUserPasswordValid(newPassword, ValidatorConstants.PASSWORD)) {
            changeUserPasswordViaForgotToken(newPassword, forgotToken);
            return "Your Password Changed";
        } else {
            throw new IllegalUserInputException();
        }
    }

    private void changeUserPasswordViaForgotToken(String password, ForgotToken forgotToken) {
        User user = forgotToken.getUser();
        user.setPassword(
                securityController.bCryptPasswordEncoder
                        .encode(password)
        );
        userService.saveUser(user);
        forgotToken.setForgotToken("Activated");
        forgotTokenService.save(forgotToken);
    }

}