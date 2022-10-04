package click.mattia.jwtserver;

import org.json.JSONObject;
import org.json.JSONTokener;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class Utils {
    public static SecureRandom random = new SecureRandom();

    public static byte[] generateSalt() {
        byte[] salt = new byte[16];
        random.nextBytes(salt);
        return salt;
    }

    public static byte[] hashPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
        var factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        return factory.generateSecret(spec).getEncoded();
    }

    public static JSONObject loadConfig() {
        InputStream is = Utils.class.getResourceAsStream("/config.json");
        if (is == null) {
            throw new NullPointerException("Cannot find resource file");
        }

        JSONTokener tokener = new JSONTokener(is);
        return new JSONObject(tokener);
    }

}
