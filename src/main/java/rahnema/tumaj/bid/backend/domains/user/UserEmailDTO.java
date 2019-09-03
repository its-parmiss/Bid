package rahnema.tumaj.bid.backend.domains.user;

import lombok.Data;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.User;

@Data
public class UserEmailDTO {
    String email;

    public static UserEmailDTO fromModel(User user){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(user, UserEmailDTO.class);
    }

}
