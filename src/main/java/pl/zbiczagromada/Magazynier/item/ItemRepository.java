package pl.zbiczagromada.Magazynier.item;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zbiczagromada.Magazynier.item.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
