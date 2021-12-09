package pl.zbiczagromada.Magazynier.user.forgotpasswordcode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import pl.zbiczagromada.Magazynier.user.User;

import javax.persistence.*;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@Entity
@Table(name = "forgotpassword_codes")
@Getter
public class ForgotPasswordCode {
    @Id
    @SequenceGenerator(
            name = "forgotpassword_sequence",
            sequenceName = "forgotpassword_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "forgotpassword_sequence",
            strategy = GenerationType.SEQUENCE
    )
    @JsonIgnore
    @Column(name = "forgotpassword_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    @Setter
    private boolean active;

    @Column(nullable = false)
    private LocalDateTime issuedOn;

    @ManyToOne(cascade = {})
    private User user;

    private ForgotPasswordCode(){}

    public ForgotPasswordCode(User user){
        this.code = this.generateCode();
        this.user = user;
        this.active = true;
        this.issuedOn = LocalDateTime.now();
    }

    public void revoke(){
        this.setActive(false);
    }

    @Transient
    @JsonIgnore
    private final static char[] charMap = initializeCharMap();

    private String generateCode(){
        UUID uuid = UUID.randomUUID();
        String code = uuid.toString() + "-";
        Random random = new SecureRandom();
        for(int i = 0; i < 64; ++i){
            code += charMap[random.nextInt(charMap.length)];
        }
        return code;
    }

    private static char[] initializeCharMap(){
        final String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String lower = upper.toLowerCase(Locale.ROOT);
        final String digits = "0123456789";
        final String alphanum = upper + lower + digits;
        return alphanum.toCharArray();
    }

    public LocalDateTime getExpires(){
        return this.getIssuedOn().plusDays(1);
    }
}
