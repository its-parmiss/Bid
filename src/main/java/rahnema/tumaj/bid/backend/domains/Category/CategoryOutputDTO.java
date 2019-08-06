package rahnema.tumaj.bid.backend.domains.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.Category;

@Data @AllArgsConstructor @NoArgsConstructor
public class CategoryOutputDTO {
    private Long id;
    private String title;
    public static CategoryOutputDTO fromModel(Category category){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(category, CategoryOutputDTO.class);
    }
}
