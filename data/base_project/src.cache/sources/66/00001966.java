package com.android.internal.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
/* loaded from: VisibleForTesting.class */
public @interface VisibleForTesting {

    /* loaded from: VisibleForTesting$Visibility.class */
    public enum Visibility {
        PROTECTED,
        PACKAGE,
        PRIVATE
    }

    Visibility visibility() default Visibility.PRIVATE;
}