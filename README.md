[![Published on Vaadin  Directory](https://img.shields.io/badge/Vaadin%20Directory-published-00b4f0.svg)](https://vaadin.com/directory/component/googleanalyticstracker)
[![Stars on Vaadin Directory](https://img.shields.io/vaadin-directory/star/googleanalyticstracker.svg)](https://vaadin.com/directory/component/googleanalyticstracker)
[![Latest version](https://img.shields.io/vaadin-directory/v/googleanalyticstracker.svg)](https://vaadin.com/directory/component/googleanalyticstracker)

# Google analytics tracking integration for Vaadin 25+

Sources for other Vaadin versions are in their own branches:
* [Vaadin 6](https://github.com/samie/vaadin-ga-tracker/tree/vaadin6)
* [Vaadin 7](https://github.com/samie/vaadin-ga-tracker/tree/vaadin7)
* [Vaadin 8](https://github.com/samie/vaadin-ga-tracker/tree/vaadin8)
* [Vaadin 10-23](https://github.com/samie/vaadin-ga-tracker/tree/vaadin10)
* [Vaadin 24](https://github.com/samie/vaadin-ga-tracker/tree/v24)

Track your Vaadin application usage using Google Analytics.

## Usage

There are two ways to enable the tracker in your Vaadin application.

### 1) Static setup with `@EnableGoogleAnalytics`

Annotate your top-level router layout (typically your main layout) with your
Google Analytics Measurement ID.

```java
import org.vaadin.googleanalytics.tracking.EnableGoogleAnalytics;

@EnableGoogleAnalytics(value = "G-XXXXXXXXXX", pageviewPrefix = "my-app")
public class MainLayout extends VerticalLayout implements RouterLayout {
}
```

Useful annotation options:
- `value`: your GA measurement ID (required)
- `pageviewPrefix`: prefix added to tracked page locations
- `sendMode`: `ALWAYS`, `PRODUCTION` (default), or `NEVER`

With this setup, page views are automatically sent on navigation.

### 2) Dynamic setup with `TrackerConfigurator`

Implement `TrackerConfigurator` in your top-level router layout and define the
tracker programmatically (including the tracking ID).

```java
import org.vaadin.googleanalytics.tracking.TrackerConfiguration;
import org.vaadin.googleanalytics.tracking.TrackerConfigurator;

public class MainLayout extends VerticalLayout
        implements RouterLayout, TrackerConfigurator {

    @Override
    public void configureTracker(TrackerConfiguration configuration) {
        configuration.setTrackingId("G-XXXXXXXXXX");
        configuration.setCreateField("send_page_view", Boolean.FALSE);
        configuration.setInitialValue("transport_type", "beacon");
    }
}
```

You can also send events and page views manually when needed:

```java
GoogleAnalyticsTracker tracker = GoogleAnalyticsTracker.getCurrent();
if (tracker != null) {
    tracker.sendEvent("signup", "click");
    tracker.sendPageView("/custom-step");
}
```

Runtime toggle one-liners (GA opt-out / opt-in for current browser):

```java
UI.getCurrent().getPage().executeJs("window['ga-disable-' + $0] = true;", "G-XXXXXXXXXX");
UI.getCurrent().getPage().executeJs("window['ga-disable-' + $0] = false;", "G-XXXXXXXXXX");
```

The demo app includes a ready-made route at `/toggle-tracking` for disabling
and enabling tracking with buttons.

To skip automatic page view tracking for a specific route, annotate that route
or its parent layout with `@IgnorePageView`.

## Download release

Official releases of this add-on are available at Vaadin Directory. For Maven instructions, download and reviews, go to https://vaadin.com/directory/component/googleanalyticstracker

## Building and running demo

```bash
git clone https://github.com/samie/vaadin-ga-tracker.git
cd vaadin-ga-tracker
mvn clean install
cd demo
mvn jetty:run
```

To see the demo, navigate to http://localhost:8080/
