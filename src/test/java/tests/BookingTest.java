package tests;

import com.base.BaseTest;
import com.clients.BookingClient;
import com.pojo.Booking;
import com.pojo.BookingResponse;
import com.testdata.BookingData;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.restassured.response.Response;
import org.testng.annotations.Test;

import java.util.Map;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.assertj.core.api.Assertions.assertThat;

@Epic("Restful Booker API")
@Feature("Booking")
public class BookingTest extends BaseTest {

    // ---------- Positive tests ----------

    @Test(groups = {"smoke", "regression"})
    @Severity(SeverityLevel.BLOCKER)
    @Description("POST /booking returns 200 and a positive booking id")
    public void createBooking() {
        BookingClient client = new BookingClient(Request);
        Booking booking = BookingData.validBooking();
        Response res = client.createBooking(booking);
        res.then().spec(responseSpec).statusCode(200);
        BookingResponse response = res.as(BookingResponse.class);
        assertThat(response.getBookingid()).isGreaterThan(0);
    }

    @Test(groups = {"smoke", "regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("GET /booking/{id} returns the booking that was created and matches the JSON schema")
    public void getBookingById() {
        BookingClient client = new BookingClient(Request);
        Booking booking = BookingData.validBooking();
        Response response = client.createBooking(booking);
        response.then().statusCode(200);
        BookingResponse bookingResponse = response.as(BookingResponse.class);
        int bookingId = bookingResponse.getBookingid();

        Response bookingidresponse = client.getBookingById(bookingId);
        bookingidresponse.then()
                .spec(responseSpec)
                .statusCode(200)
                .body(matchesJsonSchemaInClasspath("schemas/booking-schema.json"));
        Booking fetchedResponse = bookingidresponse.as(Booking.class);
        assertThat(fetchedResponse).isEqualTo(booking);
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("PUT /booking/{id} with a token replaces the booking")
    public void updateBooking() {
        BookingClient client = new BookingClient(Request);
        Booking booking = BookingData.validBooking();
        Response response = client.createBooking(booking);
        response.then().statusCode(200);
        BookingResponse bookingResponse = response.as(BookingResponse.class);
        int bookingId = bookingResponse.getBookingid();

        BookingClient authClient = new BookingClient(getAuthSpec());
        Booking updateBooking = BookingData.updateCall(booking);
        Response putResponse = authClient.putCallBooking(bookingId, updateBooking);

        putResponse.then().spec(responseSpec).statusCode(200);
        Booking updatedBody = putResponse.as(Booking.class);
        assertThat(updatedBody).isEqualTo(updateBooking);
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.NORMAL)
    @Description("PATCH /booking/{id} changes only the sent field; all other fields stay the same")
    public void patchBooking() {
        BookingClient client = new BookingClient(getAuthSpec());
        Booking booking = BookingData.validBooking();
        Response bookingResponse = client.createBooking(booking);
        bookingResponse.then().statusCode(200);
        BookingResponse response = bookingResponse.as(BookingResponse.class);
        int bookingId = response.getBookingid();

        Map<String, Object> patchBody = BookingData.patchCall();
        Response patchResponse = client.patchBooking(bookingId, patchBody);
        patchResponse.then().spec(responseSpec).statusCode(200);

        Booking patchActualResponse = patchResponse.as(Booking.class);
        Booking expected = booking.toBuilder().firstname((String) patchBody.get("firstname")).build();
        assertThat(patchActualResponse).isEqualTo(expected);
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("DELETE /booking/{id} returns 201 (Restful-Booker quirk); a GET afterwards returns 404")
    public void deleteBooking() {
        Booking booking = BookingData.validBooking();
        BookingClient client = new BookingClient(getAuthSpec());
        Response bookingResponse = client.createBooking(booking);
        bookingResponse.then().statusCode(200);
        BookingResponse response = bookingResponse.as(BookingResponse.class);
        int bookingId = response.getBookingid();

        Response deleteResponse = client.deleteBooking(bookingId);
        deleteResponse.then().spec(responseSpec).statusCode(201);
        Response checkResponse = client.getBookingById(bookingId);
        checkResponse.then().statusCode(404);
    }

    // ---------- Negative tests ----------

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.NORMAL)
    @Description("GET /booking/{id} with an id that does not exist returns 404")
    public void getUnknownBookingReturns404() {
        new BookingClient(Request).getBookingById(999999999)
                .then()
                .spec(responseSpec)
                .statusCode(404);
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("PUT without a token is rejected with 403")
    public void updateWithoutTokenReturns403() {
        BookingClient client = new BookingClient(Request);   // base spec: no cookie
        Booking booking = BookingData.validBooking();
        Response created = client.createBooking(booking);
        created.then().statusCode(200);
        int bookingId = created.as(BookingResponse.class).getBookingid();

        client.putCallBooking(bookingId, BookingData.updateCall(booking))
                .then()
                .spec(responseSpec)
                .statusCode(403);
    }

    @Test(groups = {"regression"})
    @Severity(SeverityLevel.CRITICAL)
    @Description("DELETE without a token is rejected with 403 and the booking still exists")
    public void deleteWithoutTokenReturns403() {
        BookingClient client = new BookingClient(Request);   // base spec: no cookie
        Response created = client.createBooking(BookingData.validBooking());
        created.then().statusCode(200);
        int bookingId = created.as(BookingResponse.class).getBookingid();

        client.deleteBooking(bookingId).then().spec(responseSpec).statusCode(403);
        client.getBookingById(bookingId).then().statusCode(200);
    }
}
