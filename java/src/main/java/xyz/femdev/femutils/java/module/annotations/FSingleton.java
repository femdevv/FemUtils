package xyz.femdev.femutils.java.module.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Indicates that the annotated class should be treated as a singleton
 * within the injector scope.
 */
@Documented
@Retention(RUNTIME)
@Target(TYPE)
public @interface FSingleton {
}
