package rahnema.tumaj.bid.backend.repositories;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import rahnema.tumaj.bid.backend.models.User;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByResetToken(String token);
    boolean existsByEmail(String email);
}