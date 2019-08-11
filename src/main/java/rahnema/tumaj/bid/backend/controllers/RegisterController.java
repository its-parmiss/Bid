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
    private UserDetailsService userDetailsService;
    public RegisterController(UserService userService,
                              UserResourceAssembler assembler,
                              TokenUtil tokenUtil,
                              UserDetailsServiceImpl userDetailsService,
                              UserValidator userValidator) {
        this.userService = userService;
        this.assembler = assembler;
        this.userDetailsService = userDetailsService;
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
            AuthenticationRequest authenticationRequest = new AuthenticationRequest();
            authenticationRequest.setEmail(user.getEmail());
            authenticationRequest.setPassword(user.getPassword());
            final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getEmail());
            final String token = tokenUtil.generateToken(userDetails);
            return new AuthenticationResponse(token);
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