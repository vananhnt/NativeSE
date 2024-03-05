package android.view;

import android.media.AudioSystem;
import android.media.videoeditor.MediaProperties;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.KeyCharacterMap;
import gov.nist.core.Separators;

/* loaded from: KeyEvent.class */
public class KeyEvent extends InputEvent implements Parcelable {
    public static final int KEYCODE_UNKNOWN = 0;
    public static final int KEYCODE_SOFT_LEFT = 1;
    public static final int KEYCODE_SOFT_RIGHT = 2;
    public static final int KEYCODE_HOME = 3;
    public static final int KEYCODE_BACK = 4;
    public static final int KEYCODE_CALL = 5;
    public static final int KEYCODE_ENDCALL = 6;
    public static final int KEYCODE_0 = 7;
    public static final int KEYCODE_1 = 8;
    public static final int KEYCODE_2 = 9;
    public static final int KEYCODE_3 = 10;
    public static final int KEYCODE_4 = 11;
    public static final int KEYCODE_5 = 12;
    public static final int KEYCODE_6 = 13;
    public static final int KEYCODE_7 = 14;
    public static final int KEYCODE_8 = 15;
    public static final int KEYCODE_9 = 16;
    public static final int KEYCODE_STAR = 17;
    public static final int KEYCODE_POUND = 18;
    public static final int KEYCODE_DPAD_UP = 19;
    public static final int KEYCODE_DPAD_DOWN = 20;
    public static final int KEYCODE_DPAD_LEFT = 21;
    public static final int KEYCODE_DPAD_RIGHT = 22;
    public static final int KEYCODE_DPAD_CENTER = 23;
    public static final int KEYCODE_VOLUME_UP = 24;
    public static final int KEYCODE_VOLUME_DOWN = 25;
    public static final int KEYCODE_POWER = 26;
    public static final int KEYCODE_CAMERA = 27;
    public static final int KEYCODE_CLEAR = 28;
    public static final int KEYCODE_A = 29;
    public static final int KEYCODE_B = 30;
    public static final int KEYCODE_C = 31;
    public static final int KEYCODE_D = 32;
    public static final int KEYCODE_E = 33;
    public static final int KEYCODE_F = 34;
    public static final int KEYCODE_G = 35;
    public static final int KEYCODE_H = 36;
    public static final int KEYCODE_I = 37;
    public static final int KEYCODE_J = 38;
    public static final int KEYCODE_K = 39;
    public static final int KEYCODE_L = 40;
    public static final int KEYCODE_M = 41;
    public static final int KEYCODE_N = 42;
    public static final int KEYCODE_O = 43;
    public static final int KEYCODE_P = 44;
    public static final int KEYCODE_Q = 45;
    public static final int KEYCODE_R = 46;
    public static final int KEYCODE_S = 47;
    public static final int KEYCODE_T = 48;
    public static final int KEYCODE_U = 49;
    public static final int KEYCODE_V = 50;
    public static final int KEYCODE_W = 51;
    public static final int KEYCODE_X = 52;
    public static final int KEYCODE_Y = 53;
    public static final int KEYCODE_Z = 54;
    public static final int KEYCODE_COMMA = 55;
    public static final int KEYCODE_PERIOD = 56;
    public static final int KEYCODE_ALT_LEFT = 57;
    public static final int KEYCODE_ALT_RIGHT = 58;
    public static final int KEYCODE_SHIFT_LEFT = 59;
    public static final int KEYCODE_SHIFT_RIGHT = 60;
    public static final int KEYCODE_TAB = 61;
    public static final int KEYCODE_SPACE = 62;
    public static final int KEYCODE_SYM = 63;
    public static final int KEYCODE_EXPLORER = 64;
    public static final int KEYCODE_ENVELOPE = 65;
    public static final int KEYCODE_ENTER = 66;
    public static final int KEYCODE_DEL = 67;
    public static final int KEYCODE_GRAVE = 68;
    public static final int KEYCODE_MINUS = 69;
    public static final int KEYCODE_EQUALS = 70;
    public static final int KEYCODE_LEFT_BRACKET = 71;
    public static final int KEYCODE_RIGHT_BRACKET = 72;
    public static final int KEYCODE_BACKSLASH = 73;
    public static final int KEYCODE_SEMICOLON = 74;
    public static final int KEYCODE_APOSTROPHE = 75;
    public static final int KEYCODE_SLASH = 76;
    public static final int KEYCODE_AT = 77;
    public static final int KEYCODE_NUM = 78;
    public static final int KEYCODE_HEADSETHOOK = 79;
    public static final int KEYCODE_FOCUS = 80;
    public static final int KEYCODE_PLUS = 81;
    public static final int KEYCODE_MENU = 82;
    public static final int KEYCODE_NOTIFICATION = 83;
    public static final int KEYCODE_SEARCH = 84;
    public static final int KEYCODE_MEDIA_PLAY_PAUSE = 85;
    public static final int KEYCODE_MEDIA_STOP = 86;
    public static final int KEYCODE_MEDIA_NEXT = 87;
    public static final int KEYCODE_MEDIA_PREVIOUS = 88;
    public static final int KEYCODE_MEDIA_REWIND = 89;
    public static final int KEYCODE_MEDIA_FAST_FORWARD = 90;
    public static final int KEYCODE_MUTE = 91;
    public static final int KEYCODE_PAGE_UP = 92;
    public static final int KEYCODE_PAGE_DOWN = 93;
    public static final int KEYCODE_PICTSYMBOLS = 94;
    public static final int KEYCODE_SWITCH_CHARSET = 95;
    public static final int KEYCODE_BUTTON_A = 96;
    public static final int KEYCODE_BUTTON_B = 97;
    public static final int KEYCODE_BUTTON_C = 98;
    public static final int KEYCODE_BUTTON_X = 99;
    public static final int KEYCODE_BUTTON_Y = 100;
    public static final int KEYCODE_BUTTON_Z = 101;
    public static final int KEYCODE_BUTTON_L1 = 102;
    public static final int KEYCODE_BUTTON_R1 = 103;
    public static final int KEYCODE_BUTTON_L2 = 104;
    public static final int KEYCODE_BUTTON_R2 = 105;
    public static final int KEYCODE_BUTTON_THUMBL = 106;
    public static final int KEYCODE_BUTTON_THUMBR = 107;
    public static final int KEYCODE_BUTTON_START = 108;
    public static final int KEYCODE_BUTTON_SELECT = 109;
    public static final int KEYCODE_BUTTON_MODE = 110;
    public static final int KEYCODE_ESCAPE = 111;
    public static final int KEYCODE_FORWARD_DEL = 112;
    public static final int KEYCODE_CTRL_LEFT = 113;
    public static final int KEYCODE_CTRL_RIGHT = 114;
    public static final int KEYCODE_CAPS_LOCK = 115;
    public static final int KEYCODE_SCROLL_LOCK = 116;
    public static final int KEYCODE_META_LEFT = 117;
    public static final int KEYCODE_META_RIGHT = 118;
    public static final int KEYCODE_FUNCTION = 119;
    public static final int KEYCODE_SYSRQ = 120;
    public static final int KEYCODE_BREAK = 121;
    public static final int KEYCODE_MOVE_HOME = 122;
    public static final int KEYCODE_MOVE_END = 123;
    public static final int KEYCODE_INSERT = 124;
    public static final int KEYCODE_FORWARD = 125;
    public static final int KEYCODE_MEDIA_PLAY = 126;
    public static final int KEYCODE_MEDIA_PAUSE = 127;
    public static final int KEYCODE_MEDIA_CLOSE = 128;
    public static final int KEYCODE_MEDIA_EJECT = 129;
    public static final int KEYCODE_MEDIA_RECORD = 130;
    public static final int KEYCODE_F1 = 131;
    public static final int KEYCODE_F2 = 132;
    public static final int KEYCODE_F3 = 133;
    public static final int KEYCODE_F4 = 134;
    public static final int KEYCODE_F5 = 135;
    public static final int KEYCODE_F6 = 136;
    public static final int KEYCODE_F7 = 137;
    public static final int KEYCODE_F8 = 138;
    public static final int KEYCODE_F9 = 139;
    public static final int KEYCODE_F10 = 140;
    public static final int KEYCODE_F11 = 141;
    public static final int KEYCODE_F12 = 142;
    public static final int KEYCODE_NUM_LOCK = 143;
    public static final int KEYCODE_NUMPAD_0 = 144;
    public static final int KEYCODE_NUMPAD_1 = 145;
    public static final int KEYCODE_NUMPAD_2 = 146;
    public static final int KEYCODE_NUMPAD_3 = 147;
    public static final int KEYCODE_NUMPAD_4 = 148;
    public static final int KEYCODE_NUMPAD_5 = 149;
    public static final int KEYCODE_NUMPAD_6 = 150;
    public static final int KEYCODE_NUMPAD_7 = 151;
    public static final int KEYCODE_NUMPAD_8 = 152;
    public static final int KEYCODE_NUMPAD_9 = 153;
    public static final int KEYCODE_NUMPAD_DIVIDE = 154;
    public static final int KEYCODE_NUMPAD_MULTIPLY = 155;
    public static final int KEYCODE_NUMPAD_SUBTRACT = 156;
    public static final int KEYCODE_NUMPAD_ADD = 157;
    public static final int KEYCODE_NUMPAD_DOT = 158;
    public static final int KEYCODE_NUMPAD_COMMA = 159;
    public static final int KEYCODE_NUMPAD_ENTER = 160;
    public static final int KEYCODE_NUMPAD_EQUALS = 161;
    public static final int KEYCODE_NUMPAD_LEFT_PAREN = 162;
    public static final int KEYCODE_NUMPAD_RIGHT_PAREN = 163;
    public static final int KEYCODE_VOLUME_MUTE = 164;
    public static final int KEYCODE_INFO = 165;
    public static final int KEYCODE_CHANNEL_UP = 166;
    public static final int KEYCODE_CHANNEL_DOWN = 167;
    public static final int KEYCODE_ZOOM_IN = 168;
    public static final int KEYCODE_ZOOM_OUT = 169;
    public static final int KEYCODE_TV = 170;
    public static final int KEYCODE_WINDOW = 171;
    public static final int KEYCODE_GUIDE = 172;
    public static final int KEYCODE_DVR = 173;
    public static final int KEYCODE_BOOKMARK = 174;
    public static final int KEYCODE_CAPTIONS = 175;
    public static final int KEYCODE_SETTINGS = 176;
    public static final int KEYCODE_TV_POWER = 177;
    public static final int KEYCODE_TV_INPUT = 178;
    public static final int KEYCODE_STB_POWER = 179;
    public static final int KEYCODE_STB_INPUT = 180;
    public static final int KEYCODE_AVR_POWER = 181;
    public static final int KEYCODE_AVR_INPUT = 182;
    public static final int KEYCODE_PROG_RED = 183;
    public static final int KEYCODE_PROG_GREEN = 184;
    public static final int KEYCODE_PROG_YELLOW = 185;
    public static final int KEYCODE_PROG_BLUE = 186;
    public static final int KEYCODE_APP_SWITCH = 187;
    public static final int KEYCODE_BUTTON_1 = 188;
    public static final int KEYCODE_BUTTON_2 = 189;
    public static final int KEYCODE_BUTTON_3 = 190;
    public static final int KEYCODE_BUTTON_4 = 191;
    public static final int KEYCODE_BUTTON_5 = 192;
    public static final int KEYCODE_BUTTON_6 = 193;
    public static final int KEYCODE_BUTTON_7 = 194;
    public static final int KEYCODE_BUTTON_8 = 195;
    public static final int KEYCODE_BUTTON_9 = 196;
    public static final int KEYCODE_BUTTON_10 = 197;
    public static final int KEYCODE_BUTTON_11 = 198;
    public static final int KEYCODE_BUTTON_12 = 199;
    public static final int KEYCODE_BUTTON_13 = 200;
    public static final int KEYCODE_BUTTON_14 = 201;
    public static final int KEYCODE_BUTTON_15 = 202;
    public static final int KEYCODE_BUTTON_16 = 203;
    public static final int KEYCODE_LANGUAGE_SWITCH = 204;
    public static final int KEYCODE_MANNER_MODE = 205;
    public static final int KEYCODE_3D_MODE = 206;
    public static final int KEYCODE_CONTACTS = 207;
    public static final int KEYCODE_CALENDAR = 208;
    public static final int KEYCODE_MUSIC = 209;
    public static final int KEYCODE_CALCULATOR = 210;
    public static final int KEYCODE_ZENKAKU_HANKAKU = 211;
    public static final int KEYCODE_EISU = 212;
    public static final int KEYCODE_MUHENKAN = 213;
    public static final int KEYCODE_HENKAN = 214;
    public static final int KEYCODE_KATAKANA_HIRAGANA = 215;
    public static final int KEYCODE_YEN = 216;
    public static final int KEYCODE_RO = 217;
    public static final int KEYCODE_KANA = 218;
    public static final int KEYCODE_ASSIST = 219;
    public static final int KEYCODE_BRIGHTNESS_DOWN = 220;
    public static final int KEYCODE_BRIGHTNESS_UP = 221;
    public static final int KEYCODE_MEDIA_AUDIO_TRACK = 222;
    private static final int LAST_KEYCODE = 222;
    @Deprecated
    public static final int MAX_KEYCODE = 84;
    public static final int ACTION_DOWN = 0;
    public static final int ACTION_UP = 1;
    public static final int ACTION_MULTIPLE = 2;
    public static final int META_CAP_LOCKED = 256;
    public static final int META_ALT_LOCKED = 512;
    public static final int META_SYM_LOCKED = 1024;
    public static final int META_SELECTING = 2048;
    public static final int META_ALT_ON = 2;
    public static final int META_ALT_LEFT_ON = 16;
    public static final int META_ALT_RIGHT_ON = 32;
    public static final int META_SHIFT_ON = 1;
    public static final int META_SHIFT_LEFT_ON = 64;
    public static final int META_SHIFT_RIGHT_ON = 128;
    public static final int META_SYM_ON = 4;
    public static final int META_FUNCTION_ON = 8;
    public static final int META_CTRL_ON = 4096;
    public static final int META_CTRL_LEFT_ON = 8192;
    public static final int META_CTRL_RIGHT_ON = 16384;
    public static final int META_META_ON = 65536;
    public static final int META_META_LEFT_ON = 131072;
    public static final int META_META_RIGHT_ON = 262144;
    public static final int META_CAPS_LOCK_ON = 1048576;
    public static final int META_NUM_LOCK_ON = 2097152;
    public static final int META_SCROLL_LOCK_ON = 4194304;
    public static final int META_SHIFT_MASK = 193;
    public static final int META_ALT_MASK = 50;
    public static final int META_CTRL_MASK = 28672;
    public static final int META_META_MASK = 458752;
    public static final int FLAG_WOKE_HERE = 1;
    public static final int FLAG_SOFT_KEYBOARD = 2;
    public static final int FLAG_KEEP_TOUCH_MODE = 4;
    public static final int FLAG_FROM_SYSTEM = 8;
    public static final int FLAG_EDITOR_ACTION = 16;
    public static final int FLAG_CANCELED = 32;
    public static final int FLAG_VIRTUAL_HARD_KEY = 64;
    public static final int FLAG_LONG_PRESS = 128;
    public static final int FLAG_CANCELED_LONG_PRESS = 256;
    public static final int FLAG_TRACKING = 512;
    public static final int FLAG_FALLBACK = 1024;
    public static final int FLAG_PREDISPATCH = 536870912;
    public static final int FLAG_START_TRACKING = 1073741824;
    public static final int FLAG_TAINTED = Integer.MIN_VALUE;
    static final boolean DEBUG = false;
    static final String TAG = "KeyEvent";
    private static final int MAX_RECYCLED = 10;
    private static int gRecyclerUsed;
    private static KeyEvent gRecyclerTop;
    private KeyEvent mNext;
    private int mDeviceId;
    private int mSource;
    private int mMetaState;
    private int mAction;
    private int mKeyCode;
    private int mScanCode;
    private int mRepeatCount;
    private int mFlags;
    private long mDownTime;
    private long mEventTime;
    private String mCharacters;
    private static final int META_MODIFIER_MASK = 487679;
    private static final int META_LOCK_MASK = 7340032;
    private static final int META_ALL_MASK = 7827711;
    private static final int META_SYNTHETIC_MASK = 3840;
    private static final int META_INVALID_MODIFIER_MASK = 7343872;
    public static final Parcelable.Creator<KeyEvent> CREATOR;
    private static final SparseArray<String> KEYCODE_SYMBOLIC_NAMES = new SparseArray<>();
    private static final String[] META_SYMBOLIC_NAMES = {"META_SHIFT_ON", "META_ALT_ON", "META_SYM_ON", "META_FUNCTION_ON", "META_ALT_LEFT_ON", "META_ALT_RIGHT_ON", "META_SHIFT_LEFT_ON", "META_SHIFT_RIGHT_ON", "META_CAP_LOCKED", "META_ALT_LOCKED", "META_SYM_LOCKED", "0x00000800", "META_CTRL_ON", "META_CTRL_LEFT_ON", "META_CTRL_RIGHT_ON", "0x00008000", "META_META_ON", "META_META_LEFT_ON", "META_META_RIGHT_ON", "0x00080000", "META_CAPS_LOCK_ON", "META_NUM_LOCK_ON", "META_SCROLL_LOCK_ON", "0x00800000", "0x01000000", "0x02000000", "0x04000000", "0x08000000", "0x10000000", "0x20000000", "0x40000000", "0x80000000"};
    private static final Object gRecyclerLock = new Object();

    /* loaded from: KeyEvent$Callback.class */
    public interface Callback {
        boolean onKeyDown(int i, KeyEvent keyEvent);

        boolean onKeyLongPress(int i, KeyEvent keyEvent);

        boolean onKeyUp(int i, KeyEvent keyEvent);

        boolean onKeyMultiple(int i, int i2, KeyEvent keyEvent);
    }

    private native boolean native_isSystemKey(int i);

    private native boolean native_hasDefaultAction(int i);

    static /* synthetic */ int access$076(KeyEvent x0, int x1) {
        int i = x0.mFlags | x1;
        x0.mFlags = i;
        return i;
    }

    static {
        populateKeycodeSymbolicNames();
        CREATOR = new Parcelable.Creator<KeyEvent>() { // from class: android.view.KeyEvent.1
            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public KeyEvent createFromParcel(Parcel in) {
                in.readInt();
                return KeyEvent.createFromParcelBody(in);
            }

            /* JADX WARN: Can't rename method to resolve collision */
            @Override // android.os.Parcelable.Creator
            public KeyEvent[] newArray(int size) {
                return new KeyEvent[size];
            }
        };
    }

    private static void populateKeycodeSymbolicNames() {
        SparseArray<String> names = KEYCODE_SYMBOLIC_NAMES;
        names.append(0, "KEYCODE_UNKNOWN");
        names.append(1, "KEYCODE_SOFT_LEFT");
        names.append(2, "KEYCODE_SOFT_RIGHT");
        names.append(3, "KEYCODE_HOME");
        names.append(4, "KEYCODE_BACK");
        names.append(5, "KEYCODE_CALL");
        names.append(6, "KEYCODE_ENDCALL");
        names.append(7, "KEYCODE_0");
        names.append(8, "KEYCODE_1");
        names.append(9, "KEYCODE_2");
        names.append(10, "KEYCODE_3");
        names.append(11, "KEYCODE_4");
        names.append(12, "KEYCODE_5");
        names.append(13, "KEYCODE_6");
        names.append(14, "KEYCODE_7");
        names.append(15, "KEYCODE_8");
        names.append(16, "KEYCODE_9");
        names.append(17, "KEYCODE_STAR");
        names.append(18, "KEYCODE_POUND");
        names.append(19, "KEYCODE_DPAD_UP");
        names.append(20, "KEYCODE_DPAD_DOWN");
        names.append(21, "KEYCODE_DPAD_LEFT");
        names.append(22, "KEYCODE_DPAD_RIGHT");
        names.append(23, "KEYCODE_DPAD_CENTER");
        names.append(24, "KEYCODE_VOLUME_UP");
        names.append(25, "KEYCODE_VOLUME_DOWN");
        names.append(26, "KEYCODE_POWER");
        names.append(27, "KEYCODE_CAMERA");
        names.append(28, "KEYCODE_CLEAR");
        names.append(29, "KEYCODE_A");
        names.append(30, "KEYCODE_B");
        names.append(31, "KEYCODE_C");
        names.append(32, "KEYCODE_D");
        names.append(33, "KEYCODE_E");
        names.append(34, "KEYCODE_F");
        names.append(35, "KEYCODE_G");
        names.append(36, "KEYCODE_H");
        names.append(37, "KEYCODE_I");
        names.append(38, "KEYCODE_J");
        names.append(39, "KEYCODE_K");
        names.append(40, "KEYCODE_L");
        names.append(41, "KEYCODE_M");
        names.append(42, "KEYCODE_N");
        names.append(43, "KEYCODE_O");
        names.append(44, "KEYCODE_P");
        names.append(45, "KEYCODE_Q");
        names.append(46, "KEYCODE_R");
        names.append(47, "KEYCODE_S");
        names.append(48, "KEYCODE_T");
        names.append(49, "KEYCODE_U");
        names.append(50, "KEYCODE_V");
        names.append(51, "KEYCODE_W");
        names.append(52, "KEYCODE_X");
        names.append(53, "KEYCODE_Y");
        names.append(54, "KEYCODE_Z");
        names.append(55, "KEYCODE_COMMA");
        names.append(56, "KEYCODE_PERIOD");
        names.append(57, "KEYCODE_ALT_LEFT");
        names.append(58, "KEYCODE_ALT_RIGHT");
        names.append(59, "KEYCODE_SHIFT_LEFT");
        names.append(60, "KEYCODE_SHIFT_RIGHT");
        names.append(61, "KEYCODE_TAB");
        names.append(62, "KEYCODE_SPACE");
        names.append(63, "KEYCODE_SYM");
        names.append(64, "KEYCODE_EXPLORER");
        names.append(65, "KEYCODE_ENVELOPE");
        names.append(66, "KEYCODE_ENTER");
        names.append(67, "KEYCODE_DEL");
        names.append(68, "KEYCODE_GRAVE");
        names.append(69, "KEYCODE_MINUS");
        names.append(70, "KEYCODE_EQUALS");
        names.append(71, "KEYCODE_LEFT_BRACKET");
        names.append(72, "KEYCODE_RIGHT_BRACKET");
        names.append(73, "KEYCODE_BACKSLASH");
        names.append(74, "KEYCODE_SEMICOLON");
        names.append(75, "KEYCODE_APOSTROPHE");
        names.append(76, "KEYCODE_SLASH");
        names.append(77, "KEYCODE_AT");
        names.append(78, "KEYCODE_NUM");
        names.append(79, "KEYCODE_HEADSETHOOK");
        names.append(80, "KEYCODE_FOCUS");
        names.append(81, "KEYCODE_PLUS");
        names.append(82, "KEYCODE_MENU");
        names.append(83, "KEYCODE_NOTIFICATION");
        names.append(84, "KEYCODE_SEARCH");
        names.append(85, "KEYCODE_MEDIA_PLAY_PAUSE");
        names.append(86, "KEYCODE_MEDIA_STOP");
        names.append(87, "KEYCODE_MEDIA_NEXT");
        names.append(88, "KEYCODE_MEDIA_PREVIOUS");
        names.append(89, "KEYCODE_MEDIA_REWIND");
        names.append(90, "KEYCODE_MEDIA_FAST_FORWARD");
        names.append(91, "KEYCODE_MUTE");
        names.append(92, "KEYCODE_PAGE_UP");
        names.append(93, "KEYCODE_PAGE_DOWN");
        names.append(94, "KEYCODE_PICTSYMBOLS");
        names.append(95, "KEYCODE_SWITCH_CHARSET");
        names.append(96, "KEYCODE_BUTTON_A");
        names.append(97, "KEYCODE_BUTTON_B");
        names.append(98, "KEYCODE_BUTTON_C");
        names.append(99, "KEYCODE_BUTTON_X");
        names.append(100, "KEYCODE_BUTTON_Y");
        names.append(101, "KEYCODE_BUTTON_Z");
        names.append(102, "KEYCODE_BUTTON_L1");
        names.append(103, "KEYCODE_BUTTON_R1");
        names.append(104, "KEYCODE_BUTTON_L2");
        names.append(105, "KEYCODE_BUTTON_R2");
        names.append(106, "KEYCODE_BUTTON_THUMBL");
        names.append(107, "KEYCODE_BUTTON_THUMBR");
        names.append(108, "KEYCODE_BUTTON_START");
        names.append(109, "KEYCODE_BUTTON_SELECT");
        names.append(110, "KEYCODE_BUTTON_MODE");
        names.append(111, "KEYCODE_ESCAPE");
        names.append(112, "KEYCODE_FORWARD_DEL");
        names.append(113, "KEYCODE_CTRL_LEFT");
        names.append(114, "KEYCODE_CTRL_RIGHT");
        names.append(115, "KEYCODE_CAPS_LOCK");
        names.append(116, "KEYCODE_SCROLL_LOCK");
        names.append(117, "KEYCODE_META_LEFT");
        names.append(118, "KEYCODE_META_RIGHT");
        names.append(119, "KEYCODE_FUNCTION");
        names.append(120, "KEYCODE_SYSRQ");
        names.append(121, "KEYCODE_BREAK");
        names.append(122, "KEYCODE_MOVE_HOME");
        names.append(123, "KEYCODE_MOVE_END");
        names.append(124, "KEYCODE_INSERT");
        names.append(125, "KEYCODE_FORWARD");
        names.append(126, "KEYCODE_MEDIA_PLAY");
        names.append(127, "KEYCODE_MEDIA_PAUSE");
        names.append(128, "KEYCODE_MEDIA_CLOSE");
        names.append(129, "KEYCODE_MEDIA_EJECT");
        names.append(130, "KEYCODE_MEDIA_RECORD");
        names.append(131, "KEYCODE_F1");
        names.append(132, "KEYCODE_F2");
        names.append(133, "KEYCODE_F3");
        names.append(134, "KEYCODE_F4");
        names.append(135, "KEYCODE_F5");
        names.append(136, "KEYCODE_F6");
        names.append(137, "KEYCODE_F7");
        names.append(138, "KEYCODE_F8");
        names.append(139, "KEYCODE_F9");
        names.append(140, "KEYCODE_F10");
        names.append(141, "KEYCODE_F11");
        names.append(142, "KEYCODE_F12");
        names.append(143, "KEYCODE_NUM_LOCK");
        names.append(144, "KEYCODE_NUMPAD_0");
        names.append(145, "KEYCODE_NUMPAD_1");
        names.append(146, "KEYCODE_NUMPAD_2");
        names.append(147, "KEYCODE_NUMPAD_3");
        names.append(148, "KEYCODE_NUMPAD_4");
        names.append(149, "KEYCODE_NUMPAD_5");
        names.append(150, "KEYCODE_NUMPAD_6");
        names.append(151, "KEYCODE_NUMPAD_7");
        names.append(152, "KEYCODE_NUMPAD_8");
        names.append(153, "KEYCODE_NUMPAD_9");
        names.append(154, "KEYCODE_NUMPAD_DIVIDE");
        names.append(155, "KEYCODE_NUMPAD_MULTIPLY");
        names.append(156, "KEYCODE_NUMPAD_SUBTRACT");
        names.append(157, "KEYCODE_NUMPAD_ADD");
        names.append(158, "KEYCODE_NUMPAD_DOT");
        names.append(159, "KEYCODE_NUMPAD_COMMA");
        names.append(160, "KEYCODE_NUMPAD_ENTER");
        names.append(161, "KEYCODE_NUMPAD_EQUALS");
        names.append(162, "KEYCODE_NUMPAD_LEFT_PAREN");
        names.append(163, "KEYCODE_NUMPAD_RIGHT_PAREN");
        names.append(164, "KEYCODE_VOLUME_MUTE");
        names.append(165, "KEYCODE_INFO");
        names.append(166, "KEYCODE_CHANNEL_UP");
        names.append(167, "KEYCODE_CHANNEL_DOWN");
        names.append(168, "KEYCODE_ZOOM_IN");
        names.append(169, "KEYCODE_ZOOM_OUT");
        names.append(170, "KEYCODE_TV");
        names.append(171, "KEYCODE_WINDOW");
        names.append(172, "KEYCODE_GUIDE");
        names.append(173, "KEYCODE_DVR");
        names.append(174, "KEYCODE_BOOKMARK");
        names.append(175, "KEYCODE_CAPTIONS");
        names.append(176, "KEYCODE_SETTINGS");
        names.append(177, "KEYCODE_TV_POWER");
        names.append(178, "KEYCODE_TV_INPUT");
        names.append(180, "KEYCODE_STB_INPUT");
        names.append(179, "KEYCODE_STB_POWER");
        names.append(181, "KEYCODE_AVR_POWER");
        names.append(182, "KEYCODE_AVR_INPUT");
        names.append(183, "KEYCODE_PROG_RED");
        names.append(184, "KEYCODE_PROG_GREEN");
        names.append(185, "KEYCODE_PROG_YELLOW");
        names.append(186, "KEYCODE_PROG_BLUE");
        names.append(187, "KEYCODE_APP_SWITCH");
        names.append(188, "KEYCODE_BUTTON_1");
        names.append(189, "KEYCODE_BUTTON_2");
        names.append(190, "KEYCODE_BUTTON_3");
        names.append(191, "KEYCODE_BUTTON_4");
        names.append(192, "KEYCODE_BUTTON_5");
        names.append(193, "KEYCODE_BUTTON_6");
        names.append(194, "KEYCODE_BUTTON_7");
        names.append(195, "KEYCODE_BUTTON_8");
        names.append(196, "KEYCODE_BUTTON_9");
        names.append(197, "KEYCODE_BUTTON_10");
        names.append(198, "KEYCODE_BUTTON_11");
        names.append(199, "KEYCODE_BUTTON_12");
        names.append(200, "KEYCODE_BUTTON_13");
        names.append(201, "KEYCODE_BUTTON_14");
        names.append(202, "KEYCODE_BUTTON_15");
        names.append(203, "KEYCODE_BUTTON_16");
        names.append(204, "KEYCODE_LANGUAGE_SWITCH");
        names.append(205, "KEYCODE_MANNER_MODE");
        names.append(206, "KEYCODE_3D_MODE");
        names.append(207, "KEYCODE_CONTACTS");
        names.append(208, "KEYCODE_CALENDAR");
        names.append(209, "KEYCODE_MUSIC");
        names.append(210, "KEYCODE_CALCULATOR");
        names.append(211, "KEYCODE_ZENKAKU_HANKAKU");
        names.append(212, "KEYCODE_EISU");
        names.append(213, "KEYCODE_MUHENKAN");
        names.append(214, "KEYCODE_HENKAN");
        names.append(215, "KEYCODE_KATAKANA_HIRAGANA");
        names.append(216, "KEYCODE_YEN");
        names.append(217, "KEYCODE_RO");
        names.append(218, "KEYCODE_KANA");
        names.append(219, "KEYCODE_ASSIST");
        names.append(220, "KEYCODE_BRIGHTNESS_DOWN");
        names.append(221, "KEYCODE_BRIGHTNESS_UP");
        names.append(222, "KEYCODE_MEDIA_AUDIO_TRACK");
    }

    public static int getMaxKeyCode() {
        return 222;
    }

    public static int getDeadChar(int accent, int c) {
        return KeyCharacterMap.getDeadChar(accent, c);
    }

    private KeyEvent() {
    }

    public KeyEvent(int action, int code) {
        this.mAction = action;
        this.mKeyCode = code;
        this.mRepeatCount = 0;
        this.mDeviceId = -1;
    }

    public KeyEvent(long downTime, long eventTime, int action, int code, int repeat) {
        this.mDownTime = downTime;
        this.mEventTime = eventTime;
        this.mAction = action;
        this.mKeyCode = code;
        this.mRepeatCount = repeat;
        this.mDeviceId = -1;
    }

    public KeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState) {
        this.mDownTime = downTime;
        this.mEventTime = eventTime;
        this.mAction = action;
        this.mKeyCode = code;
        this.mRepeatCount = repeat;
        this.mMetaState = metaState;
        this.mDeviceId = -1;
    }

    public KeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState, int deviceId, int scancode) {
        this.mDownTime = downTime;
        this.mEventTime = eventTime;
        this.mAction = action;
        this.mKeyCode = code;
        this.mRepeatCount = repeat;
        this.mMetaState = metaState;
        this.mDeviceId = deviceId;
        this.mScanCode = scancode;
    }

    public KeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState, int deviceId, int scancode, int flags) {
        this.mDownTime = downTime;
        this.mEventTime = eventTime;
        this.mAction = action;
        this.mKeyCode = code;
        this.mRepeatCount = repeat;
        this.mMetaState = metaState;
        this.mDeviceId = deviceId;
        this.mScanCode = scancode;
        this.mFlags = flags;
    }

    public KeyEvent(long downTime, long eventTime, int action, int code, int repeat, int metaState, int deviceId, int scancode, int flags, int source) {
        this.mDownTime = downTime;
        this.mEventTime = eventTime;
        this.mAction = action;
        this.mKeyCode = code;
        this.mRepeatCount = repeat;
        this.mMetaState = metaState;
        this.mDeviceId = deviceId;
        this.mScanCode = scancode;
        this.mFlags = flags;
        this.mSource = source;
    }

    public KeyEvent(long time, String characters, int deviceId, int flags) {
        this.mDownTime = time;
        this.mEventTime = time;
        this.mCharacters = characters;
        this.mAction = 2;
        this.mKeyCode = 0;
        this.mRepeatCount = 0;
        this.mDeviceId = deviceId;
        this.mFlags = flags;
        this.mSource = 257;
    }

    public KeyEvent(KeyEvent origEvent) {
        this.mDownTime = origEvent.mDownTime;
        this.mEventTime = origEvent.mEventTime;
        this.mAction = origEvent.mAction;
        this.mKeyCode = origEvent.mKeyCode;
        this.mRepeatCount = origEvent.mRepeatCount;
        this.mMetaState = origEvent.mMetaState;
        this.mDeviceId = origEvent.mDeviceId;
        this.mSource = origEvent.mSource;
        this.mScanCode = origEvent.mScanCode;
        this.mFlags = origEvent.mFlags;
        this.mCharacters = origEvent.mCharacters;
    }

    @Deprecated
    public KeyEvent(KeyEvent origEvent, long eventTime, int newRepeat) {
        this.mDownTime = origEvent.mDownTime;
        this.mEventTime = eventTime;
        this.mAction = origEvent.mAction;
        this.mKeyCode = origEvent.mKeyCode;
        this.mRepeatCount = newRepeat;
        this.mMetaState = origEvent.mMetaState;
        this.mDeviceId = origEvent.mDeviceId;
        this.mSource = origEvent.mSource;
        this.mScanCode = origEvent.mScanCode;
        this.mFlags = origEvent.mFlags;
        this.mCharacters = origEvent.mCharacters;
    }

    private static KeyEvent obtain() {
        synchronized (gRecyclerLock) {
            KeyEvent ev = gRecyclerTop;
            if (ev == null) {
                return new KeyEvent();
            }
            gRecyclerTop = ev.mNext;
            gRecyclerUsed--;
            ev.mNext = null;
            ev.prepareForReuse();
            return ev;
        }
    }

    public static KeyEvent obtain(long downTime, long eventTime, int action, int code, int repeat, int metaState, int deviceId, int scancode, int flags, int source, String characters) {
        KeyEvent ev = obtain();
        ev.mDownTime = downTime;
        ev.mEventTime = eventTime;
        ev.mAction = action;
        ev.mKeyCode = code;
        ev.mRepeatCount = repeat;
        ev.mMetaState = metaState;
        ev.mDeviceId = deviceId;
        ev.mScanCode = scancode;
        ev.mFlags = flags;
        ev.mSource = source;
        ev.mCharacters = characters;
        return ev;
    }

    public static KeyEvent obtain(KeyEvent other) {
        KeyEvent ev = obtain();
        ev.mDownTime = other.mDownTime;
        ev.mEventTime = other.mEventTime;
        ev.mAction = other.mAction;
        ev.mKeyCode = other.mKeyCode;
        ev.mRepeatCount = other.mRepeatCount;
        ev.mMetaState = other.mMetaState;
        ev.mDeviceId = other.mDeviceId;
        ev.mScanCode = other.mScanCode;
        ev.mFlags = other.mFlags;
        ev.mSource = other.mSource;
        ev.mCharacters = other.mCharacters;
        return ev;
    }

    @Override // android.view.InputEvent
    public KeyEvent copy() {
        return obtain(this);
    }

    @Override // android.view.InputEvent
    public final void recycle() {
        super.recycle();
        this.mCharacters = null;
        synchronized (gRecyclerLock) {
            if (gRecyclerUsed < 10) {
                gRecyclerUsed++;
                this.mNext = gRecyclerTop;
                gRecyclerTop = this;
            }
        }
    }

    @Override // android.view.InputEvent
    public final void recycleIfNeededAfterDispatch() {
    }

    public static KeyEvent changeTimeRepeat(KeyEvent event, long eventTime, int newRepeat) {
        return new KeyEvent(event, eventTime, newRepeat);
    }

    public static KeyEvent changeTimeRepeat(KeyEvent event, long eventTime, int newRepeat, int newFlags) {
        KeyEvent ret = new KeyEvent(event);
        ret.mEventTime = eventTime;
        ret.mRepeatCount = newRepeat;
        ret.mFlags = newFlags;
        return ret;
    }

    private KeyEvent(KeyEvent origEvent, int action) {
        this.mDownTime = origEvent.mDownTime;
        this.mEventTime = origEvent.mEventTime;
        this.mAction = action;
        this.mKeyCode = origEvent.mKeyCode;
        this.mRepeatCount = origEvent.mRepeatCount;
        this.mMetaState = origEvent.mMetaState;
        this.mDeviceId = origEvent.mDeviceId;
        this.mSource = origEvent.mSource;
        this.mScanCode = origEvent.mScanCode;
        this.mFlags = origEvent.mFlags;
    }

    public static KeyEvent changeAction(KeyEvent event, int action) {
        return new KeyEvent(event, action);
    }

    public static KeyEvent changeFlags(KeyEvent event, int flags) {
        KeyEvent event2 = new KeyEvent(event);
        event2.mFlags = flags;
        return event2;
    }

    @Override // android.view.InputEvent
    public final boolean isTainted() {
        return (this.mFlags & Integer.MIN_VALUE) != 0;
    }

    @Override // android.view.InputEvent
    public final void setTainted(boolean tainted) {
        this.mFlags = tainted ? this.mFlags | Integer.MIN_VALUE : this.mFlags & Integer.MAX_VALUE;
    }

    @Deprecated
    public final boolean isDown() {
        return this.mAction == 0;
    }

    public final boolean isSystem() {
        return native_isSystemKey(this.mKeyCode);
    }

    public final boolean hasDefaultAction() {
        return native_hasDefaultAction(this.mKeyCode);
    }

    public static final boolean isGamepadButton(int keyCode) {
        switch (keyCode) {
            case 96:
            case 97:
            case 98:
            case 99:
            case 100:
            case 101:
            case 102:
            case 103:
            case 104:
            case 105:
            case 106:
            case 107:
            case 108:
            case 109:
            case 110:
            case 188:
            case 189:
            case 190:
            case 191:
            case 192:
            case 193:
            case 194:
            case 195:
            case 196:
            case 197:
            case 198:
            case 199:
            case 200:
            case 201:
            case 202:
            case 203:
                return true;
            case 111:
            case 112:
            case 113:
            case 114:
            case 115:
            case 116:
            case 117:
            case 118:
            case 119:
            case 120:
            case 121:
            case 122:
            case 123:
            case 124:
            case 125:
            case 126:
            case 127:
            case 128:
            case 129:
            case 130:
            case 131:
            case 132:
            case 133:
            case 134:
            case 135:
            case 136:
            case 137:
            case 138:
            case 139:
            case 140:
            case 141:
            case 142:
            case 143:
            case 144:
            case 145:
            case 146:
            case 147:
            case 148:
            case 149:
            case 150:
            case 151:
            case 152:
            case 153:
            case 154:
            case 155:
            case 156:
            case 157:
            case 158:
            case 159:
            case 160:
            case 161:
            case 162:
            case 163:
            case 164:
            case 165:
            case 166:
            case 167:
            case 168:
            case 169:
            case 170:
            case 171:
            case 172:
            case 173:
            case 174:
            case 175:
            case 176:
            case 177:
            case 178:
            case 179:
            case 180:
            case 181:
            case 182:
            case 183:
            case 184:
            case 185:
            case 186:
            case 187:
            default:
                return false;
        }
    }

    public static final boolean isConfirmKey(int keyCode) {
        switch (keyCode) {
            case 23:
            case 66:
                return true;
            default:
                return false;
        }
    }

    @Override // android.view.InputEvent
    public final int getDeviceId() {
        return this.mDeviceId;
    }

    @Override // android.view.InputEvent
    public final int getSource() {
        return this.mSource;
    }

    @Override // android.view.InputEvent
    public final void setSource(int source) {
        this.mSource = source;
    }

    public final int getMetaState() {
        return this.mMetaState;
    }

    public final int getModifiers() {
        return normalizeMetaState(this.mMetaState) & META_MODIFIER_MASK;
    }

    public final int getFlags() {
        return this.mFlags;
    }

    public static int getModifierMetaStateMask() {
        return META_MODIFIER_MASK;
    }

    public static boolean isModifierKey(int keyCode) {
        switch (keyCode) {
            case 57:
            case 58:
            case 59:
            case 60:
            case 63:
            case 78:
            case 113:
            case 114:
            case 117:
            case 118:
            case 119:
                return true;
            default:
                return false;
        }
    }

    public static int normalizeMetaState(int metaState) {
        if ((metaState & 192) != 0) {
            metaState |= 1;
        }
        if ((metaState & 48) != 0) {
            metaState |= 2;
        }
        if ((metaState & AudioSystem.DEVICE_OUT_ALL_USB) != 0) {
            metaState |= 4096;
        }
        if ((metaState & 393216) != 0) {
            metaState |= 65536;
        }
        if ((metaState & 256) != 0) {
            metaState |= 1048576;
        }
        if ((metaState & 512) != 0) {
            metaState |= 2;
        }
        if ((metaState & 1024) != 0) {
            metaState |= 4;
        }
        return metaState & META_ALL_MASK;
    }

    public static boolean metaStateHasNoModifiers(int metaState) {
        return (normalizeMetaState(metaState) & META_MODIFIER_MASK) == 0;
    }

    public static boolean metaStateHasModifiers(int metaState, int modifiers) {
        if ((modifiers & META_INVALID_MODIFIER_MASK) != 0) {
            throw new IllegalArgumentException("modifiers must not contain META_CAPS_LOCK_ON, META_NUM_LOCK_ON, META_SCROLL_LOCK_ON, META_CAP_LOCKED, META_ALT_LOCKED, META_SYM_LOCKED, or META_SELECTING");
        }
        return metaStateFilterDirectionalModifiers(metaStateFilterDirectionalModifiers(metaStateFilterDirectionalModifiers(metaStateFilterDirectionalModifiers(normalizeMetaState(metaState) & META_MODIFIER_MASK, modifiers, 1, 64, 128), modifiers, 2, 16, 32), modifiers, 4096, 8192, 16384), modifiers, 65536, 131072, 262144) == modifiers;
    }

    private static int metaStateFilterDirectionalModifiers(int metaState, int modifiers, int basic, int left, int right) {
        boolean wantBasic = (modifiers & basic) != 0;
        int directional = left | right;
        boolean wantLeftOrRight = (modifiers & directional) != 0;
        if (wantBasic) {
            if (wantLeftOrRight) {
                throw new IllegalArgumentException("modifiers must not contain " + metaStateToString(basic) + " combined with " + metaStateToString(left) + " or " + metaStateToString(right));
            }
            return metaState & (directional ^ (-1));
        } else if (wantLeftOrRight) {
            return metaState & (basic ^ (-1));
        } else {
            return metaState;
        }
    }

    public final boolean hasNoModifiers() {
        return metaStateHasNoModifiers(this.mMetaState);
    }

    public final boolean hasModifiers(int modifiers) {
        return metaStateHasModifiers(this.mMetaState, modifiers);
    }

    public final boolean isAltPressed() {
        return (this.mMetaState & 2) != 0;
    }

    public final boolean isShiftPressed() {
        return (this.mMetaState & 1) != 0;
    }

    public final boolean isSymPressed() {
        return (this.mMetaState & 4) != 0;
    }

    public final boolean isCtrlPressed() {
        return (this.mMetaState & 4096) != 0;
    }

    public final boolean isMetaPressed() {
        return (this.mMetaState & 65536) != 0;
    }

    public final boolean isFunctionPressed() {
        return (this.mMetaState & 8) != 0;
    }

    public final boolean isCapsLockOn() {
        return (this.mMetaState & 1048576) != 0;
    }

    public final boolean isNumLockOn() {
        return (this.mMetaState & 2097152) != 0;
    }

    public final boolean isScrollLockOn() {
        return (this.mMetaState & 4194304) != 0;
    }

    public final int getAction() {
        return this.mAction;
    }

    public final boolean isCanceled() {
        return (this.mFlags & 32) != 0;
    }

    public final void startTracking() {
        this.mFlags |= 1073741824;
    }

    public final boolean isTracking() {
        return (this.mFlags & 512) != 0;
    }

    public final boolean isLongPress() {
        return (this.mFlags & 128) != 0;
    }

    public final int getKeyCode() {
        return this.mKeyCode;
    }

    public final String getCharacters() {
        return this.mCharacters;
    }

    public final int getScanCode() {
        return this.mScanCode;
    }

    public final int getRepeatCount() {
        return this.mRepeatCount;
    }

    public final long getDownTime() {
        return this.mDownTime;
    }

    @Override // android.view.InputEvent
    public final long getEventTime() {
        return this.mEventTime;
    }

    @Override // android.view.InputEvent
    public final long getEventTimeNano() {
        return this.mEventTime * 1000000;
    }

    @Deprecated
    public final int getKeyboardDevice() {
        return this.mDeviceId;
    }

    public final KeyCharacterMap getKeyCharacterMap() {
        return KeyCharacterMap.load(this.mDeviceId);
    }

    public char getDisplayLabel() {
        return getKeyCharacterMap().getDisplayLabel(this.mKeyCode);
    }

    public int getUnicodeChar() {
        return getUnicodeChar(this.mMetaState);
    }

    public int getUnicodeChar(int metaState) {
        return getKeyCharacterMap().get(this.mKeyCode, metaState);
    }

    @Deprecated
    public boolean getKeyData(KeyCharacterMap.KeyData results) {
        return getKeyCharacterMap().getKeyData(this.mKeyCode, results);
    }

    public char getMatch(char[] chars) {
        return getMatch(chars, 0);
    }

    public char getMatch(char[] chars, int metaState) {
        return getKeyCharacterMap().getMatch(this.mKeyCode, chars, metaState);
    }

    public char getNumber() {
        return getKeyCharacterMap().getNumber(this.mKeyCode);
    }

    public boolean isPrintingKey() {
        return getKeyCharacterMap().isPrintingKey(this.mKeyCode);
    }

    @Deprecated
    public final boolean dispatch(Callback receiver) {
        return dispatch(receiver, null, null);
    }

    public final boolean dispatch(Callback receiver, DispatcherState state, Object target) {
        switch (this.mAction) {
            case 0:
                this.mFlags &= -1073741825;
                boolean res = receiver.onKeyDown(this.mKeyCode, this);
                if (state != null) {
                    if (res && this.mRepeatCount == 0 && (this.mFlags & 1073741824) != 0) {
                        state.startTracking(this, target);
                    } else if (isLongPress() && state.isTracking(this)) {
                        try {
                            if (receiver.onKeyLongPress(this.mKeyCode, this)) {
                                state.performedLongPress(this);
                                res = true;
                            }
                        } catch (AbstractMethodError e) {
                        }
                    }
                }
                return res;
            case 1:
                if (state != null) {
                    state.handleUpEvent(this);
                }
                return receiver.onKeyUp(this.mKeyCode, this);
            case 2:
                int count = this.mRepeatCount;
                int code = this.mKeyCode;
                if (receiver.onKeyMultiple(code, count, this)) {
                    return true;
                }
                if (code != 0) {
                    this.mAction = 0;
                    this.mRepeatCount = 0;
                    boolean handled = receiver.onKeyDown(code, this);
                    if (handled) {
                        this.mAction = 1;
                        receiver.onKeyUp(code, this);
                    }
                    this.mAction = 2;
                    this.mRepeatCount = count;
                    return handled;
                }
                return false;
            default:
                return false;
        }
    }

    /* loaded from: KeyEvent$DispatcherState.class */
    public static class DispatcherState {
        int mDownKeyCode;
        Object mDownTarget;
        SparseIntArray mActiveLongPresses = new SparseIntArray();

        public void reset() {
            this.mDownKeyCode = 0;
            this.mDownTarget = null;
            this.mActiveLongPresses.clear();
        }

        public void reset(Object target) {
            if (this.mDownTarget == target) {
                this.mDownKeyCode = 0;
                this.mDownTarget = null;
            }
        }

        public void startTracking(KeyEvent event, Object target) {
            if (event.getAction() != 0) {
                throw new IllegalArgumentException("Can only start tracking on a down event");
            }
            this.mDownKeyCode = event.getKeyCode();
            this.mDownTarget = target;
        }

        public boolean isTracking(KeyEvent event) {
            return this.mDownKeyCode == event.getKeyCode();
        }

        public void performedLongPress(KeyEvent event) {
            this.mActiveLongPresses.put(event.getKeyCode(), 1);
        }

        public void handleUpEvent(KeyEvent event) {
            int keyCode = event.getKeyCode();
            int index = this.mActiveLongPresses.indexOfKey(keyCode);
            if (index >= 0) {
                KeyEvent.access$076(event, MediaProperties.HEIGHT_288);
                this.mActiveLongPresses.removeAt(index);
            }
            if (this.mDownKeyCode == keyCode) {
                KeyEvent.access$076(event, 512);
                this.mDownKeyCode = 0;
                this.mDownTarget = null;
            }
        }
    }

    public String toString() {
        StringBuilder msg = new StringBuilder();
        msg.append("KeyEvent { action=").append(actionToString(this.mAction));
        msg.append(", keyCode=").append(keyCodeToString(this.mKeyCode));
        msg.append(", scanCode=").append(this.mScanCode);
        if (this.mCharacters != null) {
            msg.append(", characters=\"").append(this.mCharacters).append(Separators.DOUBLE_QUOTE);
        }
        msg.append(", metaState=").append(metaStateToString(this.mMetaState));
        msg.append(", flags=0x").append(Integer.toHexString(this.mFlags));
        msg.append(", repeatCount=").append(this.mRepeatCount);
        msg.append(", eventTime=").append(this.mEventTime);
        msg.append(", downTime=").append(this.mDownTime);
        msg.append(", deviceId=").append(this.mDeviceId);
        msg.append(", source=0x").append(Integer.toHexString(this.mSource));
        msg.append(" }");
        return msg.toString();
    }

    public static String actionToString(int action) {
        switch (action) {
            case 0:
                return "ACTION_DOWN";
            case 1:
                return "ACTION_UP";
            case 2:
                return "ACTION_MULTIPLE";
            default:
                return Integer.toString(action);
        }
    }

    public static String keyCodeToString(int keyCode) {
        String symbolicName = KEYCODE_SYMBOLIC_NAMES.get(keyCode);
        return symbolicName != null ? symbolicName : Integer.toString(keyCode);
    }

    public static int keyCodeFromString(String symbolicName) {
        if (symbolicName == null) {
            throw new IllegalArgumentException("symbolicName must not be null");
        }
        int count = KEYCODE_SYMBOLIC_NAMES.size();
        for (int i = 0; i < count; i++) {
            if (symbolicName.equals(KEYCODE_SYMBOLIC_NAMES.valueAt(i))) {
                return i;
            }
        }
        try {
            return Integer.parseInt(symbolicName, 10);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static String metaStateToString(int metaState) {
        if (metaState == 0) {
            return "0";
        }
        StringBuilder result = null;
        int i = 0;
        while (metaState != 0) {
            boolean isSet = (metaState & 1) != 0;
            metaState >>>= 1;
            if (isSet) {
                String name = META_SYMBOLIC_NAMES[i];
                if (result == null) {
                    if (metaState == 0) {
                        return name;
                    }
                    result = new StringBuilder(name);
                } else {
                    result.append('|');
                    result.append(name);
                }
            }
            i++;
        }
        return result.toString();
    }

    public static KeyEvent createFromParcelBody(Parcel in) {
        return new KeyEvent(in);
    }

    private KeyEvent(Parcel in) {
        this.mDeviceId = in.readInt();
        this.mSource = in.readInt();
        this.mAction = in.readInt();
        this.mKeyCode = in.readInt();
        this.mRepeatCount = in.readInt();
        this.mMetaState = in.readInt();
        this.mScanCode = in.readInt();
        this.mFlags = in.readInt();
        this.mDownTime = in.readLong();
        this.mEventTime = in.readLong();
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeInt(2);
        out.writeInt(this.mDeviceId);
        out.writeInt(this.mSource);
        out.writeInt(this.mAction);
        out.writeInt(this.mKeyCode);
        out.writeInt(this.mRepeatCount);
        out.writeInt(this.mMetaState);
        out.writeInt(this.mScanCode);
        out.writeInt(this.mFlags);
        out.writeLong(this.mDownTime);
        out.writeLong(this.mEventTime);
    }
}