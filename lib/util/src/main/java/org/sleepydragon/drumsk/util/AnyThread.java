package org.sleepydragon.drumsk.util;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.CLASS;

/**
 * Denotes that the annotated method can be safely called by any thread, including the main thread.
 * If the annotated element is a class, then all methods in the class should be called
 * on the main thread.
 * <p>
 * Example:
 * <pre>{@code
 *  &#64;AnyThread
 *  public void dispatchResult(D data) { ... }
 * }</pre>
 */
@Retention(CLASS)
@Target({METHOD, CONSTRUCTOR, TYPE})
public @interface AnyThread {
}
