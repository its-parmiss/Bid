package rahnema.tumaj.bid.backend.domains.user;

import lombok.Data;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.User;

import javax.validation.constraints.Null;

@Data
public class UserOutputDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String profilePicture;

    public static UserOutputDTO fromModel(User user){
        ModelMapper mapper = new ModelMapper();

        UserOutputDTO outputDTO = mapper.map(user, UserOutputDTO.class);
        try {
            String[] profilePath = outputDTO.getProfilePicture().split("/");
            String profileName = profilePath[profilePath.length-1];
            outputDTO.setProfilePicture(profileName);
        } catch (NullPointerException ignore){}

        return outputDTO;
    }

}
