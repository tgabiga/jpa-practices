package org.gbg.tutorials.jpadissected.ch03;

import jakarta.persistence.EntityManagerFactory;
import org.gbg.tutorials.jpadissected.domain.Booking;
import org.gbg.tutorials.jpadissected.domain.Court;
import org.gbg.tutorials.jpadissected.domain.Player;
import org.gbg.tutorials.jpadissected.junit5.EntityManagerInjector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(EntityManagerInjector.class)
public class FirstLevelCacheTest {

    @Test
    public void shouldNotRetrieveEntityTwice(EntityManagerFactory emf) {
        //  given
        UUID id = UUID.randomUUID();

        //  when
        Court court;
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            court = new Court(id, "court-name");
            em.persist(court);
            tx.commit();
        }

        //  then
        try (var em = emf.createEntityManager()) {
            var court1 = em.find(Court.class, id);
            court1.setName("new-name");

            var court2 = em.find(Court.class, id);
            //  at this point court1 and court2 refer to the same object in memory

            assertThat(court1).isSameAs(court2);
            assertThat(court1).isEqualTo(court2);
            assertThat(court2.getName()).isEqualTo("new-name");

            assertThat(court).isNotSameAs(court1);  //  it is not same as it comes from different persistence context
            assertThat(court).isEqualTo(court1);
        }
    }

    @Test
    public void shouldUseFirstLevelCacheFromQuery(EntityManagerFactory emf) {
        //  given
        UUID id = UUID.randomUUID();

        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            var court = new Court(id, "court-name");
            em.persist(court);
            tx.commit();
        }

        //  then
        try (var em = emf.createEntityManager()) {
            var allCourts = em.createQuery("select c from Court c", Court.class).getResultList();
            assertThat(allCourts).isNotEmpty();

            em.clear();  //  without em clear we do not reach out to db

            var court = em.find(Court.class, id);
            assertThat(court).isNotNull();
        }
    }


}
