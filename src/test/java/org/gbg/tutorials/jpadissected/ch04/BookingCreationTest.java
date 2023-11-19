package org.gbg.tutorials.jpadissected.ch04;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.gbg.tutorials.jpadissected.BookingService;
import org.gbg.tutorials.jpadissected.domain.Booking;
import org.gbg.tutorials.jpadissected.domain.Court;
import org.gbg.tutorials.jpadissected.domain.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BookingCreationTest {

    @Autowired
    private BookingService bookingService;

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

    @Test
    void shouldCreateNewBooking() {
        //  given
        var court = new Court(UUID.randomUUID(), "court-name");
        var player = new Player(UUID.randomUUID(), "foo@invalid.com");

        var tx = em.getTransaction();
        tx.begin();
        em.persist(court);
        em.persist(player);
        tx.commit();

        //  when
        bookingService.bookWithReference(new BookingService.Request(
                court.getId(),
                player.getId(),
                LocalDateTime.now()));

        //  then
        var bookings = em.createQuery("select b from Booking b", Booking.class).getResultList();
        assertThat(bookings).isNotEmpty();
    }
}
