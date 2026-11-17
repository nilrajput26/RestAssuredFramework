package com.base;

import com.specs.SpecFactory;
import io.restassured.specification.RequestSpecification;
import io.restassured.specification.ResponseSpecification;
import org.testng.annotations.BeforeClass;

public class BaseTest {

    protected RequestSpecification Request;
    protected ResponseSpecification responseSpec;   // shared checks for every response (response time)

    @BeforeClass(alwaysRun = true)   // alwaysRun: still runs when a suite filters by groups (e.g. smoke)
    public void setUp() {
        Request = SpecFactory.getRequestSpec();
        responseSpec = SpecFactory.getResponseSpec();
    }

    protected RequestSpecification getAuthSpec() {
        String token = TokenManager.getToken(Request);   // test layer fetches the token
        return SpecFactory.getAuthSpec(token);           // factory builds the spec
    }
}
