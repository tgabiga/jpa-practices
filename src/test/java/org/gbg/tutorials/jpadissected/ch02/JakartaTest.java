package org.gbg.tutorials.jpadissected.ch02;

import jakarta.persistence.EntityManagerFactory;
import org.gbg.tutorials.jpadissected.domain.Court;
import org.gbg.tutorials.jpadissected.junit5.EntityManagerInjector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(EntityManagerInjector.class)
public class JakartaTest {

    @Test
    public void shouldStoreEntity(EntityManagerFactory emf) {
        //  given
        UUID id = UUID.randomUUID();

        //  when
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();
            var court = new Court(id, "court-name");
            em.persist(court);
            tx.commit();
        }

        //  then
        try (var em = emf.createEntityManager()) {
            var court = em.find(Court.class, id);
            assertThat(court).isNotNull();
            assertThat(court.getName()).isEqualTo("court-name");
        }
    }

    @Test
    public void shouldNotStoreEntityOnNoTransaction(EntityManagerFactory emf) {
        //  given
        UUID id = UUID.randomUUID();

        //  when
        try (var em = emf.createEntityManager()) {
            var court = new Court(id, "court-name");
            em.persist(court);
        }

        //  then
        try (var em = emf.createEntityManager()) {
            var court = em.find(Court.class, id);
            assertThat(court).isNull();
        }
    }
}
