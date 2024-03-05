package com.android.server.wm;

import android.view.InputChannel;
import android.view.InputEventReceiver;
import android.view.WindowManagerPolicy;
import com.android.server.UiThread;
import java.util.ArrayList;

/* loaded from: PointerEventDispatcher.class */
public class PointerEventDispatcher extends InputEventReceiver {
    ArrayList<WindowManagerPolicy.PointerEventListener> mListeners;
    WindowManagerPolicy.PointerEventListener[] mListenersArray;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.PointerEventDispatcher.onInputEvent(android.view.InputEvent):void, file: PointerEventDispatcher.class
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
    @Override // android.view.InputEventReceiver
    public void onInputEvent(android.view.InputEvent r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: com.android.server.wm.PointerEventDispatcher.onInputEvent(android.view.InputEvent):void, file: PointerEventDispatcher.class
        */
        throw new UnsupportedOperationException("Method not decompiled: com.android.server.wm.PointerEventDispatcher.onInputEvent(android.view.InputEvent):void");
    }

    public PointerEventDispatcher(InputChannel inputChannel) {
        super(inputChannel, UiThread.getHandler().getLooper());
        this.mListeners = new ArrayList<>();
        this.mListenersArray = new WindowManagerPolicy.PointerEventListener[0];
    }

    public void registerInputEventListener(WindowManagerPolicy.PointerEventListener listener) {
        synchronized (this.mListeners) {
            if (this.mListeners.contains(listener)) {
                throw new IllegalStateException("registerInputEventListener: trying to register" + listener + " twice.");
            }
            this.mListeners.add(listener);
            this.mListenersArray = null;
        }
    }

    public void unregisterInputEventListener(WindowManagerPolicy.PointerEventListener listener) {
        synchronized (this.mListeners) {
            if (!this.mListeners.contains(listener)) {
                throw new IllegalStateException("registerInputEventListener: " + listener + " not registered.");
            }
            this.mListeners.remove(listener);
            this.mListenersArray = null;
        }
    }
}