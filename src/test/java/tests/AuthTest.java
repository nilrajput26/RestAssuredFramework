package tests;

import com.base.BaseTest;
import com.clients.AuthClient;
import com.config.ConfigManager;
import com.pojo.AuthRequest;
import com.pojo.AuthResponse;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful Booker API")
@Feature("Authentication")
public class AuthTest extends BaseTest {

    @Test(groups = {"smoke", "regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("Valid credentials return a token and no error reason")
    public void validCredentialsReturnToken() {
        String username = ConfigManager.getInstance().getProperty("username");
        String password = ConfigManager.getInstance().getSecret("BOOKER_PASSWORD");

        Response response = new AuthClient(Request).createToken(new AuthRequest(username, password));
        response.then().spec(responseSpec).statusCode(200);

        AuthResponse body = response.as(AuthResponse.class);
        assertThat(body.getToken()).isNotBlank();
        assertThat(body.getReason()).isNull();
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.NORMAL)
    @Description("Wrong password returns 200 with reason 'Bad credentials' and no token (Restful-Booker quirk)")
    public void invalidPasswordReturnsBadCredentials() {
        String username = ConfigManager.getInstance().getProperty("username");

        Response response = new AuthClient(Request).createToken(new AuthRequest(username, "wrong-password"));
        response.then().spec(responseSpec).statusCode(200);

        AuthResponse body = response.as(AuthResponse.class);
        assertThat(body.getToken()).isNull();
        assertThat(body.getReason()).isEqualTo("Bad credentials");
    }
}
