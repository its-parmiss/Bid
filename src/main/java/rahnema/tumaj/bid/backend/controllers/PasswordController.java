package rahnema.tumaj.bid.backend.controllers;

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
    public void resetPasswordViaEmail(@RequestBody EmailDTO emailDto) {
        User user = userService.findByEmail(emailDto.email)
                .orElseThrow(() -> new UserNotFoundException(emailDto.email));
        user.setResetToken(UUID.randomUUID().toString());
        userService.saveUser(user);
        String message = "Click this link below to reset your password:\n" +
                         "localhost:8080/reset.html?token=" +
                          user.getResetToken();

        emailService.sendSimpleEmail(emailDto.email, "Tumaj Password Recovery", message);
        System.out.println("sent");
    }

    @PostMapping("/reset")
    public void reset(@RequestBody Map<String, String> params) {

        User user = userService.findByResetToken(params.get("token"))
                .orElseThrow(TokenNotFoundException::new);

        String passwordValidator = "^.{6,37}$";
        if(params.get("password").matches(passwordValidator)){
            user.setPassword(
                securityController.bCryptPasswordEncoder
                        .encode(params.get("password"))
            );
            user.setResetToken(null);
            userService.saveUser(user);
        } else {
            throw new IllegalUserInputException();
        }
    }
}