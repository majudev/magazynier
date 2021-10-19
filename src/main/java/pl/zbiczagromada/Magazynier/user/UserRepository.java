package pl.zbiczagromada.Magazynier.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);
    boolean existsByUsername(String username);

    public static class UserNotFoundException extends ResponseStatusException {
        public UserNotFoundException(String username) {
            super(HttpStatus.INTERNAL_SERVER_ERROR, "user '" + username + "' not found in internal database");
        }

        public UserNotFoundException(Long id) {
            super(HttpStatus.INTERNAL_SERVER_ERROR, "user with id '" + id + "' not found in internal database");
        }
    }
}
