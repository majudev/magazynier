package pl.zbiczagromada.Magazynier.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.server.ResponseStatusException;

import javax.print.attribute.standard.Media;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;
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
        Long userId = (Long) session.getAttribute("id");
        if(userId == null) throw new UserNotLoggedInException();
        Optional<User> user = repo.findById(userId);
        if(!user.isPresent()) throw new UserRepository.UserNotFoundException(userId);
        return user.get();
    }

    @RequestMapping(
            method = RequestMethod.POST,
            path = "/login",
            consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE}
    )
    public User login(@RequestBody LoginRequest request, HttpSession session) throws UserAlreadyLoggedInException, UserRepository.UserNotFoundException, InvalidParametersException {
        Long userId = (Long) session.getAttribute("id");
        if(userId != null) throw new UserAlreadyLoggedInException(request.getUsername());
        if(request.getUsername() == null || request.getPassword() == null) throw new InvalidParametersException();

        Optional<User> userOptional = repo.findByUsername(request.getUsername());
        if(!userOptional.isPresent()) throw new UserRepository.UserNotFoundException(request.getUsername());
        User user = userOptional.get();

        if(!user.getPassword().validate(request.getPassword())) throw new BadCredentialsException(request.getUsername());

        session.setAttribute("id", user.getId());
        session.setAttribute("username", user.getUsername());
        session.setAttribute("userobject", user);
        if(request.isRememberMe()) session.setMaxInactiveInterval(extendedSessionTimeout);
        else session.setMaxInactiveInterval(standardSessionTimeout);

        return user;
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
        public UserAlreadyLoggedInException() {
            super(HttpStatus.UNAUTHORIZED, "user already logged in");
        }

        public UserAlreadyLoggedInException(String username) {
            super(HttpStatus.UNAUTHORIZED, "user '" + username + "' already logged in");
        }

        public UserAlreadyLoggedInException(Long id) {
            super(HttpStatus.UNAUTHORIZED, "user with id '" + id + "' already logged in");
        }
    }

    public static class BadCredentialsException extends ResponseStatusException {
        public BadCredentialsException() {
            super(HttpStatus.UNAUTHORIZED, "bad credentials provided for user");
        }

        public BadCredentialsException(String username) {
            super(HttpStatus.UNAUTHORIZED, "bad credentials provided for user '" + username + "'");
        }

        public BadCredentialsException(Long id) {
            super(HttpStatus.UNAUTHORIZED, "bad credentials provided for user with id '" + id + "'");
        }
    }
}
