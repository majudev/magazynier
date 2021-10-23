package pl.zbiczagromada.Magazynier.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class EmailAlreadyTakenException extends ResponseStatusException {
    private static final String base1 = "email";
    private static final String base2 = "already taken";

    public EmailAlreadyTakenException() {
        super(HttpStatus.UNAUTHORIZED, base1 + " " + base2);
    }

    public EmailAlreadyTakenException(String email) {
        super(HttpStatus.UNAUTHORIZED, base1 + " '" + email + "' " + base2);
    }
}
