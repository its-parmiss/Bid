package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.domains.user.UserInputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.user.UserServiceImpl;
import rahnema.tumaj.bid.backend.utils.assemblers.UserResourceAssembler;
import rahnema.tumaj.bid.backend.utils.exceptions.IllegalUserInputException;
import rahnema.tumaj.bid.backend.utils.exceptions.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class RegisterController {

    private final UserServiceImpl userService;
    private final UserResourceAssembler assembler;

    public RegisterController(UserServiceImpl userService,
                              UserResourceAssembler assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }

    @GetMapping(path = "/users/{id}")
    public Resource<UserOutputDTO> getOneUser(@PathVariable Long id) {
        User user = userService.getOne(id).orElseThrow(() -> new UserNotFoundException(id));
        return assembler.toResource(user);
    }

    @GetMapping(path = "/users")
    public Resources<Resource<UserOutputDTO>> getAllUsers() {
        List<Resource<UserOutputDTO>> users = userService.getAll().stream()
                .map(assembler::toResource)
                .collect(Collectors.toList());
        return new Resources<>(
            users,
            linkTo(methodOn(RegisterController.class).getAllUsers()).withSelfRel()
        );
    }

    @PostMapping(path = "/users")
    public Resource<UserOutputDTO> addUser(@RequestBody UserInputDTO user) {
        if(isUserValid(user)){
            this.userService.addOne(user);
            return assembler.toResource(UserInputDTO.toModel(user));
        } else
            throw new IllegalUserInputException();
    }

    private boolean isUserValid(UserInputDTO user) {
        String emailValidator = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$";
        String passwordValidator = "^.{6,37}$";
        String nameValidator = "^.{3,36}$";
        return  user.getEmail().matches(emailValidator) &&
                user.getPassword().matches(passwordValidator) &&
                user.getFirst_name().matches(nameValidator) &&
                (
                    user.getLast_name() == null ||
                    user.getLast_name().matches(nameValidator)
                );
    }

}