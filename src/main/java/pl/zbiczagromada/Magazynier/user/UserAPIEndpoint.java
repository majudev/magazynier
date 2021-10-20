package pl.zbiczagromada.Magazynier.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

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
        final Long userId = (Long) session.getAttribute("id");
        if(userId == null) throw new UserNotLoggedInException();
        //Optional<User> user = repo.findById(userId);
        //if(!user.isPresent()) throw new UserRepository.UserNotFoundException(userId);
        //return user.get();
        Optional<User> user = userCache.getUserById(userId);
        if(!user.isPresent()) throw new UserRepository.UserNotFoundException(userId);
        return user.get();
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
        if(!userOptional.isPresent()) throw new UserRepository.UserNotFoundException(request.getUsername());
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
        final Long userId = (Long) session.getAttribute("id");
        final String username = (String) session.getAttribute("username");
        if(userId == null) throw new UserNotLoggedInException();
        if(request.getPassword() == null || request.getNewpassword() == null) throw new InvalidParametersException();

        // perform new password validation
        Optional<User> userOptional = repo.findById(userId);
        if(userOptional.isEmpty()) throw new UserRepository.UserNotFoundException(username);
        User user = userOptional.get();

        if(!user.getPassword().validate(request.getPassword())) throw new InvalidCredentialsException(username);

        user.setPassword(new HashPassword(request.getNewpassword()));

        repo.saveAndFlush(user);

        session.invalidate();
    }

    @GetMapping(
            path = "/logout"
    )
    public void logout(HttpSession session) {
        Long userId = (Long) session.getAttribute("id");
        if(userId == null) throw new UserNotLoggedInException();

        session.invalidate();
    }



    public static class InvalidParametersException extends ResponseStatusException {
        public InvalidParametersException() {
            super(HttpStatus.UNAUTHORIZED, "invalid parameters have been supplied for this request");
        }
    }

    public static class UserNotLoggedInException extends ResponseStatusException {
        public UserNotLoggedInException() {
            super(HttpStatus.UNAUTHORIZED, "user not logged in");
        }
    }

    public static class UserAlreadyLoggedInException extends ResponseStatusException {
        private final static String base1 = "user";
        private final static String base2 = "already logged in";
        public UserAlreadyLoggedInException() {
            super(HttpStatus.UNAUTHORIZED, base1 + " " + base2);
        }

        public UserAlreadyLoggedInException(String username) {
            super(HttpStatus.UNAUTHORIZED, base1 + " '" + username + "' " + base2);
        }

        public UserAlreadyLoggedInException(Long id) {
            super(HttpStatus.UNAUTHORIZED, base1 + " with id '" + id + "' " + base2);
        }
    }

    public static class UsernameAlreadyTakenException extends ResponseStatusException {
        private static final String base1 = "username";
        private static final String base2 = "already taken";
        public UsernameAlreadyTakenException() {
            super(HttpStatus.UNAUTHORIZED, base1 + " " + base2);
        }

        public UsernameAlreadyTakenException(String username) {
            super(HttpStatus.UNAUTHORIZED, base1 + " '" + username + "' " + base2);
        }
    }

    public static class EmailAlreadyTakenException extends ResponseStatusException {
        private static final String base1 = "email";
        private static final String base2 = "already taken";
        public EmailAlreadyTakenException() {
            super(HttpStatus.UNAUTHORIZED, base1 + " " + base2);
        }

        public EmailAlreadyTakenException(String email) {
            super(HttpStatus.UNAUTHORIZED, base1 + " '" + email + "' " + base2);
        }
    }

    public static class InvalidCredentialsException extends ResponseStatusException {
        private final static String base = "invalid credentials provided for user";
        public InvalidCredentialsException() {
            super(HttpStatus.UNAUTHORIZED, base);
        }

        public InvalidCredentialsException(String username) {
            super(HttpStatus.UNAUTHORIZED, base + " '" + username + "'");
        }

        public InvalidCredentialsException(Long id) {
            super(HttpStatus.UNAUTHORIZED, base + " with id '" + id + "'");
        }
    }
}
