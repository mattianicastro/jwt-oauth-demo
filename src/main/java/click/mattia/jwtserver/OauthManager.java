package click.mattia.jwtserver;


import org.apache.http.client.utils.URIBuilder;
import org.json.JSONObject;
import org.tinylog.Logger;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

import static click.mattia.jwtserver.Config.*;
import static click.mattia.jwtserver.Utils.*;

public class OauthManager {
    final static String AUTHORIZATION_ENDPOINT = "https://github.com/login/oauth/authorize";
    final static String ACCESS_TOKEN_ENDPOINT = "https://github.com/login/oauth/access_token";
    final static String USER_ENDPOINT = "https://api.github.com/user";
    final static HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .followRedirects(HttpClient.Redirect.NORMAL)
            .connectTimeout(Duration.ofSeconds(20))
            .build();
    public static URI getAuthEndpoint() throws URISyntaxException {
        return new URIBuilder(new URI(AUTHORIZATION_ENDPOINT))
                .addParameter("client_id", clientId)
                .addParameter("redirect_uri", callbackUrl)
                .addParameter("state", signStateJwt())
                .build();
    }

    public static URI getAccessTokenEndpoint(String code) throws URISyntaxException {
        return new URIBuilder(new URI(ACCESS_TOKEN_ENDPOINT))
                .addParameter("client_id", clientId)
                .addParameter("client_secret", clientSecret)
                .addParameter("code", code)
                .build();
    }

    public static String getAccessToken(String code) throws URISyntaxException, IOException, InterruptedException {
        var req = HttpRequest.newBuilder()
                .uri(new URI(getAccessTokenEndpoint(code).toString()))
                .POST(HttpRequest.BodyPublishers.noBody())
                .header("Accept", "application/json")
                .build();
        var res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        var json = new JSONObject(res.body());
        Logger.info(json);
        return json.getString("access_token");
    }

    public static String getGithubUser(String accessToken) throws URISyntaxException, IOException, InterruptedException {
        var req = HttpRequest.newBuilder()
                .uri(new URI(USER_ENDPOINT))
                .GET()
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .build();
        var res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());
        return res.body();
    }
}
