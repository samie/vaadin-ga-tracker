package org.vaadin.googleanalytics.tracking;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import org.vaadin.googleanalytics.tracking.EnableGoogleAnalytics.LogLevel;
import org.vaadin.googleanalytics.tracking.EnableGoogleAnalytics.SendMode;

import com.vaadin.flow.component.ComponentUtil;
import com.vaadin.flow.component.HasElement;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.internal.JsonCodec;
import com.vaadin.flow.internal.JsonUtils;
import com.vaadin.flow.shared.ui.LoadMode;

import elemental.json.JsonObject;

/**
 * Sends commands to Google Analytics in the browser. An instance of the tracker
 * can be retrieved from a given UI instance ({@link #get(UI)}) or for the
 * current UI instance ({@link #getCurrent()}).
 * <p>
 * Page view commands will automatically be sent for any navigation if the
 * tracker can be configured.
 * <p>
 * The first time any command is sent, the tracker will configure itself based
 * on the top-level router layout in the corresponding UI. The layout should be
 * annotated with @{@link EnableGoogleAnalytics} or implement
 * {@link TrackerConfigurator} for the configuration to succeed.
 */
public class GoogleAnalyticsTracker {
    private final UI ui;

    private boolean inited = false;

    private String pageViewPrefix = "";

    /**
     * List of actions to send before the next response is created.
     * Initialization can only happen after routing has completed since the
     * top-level layout can only be identified at that point. This queue is only
     * needed for actions that are issues before initialization has happened,
     * but it is still used in all cases to keep the internal logic simpler.
     */
    private ArrayList<Serializable[]> pendingActions = new ArrayList<>();

    private GoogleAnalyticsTracker(UI ui) {
        this.ui = ui;
    }

    /**
     * Gets or creates a tracker for the current UI.
     * 
     * @see UI#getCurrent()
     * 
     * @return the tracker for the current UI, or <code>null</code> if there is
     *         no current UI
     */
    public static GoogleAnalyticsTracker getCurrent() {
        UI ui = UI.getCurrent();
        if (ui == null) {
            return null;
        }
        return get(ui);
    }

    /**
     * Gets or creates a tracker for the given UI.
     * 
     * @param ui
     *            the UI for which to get at tracker, not <code>null</code>
     * @return the tracker for the given ui
     */
    public static GoogleAnalyticsTracker get(UI ui) {
        GoogleAnalyticsTracker tracker = ComponentUtil.getData(ui, GoogleAnalyticsTracker.class);
        if (tracker == null) {
            tracker = new GoogleAnalyticsTracker(ui);
            ComponentUtil.setData(ui, GoogleAnalyticsTracker.class, tracker);
        }
        return tracker;
    }

    private void init() {
        TrackerConfiguration config = createConfig(ui);

        if (config == null) {
            throw new IllegalStateException(
                    "There are pending actions for a tracker that isn't initialized and cannot be initialized automatically. Ensure there is a @"
                            + EnableGoogleAnalytics.class.getSimpleName()
                            + " on the application's main layout or that it implements "
                            + TrackerConfigurator.class.getSimpleName() + ".");
        }

        String trackingId = config.getTrackingId();
        if (trackingId == null || trackingId.isEmpty()) {
            throw new IllegalStateException("No tracking id has been defined.");
        }

        pageViewPrefix = config.getPageViewPrefix();

        ui.getPage()
                .executeJavaScript("window.dataLayer = window.dataLayer || []; window.gtag = function() { window.dataLayer.push(arguments); } ; window.gtag('js', new Date()); console.log('Loaded Vaadin GA'); ");


        Map<String, Serializable> createFields = new HashMap<>(config.getCreateFields());
        Map<String, Serializable> initialValues = config.getInitialValues();
        if (!initialValues.isEmpty()) {
            createFields.putAll(initialValues);
        }

        Map<String, Serializable> gaDebug = config.getGaDebug();
        if (!gaDebug.isEmpty()) {
            createFields.putAll(gaDebug);
            // Todo: ga_debug is legacy, not sure if that is needed any more with GA4
            ui.getPage().executeJavaScript("window.ga_debug = $0;", toJsonObject(gaDebug));
        }

        sendAction(createAction("config",createFields , trackingId));

        ui.getPage().addJavaScript(config.getScriptUrl(), LoadMode.EAGER);

        inited = true;
    }

    private static TrackerConfiguration createConfig(UI ui) {
        TrackerConfiguration config = null;

        HasElement routeLayout = findRouteLayout(ui);
        boolean productionMode = ui.getSession().getConfiguration().isProductionMode();

        EnableGoogleAnalytics annotation = routeLayout.getClass().getAnnotation(EnableGoogleAnalytics.class);

        if (annotation != null) {
            config = TrackerConfiguration.fromAnnotation(annotation, productionMode);
        }

        if (routeLayout instanceof TrackerConfigurator) {
            if (config == null) {
                // Use same defaults as in the annotation
                LogLevel logLevel = productionMode ? LogLevel.NONE : LogLevel.DEBUG;
                boolean sendHits = SendMode.PRODUCTION.shouldSend(productionMode);

                config = TrackerConfiguration.create(logLevel, sendHits);
            }

            ((TrackerConfigurator) routeLayout).configureTracker(config);
        }

        return config;
    }

    private static HasElement findRouteLayout(UI ui) {
        List<HasElement> routeChain = ui.getInternals().getActiveRouterTargetsChain();
        if (routeChain.isEmpty()) {
            throw new IllegalStateException("Cannot initialize when no router target is active");
        }
        return routeChain.get(routeChain.size() - 1);
    }

    private void sendAction(Serializable[] action) {
        /*
         * Append prefix for page views. This is done in the send phase so that
         * the prefix is considered also if the page view was created before the
         * prefix was read from the config.
         */
        if (!pageViewPrefix.isEmpty()) {
            // ["set", "page", location]
            if (action.length == 3 && "set".equals(action[0]) && "page_location".equals(action[1])) {
                action[2] = pageViewPrefix + action[2];
            }
        }

        ui.getPage().executeJavaScript("if (Vaadin.developmentMode) console.log(arguments); window.gtag.apply(null, arguments)", action);
    }

    private static Serializable[] createAction(String command, Map<String, Serializable> fieldsObject,
            Serializable... fields) {
        if (fields == null) {
            fields = new Serializable[] { null };
        }

        // [command, fields...]
        Stream<Serializable> argsStream = Stream.concat(Stream.of(command), Stream.of(fields));
        if (fieldsObject != null && !fieldsObject.isEmpty()) {
            // [command, fields..., fieldsObject]
            argsStream = Stream.concat(argsStream, Stream.of(toJsonObject(fieldsObject)));
        }

        return argsStream.toArray(Serializable[]::new);
    }

    private static JsonObject toJsonObject(Map<String, ? extends Serializable> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        return JsonUtils.createObject(map, JsonCodec::encodeWithoutTypeInfo);
    }

    /**
     * Sends a generic command to Google Analytics. This corresponds to a
     * client-side call to the <code>ga</code> function except that fieldsObject
     * is not the last parameter because of the way varargs work in Java.
     * 
     * @param command
     *            the name of the command to send, not <code>null</code>
     * @param fieldsObject
     *            a map of additional fields, or <code>null</code> to to not
     *            send any additional fields
     * @param fields
     *            a list of field values to send
     */
    public void ga(String command, Map<String,Serializable> fieldsObject, Serializable... fields) {
        if (pendingActions.isEmpty()) {
            ui.beforeClientResponse(ui, context -> {
                if (!inited) {
                    init();
                }

                pendingActions.forEach(this::sendAction);
                pendingActions.clear();
            });
        }

        pendingActions.add(createAction(command, fieldsObject, fields));
    }

    /**
     * Sends a page view command to Google Analytics.
     * 
     * @param location
     *            the location of the viewed page, not <code>null</code>
     */
    public void sendPageView(String location) {
        sendPageView(location, null);
    }

    /**
     * Sends a page view command with arbitrary additional fields to Google
     * Analytics. See <a href=
     * "https://developers.google.com/analytics/devguides/collection/analyticsjs/tracker-object-reference#send">the
     * reference documentation</a> for more information about supported
     * additional fields.
     * 
     * @param location
     *            the location of the viewed page, not <code>null</code>
     * @param fieldsObject
     *            map of additional fields to include in the <code>send</code>
     *            command
     */
    public void sendPageView(String location, Map<String, Serializable> fieldsObject) {
        if (fieldsObject == null) {
            fieldsObject = new HashMap<>();
        }
        if (!fieldsObject.containsKey("page_location")) {
            fieldsObject.put("page_location", location);
        }
        ga("event", fieldsObject,"page_view");
    }

    /**
     * Sends an event command with the given category and action. See <a href=
     * "https://developers.google.com/analytics/devguides/collection/analyticsjs/tracker-object-reference#send">the
     * reference documentation</a> for information about the semantics of the
     * parameters.
     * 
     * @param groupId
     *            the category name, not <code>null</code>
     * @param eventName
     *            the action name, not <code>null</code>
     */
    public void sendEvent(String groupId, String eventName) {
        Map<String, Serializable> fields = new HashMap<>();
        fields.put("group_id", groupId);
        fields.put("event_name", eventName);
        ga("event", fields, eventName);
    }

    /**
     * Sends an event command with the given category, action and label. See
     * <a href=
     * "https://developers.google.com/analytics/devguides/collection/analyticsjs/tracker-object-reference#send">the
     * reference documentation</a> for information about the semantics of the
     * parameters.
     * 
     * @param category
     *            the category name, not <code>null</code>
     * @param action
     *            the action name, not <code>null</code>
     * @param label
     *            the event label, not <code>null</code>
     */
    public void sendEvent(String category, String action, String label) {
        ga("event", null,  category, action, label);
    }

    /**
     * Sends an event command with the given category, action, label and value.
     * See <a href=
     * "https://developers.google.com/analytics/devguides/collection/analyticsjs/tracker-object-reference#send">the
     * reference documentation</a> for information about the semantics of the
     * parameters.
     * 
     * @param category
     *            the category name, not <code>null</code>
     * @param action
     *            the action name, not <code>null</code>
     * @param label
     *            the event label, not <code>null</code>
     * @param value
     *            the event value
     */
    public void sendEvent(String category, String action, String label, int value) {
        ga("event", null, category, action, label, Integer.valueOf(value));
    }

    /**
     * Sends an event command with the given category, action and arbitrary
     * additional fields. See <a href=
     * "https://developers.google.com/analytics/devguides/collection/analyticsjs/tracker-object-reference#send">the
     * reference documentation</a> for information about the semantics of the
     * parameters.
     * 
     * @param category
     *            the category name, not <code>null</code>
     * @param action
     *            the action name, not <code>null</code>
     * @param fieldsObject field object
     */
    public void sendEvent(String category, String action, Map<String, Serializable> fieldsObject) {
        ga("event", fieldsObject, category, action);
    }

    /**
     * Checks whether this tracker has been initialized.
     * 
     * @return <code>true</code> if this tracker is initialized, otherwise
     *         <code>false</code>
     */
    public boolean isInitialized() {
        return inited;
    }
}
