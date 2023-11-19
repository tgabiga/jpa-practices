package org.gbg.tutorials.jpadissected.booking;

import jakarta.persistence.EntityManagerFactory;
import org.gbg.tutorials.jpadissected.domain.Booking;
import org.gbg.tutorials.jpadissected.domain.Court;
import org.gbg.tutorials.jpadissected.domain.Player;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class EmfBookingService implements BookingService {

    /**
     * We need to manually create EntityManager as injected is already proxied by Spring.
     */
    private final EntityManagerFactory emf;

    public EmfBookingService(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public BookingDto book(Request request) {
        var entityManager = emf.createEntityManager();

        var tx = entityManager.getTransaction();
        try {
            tx.begin();
            //  getReference does not actually fetch data from DB
/*

            var court = entityManager.getReference(Court.class, request.courtId());
            var player = entityManager.getReference(Player.class, request.playerId());
*/

            var court = entityManager.find(Court.class, request.courtId());
            var player = entityManager.find(Player.class, request.playerId());
            var booking = new Booking(UUID.randomUUID(), request.dateTime(), court, player);

            entityManager.persist(booking);

            tx.commit();

            return BookingDtoMapper.map(booking);
        } catch (Exception e) {
            tx.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }
}
