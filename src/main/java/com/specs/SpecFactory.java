package com.specs;

import com.config.ConfigManager;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.builder.ResponseSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;

import static org.hamcrest.Matchers.lessThan;

public class SpecFactory {

    private SpecFactory() {
    }

    public static RequestSpecification getRequestSpec() {
        return new RequestSpecBuilder()
                .setBaseUri(ConfigManager.getInstance().getProperty("baseUri"))
                .setAccept("application/json")
                .setContentType(ContentType.JSON)
                .addFilter(new AllureRestAssured())   // attaches every request/response to the Allure report
                .build();
    }

    // The token is passed in: main code can't call TokenManager (it lives in src/test)
    public static RequestSpecification getAuthSpec(String token) {
        return new RequestSpecBuilder()
                .addRequestSpecification(getRequestSpec())
                .addCookie("token", token)
                .build();
    }

    public static ResponseSpecification getResponseSpec(){
              return new ResponseSpecBuilder()
                      .expectResponseTime(lessThan(5000L))
                      .build();

    }
}
