package android.telephony;

/* loaded from: CellSignalStrength.class */
public abstract class CellSignalStrength {
    public static final int SIGNAL_STRENGTH_NONE_OR_UNKNOWN = 0;
    public static final int SIGNAL_STRENGTH_POOR = 1;
    public static final int SIGNAL_STRENGTH_MODERATE = 2;
    public static final int SIGNAL_STRENGTH_GOOD = 3;
    public static final int SIGNAL_STRENGTH_GREAT = 4;
    public static final int NUM_SIGNAL_STRENGTH_BINS = 5;
    public static final String[] SIGNAL_STRENGTH_NAMES = {"none", "poor", "moderate", "good", "great"};

    public abstract void setDefaultValues();

    public abstract int getLevel();

    public abstract int getAsuLevel();

    public abstract int getDbm();

    public abstract CellSignalStrength copy();

    public abstract int hashCode();

    public abstract boolean equals(Object obj);
}