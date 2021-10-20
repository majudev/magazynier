package pl.zbiczagromada.Magazynier.server;

import lombok.Getter;
import pl.zbiczagromada.Magazynier.user.User;
import pl.zbiczagromada.Magazynier.user.UserCacheService;

@Getter
public class ServerCacheStatus {
    private int userCacheMaxSize;
    private int userCacheUsage;

    public ServerCacheStatus(UserCacheService userCacheService){
        this.userCacheMaxSize = userCacheService.getCacheMaxSize();
        this.userCacheUsage = userCacheService.getCacheUsage();
    }
}
