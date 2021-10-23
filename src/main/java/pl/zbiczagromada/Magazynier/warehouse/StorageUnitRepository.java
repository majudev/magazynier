package pl.zbiczagromada.Magazynier.warehouse;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.zbiczagromada.Magazynier.user.User;

import java.util.Optional;

public interface StorageUnitRepository extends JpaRepository<StorageUnit, Long> {
    Optional<StorageUnit> findByName(String name);
}
