package pl.zbiczagromada.Magazynier.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidCredentialsException extends ResponseStatusException {
    private final static String base = "invalid credentials provided for user";

    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, base);
    }

    public InvalidCredentialsException(String username) {
        super(HttpStatus.UNAUTHORIZED, base + " '" + username + "'");
    }

    public InvalidCredentialsException(Long id) {
        super(HttpStatus.UNAUTHORIZED, base + " with id '" + id + "'");
    }
}
