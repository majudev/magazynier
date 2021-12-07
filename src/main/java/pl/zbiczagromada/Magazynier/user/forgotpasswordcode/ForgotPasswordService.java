package pl.zbiczagromada.Magazynier.user.forgotpasswordcode;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

@Service
public class ForgotPasswordService {
    @Autowired
    private ForgotPasswordCodeRepository forgotPasswordCodeRepository;

    @Scheduled(fixedDelay = 3600000)
    private void doCleanup(){
        forgotPasswordCodeRepository.findAllByActive(true).forEach((code) -> {
            if(code.getExpires().isBefore(LocalDateTime.now())){
                code.setActive(false);
                forgotPasswordCodeRepository.save(code);
            }
        });
        forgotPasswordCodeRepository.flush();
    }
}
