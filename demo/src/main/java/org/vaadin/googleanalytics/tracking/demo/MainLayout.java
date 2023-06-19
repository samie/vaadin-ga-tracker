package org.vaadin.googleanalytics.tracking.demo;

import org.vaadin.googleanalytics.tracking.EnableGoogleAnalytics;
import org.vaadin.googleanalytics.tracking.TrackerConfiguration;
import org.vaadin.googleanalytics.tracking.TrackerConfigurator;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.router.RouterLink;

@EnableGoogleAnalytics(value = "G-69HHY4H0SJ", pageviewPrefix = "v24")
public class MainLayout extends VerticalLayout implements RouterLayout, TrackerConfigurator {
    public MainLayout() {
        add(new HorizontalLayout(new RouterLink("Main view", MainView.class),
                new RouterLink("Second view", SecondView.class), new RouterLink("Ignored view", IgnoredView.class)));
    }

    @Override
    public void configureTracker(TrackerConfiguration configuration) {
        configuration.setCreateField("send_page_view", Boolean.FALSE);
        configuration.setInitialValue("transport_type", "beacon");
    }
}
