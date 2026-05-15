package org.vaadin.googleanalytics.tracking.demo;

import org.vaadin.googleanalytics.tracking.EnableGoogleAnalytics;
import org.vaadin.googleanalytics.tracking.GoogleAnalyticsTracker;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "toggle-tracking", layout = MainLayout.class)
@PageTitle("Toggle tracking")
public class ToggleTrackingView extends VerticalLayout {
    private final String trackingId;
    private final Span status = new Span();

    public ToggleTrackingView() {

        // Get the trackingId from the MainLayout annotation
        EnableGoogleAnalytics analytics = MainLayout.class
                .getAnnotation(EnableGoogleAnalytics.class);
        trackingId = analytics != null ? analytics.value() : null;

        add(new Text("Toggle Google Analytics tracking at runtime."));

        Button disable = new Button("Disable tracking",
                click -> setTrackingEnabled(false));
        Button enable = new Button("Enable tracking",
                click -> setTrackingEnabled(true));
        Button sendEvent = new Button("Send test event", click -> {
            GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getCurrent();
            if (tracker != null) {
                tracker.sendEvent("demo", "toggle_tracking_test");
            }
        });

        add(disable, enable, sendEvent, status);
        setTrackingEnabled(true);
    }

    private void setTrackingEnabled(boolean enabled) {
        getUI().ifPresent(ui ->
                ui.getPage()
                        .executeJs("window['ga-disable-' + $0] = $1;", trackingId, !enabled));
        status.setText("Tracking is " + (enabled ? "enabled" : "disabled"));
    }
}

