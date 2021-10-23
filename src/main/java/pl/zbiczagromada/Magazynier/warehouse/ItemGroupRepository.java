package pl.zbiczagromada.Magazynier.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zbiczagromada.Magazynier.user.User;

import java.util.Optional;

public interface ItemGroupRepository extends JpaRepository<ItemGroup, Long> {
    Optional<ItemGroup> findByName(String name);
}
