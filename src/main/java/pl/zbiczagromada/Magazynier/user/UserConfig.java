package pl.zbiczagromada.Magazynier.user;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession
public class UserConfig {

    @Bean
    public CommandLineRunner initUsers(UserRepository repo){
        return args -> {
            /*repo.save(new User(
                    "nobody",
                    "nobody@all",
                    new HashPassword("helloworld")
            ));*/
        };
    }
}
