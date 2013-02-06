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
@JavaScript({ "https://www.google-analytics.com/ga.js", "tracker_extension.js" })
public class GoogleAnalyticsTracker extends AbstractJavaScriptExtension
        implements ViewChangeListener {

    /**
     * Instantiate new Google Analytics tracker by id.
     */
    public GoogleAnalyticsTracker() {
    }

    /**
     * Instantiate new Google Analytics tracker by id.
     * 
     * @param trackerId
     *            The tracking id from Google Analytics. Something like
     *            'UA-658457-8'.
     */
    public GoogleAnalyticsTracker(String trackerId) {
        setTrackerId(trackerId);
    }

    /**
     * Instantiate new Google Analytics tracker by id and domain.
     * 
     * @param trackerId
     *            The tracking id from Google Analytics. Something like
     *            'UA-658457-8'.
     * @param domainName
     *            The name of the domain to be tracked. Something like
     *            'vaadin.com'.
     */
    public GoogleAnalyticsTracker(String trackerId, String domainName) {
        this(trackerId);
        setDomainName(domainName);
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
     * @param domainName
     *            The domain name
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
     * Sets the Google Analytics tracking id.
     * 
     * @param trackerId
     *            The tracking id like 'UA-586743-2'
     */
    public void setTrackerId(String trackerId) {
        getState().trackerId = trackerId;
    }

    /**
     * Track a single page view. This effectively invokes the 'trackPageview' in
     * ga.js file.
     * 
     * @param pageId
     *            The page id. Use a scheme like '/topic/page' or
     *            '/view/action'.
     */
    public void trackPageview(String pageId) {
        getRpcProxy(GoogleAnalyticsTrackerClientRpc.class).track(pageId);
    }

    /**
     * Attach this analytics component to a UI to enable tracking
     * 
     * @param target
     *            The UI to track
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