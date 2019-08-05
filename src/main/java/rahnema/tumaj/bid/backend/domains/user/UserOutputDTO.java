package rahnema.tumaj.bid.backend.domains.user;

import lombok.Data;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.User;

@Data
public class UserOutputDTO {
    private Long id;
    private String first_name;
    private String last_name;
    private String profile_picture;

    public static UserOutputDTO fromModel(User user){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(user, UserOutputDTO.class);
    }

}
