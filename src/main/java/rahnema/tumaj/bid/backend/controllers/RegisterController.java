package rahnema.tumaj.bid.backend.controllers;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.web.bind.annotation.*;
import rahnema.tumaj.bid.backend.domains.user.UserInputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.User;
import rahnema.tumaj.bid.backend.services.UserService;
import rahnema.tumaj.bid.backend.utils.assemblers.UserResourceAssembler;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@RestController
public class RegisterController {

    private final UserService userService;
    private final UserResourceAssembler assembler;

    public RegisterController(UserService userService,
                              UserResourceAssembler assembler) {
        this.userService = userService;
        this.assembler = assembler;
    }

    @GetMapping(path = "/users/{id}")
    public Resource<UserOutputDTO> getOneUser(@PathVariable Long id) {
        User user = userService.getOne(id).orElseThrow(EntityNotFoundException::new);
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
        this.userService.addOne(user);
        return assembler.toResource(UserInputDTO.toModel(user));
    }

}