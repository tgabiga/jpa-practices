package org.gbg.tutorials.jpadissected.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    private UUID id;

    @Column(name = "date")
    private LocalDateTime dateTime;

    @ManyToOne
    private Court court;

    @ManyToOne
    private Player player;

    public Booking() {
    }

    public Booking(UUID id, LocalDateTime dateTime, Court court, Player player) {
        this.id = id;
        this.dateTime = dateTime;
        this.court = court;
        this.player = player;
    }
}
