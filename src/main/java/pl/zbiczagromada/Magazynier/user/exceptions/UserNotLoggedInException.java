package pl.zbiczagromada.Magazynier.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserNotLoggedInException extends ResponseStatusException {
    public UserNotLoggedInException() {
        super(HttpStatus.UNAUTHORIZED, "user not logged in");
    }
}
