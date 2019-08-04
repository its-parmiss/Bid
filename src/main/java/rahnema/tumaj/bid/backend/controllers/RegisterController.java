package rahnema.tumaj.bid.backend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import rahnema.tumaj.bid.backend.domains.UserInputDTO;

@RestController
public class RegisterController {
    // TODO: Change the return type to <UserOutputDto>
    @PostMapping(path = "/users")
    public void addUser(UserInputDTO user) {

    }

    // TODO: Change the return type to <UserOutputDto>
    @GetMapping(path = "/users/{id}")
    public void getUser(UserInputDTO user, @PathVariable String id) {

    }

}