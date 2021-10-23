package pl.zbiczagromada.Magazynier.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class UsernameAlreadyTakenException extends ResponseStatusException {
    private static final String base1 = "username";
    private static final String base2 = "already taken";

    public UsernameAlreadyTakenException() {
        super(HttpStatus.UNAUTHORIZED, base1 + " " + base2);
    }

    public UsernameAlreadyTakenException(String username) {
        super(HttpStatus.UNAUTHORIZED, base1 + " '" + username + "' " + base2);
    }
}
