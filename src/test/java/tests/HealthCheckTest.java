package tests;

import com.base.BaseTest;
import com.clients.HealthClient;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import org.testng.annotations.Test;

@Epic("Restful Booker API")
@Feature("Health check")
public class HealthCheckTest extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    @Severity(SeverityLevel.BLOCKER)
    @Description("GET /ping returns 201 (Restful-Booker quirk) when the API is up")
    public void pingReturns201() {
        new HealthClient(Request).ping()
                .then()
                .spec(responseSpec)
                .statusCode(201);
    }
}
