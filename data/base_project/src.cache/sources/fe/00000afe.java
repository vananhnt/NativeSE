package android.os;

/* loaded from: FactoryTest.class */
public final class FactoryTest {
    public static boolean isLongPressOnPowerOffEnabled() {
        return SystemProperties.getInt("factory.long_press_power_off", 0) != 0;
    }
}