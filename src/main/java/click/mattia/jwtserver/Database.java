package click.mattia.jwtserver;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.*;

import org.tinylog.Logger;

import static click.mattia.jwtserver.Utils.generateSalt;
import static click.mattia.jwtserver.Utils.hashPassword;

public class Database {
    Connection conn = null;
    public Database() {
        this.connect();
    }

    private Connection connect() {
        String url = "jdbc:sqlite:src/main/resources/users";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            Logger.error(e);
        }
        Logger.info("Database connection OK");
        return conn;
    }

    public Response registerUser(String username, String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        var sql = "INSERT INTO users (username, password, salt) VALUES (?, ? , ?)";
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // generate salt and hash password
            var salt = generateSalt();
            var hashedPassword = hashPassword(password, salt);

            pstmt.setString(1, username);
            pstmt.setBytes(2, hashedPassword);
            pstmt.setBytes(3, salt);

            pstmt.executeUpdate();
            Logger.info("Registered user {}", username);
            return new Response(ResponseCode.OK, "OK");
        } catch (SQLException e) {
            return new Response(ResponseCode.BAD_REQUEST, "user already exists");
        }

    }
    public User getUserByUsername(String username) throws SQLException {
        var sql = "SELECT * FROM users WHERE username=?";
        try (Connection conn = this.connect();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();

            if(!rs.next()) {
                return null;
            }

            return new User(rs.getString("username"),
                    rs.getString("picture"), rs.getInt("github_id"),
                    rs.getBytes("salt"), rs.getBytes("password"));

        }
    }

    public void updateGithubId(String username, Integer githubId) {

    }
}
