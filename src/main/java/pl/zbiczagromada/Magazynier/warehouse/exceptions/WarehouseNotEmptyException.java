package pl.zbiczagromada.Magazynier.warehouse.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class WarehouseNotEmptyException extends ResponseStatusException {
    private final static String base1 = "warehouse";
    private final static String base2 = "is not empty";
    public WarehouseNotEmptyException() {
        super(HttpStatus.CONFLICT, base1 + " " + base2);
    }

    public WarehouseNotEmptyException(String name) {
        super(HttpStatus.CONFLICT, base1 + " '" + name + "' " + base2);
    }

    public WarehouseNotEmptyException(Long id) {
        super(HttpStatus.CONFLICT, base1 + " with id '" + id + "' " + base2);
    }
}