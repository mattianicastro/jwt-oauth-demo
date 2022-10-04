package click.mattia.jwtserver;

public class User {
    private String username;
    private String picture;
    private Integer githubId;

    private byte[] salt;

    private byte[] password;

    public User(String username, String picture, Integer githubId) {
        this.username = username;
        this.picture = picture;
        this.githubId = githubId;
    }

    public User(String username, String picture, Integer githubId, byte[] salt, byte[] password) {
        this.username = username;
        this.picture = picture;
        this.githubId = githubId;
        this.salt = salt;
        this.password = password;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getPassword() {
        return password;
    }

    public void setPassword(byte[] password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Integer getGithubId() {
        return githubId;
    }

    public void setGithubId(Integer githubId) {
        this.githubId = githubId;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", picture='" + picture + '\'' +
                ", githubId=" + githubId +
                '}';
    }

}
