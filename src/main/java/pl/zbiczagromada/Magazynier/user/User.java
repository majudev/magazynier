package pl.zbiczagromada.Magazynier.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import pl.zbiczagromada.Magazynier.user.permissiongroups.PermissionGroup;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "users")
@Getter @Setter
public class User {
    @Id
    @SequenceGenerator(
            name = "user_sequence",
            sequenceName = "user_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "user_sequence",
            strategy = GenerationType.SEQUENCE
    )
    @JsonIgnore
    @Column(name = "user_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, unique = true)
    @Setter(AccessLevel.NONE)
    private String username;

    private String displayname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @Embedded
    @JsonIgnore
    private HashPassword password;

    @Column
    private String permissionGroup;

    public User(String username, String displayname, String email, HashPassword password) {
        this.username = username;
        this.displayname = displayname;
        this.email = email;
        this.password = password;
    }

    protected User(){}
}
