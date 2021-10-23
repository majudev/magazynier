package pl.zbiczagromada.Magazynier.itemgroup.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ItemGroupNotFoundException extends ResponseStatusException {
    private final static String base1 = "item group";
    private final static String base2 = "not found";
    public ItemGroupNotFoundException() {
        super(HttpStatus.UNAUTHORIZED, base1 + " " + base2);
    }

    public ItemGroupNotFoundException(String name) {
        super(HttpStatus.UNAUTHORIZED, base1 + " '" + name + "' " + base2);
    }

    public ItemGroupNotFoundException(Long id) {
        super(HttpStatus.UNAUTHORIZED, base1 + " with id '" + id + "' " + base2);
    }
}
