package org.gbg.tutorials.jpadissected.ch05;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import org.gbg.tutorials.jpadissected.booking.BookingService;
import org.gbg.tutorials.jpadissected.booking.DeclarativeTxBookingServiceExternal;
import org.gbg.tutorials.jpadissected.booking.PlayerBookingService;
import org.hibernate.TransactionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.gbg.tutorials.jpadissected.TestData.ALL_PLAYER_IDS;
import static org.gbg.tutorials.jpadissected.TestData.FIRST_COURT_ID;
import static org.gbg.tutorials.jpadissected.TestData.FIRST_PLAYER_ID;
import static org.gbg.tutorials.jpadissected.TestData.SECOND_PLAYER_ID;

@SpringBootTest
public class ExternalResourceWithingTxTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExternalResourceWithingTxTest.class);

    @Autowired
    private DeclarativeTxBookingServiceExternal bookingService;
    //
    //    @Autowired
    //    private ProgrammaticTxBookingService bookingService;

    @Autowired
    private PlayerBookingService playerBookingService;
    private ExecutorService serverPool;

    @BeforeEach
    public void setup() {
        serverPool = Executors.newFixedThreadPool(10, new ThreadFactoryBuilder()
                .setNameFormat("server-pool-%d")
                .setUncaughtExceptionHandler((t, e) -> LOGGER.error("Uncaught exception in thread {}", t, e))
                .build());
    }

    @AfterEach
    public void tearDown() {
        serverPool.shutdown();
    }

    /**
     * Settings
     * - tx timeout=2
     * - delay = 5
     */
    @Test
    public void shouldDemonstrateTransactionTimeout() {
        //  given
        var request = new BookingService.Request(FIRST_COURT_ID, FIRST_PLAYER_ID, LocalDateTime.now());

        //  when
        //        bookingService.book(request);
        assertThatThrownBy(() -> bookingService.book(request))
                .hasCauseInstanceOf(TransactionException.class);
    }

    /**
     * Assume that we improved external service and increased transaction timeout.
     * Let us simulate what is happening when one of our services occupies DB connections for a long time.
     * <p>
     * Settings
     * - turn off SQL logging
     * - turn off Transaction Logging
     * - tx timeout=10, delay=3
     * - max pool size = 2
     */
    @Test
    public void shouldDemonstrateDataSourcePoolExhaustion() {
        //  given
        var bookingFutures = Stream.of(FIRST_PLAYER_ID, SECOND_PLAYER_ID)
                .map(this::bookTask)
                .map(task -> CompletableFuture.runAsync(task, serverPool))
                .toArray(CompletableFuture[]::new);

        ALL_PLAYER_IDS.stream()
                .flatMap(it -> IntStream.range(0, 10).mapToObj(i -> it))
                .map(this::getBookingsTask)
                .map(task -> CompletableFuture.runAsync(task, serverPool).orTimeout(200, TimeUnit.MILLISECONDS))
                .forEach(ExternalResourceWithingTxTest::join);

        //  when
        CompletableFuture.allOf(bookingFutures).join();
    }

    private static void join(CompletableFuture<Void> future) {
        try {
            future.join();
        } catch (Exception e) {
            LOGGER.error("Exception while retrieving bookings", e);
        }
    }

    private Runnable getBookingsTask(UUID playerId) {
        return () -> {
            playerBookingService.getBookings(playerId);
            LOGGER.info("Retrieved bookings for player {}", playerId);
        };
    }

    private Runnable bookTask(UUID playerId) {
        return () -> {
            LOGGER.info("Creating a new booking");
            var request = new BookingService.Request(FIRST_COURT_ID, playerId, LocalDateTime.now());
            bookingService.book(request);
            LOGGER.info("Booking created");
        };
    }
}
