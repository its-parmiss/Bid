package rahnema.tumaj.bid.backend.services.Images;

import rahnema.tumaj.bid.backend.domains.Image.ImageInputDTO;
import rahnema.tumaj.bid.backend.domains.Image.ImageOutputDTO;
import rahnema.tumaj.bid.backend.models.Images;

public interface ImageService {
    Images addOne(ImageInputDTO image);
}
