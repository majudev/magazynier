package pl.zbiczagromada.Magazynier.user.permissiongroups.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InsufficientPermissionsException extends ResponseStatusException {
    private final static String base1 = "user";
    private final static String base2 = "doesnt have permission to do this";

    public InsufficientPermissionsException() {
        super(HttpStatus.FORBIDDEN, base1 + " " + base2);
    }

    public InsufficientPermissionsException(String username) {
        super(HttpStatus.FORBIDDEN, base1 + " '" + username + "' " + base2);
    }

    public InsufficientPermissionsException(Long id) {
        super(HttpStatus.FORBIDDEN, base1 + " with id '" + id + "' " + base2);
    }
}
