package pl.zbiczagromada.Magazynier.user.activationcode;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;
import pl.zbiczagromada.Magazynier.user.User;

import javax.persistence.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

@Entity
@Table(name = "activation_codes")
@Getter
public class ActivationCode {
    @Id
    @SequenceGenerator(
            name = "activationcode_sequence",
            sequenceName = "activationcode_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "activationcode_sequence",
            strategy = GenerationType.SEQUENCE
    )
    @JsonIgnore
    @Column(name = "activationcode_id")
    @Setter(AccessLevel.NONE)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    @Setter
    private boolean active;

    @OneToOne(cascade = {})
    private User user;

    private ActivationCode(){}

    public ActivationCode(User user){
        this.code = this.generateCode();
        this.user = user;
        this.active = true;
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
}
