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
            if(repo.findAll().isEmpty()){
                User admin = new User(
                        "admin",
                        null,
                        "admin@localhost",
                        new HashPassword("zmień-mnie"),
                        "admin"
                );
                admin.getActivationCode().revoke();
                repo.saveAndFlush(admin);
            }
        };
    }
}
