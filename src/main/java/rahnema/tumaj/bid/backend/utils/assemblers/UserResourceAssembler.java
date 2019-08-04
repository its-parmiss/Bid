package rahnema.tumaj.bid.backend.utils.assemblers;

import org.springframework.hateoas.Resource;
import rahnema.tumaj.bid.backend.controllers.RegisterController;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.User;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;


public class UserResourceAssembler {
    public Resource<UserOutputDTO> toResource(User user){
        return new Resource<>(
            UserOutputDTO.fromModel(user),
            linkTo(methodOn(RegisterController.class).getOneUser(user.getId())).withSelfRel(),
            linkTo(methodOn(RegisterController.class).getAllUsers()).withRel("all")
        );
    }
}
