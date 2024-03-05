package gov.nist.javax.sip;

import java.util.LinkedList;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: EventScanner.class */
public class EventScanner implements Runnable {
    private boolean isStopped;
    private int refCount;
    private LinkedList pendingEvents;
    private int[] eventMutex = {0};
    private SipStackImpl sipStack;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.EventScanner.deliverEvent(gov.nist.javax.sip.EventWrapper):void, file: EventScanner.class
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
    public void deliverEvent(gov.nist.javax.sip.EventWrapper r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.EventScanner.deliverEvent(gov.nist.javax.sip.EventWrapper):void, file: EventScanner.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.EventScanner.deliverEvent(gov.nist.javax.sip.EventWrapper):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.EventScanner.run():void, file: EventScanner.class
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
    @Override // java.lang.Runnable
    public void run() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.EventScanner.run():void, file: EventScanner.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.EventScanner.run():void");
    }

    public void incrementRefcount() {
        synchronized (this.eventMutex) {
            this.refCount++;
        }
    }

    public EventScanner(SipStackImpl sipStackImpl) {
        this.pendingEvents = new LinkedList();
        this.pendingEvents = new LinkedList();
        Thread myThread = new Thread(this);
        myThread.setDaemon(false);
        this.sipStack = sipStackImpl;
        myThread.setName("EventScannerThread");
        myThread.start();
    }

    public void addEvent(EventWrapper eventWrapper) {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("addEvent " + eventWrapper);
        }
        synchronized (this.eventMutex) {
            this.pendingEvents.add(eventWrapper);
            this.eventMutex.notify();
        }
    }

    public void stop() {
        synchronized (this.eventMutex) {
            if (this.refCount > 0) {
                this.refCount--;
            }
            if (this.refCount == 0) {
                this.isStopped = true;
                this.eventMutex.notify();
            }
        }
    }

    public void forceStop() {
        synchronized (this.eventMutex) {
            this.isStopped = true;
            this.refCount = 0;
            this.eventMutex.notify();
        }
    }
}