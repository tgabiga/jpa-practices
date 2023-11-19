package org.gbg.tutorials.jpadissected.ch03;

import com.google.common.collect.Lists;
import jakarta.persistence.EntityManagerFactory;
import org.gbg.tutorials.jpadissected.domain.Court;
import org.gbg.tutorials.jpadissected.junit5.EntityManagerInjector;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.UUID;
import java.util.stream.IntStream;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@ExtendWith(EntityManagerInjector.class)
public class FlushesTest {

    @Test
    public void shouldPersistDataLazy(EntityManagerFactory emf) {
        //  given
        var courts = IntStream.range(0, 200)
                .mapToObj(it -> new Court(UUID.randomUUID(), "court-name-" + it))
                .toList();

        //  then
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();

            courts.forEach(em::persist);

            veryExpensiveOperation();

            //  note that until this point data is still kept in memory
            tx.commit();
        }
    }

    @Test
    public void shouldFlushDataOnRequest(EntityManagerFactory emf) {
        //  given
        var courts = IntStream.range(0, 200)
                .mapToObj(it -> new Court(UUID.randomUUID(), "court-name-" + it))
                .toList();

        //  when
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();

            Lists.partition(courts, 20).forEach(chunk -> {
                chunk.forEach(em::persist);
                em.flush();
                //  chunk.forEach(em::detach);  //  without detach entity is still kept in 1st level cache
            });

            //  during this operation without flush & detach we still keep all object in memory
            veryExpensiveOperation();

            tx.commit();

            var first = em.find(Court.class, courts.get(0).getId());
            System.out.println(first.getName());
        }
    }

    @Test
    public void shouldFlushDataBeforeSelect(EntityManagerFactory emf) {
        //  given
        var courts = IntStream.range(0, 200)
                .mapToObj(it -> new Court(UUID.randomUUID(), "court-name-" + it))
                .toList();

        //  then
        try (var em = emf.createEntityManager()) {
            var tx = em.getTransaction();
            tx.begin();

            courts.forEach(em::persist);

            //  this line forces flush
            var count = em.createQuery("select count(*) from Court c", Long.class).getSingleResult();
            assertThat(count).isGreaterThan(10);
            tx.commit();
        }
    }

    private void veryExpensiveOperation() {
        //  just pretending :)
    }
}
