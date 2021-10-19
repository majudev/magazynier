package pl.zbiczagromada.Magazynier.user;

import lombok.Getter;
import lombok.Setter;

public class ChangePasswordRequest {
    @Getter @Setter
    private String password;
    @Getter @Setter
    private String newpassword;
}
