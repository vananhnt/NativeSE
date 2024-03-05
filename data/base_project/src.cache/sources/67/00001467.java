package android.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Collection;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/* loaded from: EventLog.class */
public class EventLog {
    private static final String TAG = "EventLog";
    private static final String TAGS_FILE = "/system/etc/event-log-tags";
    private static final String COMMENT_PATTERN = "^\\s*(#.*)?$";
    private static final String TAG_PATTERN = "^\\s*(\\d+)\\s+(\\w+)\\s*(\\(.*\\))?\\s*$";
    private static HashMap<String, Integer> sTagCodes = null;
    private static HashMap<Integer, String> sTagNames = null;

    public static native int writeEvent(int i, int i2);

    public static native int writeEvent(int i, long j);

    public static native int writeEvent(int i, String str);

    public static native int writeEvent(int i, Object... objArr);

    public static native void readEvents(int[] iArr, Collection<Event> collection) throws IOException;

    /* loaded from: EventLog$Event.class */
    public static final class Event {
        private final ByteBuffer mBuffer;
        private static final int LENGTH_OFFSET = 0;
        private static final int PROCESS_OFFSET = 4;
        private static final int THREAD_OFFSET = 8;
        private static final int SECONDS_OFFSET = 12;
        private static final int NANOSECONDS_OFFSET = 16;
        private static final int PAYLOAD_START = 20;
        private static final int TAG_OFFSET = 20;
        private static final int DATA_START = 24;
        private static final byte INT_TYPE = 0;
        private static final byte LONG_TYPE = 1;
        private static final byte STRING_TYPE = 2;
        private static final byte LIST_TYPE = 3;

        Event(byte[] data) {
            this.mBuffer = ByteBuffer.wrap(data);
            this.mBuffer.order(ByteOrder.nativeOrder());
        }

        public int getProcessId() {
            return this.mBuffer.getInt(4);
        }

        public int getThreadId() {
            return this.mBuffer.getInt(8);
        }

        public long getTimeNanos() {
            return (this.mBuffer.getInt(12) * 1000000000) + this.mBuffer.getInt(16);
        }

        public int getTag() {
            return this.mBuffer.getInt(20);
        }

        public synchronized Object getData() {
            try {
                this.mBuffer.limit(20 + this.mBuffer.getShort(0));
                this.mBuffer.position(24);
                return decodeObject();
            } catch (IllegalArgumentException e) {
                Log.wtf(EventLog.TAG, "Illegal entry payload: tag=" + getTag(), e);
                return null;
            } catch (BufferUnderflowException e2) {
                Log.wtf(EventLog.TAG, "Truncated entry payload: tag=" + getTag(), e2);
                return null;
            }
        }

        private Object decodeObject() {
            byte type = this.mBuffer.get();
            switch (type) {
                case 0:
                    return Integer.valueOf(this.mBuffer.getInt());
                case 1:
                    return Long.valueOf(this.mBuffer.getLong());
                case 2:
                    try {
                        int length = this.mBuffer.getInt();
                        int start = this.mBuffer.position();
                        this.mBuffer.position(start + length);
                        return new String(this.mBuffer.array(), start, length, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        Log.wtf(EventLog.TAG, "UTF-8 is not supported", e);
                        return null;
                    }
                case 3:
                    int length2 = this.mBuffer.get();
                    if (length2 < 0) {
                        length2 += 256;
                    }
                    Object[] array = new Object[length2];
                    for (int i = 0; i < length2; i++) {
                        array[i] = decodeObject();
                    }
                    return array;
                default:
                    throw new IllegalArgumentException("Unknown entry type: " + ((int) type));
            }
        }
    }

    public static String getTagName(int tag) {
        readTagsFile();
        return sTagNames.get(Integer.valueOf(tag));
    }

    public static int getTagCode(String name) {
        readTagsFile();
        Integer code = sTagCodes.get(name);
        if (code != null) {
            return code.intValue();
        }
        return -1;
    }

    private static synchronized void readTagsFile() {
        if (sTagCodes == null || sTagNames == null) {
            sTagCodes = new HashMap<>();
            sTagNames = new HashMap<>();
            Pattern comment = Pattern.compile(COMMENT_PATTERN);
            Pattern tag = Pattern.compile(TAG_PATTERN);
            BufferedReader reader = null;
            try {
                try {
                    reader = new BufferedReader(new FileReader(TAGS_FILE), 256);
                    while (true) {
                        String line = reader.readLine();
                        if (line == null) {
                            break;
                        } else if (!comment.matcher(line).matches()) {
                            Matcher m = tag.matcher(line);
                            if (!m.matches()) {
                                Log.wtf(TAG, "Bad entry in /system/etc/event-log-tags: " + line);
                            } else {
                                try {
                                    int num = Integer.parseInt(m.group(1));
                                    String name = m.group(2);
                                    sTagCodes.put(name, Integer.valueOf(num));
                                    sTagNames.put(Integer.valueOf(num), name);
                                } catch (NumberFormatException e) {
                                    Log.wtf(TAG, "Error in /system/etc/event-log-tags: " + line, e);
                                }
                            }
                        }
                    }
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e2) {
                        }
                    }
                } catch (IOException e3) {
                    Log.wtf(TAG, "Error reading /system/etc/event-log-tags", e3);
                    if (reader != null) {
                        try {
                            reader.close();
                        } catch (IOException e4) {
                        }
                    }
                }
            } catch (Throwable th) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e5) {
                        throw th;
                    }
                }
                throw th;
            }
        }
    }
}