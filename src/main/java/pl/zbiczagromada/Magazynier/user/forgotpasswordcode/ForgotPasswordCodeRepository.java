package pl.zbiczagromada.Magazynier.user.forgotpasswordcode;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ForgotPasswordCodeRepository extends JpaRepository<ForgotPasswordCode, Long> {
    List<ForgotPasswordCode> findAllByActive(boolean active);
    Optional<ForgotPasswordCode> findByCode(String code);
}
