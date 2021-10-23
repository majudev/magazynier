package pl.zbiczagromada.Magazynier.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zbiczagromada.Magazynier.user.User;

import java.util.Optional;

public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {
    Optional<Warehouse> findByName(String name);
}
