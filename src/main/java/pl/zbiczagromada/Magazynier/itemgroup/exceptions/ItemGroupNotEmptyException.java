package pl.zbiczagromada.Magazynier.itemgroup.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class ItemGroupNotEmptyException extends ResponseStatusException {
    private final static String base1 = "item group";
    private final static String base2 = "is not empty";
    public ItemGroupNotEmptyException() {
        super(HttpStatus.CONFLICT, base1 + " " + base2);
    }

    public ItemGroupNotEmptyException(String name) {
        super(HttpStatus.CONFLICT, base1 + " '" + name + "' " + base2);
    }

    public ItemGroupNotEmptyException(Long id) {
        super(HttpStatus.CONFLICT, base1 + " with id '" + id + "' " + base2);
    }
}
