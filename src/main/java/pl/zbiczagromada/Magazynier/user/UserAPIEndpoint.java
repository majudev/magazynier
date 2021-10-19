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
    private final UserMgmtService userMgmtService;

    @Autowired
    public UserAPIEndpoint(UserRepository repo, UserMgmtService userMgmtService){
        this.repo = repo;
        this.userMgmtService = userMgmtService;
    }

    @GetMapping
    public User getInfo(HttpSession session) throws UserNotLoggedInException, UserRepository.UserNotFoundException {
        final Long userId = (Long) session.getAttribute("id");
        if(userId == null) throw new UserNotLoggedInException();
        Optional<User> user = repo.findById(userId);
        if(!user.isPresent()) throw new UserRepository.UserNotFoundException(userId);
        return user.get();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/login",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    public User login(@RequestBody LoginRequest request, HttpSession session) throws UserAlreadyLoggedInException, UserRepository.UserNotFoundException, InvalidParametersException {
        final Long userId = (Long) session.getAttribute("id");
        if(userId != null) throw new UserAlreadyLoggedInException(request.getUsername());
        if(request.getUsername() == null || request.getPassword() == null) throw new InvalidParametersException();

        Optional<User> userOptional = repo.findByUsername(request.getUsername());
        if(!userOptional.isPresent()) throw new UserRepository.UserNotFoundException(request.getUsername());
        User user = userOptional.get();

        if(!user.getPassword().validate(request.getPassword())) throw new InvalidCredentialsException(request.getUsername());

        session.setAttribute("id", user.getId());
        session.setAttribute("username", user.getUsername());
        //session.setAttribute("userobject", user);
        if(request.isRememberMe()) session.setMaxInactiveInterval(extendedSessionTimeout);
        else session.setMaxInactiveInterval(standardSessionTimeout);

        return user;
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/register",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Transactional
    public void register(@RequestBody RegisterRequest request, HttpSession session) throws UserAlreadyLoggedInException, InvalidParametersException {
        final Long userId = (Long) session.getAttribute("id");
        if(userId != null) throw new UserAlreadyLoggedInException(request.getUsername());
        if(request.getUsername() == null || request.getPassword() == null || request.getEmail() == null) throw new InvalidParametersException();

        // perform username, password & email validation

        if(repo.existsByUsername(request.getUsername())) throw new UsernameAlreadyTakenException(request.getUsername());
        if(repo.existsByEmail(request.getEmail())) throw new EmailAlreadyTakenException(request.getEmail());

        User user = new User(request.getUsername(), request.getEmail(), new HashPassword(request.getPassword()));
        repo.saveAndFlush(user);
    }

    @RequestMapping(
            method = RequestMethod.PUT,
            path = "/changepassword",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Transactional
    public void changePassword(@RequestBody ChangePasswordRequest request, HttpSession session) throws UserAlreadyLoggedInException, InvalidParametersException {
        final Long userId = (Long) session.getAttribute("id");
        final String username = (String) session.getAttribute("username");
        if(userId == null) throw new UserNotLoggedInException();
        if(request.getPassword() == null || request.getNewpassword() == null) throw new InvalidParametersException();

        // perform new password validation
        Optional<User> userOptional = repo.findById(userId);
        if(!userOptional.isPresent()) throw new UserRepository.UserNotFoundException(username);
        User user = userOptional.get();

        if(!user.getPassword().validate(request.getPassword())) throw new InvalidCredentialsException(username);

        user.setPassword(new HashPassword(request.getNewpassword()));

        repo.saveAndFlush(user);

        session.invalidate();
    }

    @GetMapping(path = "/logout")
    public void logout(HttpSession session) throws UserNotLoggedInException, UserRepository.UserNotFoundException {
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
