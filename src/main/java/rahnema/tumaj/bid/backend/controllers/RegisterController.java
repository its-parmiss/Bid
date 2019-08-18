package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.domains.user.UserInputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.ConfirmationToken;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.confiramtionToken.ConfirmationTokenService;
import rahnema.tumaj.bid.backend.services.email.EmailService;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.athentication.TokenUtil;
import rahnema.tumaj.bid.backend.utils.assemblers.UserResourceAssembler;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalUserInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;
import rahnema.tumaj.bid.backend.utils.validators.UserValidator;
import rahnema.tumaj.bid.backend.utils.validators.ValidatorConstants;


@RestController
public class RegisterController {

    private final UserService userService;
    private final UserResourceAssembler assembler;
    private final UserValidator userValidator;
    private final ConfirmationTokenService confirmationTokenService;
    private final EmailService emailService;
    private TokenUtil tokenUtil;

    public RegisterController(UserService userService,
                              UserResourceAssembler assembler,
                              TokenUtil tokenUtil,
                              UserValidator userValidator,
                              ConfirmationTokenService confirmationTokenService,
                              EmailService emailService) {
        this.userService = userService;
        this.assembler = assembler;
        this.userValidator = userValidator;
        this.tokenUtil = tokenUtil;
        this.confirmationTokenService = confirmationTokenService;
        this.emailService = emailService;
    }

    @GetMapping(path="/me")
    public Resource<UserOutputDTO> getUserInfo(@RequestHeader("Authorization") String token){
        User user = userService.getUserWithToken(token);
        return assembler.toResource(UserOutputDTO.fromModel(user));
    }

    @PostMapping(path = "/users")
    public void addUser(@RequestBody UserInputDTO user) {
        if (isUserValid(user)) {
            User savedUser = this.userService.addOne(user);
            ConfirmationToken ct = new ConfirmationToken();
            ct.setUser(savedUser);
            ConfirmationToken confirmationToken =
                    this.confirmationTokenService.save(ct);

            sendVerificationEmailToUser(user, confirmationToken);
        } else {
            throw new IllegalUserInputException();
        }
    }

    @GetMapping(path = "/confirmAccount")
    public String confirmAccount(@RequestParam("token") String token) {
        ConfirmationToken confirmationToken = confirmationTokenService.findByConfirmationToken(token)
                .orElseThrow(TokenNotFoundException::new);
        User user = confirmationToken.getUser();
        user.setEnabled(true);
        User savedUser = userService.saveUser(user);
        return "Your Account Has Been Confirmed!";
    }

    private void sendVerificationEmailToUser(@RequestBody UserInputDTO user, ConfirmationToken confirmationToken) {
        String to = user.getEmail();
        String subject = "Tumaj Account Verification";
        String message = "Click this link below to confirm your account:\n" +
                "http://localhost:8080/confirmAccount?token=" +
                confirmationToken.getConfirmationToken();

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

    private boolean isUserValid(UserInputDTO user) {
        return
            userValidator.isUserEmailValid(user.getEmail(), ValidatorConstants.EMAIL) &&
            userValidator.isUserNameValid(user.getFirstName(), user.getLastName(), ValidatorConstants.NAME) &&
            userValidator.isUserPasswordValid(user.getPassword(), ValidatorConstants.PASSWORD);
    }

}