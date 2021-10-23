package pl.zbiczagromada.Magazynier.itemgroup;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemGroupRepository extends JpaRepository<ItemGroup, Long> {
    Optional<ItemGroup> findByName(String name);
}
