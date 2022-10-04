package click.mattia.jwtserver;

public enum ResponseCode {
    OK(200), USER_EXISTS(400), NOT_FOUND(404), UNAUTHORIZED(403);

    public int code;

    ResponseCode(int i) {
    }

}
