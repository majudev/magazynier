package pl.zbiczagromada.Magazynier.user.permissiongroups;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table(name = "pgroups")
public class PermissionGroup {
    @Id
    @SequenceGenerator(
            name = "permissiongroups_sequence",
            sequenceName = "permissiongroups_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "permissiongroups_sequence",
            strategy = GenerationType.SEQUENCE
    )
    @JsonIgnore
    @Column(name = "permissiongroup_id")
    @Getter
    private Long id;

    @Convert(converter = PermissionGroupConverter.class)
    @Getter(AccessLevel.PROTECTED)
    private Map<String, UserPermissionService.AccessLevel> permissionsList = new HashMap<String, UserPermissionService.AccessLevel>();

    @Getter
    private String groupName;

    private PermissionGroup() {}

    public PermissionGroup(String groupName) {
        this.groupName = groupName;
    }

    protected PermissionGroup(String groupName, Map<String, UserPermissionService.AccessLevel> permissionsList){
        this.groupName = groupName;
        this.permissionsList = permissionsList;
    }

    public void grant(String permission){
        permissionsList.put(permission, UserPermissionService.AccessLevel.ALLOW);
    }

    public void deny(String permission){
        permissionsList.put(permission, UserPermissionService.AccessLevel.DENY);
    }

    public void unset(String permission){
        permissionsList.remove(permission);
    }
}
