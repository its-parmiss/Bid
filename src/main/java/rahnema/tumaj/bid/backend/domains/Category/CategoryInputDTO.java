package rahnema.tumaj.bid.backend.domains.Category;

import lombok.Data;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.Category;

@Data
public class CategoryInputDTO {


    private String title;
    public static Category toModel(CategoryInputDTO category){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(category, Category.class);
    }
}
