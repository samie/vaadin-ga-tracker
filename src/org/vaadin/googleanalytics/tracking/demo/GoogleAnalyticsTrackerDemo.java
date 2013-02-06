package org.vaadin.googleanalytics.tracking.demo;

import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import com.vaadin.navigator.Navigator;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class GoogleAnalyticsTrackerDemo extends UI {
    private static final long serialVersionUID = 1L;

    @Override
    protected void init(VaadinRequest request) {
        // Create a tracker for vaadin.com domain.
        GoogleAnalyticsTracker tracker = new GoogleAnalyticsTracker(
                "UA-658457-8", "vaadin.com");
        tracker.extend(this);

        VerticalLayout vl = new VerticalLayout();
        setContent(vl);
        // A meaningless Label just to add some content
        Label label = new Label("Hello Vaadin user");
        vl.addComponent(label);

        Navigator n = new Navigator(this, this);
        n.addView("", MainView.class);
        n.addView("second", SecondView.class);
        n.addView("third", ThirdView.class);

        // attach the tracker to the Navigator to automatically track all views
        getNavigator().addViewChangeListener(tracker);

        Link l = new Link("Navigate to second",
                new ExternalResource("#!second"));
        vl.addComponent(l);
    }
}