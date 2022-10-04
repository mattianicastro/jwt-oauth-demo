package click.mattia.jwtserver;

public enum ResponseCode {
    OK(200), BAD_REQUEST(400), NOT_FOUND(404), FORBIDDEN(403),
    UNAUTHORIZED(401);

    public final int code;

    ResponseCode(int code) {
        this.code = code;
    }

}
