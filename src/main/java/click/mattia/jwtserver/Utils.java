package click.mattia.jwtserver;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.tinylog.Logger;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

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

    public static String signAuthJwt(User user, String jwtSecret) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MINUTE, 10);
        return JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(date.getTime())
                .withClaim("username", user.getUsername())
                .sign(algorithm);
    }

    public static User validateJwt(String jwt, String jwtSecret, Database db) throws SQLException {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .build();
            DecodedJWT token = verifier.verify(jwt);
            return db.getUserByUsername(token.getClaim("username").asString());
        }catch(JWTVerificationException e){
            Logger.error(e);
            return null;
        }
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
