package pl.zbiczagromada.Magazynier.storageunit.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class StorageUnitNotFoundException extends ResponseStatusException {
    private final static String base1 = "storage unit";
    private final static String base2 = "not found";
    public StorageUnitNotFoundException() {
        super(HttpStatus.BAD_REQUEST, base1 + " " + base2);
    }

    public StorageUnitNotFoundException(String name) {
        super(HttpStatus.BAD_REQUEST, base1 + " '" + name + "' " + base2);
    }

    public StorageUnitNotFoundException(Long id) {
        super(HttpStatus.BAD_REQUEST, base1 + " with id '" + id + "' " + base2);
    }
}