package rahnema.tumaj.bid.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rahnema.tumaj.bid.backend.models.User;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
}
