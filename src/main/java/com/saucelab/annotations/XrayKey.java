package com.saucelab.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark test methods or classes with Xray test key.
 * Used by XrayListener to automatically report test results to Xray.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface XrayKey {
    /**
     * The Xray test key (e.g., "SAUCE-123")
     */
    String value();
}
