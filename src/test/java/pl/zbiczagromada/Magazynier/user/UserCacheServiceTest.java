package pl.zbiczagromada.Magazynier.user;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@AutoConfigureTestDatabase
@TestPropertySource(
        locations = "classpath:dbtests.properties"
)
class UserCacheServiceTest {
    @Value("${magazynier.user.cache.cache_max_size}")
    private int cacheMaxSize;

    @Autowired
    private UserRepository repo;

    @Autowired
    private UserCacheService cacheService;

    @BeforeEach
    void cleanup(){
        repo.deleteAll();
    }

    @Test
    void getUserById() {
        List<User> userList = new ArrayList<User>();
        for(int i = 0; i < cacheMaxSize*2; ++i){
            userList.add(new User("username" + i, "displayname" + i, "username" + i + "@localhost", new HashPassword("passw0rd")));
        }


        ListIterator<User> iterator = userList.listIterator();
        while(iterator.hasNext()){
            User u = iterator.next();
            repo.save(u);
        }
        repo.flush();

        iterator = repo.findAll().listIterator();
        while(iterator.hasNext()){
            User repoUser = iterator.next();

            assertEquals(repoUser.getEmail(), repoUser.getUsername() + "@localhost");
            assertEquals("passw0rd", repoUser.getPassword().getPassword());

            Optional<User> cachedUserOptional = cacheService.getUserById(repoUser.getId());
            assertTrue(cachedUserOptional.isPresent());
            User cachedUser = cachedUserOptional.get();

            assertEquals(repoUser.getId(), cachedUser.getId());
            assertEquals(repoUser.getEmail(), cachedUser.getEmail());
            assertEquals(repoUser.getUsername(), cachedUser.getUsername());
            assertEquals(repoUser.getPassword().getPassword(), cachedUser.getPassword().getPassword());
            assertEquals(repoUser.getPassword().getPassword_algo(), cachedUser.getPassword().getPassword_algo());
            assertEquals(repoUser.getPassword().getPassword_salt(), cachedUser.getPassword().getPassword_salt());

            cachedUserOptional = cacheService.getUserByUsername(repoUser.getUsername());
            assertTrue(cachedUserOptional.isPresent());
            cachedUser = cachedUserOptional.get();

            assertEquals(repoUser.getId(), cachedUser.getId());
            assertEquals(repoUser.getEmail(), cachedUser.getEmail());
            assertEquals(repoUser.getUsername(), cachedUser.getUsername());
            assertEquals(repoUser.getPassword().getPassword(), cachedUser.getPassword().getPassword());
            assertEquals(repoUser.getPassword().getPassword_algo(), cachedUser.getPassword().getPassword_algo());
            assertEquals(repoUser.getPassword().getPassword_salt(), cachedUser.getPassword().getPassword_salt());
        }
    }
}