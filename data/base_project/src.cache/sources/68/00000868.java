package android.media.videoeditor;

/* loaded from: EffectColor.class */
public class EffectColor extends Effect {
    public static final int TYPE_COLOR = 1;
    public static final int TYPE_GRADIENT = 2;
    public static final int TYPE_SEPIA = 3;
    public static final int TYPE_NEGATIVE = 4;
    public static final int TYPE_FIFTIES = 5;
    public static final int GREEN = 65280;
    public static final int PINK = 16737996;
    public static final int GRAY = 8355711;
    private final int mType;
    private final int mColor;

    private EffectColor() {
        this(null, null, 0L, 0L, 0, 0);
    }

    public EffectColor(MediaItem mediaItem, String effectId, long startTimeMs, long durationMs, int type, int color) {
        super(mediaItem, effectId, startTimeMs, durationMs);
        switch (type) {
            case 1:
            case 2:
                switch (color) {
                    case 65280:
                    case GRAY /* 8355711 */:
                    case PINK /* 16737996 */:
                        this.mColor = color;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid Color: " + color);
                }
            case 3:
            case 4:
            case 5:
                this.mColor = -1;
                break;
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }
        this.mType = type;
    }

    public int getType() {
        return this.mType;
    }

    public int getColor() {
        return this.mColor;
    }
}