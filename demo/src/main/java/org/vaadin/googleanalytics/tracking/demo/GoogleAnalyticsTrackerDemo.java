package org.vaadin.googleanalytics.tracking.demo;

import com.vaadin.annotations.VaadinServletConfiguration;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.UI;
import javax.servlet.annotation.WebServlet;

public class GoogleAnalyticsTrackerDemo extends UI {

    private static final long serialVersionUID = 1L;

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = GoogleAnalyticsTrackerDemo.class)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {

        // Create a tracker for vaadin.com domain and "ga-demo" prefix
        GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(
                "UA-658457-8", "none", "/ga-demo/");
        
        // Use this if you still haven't upgraded to Universal tracking API
        // tracker.setUniversalTracking(false);

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
