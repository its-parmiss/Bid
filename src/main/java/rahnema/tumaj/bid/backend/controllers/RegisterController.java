package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import rahnema.tumaj.bid.backend.domains.user.UserEmailDTO;
import rahnema.tumaj.bid.backend.domains.user.UserInputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.assemblers.UserResourceAssembler;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalUserInputException;
import rahnema.tumaj.bid.backend.utils.validators.UserValidator;
import rahnema.tumaj.bid.backend.utils.validators.ValidatorConstants;

@RestController
public class RegisterController {

    private final UserService userService;
    private final UserResourceAssembler assembler;
    private final UserValidator userValidator;
    private final RestTemplate restTemplate;

    public RegisterController(UserService userService,
                              UserResourceAssembler assembler,
                              UserValidator userValidator) {
        this.userService = userService;
        this.assembler = assembler;
        this.userValidator = userValidator;
        this.restTemplate = new RestTemplate();
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
            restTemplate.postForEntity("http://localhost:8701/users", UserEmailDTO.fromModel(savedUser), String.class);
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