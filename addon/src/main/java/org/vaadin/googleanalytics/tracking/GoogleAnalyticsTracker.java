package org.vaadin.googleanalytics.tracking;

import com.vaadin.annotations.JavaScript;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.ui.UI;

/**
 * Component for triggering Google Analytics page views. Usage:
 *
 * <pre>
 * GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker("UA-658457-8", "vaadin.com");
 * tracker.extend(myUI);
 *   ....
 * tracker.trackPageview("/samplecode/googleanalytics");
 * </pre>
 *
 * To connect it to a Navigator to automatically track page views, you can do
 *
 * <pre>
 * GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(&quot;UA-658457-8&quot;,
 *         &quot;vaadin.com&quot;);
 * tracker.extend(myUI);
 * myUI.getNavigator().addViewChangeListener(tracker);
 * </pre>
 *
 * @author Sami Ekblad / Marc Englund / Artur Signell
 */
@JavaScript("tracker_extension.js")
public class GoogleAnalyticsTracker extends AbstractJavaScriptExtension
        implements ViewChangeListener {

    private static final long serialVersionUID = 1L;

    private String trackingPrefix = "";

    /**
     * Get the value of tracking prefix used in all page track calls.
     * 
     * Tracking prefix in added before all page ids passed to trackPageview(id) methods.
     * @see #trackPageview(java.lang.String) 
     *
     * 
     * @return the value of trackingPrefix
     */
    public String getTrackingPrefix() {
        return trackingPrefix;
    }

    /**
     * Set the value of tracking prefix used in all trackPageview calls.
     * 
     * Tracking prefix in added before all page ids passed to trackPageview(id) methods.
     * @see #trackPageview(java.lang.String) 
     * @param trackingPrefix Prefix added to all trackPageview calls.
     */
    public void setTrackingPrefix(String trackingPrefix) {
        this.trackingPrefix = trackingPrefix;
    }
    
    /**
     * Instantiate new Google Analytics tracker without id. Universal tracker is
     * created by default.
     */
    public GoogleAnalyticsTracker() {
    }

    /**
     * Instantiate new Google Analytics tracker by id.
     *
     * @param trackerId The tracking id from Google Analytics. Something like
     * 'UA-658457-8'. Universal tracker is created by default.
     */
    public GoogleAnalyticsTracker(String trackerId) {
        setTrackerId(trackerId);
    }

    /**
     * Instantiate new Google Analytics tracker by id and domain.
     *
     * @param trackerId The tracking id from Google Analytics. Something like
     * 'UA-658457-8'.
     * @param domainName The name of the domain to be tracked. Something like
     * 'vaadin.com'. Universal tracker is created by default.
     */
    public GoogleAnalyticsTracker(String trackerId, String domainName) {
        this(trackerId);
        setDomainName(domainName);
    }
    
        /**
     * Instantiate new Google Analytics tracker by id and domain.
     *
     * @param trackerId The tracking id from Google Analytics. Something like
     * 'UA-658457-8'.
     * @param domainName The name of the domain to be tracked. Something like
     * 'vaadin.com'. Universal tracker is created by default.
     */
    public GoogleAnalyticsTracker(String trackerId, String domainName, String trackingPrefix) {
        this(trackerId);
        setDomainName(domainName);
        setTrackingPrefix(trackingPrefix);
    }


    /**
     * Get the domain name associated with this tracker.
     *
     * @return The domain name
     */
    public String getDomainName() {
        return getState().domainName;
    }

    /**
     * Sets the domain name you are tracking
     *
     * @param domainName The domain name
     */
    public void setDomainName(String domainName) {
        getState().domainName = domainName;

    }

    @Override
    protected GoogleAnalyticsTrackerState getState() {
        return (GoogleAnalyticsTrackerState) super.getState();
    }

    /**
     * Gets the Google Analytics tracking id.
     *
     * @return Tracking id like 'UA-658457-8'.
     */
    public String getTrackerId() {
        return getState().trackerId;
    }

    /**
     * Set the "Universal" tracking mode.
     *
     * Google Analytics update in 2014 introduced new Universal tracking API,
     * but the old GA tracking is still available.
     *
     * Since 2.1 this is the default.
     *
     * @param universalTracking true, if Universal tracking API should be used,
     * false, if old GA tracking API is used.
     */
    public void setUniversalTracking(boolean universalTracking) {
        getState().universalTracking = universalTracking;
    }

    /**
     * Set the "Universal" tracking mode.
     *
     * Google Analytics update in 2014 introduced new Universal tracking API,
     * but the old GA tracking is still available.
     *
     * Since 2.1 this is the default.
     *
     * @return true, if Universal tracking API should be used, false, if old GA
     * tracking API is used.
     */
    public boolean isUniversalTracking() {
        return getState().universalTracking;
    }

    /**
     * This method sets the # sign as the query string delimiter in campaign
     * tracking.
     *
     * https://developers.google.com/analytics/devguides/collection/gajs/methods/
     *
     * @param allowAnchor Are anchors allowed in URIs. This is the default.
     *
     */
    public void setAllowAnchor(boolean allowAnchor) {
        getState().allowAnchor = allowAnchor;
    }

    /**
     * This method sets the # sign as the query string delimiter in campaign
     * tracking.
     *
     * https://developers.google.com/analytics/devguides/collection/gajs/methods/
     *
     * @return true if anchors are allowed in the URIs
     */
    public boolean isAllowAnchor() {
        return getState().allowAnchor;
    }

    /**
     * Sets the Google Analytics tracking id.
     *
     * @param trackerId The tracking id like 'UA-586743-2'
     */
    public void setTrackerId(String trackerId) {
        getState().trackerId = trackerId;
    }

    /**
     * Track a single page view. This effectively invokes the 'trackPageview' in
     * ga.js file.
     *
     * @param pageId The page id. Use a scheme like '/topic/page' or
     * '/view/action'.
     */
    public void trackPageview(String pageId) {
        callFunction("track", trackingPrefix != null && trackingPrefix.length() > 0? trackingPrefix+pageId: pageId);
    }

    /**
     * Attach this analytics component to a UI to enable tracking
     *
     * @param target The UI to track
     */
    public void extend(UI target) {
        super.extend(target);
    }

    @Override
    public boolean beforeViewChange(ViewChangeEvent event) {
        return true;
    }

    @Override
    public void afterViewChange(ViewChangeEvent event) {
        trackPageview(event.getViewName());
    }
}
