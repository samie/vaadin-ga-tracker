package org.vaadin.googleanalytics.tracking;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Indicates that a page view should not be automatically sent when navigating
 * to a route target. The annotation can be placed either not the route target
 * itself or on any router layout parent.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface IgnorePageView {
    // Empty marker annotation
}
