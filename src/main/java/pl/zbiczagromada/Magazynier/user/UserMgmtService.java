package pl.zbiczagromada.Magazynier.user;

import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Service
public class UserMgmtService {
    private final UserRepository repo;

    @Autowired
    public UserMgmtService(UserRepository userRepository) {
        this.repo = userRepository;
    }

}
