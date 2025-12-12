package com.exemplo.controlemesas.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility logger to keep logging helpers in config package without registering a Spring bean.
 */
class ExceptionLogger {

    private static final Logger log = LoggerFactory.getLogger(ExceptionLogger.class);

    public static void log(Throwable ex) {
        log.error("Logged by ExceptionLogger utility", ex);
    }
}
