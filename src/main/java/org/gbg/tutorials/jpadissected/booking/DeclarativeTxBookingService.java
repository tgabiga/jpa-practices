package org.gbg.tutorials.jpadissected.booking;

import jakarta.persistence.EntityManager;
import org.gbg.tutorials.jpadissected.domain.Booking;
import org.gbg.tutorials.jpadissected.domain.Court;
import org.gbg.tutorials.jpadissected.domain.Player;
import org.hibernate.annotations.Cache;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.transaction.support.AbstractPlatformTransactionManager;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.lang.reflect.Method;
import java.util.UUID;

@Service
public class DeclarativeTxBookingService implements BookingService {

    private final EntityManager entityManager;

    public DeclarativeTxBookingService(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * {@link Transactional} transactional pseudocode:
     * - open transaction
     * - perform operation
     * - commit transaction or rollback on exception
     * <p>
     * Transaction is bound to a current thread so other calls may check the status.
     * <p>
     * See {@link AbstractPlatformTransactionManager} for transaction handling,
     * {@link TransactionSynchronizationManager} for ThreadLocal variables and {@link JpaTransactionManager} for a
     * concrete implementation.
     * <p>
     * Aspect is handled in
     * {@link TransactionAspectSupport#invokeWithinTransaction(Method, Class,
     * TransactionAspectSupport.InvocationCallback)}
     */
    @Transactional
    @Override
    public BookingDto book(Request request) {
        var court = entityManager.getReference(Court.class, request.courtId());
        var player = entityManager.getReference(Player.class, request.playerId());

        var booking = new Booking(UUID.randomUUID(), request.dateTime(), court, player);
        entityManager.persist(booking);
        return BookingDtoMapper.map(booking);
    }
}
