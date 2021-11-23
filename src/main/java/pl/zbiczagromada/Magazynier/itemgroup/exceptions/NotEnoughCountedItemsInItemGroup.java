package pl.zbiczagromada.Magazynier.itemgroup.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NotEnoughCountedItemsInItemGroup extends ResponseStatusException {
    private final static String base1 = "item group";
    private final static String base2 = "doesn't have enough counted members to perform this operation";
    public NotEnoughCountedItemsInItemGroup() {
        super(HttpStatus.BAD_REQUEST, base1 + " " + base2);
    }

    public NotEnoughCountedItemsInItemGroup(String name) {
        super(HttpStatus.BAD_REQUEST, base1 + " '" + name + "' " + base2);
    }

    public NotEnoughCountedItemsInItemGroup(Long id) {
        super(HttpStatus.BAD_REQUEST, base1 + " with id '" + id + "' " + base2);
    }
}
