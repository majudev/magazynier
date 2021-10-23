package pl.zbiczagromada.Magazynier.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zbiczagromada.Magazynier.user.User;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
