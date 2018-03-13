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
     *   Instantiate new Google Analytics tracker by id and domain.
     *
     * @param trackerId The tracking id from Google Analytics. Something like
     * 'UA-658457-8'.
     * @param domainName The name of the domain to be tracked. Something like
     * 'vaadin.com'. Universal tracker is created by default.
     * @param trackingPrefix Page id prefix to be used in all trackPageView calls, like "myapp/".
     *
     */
    public GoogleAnalyticsTracker(String trackerId, String domainName, String trackingPrefix) {
        this(trackerId);
        setDomainName(domainName);
        setTrackingPrefix(trackingPrefix);
    }

    /**
     * Instantiate new Google Analytics tracker by id and domain.
     *
     * @param trackerId The tracking id from Google Analytics. Like
     * 'UA-658457-8'.
     * @param domainName The name of the domain to be tracked. Like
     * 'vaadin.com'. Universal tracker is created by default.
     * @param userId a unique, persistent, and non-personally identifiable string ID.
     * @param trackingPrefix Page id prefix to be used in all trackPageView calls, like "myapp/".
	  */
	 public GoogleAnalyticsTracker(String trackerId, String domainName, String trackingPrefix, String userId) {
	     this(trackerId);
	     setDomainName(domainName);
	     setTrackingPrefix(trackingPrefix);
	     setUserId(userId);
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
	 * Sets the UserId to send to Google Analytics. The User-ID feature must be
	 * enabled within the Google Analytics admin for User-ID tracking to work.
	 * See the Google Analytics documentation for more information. User-ID
	 * tracking is only available if using the universal tracker.
	 *
	 * @param userId
	 *            a unique, persistent, and non-personally identifiable string
	 *            ID.
	 * @throws UnsupportedOperationException
	 *             when attempting to call this method and not using universal
	 *             tracking mode.
	 */
	public void setUserId(String userId) throws UnsupportedOperationException {
		if (!getState().universalTracking)
			throw new UnsupportedOperationException("User-ID tracking only supported when using universal tracking mode.");
		getState().userId = userId;
	}
    
    /**
     * Gets the User ID that was previously set.
     * 
     * @return User ID or null if not set.
     */
    public String getUserId() {
    	return getState().userId;
    }
    
    /**
     * Enables/disables the anonymizeIp feature.
     * 
     * https://developers.google.com/analytics/devguides/collection/analyticsjs/ip-anonymization
     * 
     * @param anonymizeIp is IP anonymization enabled (default is 'false')
     */
    public void setAnonymizeIp(boolean anonymizeIp) {
        getState().anonymizeIp = anonymizeIp;
    }
    
    /**
     * This method returns if IP anonymization is enabled (default is 'false')
     *
     * https://developers.google.com/analytics/devguides/collection/analyticsjs/ip-anonymization
     *
     * @return true if IP anonymization is enabled
     */
    public boolean isAnonymizeIp() {
        return getState().anonymizeIp;
    }
    
    /**
     * Track a single page view. This effectively invokes the 'trackPageview' in
     * ga.js file.
     *
     * @param pageId The page id. Use a scheme like '/topic/page' or
     * '/view/action'.
     */
    public void trackPageview(String pageId) {
        callFunction("trackPageView", trackingPrefix != null && trackingPrefix.length() > 0 ? trackingPrefix + pageId : pageId);
    }

    /**
     * Track an event. See the Google Analytics documentation for more information.
     * Event tracking is only available if using the universal tracker.
     * @see <a href="https://developers.google.com/analytics/devguides/collection/analyticsjs/events">Google Analytics documentation</a>
     *
     * @param eventCategory Typically the object that was interacted with (e.g. 'Video')
     * @param eventAction The type of interaction (e.g. 'play')
     *
     *
     * @throws UnsupportedOperationException
     *             when attempting to call this method and not using universal
     *             tracking mode.
     */
    public void trackEvent(String eventCategory, String eventAction) {
        if (!getState().universalTracking)
            throw new UnsupportedOperationException("Event tracking only supported when using universal tracking mode.");
        callFunction("trackEvent", eventCategory, eventAction);
    }

    /**
     * Track an event. See the Google Analytics documentation for more information.
     * Event tracking is only available if using the universal tracker.
     * @see <a href="https://developers.google.com/analytics/devguides/collection/analyticsjs/events">Google Analytics documentation</a>
     *
     * @param eventCategory Typically the object that was interacted with (e.g. 'Video')
     * @param eventAction The type of interaction (e.g. 'play')
     * @param eventLabel Useful for categorizing events (e.g. 'Fall Campaign'). Optional.
     *
     *
     * @throws UnsupportedOperationException
     *             when attempting to call this method and not using universal
     *             tracking mode.
     */
    public void trackEvent(String eventCategory, String eventAction, String eventLabel) {
        if (!getState().universalTracking)
            throw new UnsupportedOperationException("Event tracking only supported when using universal tracking mode.");
        callFunction("trackEvent", eventCategory, eventAction, eventLabel);
    }


    /**
     * Track an event. See the Google Analytics documentation for more information.
     * Event tracking is only available if using the universal tracker.
     * @see <a href="https://developers.google.com/analytics/devguides/collection/analyticsjs/events">Google Analytics documentation</a>
     *
     * @param eventCategory Typically the object that was interacted with (e.g. 'Video')
     * @param eventAction The type of interaction (e.g. 'play')
     * @param eventLabel Useful for categorizing events (e.g. 'Fall Campaign'). Optional.
     * @param eventValue A numeric value associated with the event (e.g. 42). Optional.
     *
     *
     * @throws UnsupportedOperationException
	 *             when attempting to call this method and not using universal
	 *             tracking mode.
     */
    public void trackEvent(String eventCategory, String eventAction, String eventLabel, int eventValue) {
        if (!getState().universalTracking)
			throw new UnsupportedOperationException("Event tracking only supported when using universal tracking mode.");
        callFunction("trackEvent", eventCategory, eventAction, eventLabel, eventValue);
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
