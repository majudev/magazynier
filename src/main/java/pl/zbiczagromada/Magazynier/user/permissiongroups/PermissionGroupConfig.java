package pl.zbiczagromada.Magazynier.user.permissiongroups;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import pl.zbiczagromada.Magazynier.user.UserRepository;

import java.util.List;

@Configuration
@EnableRedisHttpSession
public class PermissionGroupConfig {

    @Bean
    public CommandLineRunner initAdminGroup(PermissionGroupRepository repo){
        return args -> {
            List<PermissionGroup> permissionGroupList = repo.findAll();
            if(!repo.existsByGroupName("admin")){
                repo.saveAndFlush(new PermissionGroup("admin"));
            }
        };
    }
}
