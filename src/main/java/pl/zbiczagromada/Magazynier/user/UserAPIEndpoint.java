package pl.zbiczagromada.Magazynier.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import pl.zbiczagromada.Magazynier.MailerService;
import pl.zbiczagromada.Magazynier.exceptions.InvalidRequestException;
import pl.zbiczagromada.Magazynier.user.apirequests.ChangePasswordRequest;
import pl.zbiczagromada.Magazynier.user.apirequests.EditUserRequest;
import pl.zbiczagromada.Magazynier.user.apirequests.LoginRequest;
import pl.zbiczagromada.Magazynier.user.apirequests.RegisterRequest;
import pl.zbiczagromada.Magazynier.user.exceptions.*;
import pl.zbiczagromada.Magazynier.user.forgotpasswordcode.ForgotPasswordCode;
import pl.zbiczagromada.Magazynier.user.forgotpasswordcode.ForgotPasswordCodeRepository;
import pl.zbiczagromada.Magazynier.user.permissiongroups.PermissionGroupRepository;
import pl.zbiczagromada.Magazynier.user.permissiongroups.UserPermissionService;
import pl.zbiczagromada.Magazynier.user.permissiongroups.exceptions.PermissionGroupNotFoundException;

import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;
import java.util.List;

@RestController
@RequestMapping(path = "/user")
public class UserAPIEndpoint {
    @Value("${magazynier.user.timeout}")
    private int standardSessionTimeout;
    @Value("${magazynier.user.timeout_extended}")
    private int extendedSessionTimeout;

    private final UserRepository repo;
    private final UserCacheService userCache;
    private final UserPermissionService userPermissionService;
    private final PermissionGroupRepository permissionGroupRepository;
    private final MailerService mailerService;
    private final ForgotPasswordCodeRepository forgotPasswordCodeRepository;

    @Autowired
    public UserAPIEndpoint(UserRepository repo, UserCacheService userCacheService, UserPermissionService userPermissionService, PermissionGroupRepository permissionGroupRepository, MailerService mailerService, ForgotPasswordCodeRepository forgotPasswordCodeRepository){
        this.repo = repo;
        this.userCache = userCacheService;
        this.userPermissionService = userPermissionService;
        this.permissionGroupRepository = permissionGroupRepository;
        this.mailerService = mailerService;
        this.forgotPasswordCodeRepository = forgotPasswordCodeRepository;

        userPermissionService.registerPermission("user.self.changepassword", UserPermissionService.AccessLevel.ALLOW);
        userPermissionService.registerPermission("user.self.resetpassword", UserPermissionService.AccessLevel.ALLOW);
        userPermissionService.registerPermission("user.self.edit", UserPermissionService.AccessLevel.ALLOW);
        userPermissionService.registerPermission("user.self.permissiongroup.assign", UserPermissionService.AccessLevel.DENY);
        userPermissionService.registerPermission("user.register", UserPermissionService.AccessLevel.DENY);
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
        if(userId != null) return userCache.getUserFromSession(session);

        if(request.getUsername() == null) throw new InvalidRequestException(List.of("username"));
        if(request.getPassword() == null) throw new InvalidRequestException(List.of("password"));

        User user = repo.findByUsername(request.getUsername()).orElseThrow(() -> new UserNotFoundException(request.getUsername()));

        if(!user.getPassword().validate(request.getPassword())) throw new InvalidCredentialsException(request.getUsername());
        if(!user.getActive()) throw new UserNotActiveException(request.getUsername());

        session.setAttribute("id", user.getId());
        session.setAttribute("username", user.getUsername());
        if(request.isRememberMe()) session.setMaxInactiveInterval(extendedSessionTimeout);
        else session.setMaxInactiveInterval(standardSessionTimeout);

        return user;
    }

    @PutMapping(
            path = "/self/edit",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Transactional
    public User edit(@RequestBody EditUserRequest request, HttpSession session){
        User user = userCache.getUserFromSession(session);
        userPermissionService.checkUserAllowed("user.self.edit", user);

        if(request.getDisplayname() != null && !request.getDisplayname().isEmpty()) user.setDisplayname(request.getDisplayname());
        if(request.getEmail() != null /*&& validate email*/) user.setEmail(request.getEmail());
        if(request.getPermissionGroup() != null && !request.getPermissionGroup().isEmpty()){
            userPermissionService.checkUserAllowed("user.self.permissiongroup.assign", user);
            if(permissionGroupRepository.existsByGroupName(request.getPermissionGroup())) throw new PermissionGroupNotFoundException(request.getPermissionGroup());
            user.setPermissionGroup(request.getPermissionGroup());
            mailerService.sendPermissionGroupChangedEmail(user);
        }

        return repo.saveAndFlush(user);
    }

    @PostMapping(
            path = "/register",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Transactional
    public void register(@RequestBody RegisterRequest request, HttpSession session) {
        User user = userCache.getUserFromSession(session);
        //userPermissionService.checkUserAllowed("user.register", user);

        /*final Long userId = (Long) session.getAttribute("id");
        if(userId != null) throw new UserAlreadyLoggedInException(request.getUsername());*/

        if(request.getUsername() == null) throw new InvalidRequestException(List.of("username"));
        if(request.getPassword() == null) throw new InvalidRequestException(List.of("password"));
        if(request.getEmail() == null) throw new InvalidRequestException(List.of("email"));

        // perform username, password & email validation

        if(repo.existsByUsername(request.getUsername())) throw new UsernameAlreadyTakenException(request.getUsername());
        if(repo.existsByEmail(request.getEmail())) throw new EmailAlreadyTakenException(request.getEmail());

        User newUser = new User(request.getUsername(), request.getDisplayname(), request.getEmail(), new HashPassword(request.getPassword()));
        repo.saveAndFlush(newUser);

        mailerService.sendAccountVerifyEmail(newUser, request.getPassword());
    }

    @PutMapping(
            path = "/self/changepassword",
            consumes = {MediaType.APPLICATION_JSON_VALUE}
    )
    @Transactional
    public void changePassword(@RequestBody ChangePasswordRequest request, HttpSession session) {
        User user = userCache.getUserFromSession(session);
        userPermissionService.checkUserAllowed("user.self.changepassword", user);

        if(request.getPassword() == null) throw new InvalidRequestException(List.of("password"));
        if(request.getNewpassword() == null) throw new InvalidRequestException(List.of("newpassword"));

        // perform new password validation

        if(!user.getPassword().validate(request.getPassword())) throw new InvalidCredentialsException(user.getUsername());

        user.setPassword(new HashPassword(request.getNewpassword()));

        repo.saveAndFlush(user);
        mailerService.sendPasswordChangedEmail(user);

        session.invalidate();
    }

    @PostMapping(
            path = "/forgotpassword/{username}"
    )
    @Transactional
    public void forgotPasswordRequest(@PathVariable("username") String username, HttpSession session) {
        User user = userCache.getUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        userPermissionService.checkUserAllowed("user.self.resetpassword", user);
        if(!user.getActive()) throw new UserNotActiveException(username);

        ForgotPasswordCode code = new ForgotPasswordCode(user);
        forgotPasswordCodeRepository.saveAndFlush(code);

        //send recovery mail
        mailerService.sendForgotPasswordEmail(user, code);
    }

    @PostMapping(
            path = "/forgotpassword/{code}/reset/{newpassword}"
    )
    @Transactional
    public void forgotPasswordSubmission(@PathVariable("code") String code, @PathVariable("newpassword") String newpassword, HttpSession session) {
        ForgotPasswordCode forgotPasswordCode = forgotPasswordCodeRepository.findByCode(code).orElseThrow(() -> new InvalidCodeException());
        if(!forgotPasswordCode.isActive()) throw new CodeExpiredException();

        User user = forgotPasswordCode.getUser();
        userPermissionService.checkUserAllowed("user.self.resetpassword", user);

        // perform new password validation

        forgotPasswordCode.revoke();
        user.setPassword(new HashPassword(newpassword));

        repo.saveAndFlush(user);
        mailerService.sendPasswordChangedEmail(user);

        session.invalidate();
    }

    @PostMapping(
            path = "/activate/{username}/code/{code}"
    )
    @Transactional
    public void activateUser(@PathVariable("username") String username, @PathVariable("code") String code, HttpSession session) {
        User user = userCache.getUserByUsername(username).orElseThrow(() -> new UserNotFoundException(username));
        if(user.getActive()) return;

        if(!user.getActivationCode().getCode().equals(code)) throw new InvalidCodeException();

        user.getActivationCode().revoke();

        repo.saveAndFlush(user);
    }

    @PostMapping(
            path = "/logout"
    )
    public void logout(HttpSession session) {
        User user = userCache.getUserFromSession(session);

        session.invalidate();
    }
}
