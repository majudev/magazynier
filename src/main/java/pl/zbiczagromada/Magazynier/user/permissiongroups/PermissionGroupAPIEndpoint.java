package pl.zbiczagromada.Magazynier.user.permissiongroups;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.zbiczagromada.Magazynier.user.User;
import pl.zbiczagromada.Magazynier.user.UserCacheService;

import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@RequestMapping(path = "/permissiongroup")
public class PermissionGroupAPIEndpoint {
    private final UserCacheService userCache;
    private final UserPermissionService userPermissionService;
    private final PermissionGroupRepository permissionGroupRepository;

    @Autowired
    public PermissionGroupAPIEndpoint(UserCacheService userCache, UserPermissionService userPermissionService, PermissionGroupRepository permissionGroupRepository) {
        this.userCache = userCache;
        this.userPermissionService = userPermissionService;
        this.permissionGroupRepository = permissionGroupRepository;

        userPermissionService.registerPermission("permissiongroup.list", UserPermissionService.AccessLevel.DENY);
    }

    @GetMapping(path = "/list")
    public List<PermissionGroup> listAll(HttpSession session){
        User user = userCache.getUserFromSession(session);
        userPermissionService.checkUserAllowed("permissiongroup.list", user);

        return permissionGroupRepository.findAll();
    }
}
