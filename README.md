Google Analytics Tracker extension for Vaadin 7
=================



[example](http://url.com/ "Title")

Sample application
------------------

GoogleAnalyticsTracker class is used as an extension to Vaadin UI class. 
It can be also be bound to the Navigator to allow automatic tacking of View changes.

Full demo source code available [here](https://github.com/samie/vaadin-ga-tracker/tree/master/src/org/vaadin/googleanalytics/tracking/demo "Demo Source").

 package org.vaadin.googleanalytics.tracking.demo;

 import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

 import com.vaadin.navigator.Navigator;
 import com.vaadin.server.VaadinRequest;
 import com.vaadin.ui.UI;

 public class GoogleAnalyticsTrackerDemo extends UI {
    private static final long serialVersionUID = 1L;

    @Override
    protected void init(VaadinRequest request) {

        // Create a tracker for vaadin.com domain.
        GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(
                "UA-658457-8");

        // Example: Create a tracker for vaadin.com domain.
        // GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(
        // "UA-658457-8", "vaadin.com");

        // attach the GA tracker to this UI
        tracker.extend(this);

        // simple view navigator sample
        Navigator n = new Navigator(this, this);
        n.addView("", MainView.class);
        n.addView("second", SecondView.class);
        n.addView("third", ThirdView.class);

        // attach the tracker to the Navigator to automatically track all views
        // To use the tracker without the Navigator, just call the
        // tracker.trackPageview(pageId) separately when tracking is needed.
        getNavigator().addViewChangeListener(tracker);

    }
 }