package click.mattia.jwtserver;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.json.JSONObject;
import org.tinylog.Logger;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.SQLException;
import java.util.*;

import static click.mattia.jwtserver.Utils.hashPassword;


public class AuthHelper {
    public Database db = new Database();
    private String jwtSecret;
    public AuthHelper() {
        JSONObject config = Utils.loadConfig();
        jwtSecret = config.getString("jwtSecret");
    }

    private String signAuthJwt(User user) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        Calendar date = Calendar.getInstance();
        date.add(Calendar.MINUTE, 10);
        return JWT.create()
                .withIssuedAt(new Date())
                .withExpiresAt(date.getTime())
                .withClaim("username", user.getUsername())
                .sign(algorithm);
    }
    public Response registerUser(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return db.registerUser(username, password);
    }

    public User validateJwt(String jwt) throws SQLException {
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
    public Response login(String username, String password) throws SQLException, NoSuchAlgorithmException, InvalidKeySpecException {
        var user = db.getUserByUsername(username);
        if(user == null){
            return new Response(ResponseCode.NOT_FOUND, "user not found");
        }
        var generatedHash = hashPassword(password, user.getSalt());
        if(Arrays.equals(generatedHash, user.getPassword())){
            return new Response(ResponseCode.OK, signAuthJwt(user));
        }

        return new Response(ResponseCode.UNAUTHORIZED, "unauthorized");
    }
}
