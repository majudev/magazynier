package pl.zbiczagromada.Magazynier.item.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ItemNotFoundException extends ResponseStatusException {
    private final static String base1 = "item";
    private final static String base2 = "not found";
    public ItemNotFoundException() {
        super(HttpStatus.UNAUTHORIZED, base1 + " " + base2);
    }

    public ItemNotFoundException(Long id) {
        super(HttpStatus.UNAUTHORIZED, base1 + " with id '" + id + "' " + base2);
    }
}
