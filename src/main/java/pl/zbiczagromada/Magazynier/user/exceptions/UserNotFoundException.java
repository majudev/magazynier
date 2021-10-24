package pl.zbiczagromada.Magazynier.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {
    public UserNotFoundException(String username) {
        super(HttpStatus.BAD_REQUEST, "user '" + username + "' not found in internal database");
    }

    public UserNotFoundException(Long id) {
        super(HttpStatus.BAD_REQUEST, "user with id '" + id + "' not found in internal database");
    }
}
