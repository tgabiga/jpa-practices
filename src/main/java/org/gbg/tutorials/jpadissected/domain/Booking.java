package org.gbg.tutorials.jpadissected.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    private UUID id;

    @Column(name = "date")
    private LocalDateTime dateTime;

//        @ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    private Court court;

//        @ManyToOne
    @ManyToOne(fetch = FetchType.LAZY)
    private Player player;

    public Booking() {
    }

    public Booking(UUID id, LocalDateTime dateTime, Court court, Player player) {
        this.id = id;
        this.dateTime = dateTime;
        this.court = court;
        this.player = player;
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public Court getCourt() {
        return court;
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Booking booking = (Booking) o;
        return Objects.equals(id, booking.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Booking{id=" + id + ", dateTime=" + dateTime + '}';
    }
}
