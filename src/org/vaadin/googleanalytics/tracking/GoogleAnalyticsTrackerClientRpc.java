package org.vaadin.googleanalytics.tracking;

import com.vaadin.shared.communication.ClientRpc;

public interface GoogleAnalyticsTrackerClientRpc extends ClientRpc {
    public void track(String pageId);
}
