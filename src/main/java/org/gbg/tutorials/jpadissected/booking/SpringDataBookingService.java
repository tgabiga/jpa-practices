package org.gbg.tutorials.jpadissected.booking;

import com.google.common.util.concurrent.Uninterruptibles;
import org.gbg.tutorials.jpadissected.domain.Booking;
import org.gbg.tutorials.jpadissected.domain.BookingRepository;
import org.gbg.tutorials.jpadissected.domain.CourtRepository;
import org.gbg.tutorials.jpadissected.domain.PlayerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

import java.time.Duration;
import java.util.UUID;

@Service
public class SpringDataBookingService implements BookingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgrammaticTxBookingService.class);

    private final CourtRepository courtRepository;
    private final PlayerRepository playerRepository;
    private final BookingRepository bookingRepository;
    private final TransactionOperations transactionOperations;

    public SpringDataBookingService(CourtRepository courtRepository,
            PlayerRepository playerRepository,
            BookingRepository bookingRepository,
            TransactionOperations transactionOperations) {
        this.courtRepository = courtRepository;
        this.playerRepository = playerRepository;
        this.bookingRepository = bookingRepository;
        this.transactionOperations = transactionOperations;
    }

    @Override
    public BookingDto book(Request request) {
        verifyBalance(request.playerId());

        return transactionOperations.execute(tx -> {
            var court = courtRepository.getReferenceById(request.courtId());
            var player = playerRepository.getReferenceById(request.playerId());

            var booking = new Booking(UUID.randomUUID(), request.dateTime(), court, player);

            bookingRepository.save(booking);

            return BookingDtoMapper.map(booking);
        });
    }

    private void verifyBalance(UUID playerId) {
        LOGGER.info("Verifying balance for {} in an external service", playerId);
        Uninterruptibles.sleepUninterruptibly(Duration.ofSeconds(3));
    }
}
