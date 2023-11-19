package org.gbg.tutorials.jpadissected;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/api/bookings")
public class BookingController {

    @GetMapping
    public void getBookings() {
        //  using hardcoded ID to avoid adding authentication

    }
}
