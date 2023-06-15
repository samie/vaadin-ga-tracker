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
                new Button("Send 'my_event'", click -> {
                    GoogleAnalyticsTracker.getCurrent().sendEvent("my_group",
                            "my_event");
                }),
                new Button("Send '/mypage' view", click -> {
                    GoogleAnalyticsTracker.getCurrent().sendPageView("/mypage");
                }));
    }
}
