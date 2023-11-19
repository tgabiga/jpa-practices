package org.gbg.tutorials.jpadissected.ch04;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.gbg.tutorials.jpadissected.booking.BookingService;
import org.gbg.tutorials.jpadissected.booking.EmfBookingService;
import org.gbg.tutorials.jpadissected.domain.Booking;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.gbg.tutorials.jpadissected.TestData.FIRST_COURT_ID;
import static org.gbg.tutorials.jpadissected.TestData.SECOND_PLAYER_ID;

@SpringBootTest
class EmfBookingServiceTest {

    @Autowired
    private EmfBookingService bookingService;

    @Autowired
    private EntityManagerFactory emf;
    private EntityManager em;

    @BeforeEach
    public void setup() {
        em = emf.createEntityManager();
    }

    @AfterEach
    public void tearDown() {
        em.close();
    }

    //  here we expect a single insert and a single select
    @Test
    void shouldCreateNewBooking() {
        //  when
        bookingService.book(new BookingService.Request(FIRST_COURT_ID, SECOND_PLAYER_ID, LocalDateTime.now()));

        //  then
        //  notice impact of fetch type
        var bookings = em.createQuery("select b from Booking b", Booking.class).getResultList();
        assertThat(bookings).isNotEmpty();
    }
}
