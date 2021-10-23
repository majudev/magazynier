package pl.zbiczagromada.Magazynier.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.zbiczagromada.Magazynier.user.apirequests.ChangePasswordRequest;
import pl.zbiczagromada.Magazynier.user.apirequests.LoginRequest;
import pl.zbiczagromada.Magazynier.user.apirequests.RegisterRequest;
import pl.zbiczagromada.Magazynier.user.exceptions.*;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.Optional;

@RestController
@RequestMapping(path = "/user")
public class UserAPIEndpoint {
    @Value("${magazynier.user.timeout}")
    private int standardSessionTimeout;
    @Value("${magazynier.user.timeout_extended}")
    private int extendedSessionTimeout;

    private final UserRepository repo;
    private final UserCacheService userCache;

    @Autowired
    public UserAPIEndpoint(UserRepository repo, UserCacheService userCacheService){
        this.repo = repo;
        this.userCache = userCacheService;
    }

    @GetMapping
    public User getInfo(HttpSession session) {
        User user = userCache.getUserFromSession(session);
        return user;
    }

    @PostMapping(
            path = "/login",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Transactional(value = Transactional.TxType.NEVER)
    public User login(@RequestBody LoginRequest request, HttpSession session) {
        final Long userId = (Long) session.getAttribute("id");
        if(userId != null) throw new UserAlreadyLoggedInException(request.getUsername());
        if(request.getUsername() == null || request.getPassword() == null) throw new InvalidParametersException();

        Optional<User> userOptional = repo.findByUsername(request.getUsername());
        if(!userOptional.isPresent()) throw new UserNotFoundException(request.getUsername());
        User user = userOptional.get();

        if(!user.getPassword().validate(request.getPassword())) throw new InvalidCredentialsException(request.getUsername());

        session.setAttribute("id", user.getId());
        session.setAttribute("username", user.getUsername());
        if(request.isRememberMe()) session.setMaxInactiveInterval(extendedSessionTimeout);
        else session.setMaxInactiveInterval(standardSessionTimeout);

        return user;
    }

    @PostMapping(
            path = "/register",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Transactional
    public void register(@RequestBody RegisterRequest request, HttpSession session) {
        final Long userId = (Long) session.getAttribute("id");
        if(userId != null) throw new UserAlreadyLoggedInException(request.getUsername());
        if(request.getUsername() == null || request.getPassword() == null || request.getEmail() == null) throw new InvalidParametersException();

        // perform username, password & email validation

        if(repo.existsByUsername(request.getUsername())) throw new UsernameAlreadyTakenException(request.getUsername());
        if(repo.existsByEmail(request.getEmail())) throw new EmailAlreadyTakenException(request.getEmail());

        User user = new User(request.getUsername(), request.getEmail(), new HashPassword(request.getPassword()));
        repo.saveAndFlush(user);
    }

    @PutMapping(
            path = "/changepassword",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Transactional
    public void changePassword(@RequestBody ChangePasswordRequest request, HttpSession session) {
        User user = userCache.getUserFromSession(session);
        if(request.getPassword() == null || request.getNewpassword() == null) throw new InvalidParametersException();

        // perform new password validation

        if(!user.getPassword().validate(request.getPassword())) throw new InvalidCredentialsException(user.getUsername());

        user.setPassword(new HashPassword(request.getNewpassword()));

        repo.saveAndFlush(user);

        session.invalidate();
    }

    @GetMapping(
            path = "/logout"
    )
    public void logout(HttpSession session) {
        User user = userCache.getUserFromSession(session);

        session.invalidate();
    }
}
