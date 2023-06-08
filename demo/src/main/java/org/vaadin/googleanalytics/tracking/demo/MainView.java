package org.vaadin.googleanalytics.tracking.demo;

import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(layout = MainLayout.class)
@PageTitle("Main view")
public class MainView extends VerticalLayout {
    public MainView() {
        add(new Text("Main view"),
                new Button("Send an event", click -> {
                    GoogleAnalyticsTracker.getCurrent().sendEvent("myevent",
                            "Event button");
                }),
                new Button("Send an page view", click -> {
                    GoogleAnalyticsTracker.getCurrent().sendPageView("/mypage");
                }));
    }
}
