package pl.zbiczagromada.Magazynier.storageunit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class StorageUnitNotEmptyException extends ResponseStatusException {
    private final static String base1 = "storage unit";
    private final static String base2 = "is not empty";
    public StorageUnitNotEmptyException() {
        super(HttpStatus.CONFLICT, base1 + " " + base2);
    }

    public StorageUnitNotEmptyException(String name) {
        super(HttpStatus.CONFLICT, base1 + " '" + name + "' " + base2);
    }

    public StorageUnitNotEmptyException(Long id) {
        super(HttpStatus.CONFLICT, base1 + " with id '" + id + "' " + base2);
    }
}