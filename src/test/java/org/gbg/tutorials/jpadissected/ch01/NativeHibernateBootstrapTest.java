package org.gbg.tutorials.jpadissected.ch01;

import com.zaxxer.hikari.HikariConfig;
import org.gbg.tutorials.jpadissected.LoggingSessionListener;
import org.gbg.tutorials.jpadissected.domain.Booking;
import org.gbg.tutorials.jpadissected.domain.Court;
import org.gbg.tutorials.jpadissected.domain.Player;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.junit.jupiter.api.Test;
import org.postgresql.ds.PGSimpleDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.cfg.AvailableSettings.AUTO_SESSION_EVENTS_LISTENER;

/**
 * - Services (see documentation)
 * - stateless session
 */
public class NativeHibernateBootstrapTest {

    private static final Logger LOGGER = LoggerFactory.getLogger("TestLogger");

    @Test
    public void vanillaHibernate() {
        //  reads hibernate.properties by default
        try (var serviceRegistry = new StandardServiceRegistryBuilder()
                .loadProperties("hibernate-vanilla.properties")
                .applySetting(AUTO_SESSION_EVENTS_LISTENER, LoggingSessionListener.class.getName())
                .build();
                var sessionFactory = new MetadataSources(serviceRegistry)
                        .addAnnotatedClasses(Court.class, Player.class, Booking.class)  //  define entities
                        .buildMetadata()
                        .buildSessionFactory();) {

            Transaction tx = null;
            try (var session = sessionFactory.openSession()) {
                tx = session.beginTransaction();

                LOGGER.info("Creating court");

                var court = new Court(UUID.randomUUID(), "jedynka");
                session.persist(court);

                LOGGER.info("Court created but not flushed");

                tx.commit();
            } finally {
                if (tx != null) {
                    tx.rollback();
                }
            }
        }
    }

    @Test
    public void shouldUseCustomDataSource() {
        var dataSource = new PGSimpleDataSource();
        dataSource.setUrl("jdbc:postgresql://localhost:5432/postgres");
        dataSource.setPassword("postgres");
        dataSource.setUser("postgres");

        try (var serviceRegistry = new StandardServiceRegistryBuilder()
                .applySetting(AvailableSettings.DATASOURCE, dataSource)
                .build();
                var sessionFactory = new MetadataSources(serviceRegistry)
                        .addAnnotatedClasses(Court.class, Player.class, Booking.class)
                        .buildMetadata()
                        .buildSessionFactory();) {
            UUID courtId = UUID.randomUUID();

            sessionFactory.inSession(session -> {
                var court = new Court(courtId, "jedynka");

                var tx = session.beginTransaction();
                session.persist(court);
                tx.commit();
            });

            sessionFactory.inTransaction(session -> {
                var court = session.find(Court.class, courtId);
                assertThat(court).isNotNull();
            });
        }
    }

    /**
     * See {@link HikariConfig}
     */
    private void withHibernate(Consumer<SessionFactory> consumer) {
        try (var serviceRegistry = new StandardServiceRegistryBuilder()
                .loadProperties("hibernate-vanilla.properties")
                .build();
                var sessionFactory = new MetadataSources(serviceRegistry)
                        .addAnnotatedClass(Court.class)
                        .buildMetadata()
                        .buildSessionFactory();) {
            consumer.accept(sessionFactory);
        }
    }

    //  todo: inject parameters
    @Test
    public void shouldDemonstrateConnectionRetrieval() {
        withHibernate(sessionFactory -> {
            sessionFactory.inTransaction(session -> {
                //  HQL is not the same as JQL
                var res = session.createQuery("from Court", Court.class).getResultList();
                assertThat(res).isNotEmpty();
            });
        });
    }
}
