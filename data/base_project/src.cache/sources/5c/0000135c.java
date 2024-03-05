package android.test;

/* loaded from: PerformanceTestCase.class */
public interface PerformanceTestCase {

    /* loaded from: PerformanceTestCase$Intermediates.class */
    public interface Intermediates {
        void setInternalIterations(int i);

        void startTiming(boolean z);

        void addIntermediate(String str);

        void addIntermediate(String str, long j);

        void finishTiming(boolean z);
    }

    int startPerformance(Intermediates intermediates);

    boolean isPerformanceOnly();
}