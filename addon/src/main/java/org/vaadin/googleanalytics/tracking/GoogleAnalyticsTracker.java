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
import com.vaadin.flow.shared.ui.LoadMode;

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
public class GoogleAnalyticsTracker implements Serializable {

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
    private final List<Action> pendingActions = new ArrayList<>();

    /**
     * A queued gtag call: the positional arguments (command + field values) and an optional fields
     * object. {@link #sendAction} serializes the fields to JSON and hands it to the browser as a
     * single trailing {@code String} param to {@code JSON.parse} — no Vaadin-internal JSON codec,
     * identical behaviour on Vaadin 24 and 25.
     */
    private record Action(Serializable[] args, Map<String, Serializable> fields) implements Serializable {
    }

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
                .executeJs("window.dataLayer = window.dataLayer || []; window.gtag = function() { window.dataLayer.push(arguments); } ; window.gtag('js', new Date()); console.log('Loaded Vaadin GA'); ");


        Map<String, Serializable> createFields = new HashMap<>(config.getCreateFields());
        Map<String, Serializable> initialValues = config.getInitialValues();
        if (!initialValues.isEmpty()) {
            createFields.putAll(initialValues);
        }

        Map<String, Serializable> gaDebug = config.getGaDebug();
        if (!gaDebug.isEmpty()) {
            createFields.putAll(gaDebug);
            // Todo: ga_debug is legacy, not sure if that is needed any more with GA4
            ui.getPage().executeJs("window.ga_debug = JSON.parse($0);", toJsonString(gaDebug));
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

    private void sendAction(Action action) {
        Serializable[] args = action.args();
        Map<String, Serializable> fields = action.fields();
        /*
         * Append prefix for page views. This is done in the send phase so that
         * the prefix is considered also if the page view was created before the
         * prefix was read from the config.
         */
        if (!pageViewPrefix.isEmpty() && fields != null
                && fields.get("page_location") instanceof String location) {
            fields.put("page_location", pageViewPrefix + location);
        }

        String fieldsJson = toJsonString(fields);
        if (fieldsJson == null) {
            ui.getPage().executeJs("window.gtag.apply(null, arguments)", args);
        } else {
            // The fields object is passed as a single trailing JSON string; the browser parses it
            // back into a real object before applying gtag. No server-side Vaadin-internal JSON codec
            // is involved, so this works identically on Vaadin 24 and 25.
            ui.getPage().executeJs(
                    "const x = Array.prototype.slice.call(arguments);"
                            + "x.push(JSON.parse(x.pop()));"
                            + "if (window.Vaadin && window.Vaadin.developmentMode) console.log(x);"
                            + "window.gtag.apply(null, x);",
                    append(args, fieldsJson));
        }
    }

    private static Serializable[] append(Serializable[] arr, Serializable tail) {
        Serializable[] out = new Serializable[arr.length + 1];
        System.arraycopy(arr, 0, out, 0, arr.length);
        out[arr.length] = tail;
        return out;
    }

    private static Action createAction(String command, Map<String, Serializable> fieldsObject,
                                       Serializable... fields) {
        if (fields == null) {
            fields = new Serializable[] { null };
        }

        Serializable[] args = Stream.concat(Stream.of(command), Stream.of(fields))
                .toArray(Serializable[]::new);
        Map<String, Serializable> copy = fieldsObject == null || fieldsObject.isEmpty()
                ? null : new HashMap<>(fieldsObject);
        return new Action(args, copy);
    }

    /**
     * Serializes a flat gtag fields map to a JSON string, with no JSON library dependency. gtag field
     * objects are flat maps of strings, numbers and booleans, which is all this handles — numbers and
     * booleans are emitted bare, everything else as a quoted (and escaped) string.
     */
    private static String toJsonString(Map<String, ? extends Serializable> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<String, ? extends Serializable> e : map.entrySet()) {
            if (!first) {
                sb.append(',');
            }
            first = false;
            writeJsonString(sb, e.getKey());
            sb.append(':');
            writeJsonValue(sb, e.getValue());
        }
        return sb.append('}').toString();
    }

    private static void writeJsonValue(StringBuilder sb, Serializable value) {
        if (value == null) {
            sb.append("null");
        } else if (value instanceof Number || value instanceof Boolean) {
            if (value instanceof Double doubleValue && !Double.isFinite(doubleValue)
                    || value instanceof Float floatValue && !Float.isFinite(floatValue)) {
                throw new IllegalArgumentException("JSON numbers must be finite");
            }
            sb.append(value);
        } else {
            writeJsonString(sb, value.toString());
        }
    }

    private static void writeJsonString(StringBuilder sb, String s) {
        sb.append('"');
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '"': sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                case '\b': sb.append("\\b"); break;
                case '\f': sb.append("\\f"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        sb.append('"');
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
        if (fieldsObject != null) {
            UI ui = UI.getCurrent();
            fieldsObject.put("vaadin_ui", ui != null? ui.getUIId() : -1);
        }
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
