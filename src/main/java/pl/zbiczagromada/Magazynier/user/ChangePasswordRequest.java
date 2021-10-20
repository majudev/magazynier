package pl.zbiczagromada.Magazynier.user;

import lombok.Getter;

@Getter
public class ChangePasswordRequest {
    private String password;
    private String newpassword;
}
