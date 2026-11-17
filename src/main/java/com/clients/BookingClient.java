package com.clients;

import com.constants.Endpoints;
import com.pojo.Booking;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

import static io.restassured.RestAssured.given;

public class BookingClient {

    private final RequestSpecification requestSpec;

    public BookingClient(RequestSpecification Req) {
        this.requestSpec = Req;
    }

    public Response createBooking(Booking booking) {
        return given().spec(requestSpec).body(booking).log().ifValidationFails().when().post(Endpoints.BOOKING);

    }

    public Response getBookingById(int id) {
        return given().spec(requestSpec).log().ifValidationFails().when().get(Endpoints.BOOKING_BY_ID, id);
    }

    public Response putCallBooking(int id, Booking booking) {
        return given().spec(requestSpec).body(booking).log().ifValidationFails().when().put(Endpoints.BOOKING_BY_ID, id);
    }

    public Response deleteBooking(int id) {
        return given().spec(requestSpec).log().ifValidationFails().when().delete(Endpoints.BOOKING_BY_ID, id);
    }

    public Response patchBooking(int id, Map<String,Object> fields) {
        return given().spec(requestSpec).body(fields).log().ifValidationFails().when().patch(Endpoints.BOOKING_BY_ID, id);

    }
}