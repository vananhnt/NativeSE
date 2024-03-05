package com.android.server.input;

import android.provider.Telephony;
import android.util.AtomicFile;
import android.util.Slog;
import com.android.internal.util.ArrayUtils;
import com.android.internal.util.XmlUtils;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import libcore.util.Objects;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: PersistentDataStore.class */
public final class PersistentDataStore {
    static final String TAG = "InputManager";
    private final HashMap<String, InputDeviceState> mInputDevices = new HashMap<>();
    private final AtomicFile mAtomicFile = new AtomicFile(new File("/data/system/input-manager-state.xml"));
    private boolean mLoaded;
    private boolean mDirty;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.PersistentDataStore.load():void, file: PersistentDataStore.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void load() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.PersistentDataStore.load():void, file: PersistentDataStore.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.PersistentDataStore.load():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.PersistentDataStore.save():void, file: PersistentDataStore.class
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
        	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
        	at jadx.core.ProcessClass.process(ProcessClass.java:67)
        	at jadx.core.ProcessClass.generateCode(ProcessClass.java:115)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
        	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
        Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
        	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
        	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
        	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
        	... 5 more
        */
    private void save() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.input.PersistentDataStore.save():void, file: PersistentDataStore.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.input.PersistentDataStore.save():void");
    }

    public void saveIfNeeded() {
        if (this.mDirty) {
            save();
            this.mDirty = false;
        }
    }

    public String getCurrentKeyboardLayout(String inputDeviceDescriptor) {
        InputDeviceState state = getInputDeviceState(inputDeviceDescriptor, false);
        if (state != null) {
            return state.getCurrentKeyboardLayout();
        }
        return null;
    }

    public boolean setCurrentKeyboardLayout(String inputDeviceDescriptor, String keyboardLayoutDescriptor) {
        InputDeviceState state = getInputDeviceState(inputDeviceDescriptor, true);
        if (state.setCurrentKeyboardLayout(keyboardLayoutDescriptor)) {
            setDirty();
            return true;
        }
        return false;
    }

    public String[] getKeyboardLayouts(String inputDeviceDescriptor) {
        InputDeviceState state = getInputDeviceState(inputDeviceDescriptor, false);
        if (state == null) {
            return (String[]) ArrayUtils.emptyArray(String.class);
        }
        return state.getKeyboardLayouts();
    }

    public boolean addKeyboardLayout(String inputDeviceDescriptor, String keyboardLayoutDescriptor) {
        InputDeviceState state = getInputDeviceState(inputDeviceDescriptor, true);
        if (state.addKeyboardLayout(keyboardLayoutDescriptor)) {
            setDirty();
            return true;
        }
        return false;
    }

    public boolean removeKeyboardLayout(String inputDeviceDescriptor, String keyboardLayoutDescriptor) {
        InputDeviceState state = getInputDeviceState(inputDeviceDescriptor, true);
        if (state.removeKeyboardLayout(keyboardLayoutDescriptor)) {
            setDirty();
            return true;
        }
        return false;
    }

    public boolean switchKeyboardLayout(String inputDeviceDescriptor, int direction) {
        InputDeviceState state = getInputDeviceState(inputDeviceDescriptor, false);
        if (state != null && state.switchKeyboardLayout(direction)) {
            setDirty();
            return true;
        }
        return false;
    }

    public boolean removeUninstalledKeyboardLayouts(Set<String> availableKeyboardLayouts) {
        boolean changed = false;
        for (InputDeviceState state : this.mInputDevices.values()) {
            if (state.removeUninstalledKeyboardLayouts(availableKeyboardLayouts)) {
                changed = true;
            }
        }
        if (changed) {
            setDirty();
            return true;
        }
        return false;
    }

    private InputDeviceState getInputDeviceState(String inputDeviceDescriptor, boolean createIfAbsent) {
        loadIfNeeded();
        InputDeviceState state = this.mInputDevices.get(inputDeviceDescriptor);
        if (state == null && createIfAbsent) {
            state = new InputDeviceState();
            this.mInputDevices.put(inputDeviceDescriptor, state);
            setDirty();
        }
        return state;
    }

    private void loadIfNeeded() {
        if (!this.mLoaded) {
            load();
            this.mLoaded = true;
        }
    }

    private void setDirty() {
        this.mDirty = true;
    }

    private void clearState() {
        this.mInputDevices.clear();
    }

    private void loadFromXml(XmlPullParser parser) throws IOException, XmlPullParserException {
        XmlUtils.beginDocument(parser, "input-manager-state");
        int outerDepth = parser.getDepth();
        while (XmlUtils.nextElementWithin(parser, outerDepth)) {
            if (parser.getName().equals("input-devices")) {
                loadInputDevicesFromXml(parser);
            }
        }
    }

    private void loadInputDevicesFromXml(XmlPullParser parser) throws IOException, XmlPullParserException {
        int outerDepth = parser.getDepth();
        while (XmlUtils.nextElementWithin(parser, outerDepth)) {
            if (parser.getName().equals("input-device")) {
                String descriptor = parser.getAttributeValue(null, "descriptor");
                if (descriptor == null) {
                    throw new XmlPullParserException("Missing descriptor attribute on input-device.");
                }
                if (this.mInputDevices.containsKey(descriptor)) {
                    throw new XmlPullParserException("Found duplicate input device.");
                }
                InputDeviceState state = new InputDeviceState();
                state.loadFromXml(parser);
                this.mInputDevices.put(descriptor, state);
            }
        }
    }

    private void saveToXml(XmlSerializer serializer) throws IOException {
        serializer.startDocument(null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        serializer.startTag(null, "input-manager-state");
        serializer.startTag(null, "input-devices");
        for (Map.Entry<String, InputDeviceState> entry : this.mInputDevices.entrySet()) {
            String descriptor = entry.getKey();
            InputDeviceState state = entry.getValue();
            serializer.startTag(null, "input-device");
            serializer.attribute(null, "descriptor", descriptor);
            state.saveToXml(serializer);
            serializer.endTag(null, "input-device");
        }
        serializer.endTag(null, "input-devices");
        serializer.endTag(null, "input-manager-state");
        serializer.endDocument();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: PersistentDataStore$InputDeviceState.class */
    public static final class InputDeviceState {
        private String mCurrentKeyboardLayout;
        private ArrayList<String> mKeyboardLayouts;
        static final /* synthetic */ boolean $assertionsDisabled;

        private InputDeviceState() {
            this.mKeyboardLayouts = new ArrayList<>();
        }

        static {
            $assertionsDisabled = !PersistentDataStore.class.desiredAssertionStatus();
        }

        public String getCurrentKeyboardLayout() {
            return this.mCurrentKeyboardLayout;
        }

        public boolean setCurrentKeyboardLayout(String keyboardLayout) {
            if (Objects.equal(this.mCurrentKeyboardLayout, keyboardLayout)) {
                return false;
            }
            addKeyboardLayout(keyboardLayout);
            this.mCurrentKeyboardLayout = keyboardLayout;
            return true;
        }

        public String[] getKeyboardLayouts() {
            if (this.mKeyboardLayouts.isEmpty()) {
                return (String[]) ArrayUtils.emptyArray(String.class);
            }
            return (String[]) this.mKeyboardLayouts.toArray(new String[this.mKeyboardLayouts.size()]);
        }

        public boolean addKeyboardLayout(String keyboardLayout) {
            int index = Collections.binarySearch(this.mKeyboardLayouts, keyboardLayout);
            if (index >= 0) {
                return false;
            }
            this.mKeyboardLayouts.add((-index) - 1, keyboardLayout);
            if (this.mCurrentKeyboardLayout == null) {
                this.mCurrentKeyboardLayout = keyboardLayout;
                return true;
            }
            return true;
        }

        public boolean removeKeyboardLayout(String keyboardLayout) {
            int index = Collections.binarySearch(this.mKeyboardLayouts, keyboardLayout);
            if (index < 0) {
                return false;
            }
            this.mKeyboardLayouts.remove(index);
            updateCurrentKeyboardLayoutIfRemoved(keyboardLayout, index);
            return true;
        }

        private void updateCurrentKeyboardLayoutIfRemoved(String removedKeyboardLayout, int removedIndex) {
            if (Objects.equal(this.mCurrentKeyboardLayout, removedKeyboardLayout)) {
                if (!this.mKeyboardLayouts.isEmpty()) {
                    int index = removedIndex;
                    if (index == this.mKeyboardLayouts.size()) {
                        index = 0;
                    }
                    this.mCurrentKeyboardLayout = this.mKeyboardLayouts.get(index);
                    return;
                }
                this.mCurrentKeyboardLayout = null;
            }
        }

        public boolean switchKeyboardLayout(int direction) {
            int index;
            int size = this.mKeyboardLayouts.size();
            if (size < 2) {
                return false;
            }
            int index2 = Collections.binarySearch(this.mKeyboardLayouts, this.mCurrentKeyboardLayout);
            if ($assertionsDisabled || index2 >= 0) {
                if (direction > 0) {
                    index = (index2 + 1) % size;
                } else {
                    index = ((index2 + size) - 1) % size;
                }
                this.mCurrentKeyboardLayout = this.mKeyboardLayouts.get(index);
                return true;
            }
            throw new AssertionError();
        }

        public boolean removeUninstalledKeyboardLayouts(Set<String> availableKeyboardLayouts) {
            boolean changed = false;
            int i = this.mKeyboardLayouts.size();
            while (true) {
                int i2 = i;
                i--;
                if (i2 > 0) {
                    String keyboardLayout = this.mKeyboardLayouts.get(i);
                    if (!availableKeyboardLayouts.contains(keyboardLayout)) {
                        Slog.i(PersistentDataStore.TAG, "Removing uninstalled keyboard layout " + keyboardLayout);
                        this.mKeyboardLayouts.remove(i);
                        updateCurrentKeyboardLayoutIfRemoved(keyboardLayout, i);
                        changed = true;
                    }
                } else {
                    return changed;
                }
            }
        }

        public void loadFromXml(XmlPullParser parser) throws IOException, XmlPullParserException {
            int outerDepth = parser.getDepth();
            while (XmlUtils.nextElementWithin(parser, outerDepth)) {
                if (parser.getName().equals("keyboard-layout")) {
                    String descriptor = parser.getAttributeValue(null, "descriptor");
                    if (descriptor == null) {
                        throw new XmlPullParserException("Missing descriptor attribute on keyboard-layout.");
                    }
                    String current = parser.getAttributeValue(null, Telephony.Carriers.CURRENT);
                    if (this.mKeyboardLayouts.contains(descriptor)) {
                        throw new XmlPullParserException("Found duplicate keyboard layout.");
                    }
                    this.mKeyboardLayouts.add(descriptor);
                    if (current != null && current.equals("true")) {
                        if (this.mCurrentKeyboardLayout != null) {
                            throw new XmlPullParserException("Found multiple current keyboard layouts.");
                        }
                        this.mCurrentKeyboardLayout = descriptor;
                    }
                }
            }
            Collections.sort(this.mKeyboardLayouts);
            if (this.mCurrentKeyboardLayout == null && !this.mKeyboardLayouts.isEmpty()) {
                this.mCurrentKeyboardLayout = this.mKeyboardLayouts.get(0);
            }
        }

        public void saveToXml(XmlSerializer serializer) throws IOException {
            Iterator i$ = this.mKeyboardLayouts.iterator();
            while (i$.hasNext()) {
                String layout = i$.next();
                serializer.startTag(null, "keyboard-layout");
                serializer.attribute(null, "descriptor", layout);
                if (layout.equals(this.mCurrentKeyboardLayout)) {
                    serializer.attribute(null, Telephony.Carriers.CURRENT, "true");
                }
                serializer.endTag(null, "keyboard-layout");
            }
        }
    }
}