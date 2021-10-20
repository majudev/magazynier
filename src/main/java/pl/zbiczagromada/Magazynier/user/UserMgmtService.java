package pl.zbiczagromada.Magazynier.user;

import lombok.Getter;
import org.apache.commons.collections4.MapIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.apache.commons.collections4.map.*;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
//@EnableScheduling
public class UserMgmtService {
    private final UserRepository repo;

    /*@Value("${magazynier.user.cache.object_lifetime}")
    private int objectLifetime;*/

    @Value("${magazynier.user.cache.object_refreshtime}")
    private int objectRefreshtime;

    @Value("${magazynier.user.cache.cache_max_size}")
    private int cacheMaxSize;

    //private Map<Long, CacheEntry> userMap = Map.of();
    //private Map<String, CacheEntry> userNameMap = Map.of();
    private MultiKeyMap cache = MultiKeyMap.multiKeyMap(new LRUMap(cacheMaxSize));

    @Autowired
    public UserMgmtService(UserRepository userRepository) {
        this.repo = userRepository;
    }

    public Optional<User> getUserById(Long id){
        if(cache.containsKey(id)/* || (cache.size() >= cacheMaxSize)*/){
            CacheEntry entry = (CacheEntry) cache.get(id);
            if(ChronoUnit.SECONDS.between(entry.getLastModification(), LocalDateTime.now()) > objectRefreshtime){
                Optional<User> userOptional = repo.findById(id);
                if(userOptional.isEmpty()){
                    cache.remove(id);
                    return userOptional;
                }
                entry = new CacheEntry(userOptional.get());
                cache.put(id, userOptional.get().getUsername(), entry);
            }
            return Optional.of(entry.getObject());
        }else{
            Optional<User> userOptional = repo.findById(id);
            if(userOptional.isEmpty()) return userOptional;
            cache.put(id, userOptional.get().getUsername(), new CacheEntry(userOptional.get()));
            return userOptional;
        }
        /*if(userMap.containsKey(id) || (userMap.size() >= cacheMaxSize)){
            CacheEntry entry = userMap.get(id);
            if(ChronoUnit.SECONDS.between(entry.getLastModification(), LocalDateTime.now()) > objectRefreshtime){
                Optional<User> userOptional = repo.findById(id);
                if(userOptional.isEmpty()){
                    userMap.remove(id);
                    return userOptional;
                }
                entry = new CacheEntry(userOptional.get());
                userMap.put(id, entry);
            }
            return Optional.of(entry.getObject());
        }else{
            Optional<User> userOptional = repo.findById(id);
            if(userOptional.isEmpty()) return userOptional;
            userMap.put(id, new CacheEntry(userOptional.get()));
            return userOptional;
        }*/
    }

    /*@Scheduled(
            fixedDelayString = "${magazynier.user.cache.cache_cleanup_interval}",
            timeUnit = TimeUnit.SECONDS
    )
    private void cacheCleanup(){
        MapIterator iterator = cache.mapIterator();
        while(iterator.hasNext()){
            CacheEntry e = (CacheEntry) iterator.next();
            if(ChronoUnit.SECONDS.between(e.getLastAccess(), LocalDateTime.now()) > objectLifetime){
                iterator.remove();
            }
        }
    }*/

    private class CacheEntry {
        private User object;
        @Getter
        private LocalDateTime lastModification;
        /*@Getter
        private LocalDateTime lastAccess;*/

        public CacheEntry(User object) {
            this.object = object;
            this.lastModification = LocalDateTime.now();
            //this.lastAccess = this.lastModification;
        }

        public User getObject() {
            //this.lastAccess = LocalDateTime.now();
            return object;
        }

        public void setObject(User object) {
            this.lastModification = LocalDateTime.now();
            //this.lastAccess = this.lastModification;
            this.object = object;
        }
    }
}
