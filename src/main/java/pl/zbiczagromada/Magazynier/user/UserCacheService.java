package pl.zbiczagromada.Magazynier.user;

import lombok.Getter;
import org.apache.commons.collections4.map.LRUMap;
import org.apache.commons.collections4.map.MultiKeyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pl.zbiczagromada.Magazynier.user.exceptions.UserNotFoundException;
import pl.zbiczagromada.Magazynier.user.exceptions.UserNotLoggedInException;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class UserCacheService {
    private final UserRepository repo;

    @Value("${magazynier.user.cache.object_refreshtime}")
    private int objectRefreshtime;

    @Getter
    @Value("${magazynier.user.cache.cache_max_size}")
    private int cacheMaxSize;

    private MultiKeyMap userMap = null;

    @Autowired
    public UserCacheService(UserRepository userRepository) {
        this.repo = userRepository;
    }

    public User getUserFromSession(HttpSession session) throws UserNotLoggedInException, UserNotFoundException {
        final Long userId = (Long) session.getAttribute("id");
        if(userId == null) throw new UserNotLoggedInException();

        Optional<User> user = this.getUserById(userId);
        if(!user.isPresent()) throw new UserNotFoundException(userId);

        return user.get();
    }

    public Optional<User> getUserById(Long id){
        try {
            return getUserByObject(id);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Optional<User> getUserByUsername(String username){
        try {
            return getUserByObject(username);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    private ReentrantLock cacheMutex = new ReentrantLock();
    private Optional<User> getUserByObject(Object o) throws RuntimeException {
        init();
        if(userMap.containsKey(o)){
            CacheEntry entry = (CacheEntry) userMap.get(o);
            if(ChronoUnit.SECONDS.between(entry.getLastModification(), LocalDateTime.now()) > objectRefreshtime){
                Optional<User> userOptional;
                if(o instanceof Long) userOptional = repo.findById((Long) o);
                else if(o instanceof String) userOptional = repo.findByUsername((String) o);
                else throw new RuntimeException();

                if(userOptional.isEmpty()){
                    cacheMutex.lock();
                    userMap.remove(o);
                    cacheMutex.unlock();
                    return userOptional;
                }
                entry = new CacheEntry(userOptional.get());
                cacheMutex.lock();
                userMap.put(o, userOptional.get().getUsername(), entry);
                cacheMutex.unlock();
            }
            return Optional.of(entry.getObject());
        }else{
            Optional<User> userOptional;
            if(o instanceof Long) userOptional = repo.findById((Long) o);
            else if(o instanceof String) userOptional = repo.findByUsername((String) o);
            else throw new RuntimeException();

            if(userOptional.isEmpty()) return userOptional;
            cacheMutex.lock();
            userMap.put(o, userOptional.get().getUsername(), new CacheEntry(userOptional.get()));
            cacheMutex.unlock();
            return userOptional;
        }
    }

    public int getCacheUsage(){
        init();
        return this.userMap.size();
    }

    private void init(){
        if(userMap == null) userMap = MultiKeyMap.multiKeyMap(new LRUMap(cacheMaxSize));
    }

    private class CacheEntry {
        private User object;
        @Getter
        private LocalDateTime lastModification;

        public CacheEntry(User object) {
            this.object = object;
            this.lastModification = LocalDateTime.now();
        }

        public User getObject() {
            return object;
        }

        public void setObject(User object) {
            this.lastModification = LocalDateTime.now();
            this.object = object;
        }
    }
}
