package org.gbg.tutorials.jpadissected;

import org.hibernate.BaseSessionEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoggingSessionListener extends BaseSessionEventListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoggingSessionListener.class);

    @Override
    public void jdbcConnectionAcquisitionEnd() {
        LOGGER.info("=======> DB Connection Acquired");
    }

    @Override
    public void jdbcConnectionReleaseEnd() {
        LOGGER.info("<======= DB Connection Released");
    }
}
