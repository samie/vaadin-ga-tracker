package org.vaadin.googleanalytics.tracking.demo;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(layout = MainLayout.class)
@PageTitle("Second view")
public class SecondView extends VerticalLayout {
    public SecondView() {
        add(new Text("Second view"));
    }
}