package com.android.internal.util;

import java.util.Arrays;

/* loaded from: Predicates.class */
public class Predicates {
    private Predicates() {
    }

    public static <T> Predicate<T> and(Predicate<? super T>... components) {
        return and(Arrays.asList(components));
    }

    public static <T> Predicate<T> and(Iterable<? extends Predicate<? super T>> components) {
        return new AndPredicate(components);
    }

    public static <T> Predicate<T> or(Predicate<? super T>... components) {
        return or(Arrays.asList(components));
    }

    public static <T> Predicate<T> or(Iterable<? extends Predicate<? super T>> components) {
        return new OrPredicate(components);
    }

    public static <T> Predicate<T> not(Predicate<? super T> predicate) {
        return new NotPredicate(predicate);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Predicates$AndPredicate.class */
    public static class AndPredicate<T> implements Predicate<T> {
        private final Iterable<? extends Predicate<? super T>> components;

        private AndPredicate(Iterable<? extends Predicate<? super T>> components) {
            this.components = components;
        }

        @Override // com.android.internal.util.Predicate
        public boolean apply(T t) {
            for (Predicate<? super T> predicate : this.components) {
                if (!predicate.apply(t)) {
                    return false;
                }
            }
            return true;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Predicates$OrPredicate.class */
    public static class OrPredicate<T> implements Predicate<T> {
        private final Iterable<? extends Predicate<? super T>> components;

        private OrPredicate(Iterable<? extends Predicate<? super T>> components) {
            this.components = components;
        }

        @Override // com.android.internal.util.Predicate
        public boolean apply(T t) {
            for (Predicate<? super T> predicate : this.components) {
                if (predicate.apply(t)) {
                    return true;
                }
            }
            return false;
        }
    }

    /* loaded from: Predicates$NotPredicate.class */
    private static class NotPredicate<T> implements Predicate<T> {
        private final Predicate<? super T> predicate;

        private NotPredicate(Predicate<? super T> predicate) {
            this.predicate = predicate;
        }

        @Override // com.android.internal.util.Predicate
        public boolean apply(T t) {
            return !this.predicate.apply(t);
        }
    }
}