package org.gbg.tutorials.jpadissected.ch06;

import org.gbg.tutorials.jpadissected.booking.BookingService.Request;
import org.gbg.tutorials.jpadissected.booking.PlayerBookingService;
import org.gbg.tutorials.jpadissected.booking.SpringDataBookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.gbg.tutorials.jpadissected.TestData.FIRST_COURT_ID;
import static org.gbg.tutorials.jpadissected.TestData.FIRST_PLAYER_ID;

@SpringBootTest
public class SpringDataBookingServiceTest {

    @Autowired
    private SpringDataBookingService bookingService;

    @Autowired
    private PlayerBookingService playerBookingService;

    @Test
    public void shouldCreateBooking() {
        //  when
        var created = bookingService.book(new Request(FIRST_COURT_ID, FIRST_PLAYER_ID, LocalDateTime.now()));

        //  then
        var bookings = playerBookingService.getBookings(FIRST_PLAYER_ID);

        assertThat(bookings)
                .filteredOn(it -> it.getId().equals(created.id()))
                .singleElement()
                .isNotNull();
    }

    @Test
    public void shouldCauseLazyInitializationException() {
        //  when
        var created = bookingService.book(new Request(FIRST_COURT_ID, FIRST_PLAYER_ID, LocalDateTime.now()));

        //  then
        var booking = playerBookingService.getBookings(FIRST_PLAYER_ID)
                .stream()
                .filter(it -> it.getId().equals(created.id()))
                .findFirst().orElseThrow();

        assertThatThrownBy(() -> booking.getCourt().getName())
                .isInstanceOf(org.hibernate.LazyInitializationException.class);
    }
}
