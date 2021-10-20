package pl.zbiczagromada.Magazynier.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.zbiczagromada.Magazynier.user.User;
import pl.zbiczagromada.Magazynier.user.UserAPIEndpoint;
import pl.zbiczagromada.Magazynier.user.UserCacheService;
import pl.zbiczagromada.Magazynier.user.UserRepository;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@RestController
@RequestMapping(path = "/server")
public class ServerAPIEndpoint {
    private final UserRepository repo;
    private final UserCacheService userCache;

    @Autowired
    public ServerAPIEndpoint(UserRepository repo, UserCacheService userCache) {
        this.repo = repo;
        this.userCache = userCache;
    }

    @GetMapping("/cache")
    public ServerCacheStatus getStatus(HttpSession session){
        /*Long userId = (Long) session.getAttribute("id");
        if(userId == null) throw new UserAPIEndpoint.UserNotLoggedInException();

        Optional<User> user = userCache.getUserById(userId);
        if(user.isEmpty()) throw new UserRepository.UserNotFoundException(userId);*/
        //if(user.get().getPermissions() < ADMIN) throw new InsufficientPermissionsException();

        return new ServerCacheStatus(userCache);
    }
}
