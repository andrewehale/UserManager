package com.andrewhale.usermanager;

import com.andrewhale.usermanager.api.Status;
import org.apache.commons.validator.GenericValidator;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by andrew on 6/21/2016.
 */
public class Tools {
    /**
     * Just validates an email address
     * @param emailAddress
     * @return
     */
    public static Status validateEmailAddress(String emailAddress) {
        Status status = new Status();

            if (!GenericValidator.isEmail(emailAddress)) {
                status.setErrorCode(Err.INVALID_EMAIL);
                status.setErrorMessage(String.format("%s is not a valid email address.", emailAddress));
            }
        return status;
    }

    /**
     * https://www.owasp.org/index.php/Hashing_Java
     * https://crackstation.net/hashing-security.htm
     * @param password
     * @param salt
     * @param iterations
     * @param keyLength
     * @return
     */
    public static byte[] hashPassword( final char[] password, final byte[] salt, final int iterations, final int keyLength ) {

        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA512" );
            PBEKeySpec spec = new PBEKeySpec( password, salt, iterations, keyLength );
            SecretKey key = skf.generateSecret( spec );
            byte[] res = key.getEncoded( );
            return res;

        } catch( NoSuchAlgorithmException | InvalidKeySpecException e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Just force length requirements.
     * @param password
     * @return
     */
    public static Status validatePasswordRequirements(String password) {
        Status status = new Status();
        if (password == null || password.length() < 10 || password.length() > 64) {
            status.setErrorCode(Err.INVALID_PASS);
            status.setErrorMessage("Password does not meet minimum requirements.");
        }
        return status;
    }
}
