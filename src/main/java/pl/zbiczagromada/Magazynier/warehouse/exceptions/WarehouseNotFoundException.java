package pl.zbiczagromada.Magazynier.warehouse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WarehouseNotFoundException extends ResponseStatusException {
    private final static String base1 = "warehouse";
    private final static String base2 = "not found";
    public WarehouseNotFoundException() {
        super(HttpStatus.UNAUTHORIZED, base1 + " " + base2);
    }

    public WarehouseNotFoundException(String name) {
        super(HttpStatus.UNAUTHORIZED, base1 + " '" + name + "' " + base2);
    }

    public WarehouseNotFoundException(Long id) {
        super(HttpStatus.UNAUTHORIZED, base1 + " with id '" + id + "' " + base2);
    }
}