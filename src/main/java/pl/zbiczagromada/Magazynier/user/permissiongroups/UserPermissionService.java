package pl.zbiczagromada.Magazynier.user.permissiongroups;

import lombok.Getter;
import org.apache.commons.collections4.map.LRUMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.zbiczagromada.Magazynier.user.User;
import pl.zbiczagromada.Magazynier.user.permissiongroups.exceptions.InsufficientPermissionsException;
import pl.zbiczagromada.Magazynier.user.permissiongroups.exceptions.PermissionAlreadyRegisteredException;
import pl.zbiczagromada.Magazynier.user.permissiongroups.exceptions.PermissionGroupNotFoundException;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class UserPermissionService {
    @Value("${magazynier.user.permissioncache.object_refreshtime}")
    private int objectRefreshtime;

    @Getter
    @Value("${magazynier.user.permissioncache.cache_max_size}")
    private int cacheMaxSize;

    @Autowired
    private PermissionGroupRepository permissionGroupRepository;

    private Map<String, AccessLevel> globalPermissionsList = new HashMap<String, AccessLevel>();
    private Map<String, CacheEntry> permissionGroupCache = null;

    private ReentrantLock cacheMutex = new ReentrantLock();
    private ReentrantLock globalPermissionsListMutex = new ReentrantLock();

    public void registerPermission(String permission, AccessLevel defaultAccessLevel) throws PermissionAlreadyRegisteredException {
        initCache();
        if(globalPermissionsList.containsKey(permission)) throw new PermissionAlreadyRegisteredException(permission);
        globalPermissionsList.put(permission, defaultAccessLevel);
        cacheMutex.lock();
        globalPermissionsListMutex.lock();
        permissionGroupCache.clear();
        globalPermissionsListMutex.unlock();
        cacheMutex.unlock();
    }

    public boolean isUserAllowed(String permission, User user){
        initCache();
        Map<String, AccessLevel> map = this.globalPermissionsList;
        try{
            map = mapFromPermissionGroup(user.getPermissionGroup());
        }catch (PermissionGroupNotFoundException e){

        }
        return map.get(permission) == AccessLevel.ALLOW;
    }

    public void checkUserAllowed(String permission, User user) throws InsufficientPermissionsException {
        initCache();
        Map<String, AccessLevel> map = this.globalPermissionsList;
        try{
            map = mapFromPermissionGroup(user.getPermissionGroup());
        }catch (PermissionGroupNotFoundException e){

        }
        if(map.get(permission) != AccessLevel.ALLOW) throw new InsufficientPermissionsException(user.getUsername());
    }

    private Map<String, AccessLevel> getGroupFromCache(String groupName) throws PermissionGroupNotFoundException {
        initCache();
        if(permissionGroupCache.containsKey(groupName)){
            CacheEntry cacheEntry = permissionGroupCache.get(groupName);
            if(ChronoUnit.SECONDS.between(cacheEntry.getLastModification(), LocalDateTime.now()) < objectRefreshtime){
                return cacheEntry.getMap();
            }
        }

        cacheMutex.lock();
        Map<String, AccessLevel> localMap = buildFullMap(mapFromPermissionGroup(groupName));
        CacheEntry cacheEntry = new CacheEntry(localMap);
        permissionGroupCache.put(groupName, cacheEntry);
        cacheMutex.unlock();
        return cacheEntry.getMap();
    }

    private Map<String, AccessLevel> buildFullMap(Map<String, AccessLevel> input){
        Map<String, AccessLevel> local = new HashMap<String, AccessLevel>();
        globalPermissionsListMutex.lock();
        globalPermissionsList.forEach(
                (key, value) -> {
                    AccessLevel accessLevel = input.get(key);
                    if(input.get(key) == AccessLevel.PASSTHROUGH) accessLevel = value;
                    local.put(key, accessLevel);
                }
        );
        globalPermissionsListMutex.unlock();
        return local;
    }

    private Map<String, AccessLevel> mapFromPermissionGroup(String groupName) throws PermissionGroupNotFoundException{
        if(groupName == "admin") return this.getAdmin();

        PermissionGroup permissionGroup = permissionGroupRepository.findByGroupName(groupName).orElseThrow(() -> new PermissionGroupNotFoundException(groupName));
        return permissionGroup.getPermissionsList();
    }

    private void initCache(){
        if(permissionGroupCache == null) permissionGroupCache = new LRUMap<String, CacheEntry>(cacheMaxSize);
    }

    protected Map<String, AccessLevel> getAdmin(){
        Map<String, AccessLevel> local = new HashMap<String, AccessLevel>();
        globalPermissionsList.forEach(
                (key, value) -> {
                    local.put(key, AccessLevel.ALLOW);
                }
        );
        return local;
    }

    public enum AccessLevel {
        ALLOW, DENY, PASSTHROUGH
    }

    @Getter
    private class CacheEntry {
        private Map<String, AccessLevel> map;
        private LocalDateTime lastModification;

        public CacheEntry(Map<String, AccessLevel> map) {
            this.map = map;
            this.lastModification = LocalDateTime.now();
        }

        public void setObject(User object) {
            this.lastModification = LocalDateTime.now();
            this.map = map;
        }
    }
}
