package android.text.method;

/* loaded from: TimeKeyListener.class */
public class TimeKeyListener extends NumberKeyListener {
    public static final char[] CHARACTERS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'm', 'p', ':'};
    private static TimeKeyListener sInstance;

    @Override // android.text.method.KeyListener
    public int getInputType() {
        return 36;
    }

    @Override // android.text.method.NumberKeyListener
    protected char[] getAcceptedChars() {
        return CHARACTERS;
    }

    public static TimeKeyListener getInstance() {
        if (sInstance != null) {
            return sInstance;
        }
        sInstance = new TimeKeyListener();
        return sInstance;
    }
}