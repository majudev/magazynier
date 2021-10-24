package pl.zbiczagromada.Magazynier.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class InvalidRequestException extends ResponseStatusException {
    private final static String base1 = "your request is missing";
    private final static String base2 = "fields";
    public InvalidRequestException() {
        super(HttpStatus.BAD_REQUEST, base1 + " some " + base2);
    }

    public InvalidRequestException(List<String> fields) {
        super(HttpStatus.BAD_REQUEST, base1 + " the following " + base2 + ": " + fields.toString());
    }
}
