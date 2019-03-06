package org.vaadin.googleanalytics.tracking.demo;

import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;
import org.vaadin.googleanalytics.tracking.IgnorePageView;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(layout = MainLayout.class)
@PageTitle("Ignored view")
@IgnorePageView
public class IgnoredView extends VerticalLayout
        implements AfterNavigationObserver {
    public IgnoredView() {
        add(new Text("Ignored view"));
    }

    @Override
    public void afterNavigation(AfterNavigationEvent event) {
        GoogleAnalyticsTracker.getCurrent().sendPageView("custom/location");
    }
}
