package rahnema.tumaj.bid.backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.domains.email.EmailDTO;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.email.EmailService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.assemblers.UserResourceAssembler;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalUserInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.TokenNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.UserNotFoundException;

import java.util.Map;
import java.util.UUID;


@RestController
public class PasswordController {
    private final UserService userService;
    private final EmailService emailService;
    private final SecurityController securityController;

    public PasswordController(UserService userService,
                              UserResourceAssembler assembler,
                              EmailService emailService,
                              SecurityController securityController) {
        this.userService = userService;
        this.emailService = emailService;
        this.securityController = securityController;
    }

    @PostMapping("/forgot")
    public ResponseEntity<String> resetPasswordViaEmail(@RequestBody EmailDTO emailDto) {
        User user = userService.findByEmail(emailDto.email)
                .orElseThrow(() -> new UserNotFoundException(emailDto.email));
        user.setResetToken(UUID.randomUUID().toString());
        userService.saveUser(user);
        String message = "Click this link below to reset your password:\n" +
                "http://localhost:8080/forgot?token=" +
                user.getResetToken();

        emailService.sendSimpleEmail(emailDto.email, "Tumaj Password Recovery", message);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PostMapping("/reset")
    public ResponseEntity<String> reset(@RequestParam Map<String, String> params) {
        User user = userService.findByResetToken(params.get("token"))
                .orElseThrow(TokenNotFoundException::new);

        String passwordValidator = "^.{6,37}$";
        if (params.get("password").matches(passwordValidator)) {
            user.setPassword(
                    securityController.bCryptPasswordEncoder
                            .encode(params.get("password"))
            );
            user.setResetToken(null);
            userService.saveUser(user);
            return new ResponseEntity<>(HttpStatus.OK);
        } else {
            throw new IllegalUserInputException();
        }
    }
}