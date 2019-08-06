package rahnema.tumaj.bid.backend.utils.assemblers;


import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;
import rahnema.tumaj.bid.backend.controllers.CategoryController;
import rahnema.tumaj.bid.backend.controllers.RegisterController;
import rahnema.tumaj.bid.backend.domains.Category.CategoryOutputDTO;
import rahnema.tumaj.bid.backend.domains.user.UserOutputDTO;
import rahnema.tumaj.bid.backend.models.Category;
import rahnema.tumaj.bid.backend.models.User;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

@Component
public class CategoryAssembler {
    public Resource<CategoryOutputDTO> toResource(Category category){
        return new Resource<>(
                CategoryOutputDTO.fromModel(category),
                linkTo(methodOn(CategoryController.class).getAllCategories()).withRel("all")
        );
    }
}