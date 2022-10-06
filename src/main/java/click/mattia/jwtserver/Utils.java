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
import static click.mattia.jwtserver.Config.*;

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

    public static String signStateJwt() {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MINUTE, 10);
        return JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(date.getTime())
                .withIssuer("oauth")
                .sign(algorithm);
    }

    public static String signAuthJwt(User user) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.HOUR, 1);
        return JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(date.getTime())
                .withIssuer("login")
                .withClaim("username", user.getUsername())
                .sign(algorithm);
    }

    public static User validateAuthJwt(String jwt, Database db) throws SQLException {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("login")
                    .build();
            DecodedJWT token = verifier.verify(jwt);
            return db.getUserByUsername(token.getClaim("username").asString());
        }catch(JWTVerificationException e){
            Logger.error(e);
            return null;
        }
    }

    public static boolean validateStateJwt(String jwt) {

        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        try {
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("oauth")
                    .build();
            DecodedJWT token = verifier.verify(jwt);
            return true;
        }catch(JWTVerificationException e){
            Logger.error(e);
            return false;
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
