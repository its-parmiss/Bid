package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.domains.AuthenticationRequest;
import rahnema.tumaj.bid.backend.domains.AuthenticationResponse;
import rahnema.tumaj.bid.backend.domains.user.UserInputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.UserDetailsServiceImpl;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.athentication.TokenUtil;
import rahnema.tumaj.bid.backend.utils.assemblers.UserResourceAssembler;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalUserInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.TokenNotFoundException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.UserNotFoundException;
import rahnema.tumaj.bid.backend.utils.validators.UserValidator;
import rahnema.tumaj.bid.backend.utils.validators.ValidatorConstants;


@RestController
public class RegisterController {

    private final UserService userService;
    private final UserResourceAssembler assembler;
    private final UserValidator userValidator;

    private TokenUtil tokenUtil;

    public RegisterController(UserService userService,
                              UserResourceAssembler assembler,
                              TokenUtil tokenUtil,
                              UserValidator userValidator) {
        this.userService = userService;
        this.assembler = assembler;
        this.userValidator = userValidator;
        this.tokenUtil = tokenUtil;
    }

    @GetMapping(path="/me")
    public Resource<UserOutputDTO> getUserInfo(@RequestHeader("Authorization") String token){
        User user = userService.getUserWithToken(token);
        return assembler.toResource(UserOutputDTO.fromModel(user));
    }

    @PostMapping(path = "/users")
    public AuthenticationResponse addUser(@RequestBody UserInputDTO user) {
        if (isUserValid(user)) {
            this.userService.addOne(user);
            return tokenUtil.generateNewAuthorization(UserInputDTO.toModel(user));
        } else {
            throw new IllegalUserInputException();
        }
    }

    private boolean isUserValid(UserInputDTO user) {
        return
            userValidator.isUserEmailValid(user.getEmail(), ValidatorConstants.EMAIL) &&
            userValidator.isUserNameValid(user.getFirstName(), user.getLastName(), ValidatorConstants.NAME) &&
            userValidator.isUserPasswordValid(user.getPassword(), ValidatorConstants.PASSWORD);
    }

}