package gov.nist.javax.sip.parser;

import java.io.IOException;
import java.io.InputStream;

/* loaded from: PipelinedMsgParser.class */
public final class PipelinedMsgParser implements Runnable {
    protected SIPMessageListener sipMessageListener;
    private Thread mythread;
    private Pipeline rawInputStream;
    private int maxMessageSize;
    private int sizeCounter;
    private static int uid = 0;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.PipelinedMsgParser.run():void, file: PipelinedMsgParser.class
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
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.parser.PipelinedMsgParser.run():void, file: PipelinedMsgParser.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.parser.PipelinedMsgParser.run():void");
    }

    protected PipelinedMsgParser() {
    }

    private static synchronized int getNewUid() {
        int i = uid;
        uid = i + 1;
        return i;
    }

    public PipelinedMsgParser(SIPMessageListener sipMessageListener, Pipeline in, boolean debug, int maxMessageSize) {
        this();
        this.sipMessageListener = sipMessageListener;
        this.rawInputStream = in;
        this.maxMessageSize = maxMessageSize;
        this.mythread = new Thread(this);
        this.mythread.setName("PipelineThread-" + getNewUid());
    }

    public PipelinedMsgParser(SIPMessageListener mhandler, Pipeline in, int maxMsgSize) {
        this(mhandler, in, false, maxMsgSize);
    }

    public PipelinedMsgParser(Pipeline in) {
        this(null, in, false, 0);
    }

    public void processInput() {
        this.mythread.start();
    }

    protected Object clone() {
        PipelinedMsgParser p = new PipelinedMsgParser();
        p.rawInputStream = this.rawInputStream;
        p.sipMessageListener = this.sipMessageListener;
        Thread mythread = new Thread(p);
        mythread.setName("PipelineThread");
        return p;
    }

    public void setMessageListener(SIPMessageListener mlistener) {
        this.sipMessageListener = mlistener;
    }

    private String readLine(InputStream inputStream) throws IOException {
        char ch;
        StringBuffer retval = new StringBuffer("");
        do {
            int i = inputStream.read();
            if (i == -1) {
                throw new IOException("End of stream");
            }
            ch = (char) i;
            if (this.maxMessageSize > 0) {
                this.sizeCounter--;
                if (this.sizeCounter <= 0) {
                    throw new IOException("Max size exceeded!");
                }
            }
            if (ch != '\r') {
                retval.append(ch);
            }
        } while (ch != '\n');
        return retval.toString();
    }

    public void close() {
        try {
            this.rawInputStream.close();
        } catch (IOException e) {
        }
    }
}