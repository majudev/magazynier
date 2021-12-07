package pl.zbiczagromada.Magazynier.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidCodeException extends ResponseStatusException {
    public InvalidCodeException() {
        super(HttpStatus.BAD_REQUEST, "provided code is invalid");
    }
}
