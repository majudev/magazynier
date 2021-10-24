package pl.zbiczagromada.Magazynier.user.apirequests;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class EditUserRequest {
    private String displayname;
    private String email;
}
