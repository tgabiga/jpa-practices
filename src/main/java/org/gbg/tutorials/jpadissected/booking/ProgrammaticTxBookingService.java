package org.gbg.tutorials.jpadissected.booking;

import com.google.common.util.concurrent.Uninterruptibles;
import jakarta.persistence.EntityManager;
import org.gbg.tutorials.jpadissected.domain.Booking;
import org.gbg.tutorials.jpadissected.domain.Court;
import org.gbg.tutorials.jpadissected.domain.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import java.time.Duration;
import java.util.UUID;

@Service
public class ProgrammaticTxBookingService implements BookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgrammaticTxBookingService.class);

    private final TransactionOperations transactionOperations;
    private final EntityManager entityManager;

    public ProgrammaticTxBookingService(TransactionOperations transactionOperations, EntityManager entityManager) {
        this.transactionOperations = transactionOperations;
        this.entityManager = entityManager;
    }

    @Override
    public BookingDto book(Request request) {
        verifyBalance(request.playerId());

        return transactionOperations.execute(tx -> {
            var court = entityManager.getReference(Court.class, request.courtId());
            var player = entityManager.getReference(Player.class, request.playerId());

            Booking booking = new Booking(UUID.randomUUID(), request.dateTime(), court, player);
            entityManager.persist(booking);
            return BookingDtoMapper.map(booking);
        });
    }

    private void verifyBalance(UUID playerId) {
        LOGGER.info("Verifying balance for {} in an external service", playerId);
        Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(3));
    }
}
