package pl.zbiczagromada.Magazynier.user.apirequests;

import lombok.Getter;

@Getter
public class LoginRequest {
    private String username;
    private String password;
    private boolean rememberMe;
}
