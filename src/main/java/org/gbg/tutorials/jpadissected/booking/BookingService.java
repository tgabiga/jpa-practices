package org.gbg.tutorials.jpadissected.booking;

import java.time.LocalDateTime;
import java.util.UUID;

public interface BookingService {

    record Request(UUID courtId, UUID playerId, LocalDateTime dateTime) {

    }

    record BookingDto(UUID id, LocalDateTime dateTime, UUID courtId, UUID playerId) {

    }

    BookingDto book(EmfBookingService.Request request);
}
