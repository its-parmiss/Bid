package rahnema.tumaj.bid.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import rahnema.tumaj.bid.backend.models.Images;

public interface ImageRepository extends CrudRepository<Images,String > {
}
