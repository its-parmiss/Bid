package rahnema.tumaj.bid.backend.utils.assemblers;

import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;
import rahnema.tumaj.bid.backend.controllers.RegisterController;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.User;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class UserResourceAssembler {
    public Resource<UserOutputDTO> toResource(UserOutputDTO user){
        return new Resource<>(
            user,
            linkTo(methodOn(RegisterController.class).getOneUser(user.getId())).withSelfRel()
        );
    }
}
