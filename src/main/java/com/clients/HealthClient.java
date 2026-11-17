package com.clients;

import com.constants.Endpoints;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class HealthClient {

    private final RequestSpecification requestSpec;

    public HealthClient(RequestSpecification requestSpec) {
        this.requestSpec = requestSpec;
    }

    public Response ping() {
        return given().spec(requestSpec).log().ifValidationFails().when().get(Endpoints.PING);
    }
}
