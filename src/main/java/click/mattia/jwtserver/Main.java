package click.mattia.jwtserver;

import static spark.Spark.get;
import static spark.Spark.staticFiles;

public class Main {
    public static void main(String[] args) {

        var authHelper = new AuthHelper();

        staticFiles.location("/frontend");

        get("/register/:username/:password", (req, res) -> {
            var r = authHelper.registerUser(req.params("username"), req.params("password"));
            res.status(r.getResponseCode().ordinal());
            return r.getResponse();
        });
        get("/login/:username/:password", (req, res) -> authHelper.login(req.params("username"), req.params("password")).getResponse());

        get("/me/:jwt", (req, res) -> {

            var user = authHelper.validateJwt(req.params("jwt"));
            if(user==null) {
                res.status(403);
                return "unauthorized";
            }
            return user;
        });

    }
}