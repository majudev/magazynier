package pl.zbiczagromada.Magazynier.user;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;

public class HashPassword {
    @Column(nullable = false)
    @Getter @Setter(AccessLevel.PROTECTED)
    private String password;

    @Column(nullable = true)
    @Getter @Setter(AccessLevel.PROTECTED)
    private String password_salt;

    @Column(nullable = false)
    @Getter @Setter(AccessLevel.PROTECTED)
    private HashAlgo password_algo;

    public HashPassword(String cleartextpassword) {
        this.password = cleartextpassword;
        this.password_algo = HashAlgo.CLEARTEXT;
        this.password_salt = null;
    }

    protected HashPassword() { }

    public HashPassword(String cleartextpassword, HashAlgo algo) {
        this.password_algo = algo;
        if(algo == HashAlgo.CLEARTEXT){
            this.password = cleartextpassword;
        }
    }

    public boolean validate(String cleatextpassword){
        if(this.password_algo == HashAlgo.CLEARTEXT){
            return this.password.equals(cleatextpassword);
        }else return false;
    }

    public enum HashAlgo{
        CLEARTEXT
    }
}
