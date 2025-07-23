package xyz.femdev.femutils.java.module.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Marks a constructor or field to be used for dependency injection.
 */
@Documented
@Retention(RUNTIME)
@Target({CONSTRUCTOR, FIELD})
public @interface FInject {
}
