package rahnema.tumaj.bid.backend.domains.Image;

import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.Images;

public class ImageOutputDTO {
    String url;
    public static ImageOutputDTO fromModel(Images image){
        ModelMapper mapper = new ModelMapper();
        return mapper.map(image, ImageOutputDTO.class);
    }
}
