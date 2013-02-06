package org.vaadin.googleanalytics.tracking.demo;

import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class GoogleAnalyticsTrackerDemo extends UI {
    private static final long serialVersionUID = 1L;

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout vl = new VerticalLayout();
        setContent(vl);
        // A meaningless Label just to add some content
        Label label = new Label("Hello Vaadin user");
        vl.addComponent(label);

        // Create a tracker for vaadin.com domain.
        GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(
                "UA-658457-8", "vaadin.com");
        tracker.extend(this);

        // Track the page view
        tracker.trackPageview("/samplecode/googleanalytics");
    }

}