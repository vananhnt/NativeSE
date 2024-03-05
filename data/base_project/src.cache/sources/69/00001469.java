package android.util;

import java.io.BufferedReader;
import java.io.IOException;

@Deprecated
/* loaded from: EventLogTags.class */
public class EventLogTags {

    /* loaded from: EventLogTags$Description.class */
    public static class Description {
        public final int mTag;
        public final String mName;

        Description(int tag, String name) {
            this.mTag = tag;
            this.mName = name;
        }
    }

    public EventLogTags() throws IOException {
    }

    public EventLogTags(BufferedReader input) throws IOException {
    }

    public Description get(String name) {
        return null;
    }

    public Description get(int tag) {
        return null;
    }
}