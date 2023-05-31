package org.vaadin.googleanalytics.tracking;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.Route;

/**
 * Annotation that enables automatic Google Analytics tracking if
 * present on the application's main layout.
 * <p>
 * Any navigation event will be tracked as a page view unless the @{@link Route}
 * class is also annotated with @{@link IgnorePageView}. It is possible to
 * manually track page views or other events through the tracker instance
 * available through {@link GoogleAnalyticsTracker#getCurrent()} or
 * {@link GoogleAnalyticsTracker#get(UI)}.
 * <p>
 * This annotation only has properties for the most commonly used configuration
 * options. For low-level configuration or programmatic configuration, the
 * application's main layout can also implement {@link TrackerConfigurator}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EnableGoogleAnalytics {
    /**
     * The Google Analytics tracking ID to use.
     * 
     * @return the tracking ID
     */
    String value();

    /**
     * The cookie domain setting. By default, <code>auto</code> is used.
     * 
     * @return the cookie domain setting
     */
    String cookieDomain() default TrackerConfiguration.DEFAULT_COOKIE_DOMAIN;

    /**
     * Client-side log level to use when Vaadin is run in production mode. By
     * default, nothing is logged during production.
     * 
     * @return the production log level
     */
    LogLevel productionLogging() default LogLevel.NONE;

    /**
     * Client-side log level to use when Vaadin is not run in production mode. By
     * default, debug logging is used when production mode is not enabled.
     * 
     * @return the non-production log level
     */
    LogLevel devLogging() default LogLevel.DEBUG;

    /**
     * Mode to determine whether page views and events should actually be sent
     * to Google Analytics. By default, sending is enabled only when Flow is run
     * in production mode.
     * 
     * @return the used send mode
     */
    SendMode sendMode() default SendMode.PRODUCTION;

    /**
     * A prefix to add to the URL of all page views. By default, not prefix is
     * added.
     * 
     * @return the prefix to add to page view URLs
     */
    String pageviewPrefix() default "";

    /**
     * Client-side log levels that can be configured through
     * {@link EnableGoogleAnalytics#productionLogging()} and
     * {@link EnableGoogleAnalytics#devLogging()}.
     */
    public enum LogLevel {
        /**
         * Log nothing to the client-side JavaScript console. This is the
         * default setting when production mode is enabled in Flow.
         */
        NONE {
            @Override
            public void apply(TrackerConfiguration config) {
                // Nothing to do
            }
        },
        /**
         * Log basic information to the client-side JavaScript console. This is
         * the default setting when production mode is not enabled in Vaadin.
         */
        DEBUG {
            @Override
            public void apply(TrackerConfiguration config) {
                config.setGaDebug("debug_mode", Boolean.TRUE);
            }
        },
        /**
         * Log all available information to the client-side JavaScript console.
         */
        TRACE {
            @Override
            public void apply(TrackerConfiguration config) {
                LogLevel.DEBUG.apply(config);
                config.setGaDebug("trace", Boolean.TRUE);
            }
        };

        /**
         * Applies this log level to the given tracker configuration object.
         * 
         * @param config
         *            the configuration object to update, not <code>null</code>
         */
        public abstract void apply(TrackerConfiguration config);
    }

    /**
     * Send mode settings that can be configured through
     * {@link EnableGoogleAnalytics#sendMode()}.
     */
    public enum SendMode {
        /**
         * Always send tracked page views and events to Google Analytics.
         */
        ALWAYS {
            @Override
            public boolean shouldSend(boolean productionMode) {
                return true;
            }
        },
        /**
         * Send tracked page views and events to Google Analytics only when Vaadin
         * is run in production mode. This is the default setting.
         */
        PRODUCTION {
            @Override
            public boolean shouldSend(boolean productionMode) {
                return productionMode;
            }
        },
        /**
         * Never send tracked page views and events to Google Analytics.
         */
        NEVER {
            @Override
            public boolean shouldSend(boolean productionMode) {
                return false;
            }
        };
        /**
         * Checks whether sending should be enabled for the given production
         * mode setting.
         * 
         * @param productionMode
         *            the production mode setting
         * @return whether sending should be enabled
         */
        public abstract boolean shouldSend(boolean productionMode);
    }
}
