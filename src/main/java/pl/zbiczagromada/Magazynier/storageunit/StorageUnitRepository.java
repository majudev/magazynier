package pl.zbiczagromada.Magazynier.storageunit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StorageUnitRepository extends JpaRepository<StorageUnit, Long> {
    Optional<StorageUnit> findByName(String name);
}
