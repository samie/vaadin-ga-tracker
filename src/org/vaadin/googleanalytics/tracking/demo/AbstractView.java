package org.vaadin.googleanalytics.tracking.demo;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

/**
 * An abstract navigator view implementation showing a navigation links to all
 * the views.
 */
public abstract class AbstractView extends VerticalLayout implements View {

    private static final long serialVersionUID = 1L;

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
