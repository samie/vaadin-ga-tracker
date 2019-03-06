package org.vaadin.googleanalytics.tracking;

/**
 * Programmatically configures a Google Analytics tracker if implemented by the
 * application's main router layout.
 */
public interface TrackerConfigurator {
    /**
     * Configure a tracker by updating the provided configuration.
     * 
     * @param configuration
     *            the configuration to update, not <code>null</code>
     */
    void configureTracker(TrackerConfiguration configuration);
}
