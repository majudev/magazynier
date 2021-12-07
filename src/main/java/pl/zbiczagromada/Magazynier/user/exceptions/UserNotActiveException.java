package pl.zbiczagromada.Magazynier.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotActiveException extends ResponseStatusException {
    public UserNotActiveException(String username) {
        super(HttpStatus.UNAUTHORIZED, "user '" + username + "' has not confirmed email yet");
    }

    public UserNotActiveException(Long id) {
        super(HttpStatus.UNAUTHORIZED, "user with id '" + id + "' has not confirmed email yet");
    }
}
