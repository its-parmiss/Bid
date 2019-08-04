package rahnema.tumaj.bid.backend.domains;

import lombok.Data;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.User;

@Data
public class UserInputDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String profilePicture;

    public static UserInputDTO fromModel(User user) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(user, UserInputDTO.class);
    }
}
