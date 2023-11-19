package org.gbg.tutorials.jpadissected;

import jakarta.persistence.EntityManagerFactory;
import org.gbg.tutorials.jpadissected.domain.Booking;
import org.gbg.tutorials.jpadissected.domain.Court;
import org.gbg.tutorials.jpadissected.domain.Player;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class BookingService {

    public record Request(UUID courtId, UUID playerId, LocalDateTime dateTime) {

    }

    /**
     * We need to manually create EntityManager as injected is already proxied by Spring.
     */
    private final EntityManagerFactory emf;

    public BookingService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public void bookWithReference(Request request) {
        var entityManager = emf.createEntityManager();

        var tx = entityManager.getTransaction();
        try {
            tx.begin();
            //  getReference does not actually fetch data from DB
            var court = entityManager.getReference(Court.class, request.courtId());
            var player = entityManager.getReference(Player.class, request.playerId());

            var booking = new Booking(UUID.randomUUID(), request.dateTime(), court, player);

            entityManager.persist(booking);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }

    public void bookWithFind(Request request) {
        var entityManager = emf.createEntityManager();

        var tx = entityManager.getTransaction();
        try {
            tx.begin();
            //  find fetches data from db
            var court = entityManager.find(Court.class, request.courtId());
            var player = entityManager.find(Player.class, request.playerId());

            var booking = new Booking(UUID.randomUUID(), request.dateTime(), court, player);

            entityManager.persist(booking);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }
}
