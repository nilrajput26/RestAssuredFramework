package com.base;

import com.clients.AuthClient;
import com.config.ConfigManager;
import com.pojo.AuthRequest;
import com.pojo.AuthResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public final class TokenManager {

    private static String newtoken;

    private TokenManager() {

    }

    public static synchronized String getToken(RequestSpecification baseSpec) {
        if (newtoken == null) {
            newtoken = fetchToken(baseSpec);
        }
        return newtoken;
    }


    private static String fetchToken(RequestSpecification baseSpec) {
        String username = ConfigManager.getInstance().getProperty("username");
        String password = ConfigManager.getInstance().getSecret("BOOKER_PASSWORD");
        AuthRequest authRequest = new AuthRequest(username, password);
        AuthClient authClient = new AuthClient(baseSpec);
        Response authResponse = authClient.createToken(authRequest);
        authResponse.then().statusCode(200);
        String token = authResponse.as(AuthResponse.class).getToken();
        if (token == null || token.isBlank()) {
            throw new RuntimeException("Auth Failed : ");
        }
        return token;
    }
}
