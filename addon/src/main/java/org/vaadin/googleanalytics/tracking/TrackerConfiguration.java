package org.vaadin.googleanalytics.tracking;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import org.vaadin.googleanalytics.tracking.EnableGoogleAnalytics.LogLevel;

/**
 * Configuration for a Google Analytics tracker. By default, the configuration
 * is created based on an @{@link EnableGoogleAnalytics} annotation on the
 * application's outermost router layout class. The layout class can also
 * implement {@link TrackerConfigurator} to declaratively update the
 * configuration.
 */
public class TrackerConfiguration {
    /**
     * The default cookie domain value.
     */
    public static final String DEFAULT_COOKIE_DOMAIN = "auto";

    private String trackingId;
    private String cookieDomain = DEFAULT_COOKIE_DOMAIN;
    private String pageViewPrefix = "";
    private String scriptUrl = "https://www.googletagmanager.com/gtag/js";

    private final Map<String, Serializable> gaDebug = new LinkedHashMap<>();

    private final Map<String, Serializable> createParameters = new LinkedHashMap<>();

    private final Map<String, Serializable> initialValues = new LinkedHashMap<>();

    private TrackerConfiguration() {
        // Create through static factory methods
    }

    /**
     * Gets the Google Analytics tracking ID to use.
     * 
     * @return the tracking id, not <code>null</code>
     */
    public String getTrackingId() {
        return trackingId;
    }

    /**
     * Sets the Google Analytics tracking ID to use.
     * 
     * @see <a href=
     *      "https://developers.google.com/analytics/devguides/collection/analyticsjs/field-reference#trackingId">Reference
     *      documentation</a>
     * 
     * @param trackingId
     *            the tracking id to use, not <code>null</code> and not an empty
     *            string
     * @return this configuration, for chaining
     */
    public TrackerConfiguration setTrackingId(String trackingId) {
        if (trackingId == null || trackingId.trim().isEmpty()) {
            throw new IllegalArgumentException("Tracking id must be defined");
        }

        this.trackingId = trackingId;

        return this;
    }

    /**
     * Gets the cookie domain setting.
     * 
     * @return the cookie domain setting, not <code>null</code>
     * @deprecated Since GA4 Configure the cookie domains through analytics interface.
     */
    @Deprecated()
    public String getCookieDomain() {
        return cookieDomain;
    }

    /**
     * Sets the cookie domain value to use.
     * 
     * @see <a href=
     *      "https://developers.google.com/analytics/devguides/collection/analyticsjs/field-reference#cookieDomain">Reference
     *      documentation</a>
     * 
     * @param cookieDomain
     *            the cookie domain value to set, or <code>null</code> to
     *            restore the default value.
     * @return this configuration, for chaining
     * @deprecated Since GA4 Configure the cookie domains through analytics interface.
     */
    @Deprecated
    public TrackerConfiguration setCookieDomain(String cookieDomain) {
        this.cookieDomain = Objects.requireNonNull(cookieDomain);
        return this;
    }

    /**
     * Sets a prefix that will be added to the location of all tracked page
     * views.
     * 
     * @param pageViewPrefix
     *            a page view prefix to use, not <code>null</code>
     * @return this configuration, for chaining
     */
    public TrackerConfiguration setPageViewPrefix(String pageViewPrefix) {
        this.pageViewPrefix = Objects.requireNonNull(pageViewPrefix);
        return this;
    }

    /**
     * Gets the current page view prefix.
     * 
     * @return the current page view prefix, not <code>null</code>
     */
    public String getPageViewPrefix() {
        return pageViewPrefix;
    }

    /**
     * Sets the URL from which to load the Google Analytics script.
     * 
     * @param scriptUrl
     *            the script URL to use, not <code>null</code>
     * @return this configuration, for chaining
     */
    public TrackerConfiguration setScriptUrl(String scriptUrl) {
        this.scriptUrl = Objects.requireNonNull(scriptUrl);
        return this;
    }

    /**
     * Gets the URL from which to load the Google Analytics script.
     * 
     * @return scriptUrl
     */
    public String getScriptUrl() {
        return scriptUrl+"?id="+this.trackingId;
    }

    /**
     * Sets a custom field value to use when creating the client-side tracker.
     * 
     * @see <a href=
     *      "https://developers.google.com/analytics/devguides/collection/analyticsjs/field-reference">Reference
     *      documentation</a>
     * 
     * @param name
     *            the name of the field to set, not <code>null</code>
     * @param value
     *            the field value
     * @return this configuration, for chaining
     */
    public TrackerConfiguration setCreateField(String name, Serializable value) {
        createParameters.put(Objects.requireNonNull(name), value);
        return this;
    }

    /**
     * Removes a create field value.
     * 
     * @see #setCreateField(String, Serializable)
     * 
     * @param name
     *            the name of the field, not <code>null</code>
     * @return this configuration, for chaining
     */
    public TrackerConfiguration removeCreateField(String name) {
        createParameters.remove(Objects.requireNonNull(name));
        return this;
    }

    /**
     * Sets a value that will be included in an initial <code>set</code>
     * command.
     * 
     * @param name
     *            the name of the value, not <code>null</code>
     * @param value
     *            the value to set
     * @return this configuration, for chaining
     */
    public TrackerConfiguration setInitialValue(String name, Serializable value) {
        if (value != null) {
            initialValues.put(Objects.requireNonNull(name), value);
        } else {
            initialValues.remove(Objects.requireNonNull(name));
        }
        return this;
    }

    /**
     * Removes an initial value.
     * 
     * @see #setInitialValue(String, Serializable)
     * 
     * @param name
     *            the name of the value, not <code>null</code>
     * @return this configuration, for chaining
     */
    public TrackerConfiguration removeInitialValue(String name) {
        initialValues.remove(Objects.requireNonNull(name));
        return this;
    }

    /**
     * Adds an entry that should be assigned to the <code>ga_debug</code>
     * variable when the client-side tracker is initialized.
     * 
     * @param name
     *            the parameter name, not <code>null</code>
     * @param value
     *            the value
     * @return this configuration, for chaining
     */
    public TrackerConfiguration setGaDebug(String name, Serializable value) {
        gaDebug.put(Objects.requireNonNull(name), value);
        return this;
    }

    /**
     * Removes a debug parameter value.
     * 
     * @see #setGaDebug(String, Serializable)
     * 
     * @param name
     *            the parameter name, not <code>null</code>
     * @return this configuration, for chaining
     */
    public TrackerConfiguration removeGaDebug(String name) {
        gaDebug.remove(Objects.requireNonNull(name));
        return this;
    }

    /**
     * Gets all the custom fields to pass when creating the client-side tracker.
     * 
     * @see #setCreateField(String, Serializable)
     * 
     * @return an unmodifiable map of parameter values, not <code>null</code>
     */
    public Map<String, Serializable> getCreateFields() {
        return Collections.unmodifiableMap(createParameters);
    }

    /**
     * Gets the initial value to set after creating a client-side tracker.
     * 
     * @see #setInitialValue(String, Serializable)
     * 
     * @return an unmodifiable map of initial values, not <code>null</code>
     */
    public Map<String, Serializable> getInitialValues() {
        return Collections.unmodifiableMap(initialValues);
    }

    /**
     * Gets the <code>ga_debug</code> values to use when creating a clients-side
     * tracker.
     * 
     * @see #setGaDebug(String, Serializable)
     * 
     * @return an unmodifiable map of debug settings, not <code>null</code>
     */
    public Map<String, Serializable> getGaDebug() {
        return Collections.unmodifiableMap(gaDebug);
    }

    /**
     * Creates a tracker configuration with default settings based on a log
     * level and whether to actually enable sending commands to Google
     * Analytics.
     * 
     * @param logLevel
     *            the log level to use, not <code>null</code>
     * @param sendHits
     *            whether to send commands to Google Analytics
     * @return a newly created tracker configuration, not <code>null</code>
     */
    public static TrackerConfiguration create(LogLevel logLevel, boolean sendHits) {
        TrackerConfiguration config = new TrackerConfiguration();
        logLevel.apply(config);
        if (!sendHits) {
            config.setInitialValue("sendHitTask", null);
        }
        return config;
    }

    /**
     * Creates a tracker configuration based on annotation values.
     * 
     * @param annotation
     *            the configuration annotation to use
     * @param productionMode
     *            whether to configure for production mode
     * @return a newly created tracker configuration, not <code>null</code>
     */
    public static TrackerConfiguration fromAnnotation(EnableGoogleAnalytics annotation, boolean productionMode) {
        LogLevel logLevel = productionMode ? annotation.productionLogging() : annotation.devLogging();
        boolean shouldSend = annotation.sendMode().shouldSend(productionMode);

        TrackerConfiguration config = create(logLevel, shouldSend);

        config.setTrackingId(annotation.value());
        config.setCookieDomain(annotation.cookieDomain());
        config.setPageViewPrefix(annotation.pageviewPrefix());

        return config;
    }
}
