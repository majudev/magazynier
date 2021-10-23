package pl.zbiczagromada.Magazynier.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidParametersException extends ResponseStatusException {
    public InvalidParametersException() {
        super(HttpStatus.UNAUTHORIZED, "invalid parameters have been supplied for this request");
    }
}
