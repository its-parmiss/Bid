package rahnema.tumaj.bid.backend.domains.Image;

import lombok.Data;
import org.modelmapper.ModelMapper;
import rahnema.tumaj.bid.backend.models.Auction;
import rahnema.tumaj.bid.backend.models.Images;
@Data
public class ImageInputDTO {
    String url;
    Auction auction;
    public static Images toModel(ImageInputDTO image){
        ModelMapper mapper = new ModelMapper();
        Images img =  mapper.map(image, Images.class);
        return img;
    }
}
