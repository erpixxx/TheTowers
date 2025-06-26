package dev.erpix.thetowers.config.i18n;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark fields that should be translated.
 *
 * <p>The value specifies the key used when looking up the translation.</p>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Translatable {
    String value();
}
