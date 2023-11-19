package org.gbg.tutorials.jpadissected.ch01;

import com.zaxxer.hikari.HikariConfig;
import org.gbg.tutorials.jpadissected.domain.Court;
import org.hibernate.SessionFactory;
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

/**
 * - Services (see documentation)
 * - stateless session
 */
public class NativeHibernateBootstrap {

    private static final Logger LOGGER = LoggerFactory.getLogger("TestLogger");

    @Test
    public void shouldCreateSession() {
        try (var serviceRegistry = new StandardServiceRegistryBuilder()
                .build()) {

            SessionFactory sessionFactory = new MetadataSources(serviceRegistry)
                    .addAnnotatedClass(Court.class)
                    .buildMetadata()
                    .buildSessionFactory();
        }
    }

    @Test
    public void shouldUseCustomDataSource() {
        var ds = new PGSimpleDataSource();
        ds.setUrl("jdbc:postgresql://localhost:5432/postgres");
        ds.setPassword("postgres");
        ds.setUser("postgres");

        try (var serviceRegistry = new StandardServiceRegistryBuilder()
                .applySetting(AvailableSettings.DATASOURCE, ds)
                .build();
                var sessionFactory = new MetadataSources(serviceRegistry)
                        .addAnnotatedClass(Court.class)
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
