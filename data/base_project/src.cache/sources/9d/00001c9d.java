package com.android.server;

import com.google.android.collect.Lists;
import gov.nist.core.Separators;
import java.util.ArrayList;

/* loaded from: NativeDaemonEvent.class */
public class NativeDaemonEvent {
    private final int mCmdNumber;
    private final int mCode;
    private final String mMessage;
    private final String mRawEvent;
    private String[] mParsed = null;

    private NativeDaemonEvent(int cmdNumber, int code, String message, String rawEvent) {
        this.mCmdNumber = cmdNumber;
        this.mCode = code;
        this.mMessage = message;
        this.mRawEvent = rawEvent;
    }

    public int getCmdNumber() {
        return this.mCmdNumber;
    }

    public int getCode() {
        return this.mCode;
    }

    public String getMessage() {
        return this.mMessage;
    }

    @Deprecated
    public String getRawEvent() {
        return this.mRawEvent;
    }

    public String toString() {
        return this.mRawEvent;
    }

    public boolean isClassContinue() {
        return this.mCode >= 100 && this.mCode < 200;
    }

    public boolean isClassOk() {
        return this.mCode >= 200 && this.mCode < 300;
    }

    public boolean isClassServerError() {
        return this.mCode >= 400 && this.mCode < 500;
    }

    public boolean isClassClientError() {
        return this.mCode >= 500 && this.mCode < 600;
    }

    public boolean isClassUnsolicited() {
        return isClassUnsolicited(this.mCode);
    }

    private static boolean isClassUnsolicited(int code) {
        return code >= 600 && code < 700;
    }

    public void checkCode(int code) {
        if (this.mCode != code) {
            throw new IllegalStateException("Expected " + code + " but was: " + this);
        }
    }

    public static NativeDaemonEvent parseRawEvent(String rawEvent) {
        String[] parsed = rawEvent.split(Separators.SP);
        if (parsed.length < 2) {
            throw new IllegalArgumentException("Insufficient arguments");
        }
        try {
            int code = Integer.parseInt(parsed[0]);
            int skiplength = parsed[0].length() + 1;
            int cmdNumber = -1;
            if (!isClassUnsolicited(code)) {
                if (parsed.length < 3) {
                    throw new IllegalArgumentException("Insufficient arguemnts");
                }
                try {
                    cmdNumber = Integer.parseInt(parsed[1]);
                    skiplength += parsed[1].length() + 1;
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("problem parsing cmdNumber", e);
                }
            }
            String message = rawEvent.substring(skiplength);
            return new NativeDaemonEvent(cmdNumber, code, message, rawEvent);
        } catch (NumberFormatException e2) {
            throw new IllegalArgumentException("problem parsing code", e2);
        }
    }

    public static String[] filterMessageList(NativeDaemonEvent[] events, int matchCode) {
        ArrayList<String> result = Lists.newArrayList();
        for (NativeDaemonEvent event : events) {
            if (event.getCode() == matchCode) {
                result.add(event.getMessage());
            }
        }
        return (String[]) result.toArray(new String[result.size()]);
    }

    public String getField(int n) {
        if (this.mParsed == null) {
            this.mParsed = unescapeArgs(this.mRawEvent);
        }
        int n2 = n + 2;
        if (n2 > this.mParsed.length) {
            return null;
        }
        return this.mParsed[n2];
    }

    public static String[] unescapeArgs(String rawEvent) {
        int wordEnd;
        ArrayList<String> parsed = new ArrayList<>();
        int length = rawEvent.length();
        int current = 0;
        boolean quoted = false;
        if (rawEvent.charAt(0) == '\"') {
            quoted = true;
            current = 0 + 1;
        }
        while (current < length) {
            if (quoted) {
                int wordEnd2 = current;
                while (true) {
                    int indexOf = rawEvent.indexOf(34, wordEnd2);
                    wordEnd = indexOf;
                    if (indexOf == -1 || rawEvent.charAt(wordEnd - 1) != '\\') {
                        break;
                    }
                    wordEnd2 = wordEnd + 1;
                }
            } else {
                wordEnd = rawEvent.indexOf(32, current);
            }
            if (wordEnd == -1) {
                wordEnd = length;
            }
            String word = rawEvent.substring(current, wordEnd);
            current += word.length();
            if (!quoted) {
                word = word.trim();
            } else {
                current++;
            }
            parsed.add(word.replace("\\\\", "\\").replace("\\\"", Separators.DOUBLE_QUOTE));
            int nextSpace = rawEvent.indexOf(32, current);
            int nextQuote = rawEvent.indexOf(" \"", current);
            if (nextQuote > -1 && nextQuote <= nextSpace) {
                quoted = true;
                current = nextQuote + 2;
            } else {
                quoted = false;
                if (nextSpace > -1) {
                    current = nextSpace + 1;
                }
            }
        }
        return (String[]) parsed.toArray(new String[parsed.size()]);
    }
}