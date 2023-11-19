package org.gbg.tutorials.jpadissected.ch07;

import jakarta.persistence.EntityManager;
import org.gbg.tutorials.jpadissected.domain.Booking;
import org.gbg.tutorials.jpadissected.domain.Court;
import org.gbg.tutorials.jpadissected.domain.Player;
import org.hibernate.LazyInitializationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionOperations;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.gbg.tutorials.jpadissected.TestData.FIRST_COURT_ID;
import static org.gbg.tutorials.jpadissected.TestData.FIRST_PLAYER_ID;

@SpringBootTest
public class LazyInitializationExceptionTest {

    @Autowired
    private TransactionOperations transactionOperations;

    @Autowired
    private EntityManager emf;

    @Test
    public void shouldCauseLazyInitializationException() {
        var bookingId = transactionOperations.execute(status1 -> {
            var player = emf.getReference(Player.class, FIRST_PLAYER_ID);
            var court = emf.getReference(Court.class, FIRST_COURT_ID);

            var id = UUID.randomUUID();
            emf.persist(new Booking(id, LocalDateTime.now(), court, player));
            return id;
        });

        var booking = transactionOperations.execute(status -> emf.find(Booking.class, bookingId));

        assertThat(booking.getDateTime()).isNotNull();
        assertThat(booking.getCourt().getId()).isEqualTo(FIRST_COURT_ID);
        assertThatThrownBy(() -> booking.getCourt().getName())
                .isInstanceOf(LazyInitializationException.class);
    }

    @Test
    public void shouldDemonstrateThatEntityBelongsToSinglePersistenceContext() {
        var booking = transactionOperations.execute(tx -> {
            var player = emf.getReference(Player.class, FIRST_PLAYER_ID);
            var court = emf.getReference(Court.class, FIRST_COURT_ID);

            var toStore = new Booking(UUID.randomUUID(), LocalDateTime.now(), court, player);
            emf.persist(toStore);
            return toStore;
        });

        transactionOperations.executeWithoutResult(tx -> {
            assertThatThrownBy(() -> booking.getCourt().getName()).isInstanceOf(LazyInitializationException.class);
        });
    }

    //  aka transactional entity manager
    @Test
    public void shouldDemonstrateSharedEntityManagerRetrieval() {
        var booking = emf.createQuery("select b from Booking b", Booking.class)
                .getResultList()
                .stream().findFirst().orElseThrow();

        assertThatThrownBy(() -> booking.getCourt().getName())
                .isInstanceOf(LazyInitializationException.class);
    }

    @Test
    public void shouldDemonstrateLazyLoading() {
        transactionOperations.executeWithoutResult(tx -> {
            var booking = emf.createQuery("select b from Booking b", Booking.class)
                    .getResultList()
                    .stream().findFirst().orElseThrow();

            assertThat(booking.getCourt().getName()).isNotNull();
        });
    }
}
