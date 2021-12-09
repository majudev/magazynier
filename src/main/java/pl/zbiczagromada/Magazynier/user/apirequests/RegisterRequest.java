package pl.zbiczagromada.Magazynier.user.apirequests;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RegisterRequest {
    private String username;
    private String displayname;
    private String password;
    private String email;
    private String permissionGroup;
}
