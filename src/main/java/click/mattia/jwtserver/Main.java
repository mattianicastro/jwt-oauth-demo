package click.mattia.jwtserver;

import org.json.JSONObject;

import java.util.Arrays;

import static click.mattia.jwtserver.Utils.hashPassword;
import static click.mattia.jwtserver.Utils.signAuthJwt;
import static click.mattia.jwtserver.Utils.validateJwt;


import static spark.Spark.*;
import static spark.Spark.staticFiles;

public class Main {
    public static void main(String[] args) {
        Database db = new Database();
        JSONObject config = Utils.loadConfig();
        String jwtSecret = config.getString("jwtSecret");

        staticFiles.location("/frontend");

        post("/register", (req, res) -> {
            var body = new JSONObject(req.body());
            var r = db.registerUser(body.getString("username"), body.getString("password"));
            res.status(r.getResponseCode().code);
            return r.getResponse();
        });
        post("/login", (req, res) -> {
            var body = new JSONObject(req.body());

            var user = db.getUserByUsername(body.getString("username"));
            var password = body.getString("password");
            if(user == null){
                res.status(404);
                return "User not found.";
            }
            // generate a new hash and compare it to the saved one
            var generatedHash = hashPassword(password, user.getSalt());
            var jwt = signAuthJwt(user, jwtSecret);
            if(Arrays.equals(generatedHash, user.getPassword())){
                res.cookie("jwt", jwt, 3600, true);
                return "ok";
            }else {
                res.status(ResponseCode.UNAUTHORIZED.code);
                return "invalid credential";
            }
        });

        get("/me", (req, res) -> {
            var jwt = req.cookie("jwt");
            if(jwt == null){
                res.status(ResponseCode.UNAUTHORIZED.code);
                return "unauthorized";
            }
            var user = validateJwt(req.cookie("jwt"), jwtSecret, db);
            if(user==null) {
                res.status(403);
                return "unauthorized";
            }
            return user;
        });

    }
}