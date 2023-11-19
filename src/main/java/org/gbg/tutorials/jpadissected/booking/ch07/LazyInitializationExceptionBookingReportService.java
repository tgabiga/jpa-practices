package org.gbg.tutorials.jpadissected.booking.ch07;

import org.gbg.tutorials.jpadissected.booking.BookingService;
import org.gbg.tutorials.jpadissected.domain.Booking;

public class LazyInitializationExceptionBookingReportService {

    public Booking findBpla(BookingService.Request request) {
        return null;
//        var booking = new Booking();
//        booking.setDateTime(request.dateTime());
//        booking.setCourt(request.court());
//        booking.setPlayer(request.player());
//        return booking;
    }
}
