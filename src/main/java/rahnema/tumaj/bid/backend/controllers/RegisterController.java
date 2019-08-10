package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.domains.user.UserInputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.user.UserService;
import rahnema.tumaj.bid.backend.utils.assemblers.UserResourceAssembler;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalInputExceptions.IllegalUserInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.NotFoundExceptions.UserNotFoundException;
import rahnema.tumaj.bid.backend.utils.validators.UserValidator;
import rahnema.tumaj.bid.backend.utils.validators.ValidatorConstants;


@RestController
public class RegisterController {

    private final UserService userService;
    private final UserResourceAssembler assembler;
    private final UserValidator userValidator;

    public RegisterController(UserService userService,
                              UserResourceAssembler assembler,
                              UserValidator userValidator) {
        this.userService = userService;
        this.assembler = assembler;
        this.userValidator = userValidator;
    }

    @GetMapping(path = "/users/{id}")
    public Resource<UserOutputDTO> getOneUser(@PathVariable Long id) {
        User user = userService.getOne(id).orElseThrow(() -> new UserNotFoundException(id));
        return assembler.toResource(UserOutputDTO.fromModel(user));
    }

    @PostMapping(path = "/users")
    public Resource<UserOutputDTO> addUser(@RequestBody UserInputDTO user) {
        if (isUserValid(user)) {
            UserOutputDTO savedUser = this.userService.addOne(user);
            return assembler.toResource(savedUser);
        } else {
            throw new IllegalUserInputException();
        }
    }

    private boolean isUserValid(UserInputDTO user) {
        return
            userValidator.isUserEmailValid(user.getEmail(), ValidatorConstants.EMAIL) &&
            userValidator.isUserNameValid(user.getFirst_name(), user.getLast_name(), ValidatorConstants.NAME) &&
            userValidator.isUserPasswordValid(user.getPassword(), ValidatorConstants.PASSWORD);
    }

}