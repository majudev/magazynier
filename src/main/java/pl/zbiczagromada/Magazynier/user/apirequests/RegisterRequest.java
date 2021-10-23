package pl.zbiczagromada.Magazynier.user.apirequests;

import lombok.Getter;
import lombok.Setter;

public class RegisterRequest {
    @Getter @Setter
    private String username;
    @Getter @Setter
    private String password;
    @Getter @Setter
    private String email;
}
