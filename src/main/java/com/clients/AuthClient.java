package com.clients;

import com.constants.Endpoints;
import com.pojo.AuthRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class AuthClient {

    private final RequestSpecification Request;

    public AuthClient(RequestSpecification Request){
          this.Request=Request;
    }

    public Response createToken(AuthRequest authRequest){
        return given().spec(Request).body(authRequest).log().ifValidationFails().when().post(Endpoints.AUTH);
    }

}
