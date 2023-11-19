package org.gbg.tutorials.jpadissected.booking;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityManager;
import org.gbg.tutorials.jpadissected.domain.Booking;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PlayerBookingService {

    private final EntityManager entityManager;

    public PlayerBookingService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Returns {@link Entity} for demonstrative purposes.
     */
    @Transactional(readOnly = true)
    public List<Booking> getBookings(UUID playerId) {
        return entityManager.createQuery("select b from Booking b where b.player.id = :playerId", Booking.class)
                .setParameter("playerId", playerId)
                .getResultList();
    }
}
