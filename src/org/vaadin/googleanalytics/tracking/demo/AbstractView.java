/*
 * Copyright 2012 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.vaadin.googleanalytics.tracking.demo;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

public abstract class AbstractView extends VerticalLayout implements View {

    private Map<String, Link> navigationLinks = new HashMap<String, Link>();
    private String[] viewNames = new String[] { "", "second", "third" };
    private Label label = new Label();

    public AbstractView() {
        addComponent(label);
        for (String viewName : viewNames) {
            Link link = new Link("Go to " + viewName, new ExternalResource("#!"
                    + viewName));
            navigationLinks.put(viewName, link);
            addComponent(link);

        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        String oldViewString = "";
        for (String viewName : viewNames) {
            boolean enabled = !(viewName.equals(event.getViewName()));
            navigationLinks.get(viewName).setEnabled(enabled);
        }

        View oldView = event.getOldView();
        if (oldView != null) {
            oldViewString = " from the " + oldView.getClass().getSimpleName()
                    + " view";
        }
        label.setValue("Welcome to the " + getClass().getSimpleName() + " view"
                + oldViewString);
    }
}
