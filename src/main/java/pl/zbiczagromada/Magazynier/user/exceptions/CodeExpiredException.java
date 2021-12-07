package pl.zbiczagromada.Magazynier.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class CodeExpiredException extends ResponseStatusException {
    public CodeExpiredException() {
        super(HttpStatus.UNAUTHORIZED, "provided code has already expired");
    }
}
