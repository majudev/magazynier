package pl.zbiczagromada.Magazynier.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotFoundException extends ResponseStatusException {
    public UserNotFoundException(String username) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "user '" + username + "' not found in internal database");
    }

    public UserNotFoundException(Long id) {
        super(HttpStatus.INTERNAL_SERVER_ERROR, "user with id '" + id + "' not found in internal database");
    }
}
