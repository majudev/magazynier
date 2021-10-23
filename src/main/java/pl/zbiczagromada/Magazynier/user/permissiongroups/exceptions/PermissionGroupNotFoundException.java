package pl.zbiczagromada.Magazynier.user.permissiongroups.exceptions;

public class PermissionGroupNotFoundException extends RuntimeException{
    private static final String base1 = "permission group";
    private static final String base2 = "not found";

    public PermissionGroupNotFoundException(){
        super(base1 + " " + base2);
    }

    public PermissionGroupNotFoundException(String groupName){
        super(base1 + " with name '" + groupName + "' " + base2);
    }
}
