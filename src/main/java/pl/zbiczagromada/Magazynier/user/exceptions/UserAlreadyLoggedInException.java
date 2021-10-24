package pl.zbiczagromada.Magazynier.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UserAlreadyLoggedInException extends ResponseStatusException {
    private final static String base1 = "user";
    private final static String base2 = "already logged in";

    public UserAlreadyLoggedInException() {
        super(HttpStatus.FORBIDDEN, base1 + " " + base2);
    }

    public UserAlreadyLoggedInException(String username) {
        super(HttpStatus.FORBIDDEN, base1 + " '" + username + "' " + base2);
    }

    public UserAlreadyLoggedInException(Long id) {
        super(HttpStatus.FORBIDDEN, base1 + " with id '" + id + "' " + base2);
    }
}
