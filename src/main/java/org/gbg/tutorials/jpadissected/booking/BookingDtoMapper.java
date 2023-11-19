package org.gbg.tutorials.jpadissected.booking;

import org.gbg.tutorials.jpadissected.domain.Booking;

public interface BookingDtoMapper {

    static BookingService.BookingDto map(Booking booking) {
        return new BookingService.BookingDto(
                booking.getId(),
                booking.getDateTime(),
                booking.getCourt().getId(),
                booking.getPlayer().getId()
        );
    }
}
