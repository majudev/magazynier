package pl.zbiczagromada.Magazynier.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

public class EmptyPropertyException extends ResponseStatusException {
    private final static String base1 = "request field";
    private final static String base2 = "is an empty string";
    public EmptyPropertyException() {
        super(HttpStatus.BAD_REQUEST, base1 + " " + base2);
    }

    public EmptyPropertyException(List<String> fields) {
        super(HttpStatus.BAD_REQUEST, base1 + " [" + fields.toString() + "] " + base2);
    }
}
