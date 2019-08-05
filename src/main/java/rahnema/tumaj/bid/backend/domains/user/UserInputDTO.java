package rahnema.tumaj.bid.backend.domains.user;

import lombok.Data;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.User;

@Data
public class UserInputDTO {
    private String first_name;
    private String last_name;
    private String password;
    private String email;
    private String profile_picture;

    public static User toModel(UserInputDTO user) {
        ModelMapper mapper = new ModelMapper();
        return mapper.map(user, User.class);
    }
}