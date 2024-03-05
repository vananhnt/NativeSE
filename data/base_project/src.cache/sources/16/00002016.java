package com.android.server.wm;

import android.content.Context;
import android.graphics.Rect;
import android.os.Environment;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.util.FastXmlSerializer;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* loaded from: DisplaySettings.class */
public class DisplaySettings {
    private static final String TAG = "WindowManager";
    private final Context mContext;
    private final AtomicFile mFile;
    private final HashMap<String, Entry> mEntries = new HashMap<>();

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.DisplaySettings.readSettingsLocked():void, file: DisplaySettings.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    public void readSettingsLocked() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.DisplaySettings.readSettingsLocked():void, file: DisplaySettings.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.DisplaySettings.readSettingsLocked():void");
    }

    /* loaded from: DisplaySettings$Entry.class */
    public static class Entry {
        public final String name;
        public int overscanLeft;
        public int overscanTop;
        public int overscanRight;
        public int overscanBottom;

        public Entry(String _name) {
            this.name = _name;
        }
    }

    public DisplaySettings(Context context) {
        this.mContext = context;
        File dataDir = Environment.getDataDirectory();
        File systemDir = new File(dataDir, "system");
        this.mFile = new AtomicFile(new File(systemDir, "display_settings.xml"));
    }

    public void getOverscanLocked(String name, Rect outRect) {
        Entry entry = this.mEntries.get(name);
        if (entry != null) {
            outRect.left = entry.overscanLeft;
            outRect.top = entry.overscanTop;
            outRect.right = entry.overscanRight;
            outRect.bottom = entry.overscanBottom;
            return;
        }
        outRect.set(0, 0, 0, 0);
    }

    public void setOverscanLocked(String name, int left, int top, int right, int bottom) {
        if (left == 0 && top == 0 && right == 0 && bottom == 0) {
            this.mEntries.remove(name);
            return;
        }
        Entry entry = this.mEntries.get(name);
        if (entry == null) {
            entry = new Entry(name);
            this.mEntries.put(name, entry);
        }
        entry.overscanLeft = left;
        entry.overscanTop = top;
        entry.overscanRight = right;
        entry.overscanBottom = bottom;
    }

    private int getIntAttribute(XmlPullParser parser, String name) {
        try {
            String str = parser.getAttributeValue(null, name);
            if (str != null) {
                return Integer.parseInt(str);
            }
            return 0;
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void readDisplay(XmlPullParser parser) throws NumberFormatException, XmlPullParserException, IOException {
        String name = parser.getAttributeValue(null, "name");
        if (name != null) {
            Entry entry = new Entry(name);
            entry.overscanLeft = getIntAttribute(parser, "overscanLeft");
            entry.overscanTop = getIntAttribute(parser, "overscanTop");
            entry.overscanRight = getIntAttribute(parser, "overscanRight");
            entry.overscanBottom = getIntAttribute(parser, "overscanBottom");
            this.mEntries.put(name, entry);
        }
        XmlUtils.skipCurrentTag(parser);
    }

    public void writeSettingsLocked() {
        try {
            FileOutputStream stream = this.mFile.startWrite();
            try {
                XmlSerializer out = new FastXmlSerializer();
                out.setOutput(stream, "utf-8");
                out.startDocument(null, true);
                out.startTag(null, "display-settings");
                for (Entry entry : this.mEntries.values()) {
                    out.startTag(null, Context.DISPLAY_SERVICE);
                    out.attribute(null, "name", entry.name);
                    if (entry.overscanLeft != 0) {
                        out.attribute(null, "overscanLeft", Integer.toString(entry.overscanLeft));
                    }
                    if (entry.overscanTop != 0) {
                        out.attribute(null, "overscanTop", Integer.toString(entry.overscanTop));
                    }
                    if (entry.overscanRight != 0) {
                        out.attribute(null, "overscanRight", Integer.toString(entry.overscanRight));
                    }
                    if (entry.overscanBottom != 0) {
                        out.attribute(null, "overscanBottom", Integer.toString(entry.overscanBottom));
                    }
                    out.endTag(null, Context.DISPLAY_SERVICE);
                }
                out.endTag(null, "display-settings");
                out.endDocument();
                this.mFile.finishWrite(stream);
            } catch (IOException e) {
                Slog.w(TAG, "Failed to write display settings, restoring backup.", e);
                this.mFile.failWrite(stream);
            }
        } catch (IOException e2) {
            Slog.w(TAG, "Failed to write display settings: " + e2);
        }
    }
}