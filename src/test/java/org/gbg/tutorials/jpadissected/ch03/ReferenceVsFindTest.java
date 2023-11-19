package org.gbg.tutorials.jpadissected.ch03;

import jakarta.persistence.EntityManagerFactory;
import org.gbg.tutorials.jpadissected.domain.Booking;
import org.gbg.tutorials.jpadissected.domain.Court;
import org.gbg.tutorials.jpadissected.domain.Player;
import org.gbg.tutorials.jpadissected.junit5.EntityManagerInjector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.UUID;

@ExtendWith(EntityManagerInjector.class)
public class ReferenceVsFindTest {

    @Test
    public void shouldDemonstrateFindReference(EntityManagerFactory emf) {
        //  given
        UUID courtId = UUID.randomUUID();
        UUID playerId = UUID.randomUUID();

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            var court = new Court(courtId, "court-name");
            var player = new Player(playerId, "player-name");
            em.persist(player);
            em.persist(court);
            tx.commit();
        }

        //  when
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();

            tx.begin();
            var court = em.getReference(Court.class, courtId);
            var player = em.getReference(Player.class, playerId);
            var booking = new Booking(UUID.randomUUID(), LocalDateTime.now(), court, player);
            em.persist(booking);

            tx.commit();
        }
    }

}
