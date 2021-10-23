package pl.zbiczagromada.Magazynier.user.permissiongroups;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PermissionGroupRepository extends JpaRepository<PermissionGroup, Long> {
    Optional<PermissionGroup> findByGroupName(String groupName);
    boolean existsByGroupName(String groupName);
}
