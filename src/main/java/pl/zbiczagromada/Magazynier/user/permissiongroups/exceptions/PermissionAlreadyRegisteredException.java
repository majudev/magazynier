package pl.zbiczagromada.Magazynier.user.permissiongroups.exceptions;

public class PermissionAlreadyRegisteredException extends RuntimeException {
    private static final String base1 = "permission";
    private static final String base2 = "is already registered";

    public PermissionAlreadyRegisteredException(){
        super(base1 + " " + base2);
    }

    public PermissionAlreadyRegisteredException(String permission){
        super(base1 + " with name '" + permission + "' " + base2);
    }
}
