package org.vaadin.googleanalytics.tracking;

import java.util.List;
import java.util.Objects;

import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

/**
 * Automatically registers a navigation listener that sends page views to Google
 * Analytics.
 */
public class InitListener implements VaadinServiceInitListener {
    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiInit -> {
            UI ui = uiInit.getUI();

            ui.addAfterNavigationListener(navigationEvent -> {
                GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.get(ui);

                if (shouldTrack(tracker, navigationEvent)) {
                    tracker.sendPageView(navigationEvent.getLocation().getPathWithQueryParameters());
                }
            });
        });
    }

    private static boolean shouldTrack(GoogleAnalyticsTracker tracker, AfterNavigationEvent navigationEvent) {
        if (hasIgnore(navigationEvent)) {
            return false;
        }

        /*
         * Track if tracker is already initialized or if it can be initialized
         * based on the current navigation event.
         */
        return tracker.isInitialized() || canInitialize(navigationEvent);
    }

    private static boolean canInitialize(AfterNavigationEvent navigationEvent) {
        List<HasElement> routerChain = navigationEvent.getActiveChain();
        if (routerChain.isEmpty()) {
            return false;
        }

        Class<? extends HasElement> rootLayoutClass = getRootLayout(routerChain);

        return rootLayoutClass.getAnnotation(EnableGoogleAnalytics.class) != null
                || TrackerConfigurator.class.isAssignableFrom(rootLayoutClass);
    }

    private static Class<? extends HasElement> getRootLayout(List<HasElement> routerChain) {
        return routerChain.get(routerChain.size() - 1).getClass();
    }

    private static boolean hasIgnore(AfterNavigationEvent navigationEvent) {
        return navigationEvent.getActiveChain().stream().anyMatch(InitListener::hasIgnoreAnnotation);
    }

    private static boolean hasIgnoreAnnotation(HasElement target) {
        return target.getClass().getAnnotation(IgnorePageView.class) != null;
    }
}
