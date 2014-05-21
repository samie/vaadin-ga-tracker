package org.vaadin.googleanalytics.tracking;

import com.vaadin.shared.JavaScriptExtensionState;

public class GoogleAnalyticsTrackerState extends JavaScriptExtensionState {
    private static final long serialVersionUID = 1L;

    public boolean universalTracking = true;
    public String trackerId;
    public boolean allowAnchor = true;
    public String domainName  = "none";

}
