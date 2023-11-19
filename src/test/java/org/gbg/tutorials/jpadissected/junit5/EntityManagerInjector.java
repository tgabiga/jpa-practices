package org.gbg.tutorials.jpadissected.junit5;

import jakarta.persistence.EntityManagerFactory;
import org.gbg.tutorials.jpadissected.LoggingSessionListener;
import org.gbg.tutorials.jpadissected.domain.Booking;
import org.gbg.tutorials.jpadissected.domain.Court;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.AvailableSettings;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityManagerInjector implements ParameterResolver, BeforeAllCallback, AfterAllCallback {

    private static final Logger LOGGER = LoggerFactory.getLogger(EntityManagerInjector.class);

    private StandardServiceRegistry serviceRegistry;
    private EntityManagerFactory emf;

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        try {
            emf.close();
            serviceRegistry.close();
        } catch (Exception e) {
            LOGGER.warn("Exception while closing EntityManagerFactory", e);
        }
    }

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        serviceRegistry = new StandardServiceRegistryBuilder()
                .applySetting(AvailableSettings.AUTO_SESSION_EVENTS_LISTENER, LoggingSessionListener.class.getName())
                //                .applySetting(AvailableSettings.LOG_SESSION_METRICS, true)
                .build();

        emf = new MetadataSources(serviceRegistry)
                .addAnnotatedClasses(Court.class, Booking.class)
                .buildMetadata()
                .buildSessionFactory();
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return parameterContext.getParameter().getType().equals(EntityManagerFactory.class);
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
            throws ParameterResolutionException {
        return emf;
    }
}
