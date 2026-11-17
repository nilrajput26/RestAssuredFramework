package com.testdata;

import com.pojo.Booking;
import com.pojo.BookingDates;
import net.datafaker.Faker;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public final class BookingData {
    public static final Faker faker = new Faker();

    private BookingData(){
    }

    public static Booking validBooking(){
        String firstname=faker.name().firstName();
        String lastname=faker.name().lastName();
        int totalprice=faker.number().numberBetween(100,500);
        LocalDate checkin=LocalDate.now().plusDays(10);
        LocalDate checkout=checkin.plusDays(4);
        BookingDates dates= BookingDates.builder()
                        .checkin(checkin.toString())
                        .checkout(checkout.toString())
                        .build();
        Booking booking=Booking.builder().firstname(firstname)
                .lastname(lastname).totalprice(totalprice)
                .depositpaid(true).bookingdates(dates).additionalneeds("breakfast").build();
                 return booking;
    }

    public static Booking bookingWithoutDeposit(){
        Booking valid= validBooking();
        Booking noDeposit=valid.toBuilder().depositpaid(false).build();
        return noDeposit;
    }

public static Booking updateCall(Booking booking){
    Booking updated=booking.toBuilder().totalprice(800).build();
    return updated;
}


public static Map<String,Object> patchCall(){
        Map<String,Object> fields= new HashMap<>();
        fields.put("firstname","Neelam");
        return fields;
    }

}
