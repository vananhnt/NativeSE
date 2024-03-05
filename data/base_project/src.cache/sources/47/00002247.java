package gov.nist.javax.sip.stack;

import android.widget.ExpandableListView;
import gov.nist.core.InternalErrorHandler;
import gov.nist.core.NameValueList;
import gov.nist.core.Separators;
import gov.nist.javax.sip.DialogExt;
import gov.nist.javax.sip.ListeningPointImpl;
import gov.nist.javax.sip.SipListenerExt;
import gov.nist.javax.sip.SipProviderImpl;
import gov.nist.javax.sip.Utils;
import gov.nist.javax.sip.address.SipUri;
import gov.nist.javax.sip.header.Authorization;
import gov.nist.javax.sip.header.CSeq;
import gov.nist.javax.sip.header.Contact;
import gov.nist.javax.sip.header.ContactList;
import gov.nist.javax.sip.header.From;
import gov.nist.javax.sip.header.MaxForwards;
import gov.nist.javax.sip.header.RAck;
import gov.nist.javax.sip.header.RSeq;
import gov.nist.javax.sip.header.Reason;
import gov.nist.javax.sip.header.RecordRoute;
import gov.nist.javax.sip.header.RecordRouteList;
import gov.nist.javax.sip.header.Require;
import gov.nist.javax.sip.header.Route;
import gov.nist.javax.sip.header.RouteList;
import gov.nist.javax.sip.header.SIPHeader;
import gov.nist.javax.sip.header.TimeStamp;
import gov.nist.javax.sip.header.To;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.message.MessageFactoryImpl;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import javax.sip.ClientTransaction;
import javax.sip.Dialog;
import javax.sip.DialogDoesNotExistException;
import javax.sip.DialogState;
import javax.sip.IOExceptionEvent;
import javax.sip.InvalidArgumentException;
import javax.sip.ListeningPoint;
import javax.sip.ObjectInUseException;
import javax.sip.SipException;
import javax.sip.Transaction;
import javax.sip.TransactionDoesNotExistException;
import javax.sip.TransactionState;
import javax.sip.address.Address;
import javax.sip.address.Hop;
import javax.sip.address.SipURI;
import javax.sip.header.CallIdHeader;
import javax.sip.header.ContactHeader;
import javax.sip.header.EventHeader;
import javax.sip.header.Header;
import javax.sip.header.OptionTag;
import javax.sip.header.ReasonHeader;
import javax.sip.header.RequireHeader;
import javax.sip.message.Request;
import javax.sip.message.Response;

/* loaded from: SIPDialog.class */
public class SIPDialog implements Dialog, DialogExt {
    private static final long serialVersionUID = -1429794423085204069L;
    private transient boolean dialogTerminatedEventDelivered;
    private transient String stackTrace;
    private String method;
    private transient boolean isAssigned;
    private boolean reInviteFlag;
    private transient Object applicationData;
    private transient SIPRequest originalRequest;
    private SIPResponse lastResponse;
    private transient SIPTransaction firstTransaction;
    private transient SIPTransaction lastTransaction;
    private String dialogId;
    private transient String earlyDialogId;
    private long localSequenceNumber;
    private long remoteSequenceNumber;
    protected String myTag;
    protected String hisTag;
    private RouteList routeList;
    private transient SIPTransactionStack sipStack;
    private int dialogState;
    protected transient boolean ackSeen;
    private transient SIPRequest lastAckSent;
    private SIPRequest lastAckReceived;
    protected transient boolean ackProcessed;
    protected transient DialogTimerTask timerTask;
    protected transient Long nextSeqno;
    private transient int retransmissionTicksLeft;
    private transient int prevRetransmissionTicks;
    private long originalLocalSequenceNumber;
    private transient int ackLine;
    public transient long auditTag;
    protected Address localParty;
    protected Address remoteParty;
    protected CallIdHeader callIdHeader;
    public static final int NULL_STATE = -1;
    public static final int EARLY_STATE = DialogState._EARLY;
    public static final int CONFIRMED_STATE = DialogState._CONFIRMED;
    public static final int TERMINATED_STATE = DialogState._TERMINATED;
    private static final int DIALOG_LINGER_TIME = 8;
    private boolean serverTransactionFlag;
    private transient SipProviderImpl sipProvider;
    private boolean terminateOnBye;
    private transient boolean byeSent;
    private Address remoteTarget;
    private EventHeader eventHeader;
    private transient long lastInviteOkReceived;
    private transient Semaphore ackSem;
    private transient int reInviteWaitTime;
    private transient DialogDeleteTask dialogDeleteTask;
    private transient DialogDeleteIfNoAckSentTask dialogDeleteIfNoAckSentTask;
    private transient boolean isAcknowledged;
    private transient long highestSequenceNumberAcknowledged;
    private boolean isBackToBackUserAgent;
    private boolean sequenceNumberValidation;
    private transient Set<SIPDialogEventListener> eventListeners;
    private Semaphore timerTaskLock;
    protected boolean firstTransactionSecure;
    protected boolean firstTransactionSeen;
    protected String firstTransactionMethod;
    protected String firstTransactionId;
    protected boolean firstTransactionIsServerTransaction;
    protected int firstTransactionPort;
    protected Contact contactHeader;

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.addRoute(gov.nist.javax.sip.header.RecordRouteList):void, file: SIPDialog.class
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
    private void addRoute(gov.nist.javax.sip.header.RecordRouteList r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.addRoute(gov.nist.javax.sip.header.RecordRouteList):void, file: SIPDialog.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.SIPDialog.addRoute(gov.nist.javax.sip.header.RecordRouteList):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.addRoute(gov.nist.javax.sip.message.SIPResponse):void, file: SIPDialog.class
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
    private synchronized void addRoute(gov.nist.javax.sip.message.SIPResponse r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.addRoute(gov.nist.javax.sip.message.SIPResponse):void, file: SIPDialog.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.SIPDialog.addRoute(gov.nist.javax.sip.message.SIPResponse):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.ackReceived(gov.nist.javax.sip.message.SIPRequest):void, file: SIPDialog.class
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
    void ackReceived(gov.nist.javax.sip.message.SIPRequest r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.ackReceived(gov.nist.javax.sip.message.SIPRequest):void, file: SIPDialog.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.SIPDialog.ackReceived(gov.nist.javax.sip.message.SIPRequest):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.startTimer(gov.nist.javax.sip.stack.SIPServerTransaction):void, file: SIPDialog.class
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
    protected void startTimer(gov.nist.javax.sip.stack.SIPServerTransaction r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.startTimer(gov.nist.javax.sip.stack.SIPServerTransaction):void, file: SIPDialog.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.SIPDialog.startTimer(gov.nist.javax.sip.stack.SIPServerTransaction):void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.stopTimer():void, file: SIPDialog.class
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
    protected void stopTimer() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.stopTimer():void, file: SIPDialog.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.SIPDialog.stopTimer():void");
    }

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.handleAck(gov.nist.javax.sip.stack.SIPServerTransaction):boolean, file: SIPDialog.class
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
    public boolean handleAck(gov.nist.javax.sip.stack.SIPServerTransaction r1) {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.handleAck(gov.nist.javax.sip.stack.SIPServerTransaction):boolean, file: SIPDialog.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.SIPDialog.handleAck(gov.nist.javax.sip.stack.SIPServerTransaction):boolean");
    }

    static /* synthetic */ SIPTransactionStack access$000(SIPDialog x0) {
        return x0.sipStack;
    }

    static /* synthetic */ SipProviderImpl access$100(SIPDialog x0) {
        return x0.sipProvider;
    }

    static /* synthetic */ void access$200(SIPDialog x0, int x1) {
        x0.raiseErrorEvent(x1);
    }

    static /* synthetic */ boolean access$600(SIPDialog x0, int x1) {
        return x0.toRetransmitFinalResponse(x1);
    }

    /* loaded from: SIPDialog$ReInviteSender.class */
    public class ReInviteSender implements Runnable, Serializable {
        private static final long serialVersionUID = 1019346148741070635L;
        ClientTransaction ctx;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.ReInviteSender.run():void, file: SIPDialog$ReInviteSender.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // java.lang.Runnable
        public void run() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.ReInviteSender.run():void, file: SIPDialog$ReInviteSender.class
            */
            throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.SIPDialog.ReInviteSender.run():void");
        }

        public void terminate() {
            try {
                this.ctx.terminate();
                Thread.currentThread().interrupt();
            } catch (ObjectInUseException e) {
                SIPDialog.this.sipStack.getStackLogger().logError("unexpected error", e);
            }
        }

        public ReInviteSender(ClientTransaction ctx) {
            this.ctx = ctx;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SIPDialog$LingerTimer.class */
    public class LingerTimer extends SIPStackTimerTask implements Serializable {
        public LingerTimer() {
        }

        @Override // gov.nist.javax.sip.stack.SIPStackTimerTask
        protected void runTask() {
            SIPDialog dialog = SIPDialog.this;
            if (SIPDialog.this.eventListeners != null) {
                SIPDialog.this.eventListeners.clear();
            }
            SIPDialog.this.timerTaskLock = null;
            SIPDialog.this.sipStack.removeDialog(dialog);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SIPDialog$DialogTimerTask.class */
    public class DialogTimerTask extends SIPStackTimerTask implements Serializable {
        int nRetransmissions = 0;
        SIPServerTransaction transaction;

        /*  JADX ERROR: Method load error
            jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.DialogTimerTask.runTask():void, file: SIPDialog$DialogTimerTask.class
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:158)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:409)
            	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:415)
            	at jadx.core.ProcessClass.process(ProcessClass.java:67)
            	at jadx.core.ProcessClass.generateCode(ProcessClass.java:107)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:383)
            	at jadx.core.dex.nodes.ClassNode.decompile(ClassNode.java:307)
            Caused by: jadx.plugins.input.java.utils.JavaClassParseException: Unknown opcode: 0xa8
            	at jadx.plugins.input.java.data.code.JavaCodeReader.visitInstructions(JavaCodeReader.java:71)
            	at jadx.core.dex.instructions.InsnDecoder.process(InsnDecoder.java:48)
            	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:148)
            	... 6 more
            */
        @Override // gov.nist.javax.sip.stack.SIPStackTimerTask
        protected void runTask() {
            /*
            // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPDialog.DialogTimerTask.runTask():void, file: SIPDialog$DialogTimerTask.class
            */
            throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.SIPDialog.DialogTimerTask.runTask():void");
        }

        public DialogTimerTask(SIPServerTransaction transaction) {
            this.transaction = transaction;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: SIPDialog$DialogDeleteTask.class */
    public class DialogDeleteTask extends SIPStackTimerTask implements Serializable {
        DialogDeleteTask() {
        }

        @Override // gov.nist.javax.sip.stack.SIPStackTimerTask
        protected void runTask() {
            SIPDialog.this.delete();
        }
    }

    /* loaded from: SIPDialog$DialogDeleteIfNoAckSentTask.class */
    class DialogDeleteIfNoAckSentTask extends SIPStackTimerTask implements Serializable {
        private long seqno;

        public DialogDeleteIfNoAckSentTask(long seqno) {
            this.seqno = seqno;
        }

        @Override // gov.nist.javax.sip.stack.SIPStackTimerTask
        protected void runTask() {
            if (SIPDialog.this.highestSequenceNumberAcknowledged < this.seqno) {
                SIPDialog.this.dialogDeleteIfNoAckSentTask = null;
                if (!SIPDialog.this.isBackToBackUserAgent) {
                    if (SIPDialog.this.sipStack.isLoggingEnabled()) {
                        SIPDialog.this.sipStack.getStackLogger().logError("ACK Was not sent. killing dialog");
                    }
                    if (SIPDialog.this.sipProvider.getSipListener() instanceof SipListenerExt) {
                        SIPDialog.this.raiseErrorEvent(2);
                        return;
                    } else {
                        SIPDialog.this.delete();
                        return;
                    }
                }
                if (SIPDialog.this.sipStack.isLoggingEnabled()) {
                    SIPDialog.this.sipStack.getStackLogger().logError("ACK Was not sent. Sending BYE");
                }
                if (SIPDialog.this.sipProvider.getSipListener() instanceof SipListenerExt) {
                    SIPDialog.this.raiseErrorEvent(2);
                    return;
                }
                try {
                    Request byeRequest = SIPDialog.this.createRequest("BYE");
                    if (MessageFactoryImpl.getDefaultUserAgentHeader() != null) {
                        byeRequest.addHeader(MessageFactoryImpl.getDefaultUserAgentHeader());
                    }
                    ReasonHeader reasonHeader = new Reason();
                    reasonHeader.setProtocol("SIP");
                    reasonHeader.setCause(1025);
                    reasonHeader.setText("Timed out waiting to send ACK");
                    byeRequest.addHeader(reasonHeader);
                    ClientTransaction byeCtx = SIPDialog.this.getSipProvider().getNewClientTransaction(byeRequest);
                    SIPDialog.this.sendRequest(byeCtx);
                } catch (Exception e) {
                    SIPDialog.this.delete();
                }
            }
        }
    }

    private SIPDialog(SipProviderImpl provider) {
        this.auditTag = 0L;
        this.ackSem = new Semaphore(1);
        this.reInviteWaitTime = 100;
        this.highestSequenceNumberAcknowledged = -1L;
        this.sequenceNumberValidation = true;
        this.timerTaskLock = new Semaphore(1);
        this.firstTransactionPort = 5060;
        this.terminateOnBye = true;
        this.routeList = new RouteList();
        this.dialogState = -1;
        this.localSequenceNumber = 0L;
        this.remoteSequenceNumber = -1L;
        this.sipProvider = provider;
        this.eventListeners = new CopyOnWriteArraySet();
    }

    private void recordStackTrace() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);
        new Exception().printStackTrace(writer);
        this.stackTrace = stringWriter.getBuffer().toString();
    }

    public SIPDialog(SIPTransaction transaction) {
        this(transaction.getSipProvider());
        SIPRequest sipRequest = (SIPRequest) transaction.getRequest();
        this.callIdHeader = sipRequest.getCallId();
        this.earlyDialogId = sipRequest.getDialogId(false);
        if (transaction == null) {
            throw new NullPointerException("Null tx");
        }
        this.sipStack = transaction.sipStack;
        this.sipProvider = transaction.getSipProvider();
        if (this.sipProvider == null) {
            throw new NullPointerException("Null Provider!");
        }
        addTransaction(transaction);
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Creating a dialog : " + this);
            this.sipStack.getStackLogger().logDebug("provider port = " + this.sipProvider.getListeningPoint().getPort());
            this.sipStack.getStackLogger().logStackTrace();
        }
        this.isBackToBackUserAgent = this.sipStack.isBackToBackUserAgent;
        addEventListener(this.sipStack);
    }

    public SIPDialog(SIPClientTransaction transaction, SIPResponse sipResponse) {
        this(transaction);
        if (sipResponse == null) {
            throw new NullPointerException("Null SipResponse");
        }
        setLastResponse(transaction, sipResponse);
        this.isBackToBackUserAgent = this.sipStack.isBackToBackUserAgent;
    }

    public SIPDialog(SipProviderImpl sipProvider, SIPResponse sipResponse) {
        this(sipProvider);
        this.sipStack = (SIPTransactionStack) sipProvider.getSipStack();
        setLastResponse(null, sipResponse);
        this.localSequenceNumber = sipResponse.getCSeq().getSeqNumber();
        this.originalLocalSequenceNumber = this.localSequenceNumber;
        this.myTag = sipResponse.getFrom().getTag();
        this.hisTag = sipResponse.getTo().getTag();
        this.localParty = sipResponse.getFrom().getAddress();
        this.remoteParty = sipResponse.getTo().getAddress();
        this.method = sipResponse.getCSeq().getMethod();
        this.callIdHeader = sipResponse.getCallId();
        this.serverTransactionFlag = false;
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Creating a dialog : " + this);
            this.sipStack.getStackLogger().logStackTrace();
        }
        this.isBackToBackUserAgent = this.sipStack.isBackToBackUserAgent;
        addEventListener(this.sipStack);
    }

    private void printRouteList() {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("this : " + this);
            this.sipStack.getStackLogger().logDebug("printRouteList : " + this.routeList.encode());
        }
    }

    private boolean isClientDialog() {
        SIPTransaction transaction = (SIPTransaction) getFirstTransaction();
        return transaction instanceof SIPClientTransaction;
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void raiseIOException(String host, int port, String protocol) {
        IOExceptionEvent ioError = new IOExceptionEvent(this, host, port, protocol);
        this.sipProvider.handleEvent(ioError, null);
        setState(TERMINATED_STATE);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void raiseErrorEvent(int dialogTimeoutError) {
        SIPDialogErrorEvent newErrorEvent = new SIPDialogErrorEvent(this, dialogTimeoutError);
        synchronized (this.eventListeners) {
            for (SIPDialogEventListener nextListener : this.eventListeners) {
                nextListener.dialogErrorEvent(newErrorEvent);
            }
        }
        this.eventListeners.clear();
        if (dialogTimeoutError != 2 && dialogTimeoutError != 1 && dialogTimeoutError != 3) {
            delete();
        }
        stopTimer();
    }

    private void setRemoteParty(SIPMessage sipMessage) {
        if (!isServer()) {
            this.remoteParty = sipMessage.getTo().getAddress();
        } else {
            this.remoteParty = sipMessage.getFrom().getAddress();
        }
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("settingRemoteParty " + this.remoteParty);
        }
    }

    void setRemoteTarget(ContactHeader contact) {
        this.remoteTarget = contact.getAddress();
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Dialog.setRemoteTarget: " + this.remoteTarget);
            this.sipStack.getStackLogger().logStackTrace();
        }
    }

    private synchronized RouteList getRouteList() {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("getRouteList " + this);
        }
        new RouteList();
        RouteList retval = new RouteList();
        if (this.routeList != null) {
            ListIterator li = this.routeList.listIterator();
            while (li.hasNext()) {
                Route route = li.next();
                retval.add((RouteList) ((Route) route.clone()));
            }
        }
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("----- ");
            this.sipStack.getStackLogger().logDebug("getRouteList for " + this);
            if (retval != null) {
                this.sipStack.getStackLogger().logDebug("RouteList = " + retval.encode());
            }
            if (this.routeList != null) {
                this.sipStack.getStackLogger().logDebug("myRouteList = " + this.routeList.encode());
            }
            this.sipStack.getStackLogger().logDebug("----- ");
        }
        return retval;
    }

    void setRouteList(RouteList routeList) {
        this.routeList = routeList;
    }

    private void sendAck(Request request, boolean throwIOExceptionAsSipException) throws SipException {
        ListeningPointImpl lp;
        SIPRequest ackRequest = (SIPRequest) request;
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("sendAck" + this);
        }
        if (!ackRequest.getMethod().equals("ACK")) {
            throw new SipException("Bad request method -- should be ACK");
        }
        if (getState() == null || getState().getValue() == EARLY_STATE) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logError("Bad Dialog State for " + this + " dialogID = " + getDialogId());
            }
            throw new SipException("Bad dialog state " + getState());
        } else if (!getCallId().getCallId().equals(((SIPRequest) request).getCallId().getCallId())) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logError("CallID " + getCallId());
                this.sipStack.getStackLogger().logError("RequestCallID = " + ackRequest.getCallId().getCallId());
                this.sipStack.getStackLogger().logError("dialog =  " + this);
            }
            throw new SipException("Bad call ID in request");
        } else {
            try {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("setting from tag For outgoing ACK= " + getLocalTag());
                    this.sipStack.getStackLogger().logDebug("setting To tag for outgoing ACK = " + getRemoteTag());
                    this.sipStack.getStackLogger().logDebug("ack = " + ackRequest);
                }
                if (getLocalTag() != null) {
                    ackRequest.getFrom().setTag(getLocalTag());
                }
                if (getRemoteTag() != null) {
                    ackRequest.getTo().setTag(getRemoteTag());
                }
                Hop hop = this.sipStack.getNextHop(ackRequest);
                if (hop == null) {
                    throw new SipException("No route!");
                }
                try {
                    if (this.sipStack.isLoggingEnabled()) {
                        this.sipStack.getStackLogger().logDebug("hop = " + hop);
                    }
                    lp = (ListeningPointImpl) this.sipProvider.getListeningPoint(hop.getTransport());
                } catch (IOException ex) {
                    if (throwIOExceptionAsSipException) {
                        throw new SipException("Could not send ack", ex);
                    }
                    raiseIOException(hop.getHost(), hop.getPort(), hop.getTransport());
                } catch (SipException ex2) {
                    if (this.sipStack.isLoggingEnabled()) {
                        this.sipStack.getStackLogger().logException(ex2);
                    }
                    throw ex2;
                } catch (Exception ex3) {
                    if (this.sipStack.isLoggingEnabled()) {
                        this.sipStack.getStackLogger().logException(ex3);
                    }
                    throw new SipException("Could not create message channel", ex3);
                }
                if (lp == null) {
                    throw new SipException("No listening point for this provider registered at " + hop);
                }
                InetAddress inetAddress = InetAddress.getByName(hop.getHost());
                MessageChannel messageChannel = lp.getMessageProcessor().createMessageChannel(inetAddress, hop.getPort());
                boolean releaseAckSem = false;
                long cseqNo = ((SIPRequest) request).getCSeq().getSeqNumber();
                if (!isAckSent(cseqNo)) {
                    releaseAckSem = true;
                }
                setLastAckSent(ackRequest);
                messageChannel.sendMessage(ackRequest);
                this.isAcknowledged = true;
                this.highestSequenceNumberAcknowledged = Math.max(this.highestSequenceNumberAcknowledged, ackRequest.getCSeq().getSeqNumber());
                if (releaseAckSem && this.isBackToBackUserAgent) {
                    releaseAckSem();
                } else if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("Not releasing ack sem for " + this + " isAckSent " + releaseAckSem);
                }
                if (this.dialogDeleteTask != null) {
                    this.dialogDeleteTask.cancel();
                    this.dialogDeleteTask = null;
                }
                this.ackSeen = true;
            } catch (ParseException ex4) {
                throw new SipException(ex4.getMessage());
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setStack(SIPTransactionStack sipStack) {
        this.sipStack = sipStack;
    }

    SIPTransactionStack getStack() {
        return this.sipStack;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean isTerminatedOnBye() {
        return this.terminateOnBye;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public synchronized boolean testAndSetIsDialogTerminatedEventDelivered() {
        boolean retval = this.dialogTerminatedEventDelivered;
        this.dialogTerminatedEventDelivered = true;
        return retval;
    }

    public void addEventListener(SIPDialogEventListener newListener) {
        this.eventListeners.add(newListener);
    }

    public void removeEventListener(SIPDialogEventListener oldListener) {
        this.eventListeners.remove(oldListener);
    }

    @Override // javax.sip.Dialog
    public void setApplicationData(Object applicationData) {
        this.applicationData = applicationData;
    }

    @Override // javax.sip.Dialog
    public Object getApplicationData() {
        return this.applicationData;
    }

    public synchronized void requestConsumed() {
        this.nextSeqno = Long.valueOf(getRemoteSeqNumber() + 1);
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Request Consumed -- next consumable Request Seqno = " + this.nextSeqno);
        }
    }

    public synchronized boolean isRequestConsumable(SIPRequest dialogRequest) {
        if (dialogRequest.getMethod().equals("ACK")) {
            throw new RuntimeException("Illegal method");
        }
        return !isSequnceNumberValidation() || this.remoteSequenceNumber < dialogRequest.getCSeq().getSeqNumber();
    }

    public void doDeferredDelete() {
        if (this.sipStack.getTimer() == null) {
            setState(TERMINATED_STATE);
            return;
        }
        this.dialogDeleteTask = new DialogDeleteTask();
        this.sipStack.getTimer().schedule(this.dialogDeleteTask, 32000L);
    }

    public void setState(int state) {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Setting dialog state for " + this + "newState = " + state);
            this.sipStack.getStackLogger().logStackTrace();
            if (state != -1 && state != this.dialogState && this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug(this + "  old dialog state is " + getState());
                this.sipStack.getStackLogger().logDebug(this + "  New dialog state is " + DialogState.getObject(state));
            }
        }
        this.dialogState = state;
        if (state == TERMINATED_STATE) {
            if (this.sipStack.getTimer() != null) {
                this.sipStack.getTimer().schedule(new LingerTimer(), 8000L);
            }
            stopTimer();
        }
    }

    public void printDebugInfo() {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("isServer = " + isServer());
            this.sipStack.getStackLogger().logDebug("localTag = " + getLocalTag());
            this.sipStack.getStackLogger().logDebug("remoteTag = " + getRemoteTag());
            this.sipStack.getStackLogger().logDebug("localSequenceNumer = " + getLocalSeqNumber());
            this.sipStack.getStackLogger().logDebug("remoteSequenceNumer = " + getRemoteSeqNumber());
            this.sipStack.getStackLogger().logDebug("ackLine:" + getRemoteTag() + Separators.SP + this.ackLine);
        }
    }

    public boolean isAckSeen() {
        return this.ackSeen;
    }

    public SIPRequest getLastAckSent() {
        return this.lastAckSent;
    }

    public boolean isAckSent(long cseqNo) {
        if (getLastTransaction() != null && (getLastTransaction() instanceof ClientTransaction)) {
            return getLastAckSent() != null && cseqNo <= getLastAckSent().getCSeq().getSeqNumber();
        }
        return true;
    }

    @Override // javax.sip.Dialog
    public Transaction getFirstTransaction() {
        return this.firstTransaction;
    }

    @Override // javax.sip.Dialog
    public Iterator getRouteSet() {
        if (this.routeList == null) {
            return new LinkedList().listIterator();
        }
        return getRouteList().listIterator();
    }

    public synchronized void addRoute(SIPRequest sipRequest) {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("setContact: dialogState: " + this + "state = " + getState());
        }
        if (this.dialogState == CONFIRMED_STATE && SIPRequest.isTargetRefresh(sipRequest.getMethod())) {
            doTargetRefresh(sipRequest);
        }
        if (this.dialogState == CONFIRMED_STATE || this.dialogState == TERMINATED_STATE || sipRequest.getToTag() != null) {
            return;
        }
        RecordRouteList rrlist = sipRequest.getRecordRouteHeaders();
        if (rrlist != null) {
            addRoute(rrlist);
        } else {
            this.routeList = new RouteList();
        }
        ContactList contactList = sipRequest.getContactHeaders();
        if (contactList != null) {
            setRemoteTarget((ContactHeader) contactList.getFirst());
        }
    }

    public void setDialogId(String dialogId) {
        this.dialogId = dialogId;
    }

    public static SIPDialog createFromNOTIFY(SIPClientTransaction subscribeTx, SIPTransaction notifyST) {
        SIPDialog d = new SIPDialog(notifyST);
        d.serverTransactionFlag = false;
        d.lastTransaction = subscribeTx;
        storeFirstTransactionInfo(d, subscribeTx);
        d.terminateOnBye = false;
        d.localSequenceNumber = subscribeTx.getCSeq();
        SIPRequest not = (SIPRequest) notifyST.getRequest();
        d.remoteSequenceNumber = not.getCSeq().getSeqNumber();
        d.setDialogId(not.getDialogId(true));
        d.setLocalTag(not.getToTag());
        d.setRemoteTag(not.getFromTag());
        d.setLastResponse(subscribeTx, subscribeTx.getLastResponse());
        d.localParty = not.getTo().getAddress();
        d.remoteParty = not.getFrom().getAddress();
        d.addRoute(not);
        d.setState(CONFIRMED_STATE);
        return d;
    }

    @Override // javax.sip.Dialog
    public boolean isServer() {
        if (!this.firstTransactionSeen) {
            return this.serverTransactionFlag;
        }
        return this.firstTransactionIsServerTransaction;
    }

    protected boolean isReInvite() {
        return this.reInviteFlag;
    }

    @Override // javax.sip.Dialog
    public String getDialogId() {
        if (this.dialogId == null && this.lastResponse != null) {
            this.dialogId = this.lastResponse.getDialogId(isServer());
        }
        return this.dialogId;
    }

    private static void storeFirstTransactionInfo(SIPDialog dialog, SIPTransaction transaction) {
        dialog.firstTransaction = transaction;
        dialog.firstTransactionSeen = true;
        dialog.firstTransactionIsServerTransaction = transaction.isServerTransaction();
        dialog.firstTransactionSecure = transaction.getRequest().getRequestURI().getScheme().equalsIgnoreCase("sips");
        dialog.firstTransactionPort = transaction.getPort();
        dialog.firstTransactionId = transaction.getBranchId();
        dialog.firstTransactionMethod = transaction.getMethod();
        if (dialog.isServer()) {
            SIPServerTransaction st = (SIPServerTransaction) transaction;
            SIPResponse response = st.getLastResponse();
            dialog.contactHeader = response != null ? response.getContactHeader() : null;
            return;
        }
        SIPClientTransaction ct = (SIPClientTransaction) transaction;
        if (ct != null) {
            SIPRequest sipRequest = ct.getOriginalRequest();
            dialog.contactHeader = sipRequest.getContactHeader();
        }
    }

    public void addTransaction(SIPTransaction transaction) {
        SIPRequest sipRequest = transaction.getOriginalRequest();
        if (this.firstTransactionSeen && !this.firstTransactionId.equals(transaction.getBranchId()) && transaction.getMethod().equals(this.firstTransactionMethod)) {
            this.reInviteFlag = true;
        }
        if (!this.firstTransactionSeen) {
            storeFirstTransactionInfo(this, transaction);
            if (sipRequest.getMethod().equals("SUBSCRIBE")) {
                this.eventHeader = (EventHeader) sipRequest.getHeader("Event");
            }
            setLocalParty(sipRequest);
            setRemoteParty(sipRequest);
            setCallId(sipRequest);
            if (this.originalRequest == null) {
                this.originalRequest = sipRequest;
            }
            if (this.method == null) {
                this.method = sipRequest.getMethod();
            }
            if (transaction instanceof SIPServerTransaction) {
                this.hisTag = sipRequest.getFrom().getTag();
            } else {
                setLocalSequenceNumber(sipRequest.getCSeq().getSeqNumber());
                this.originalLocalSequenceNumber = this.localSequenceNumber;
                this.myTag = sipRequest.getFrom().getTag();
                if (this.myTag == null && this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logError("The request's From header is missing the required Tag parameter.");
                }
            }
        } else if (transaction.getMethod().equals(this.firstTransactionMethod) && this.firstTransactionIsServerTransaction != transaction.isServerTransaction()) {
            storeFirstTransactionInfo(this, transaction);
            setLocalParty(sipRequest);
            setRemoteParty(sipRequest);
            setCallId(sipRequest);
            this.originalRequest = sipRequest;
            this.method = sipRequest.getMethod();
        }
        if (transaction instanceof SIPServerTransaction) {
            setRemoteSequenceNumber(sipRequest.getCSeq().getSeqNumber());
        }
        this.lastTransaction = transaction;
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Transaction Added " + this + this.myTag + Separators.SLASH + this.hisTag);
            this.sipStack.getStackLogger().logDebug("TID = " + transaction.getTransactionId() + Separators.SLASH + transaction.isServerTransaction());
            this.sipStack.getStackLogger().logStackTrace();
        }
    }

    private void setRemoteTag(String hisTag) {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("setRemoteTag(): " + this + " remoteTag = " + this.hisTag + " new tag = " + hisTag);
        }
        if (this.hisTag != null && hisTag != null && !hisTag.equals(this.hisTag)) {
            if (getState() != DialogState.EARLY) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("Dialog is already established -- ignoring remote tag re-assignment");
                }
            } else if (this.sipStack.isRemoteTagReassignmentAllowed()) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("UNSAFE OPERATION !  tag re-assignment " + this.hisTag + " trying to set to " + hisTag + " can cause unexpected effects ");
                }
                boolean removed = false;
                if (this.sipStack.getDialog(this.dialogId) == this) {
                    this.sipStack.removeDialog(this.dialogId);
                    removed = true;
                }
                this.dialogId = null;
                this.hisTag = hisTag;
                if (removed) {
                    if (this.sipStack.isLoggingEnabled()) {
                        this.sipStack.getStackLogger().logDebug("ReInserting Dialog");
                    }
                    this.sipStack.putDialog(this);
                }
            }
        } else if (hisTag != null) {
            this.hisTag = hisTag;
        } else if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logWarning("setRemoteTag : called with null argument ");
        }
    }

    public SIPTransaction getLastTransaction() {
        return this.lastTransaction;
    }

    public SIPServerTransaction getInviteTransaction() {
        DialogTimerTask t = this.timerTask;
        if (t != null) {
            return t.transaction;
        }
        return null;
    }

    private void setLocalSequenceNumber(long lCseq) {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("setLocalSequenceNumber: original  " + this.localSequenceNumber + " new  = " + lCseq);
        }
        if (lCseq <= this.localSequenceNumber) {
            throw new RuntimeException("Sequence number should not decrease !");
        }
        this.localSequenceNumber = lCseq;
    }

    public void setRemoteSequenceNumber(long rCseq) {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("setRemoteSeqno " + this + Separators.SLASH + rCseq);
        }
        this.remoteSequenceNumber = rCseq;
    }

    @Override // javax.sip.Dialog
    public void incrementLocalSequenceNumber() {
        this.localSequenceNumber++;
    }

    @Override // javax.sip.Dialog
    public int getRemoteSequenceNumber() {
        return (int) this.remoteSequenceNumber;
    }

    @Override // javax.sip.Dialog
    public int getLocalSequenceNumber() {
        return (int) this.localSequenceNumber;
    }

    public long getOriginalLocalSequenceNumber() {
        return this.originalLocalSequenceNumber;
    }

    @Override // javax.sip.Dialog
    public long getLocalSeqNumber() {
        return this.localSequenceNumber;
    }

    @Override // javax.sip.Dialog
    public long getRemoteSeqNumber() {
        return this.remoteSequenceNumber;
    }

    @Override // javax.sip.Dialog
    public String getLocalTag() {
        return this.myTag;
    }

    @Override // javax.sip.Dialog
    public String getRemoteTag() {
        return this.hisTag;
    }

    private void setLocalTag(String mytag) {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("set Local tag " + mytag + Separators.SP + this.dialogId);
            this.sipStack.getStackLogger().logStackTrace();
        }
        this.myTag = mytag;
    }

    @Override // javax.sip.Dialog
    public void delete() {
        setState(TERMINATED_STATE);
    }

    @Override // javax.sip.Dialog
    public CallIdHeader getCallId() {
        return this.callIdHeader;
    }

    private void setCallId(SIPRequest sipRequest) {
        this.callIdHeader = sipRequest.getCallId();
    }

    @Override // javax.sip.Dialog
    public Address getLocalParty() {
        return this.localParty;
    }

    private void setLocalParty(SIPMessage sipMessage) {
        if (!isServer()) {
            this.localParty = sipMessage.getFrom().getAddress();
        } else {
            this.localParty = sipMessage.getTo().getAddress();
        }
    }

    @Override // javax.sip.Dialog
    public Address getRemoteParty() {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("gettingRemoteParty " + this.remoteParty);
        }
        return this.remoteParty;
    }

    @Override // javax.sip.Dialog
    public Address getRemoteTarget() {
        return this.remoteTarget;
    }

    @Override // javax.sip.Dialog
    public DialogState getState() {
        if (this.dialogState == -1) {
            return null;
        }
        return DialogState.getObject(this.dialogState);
    }

    @Override // javax.sip.Dialog
    public boolean isSecure() {
        return this.firstTransactionSecure;
    }

    @Override // javax.sip.Dialog
    public void sendAck(Request request) throws SipException {
        sendAck(request, true);
    }

    @Override // javax.sip.Dialog
    public Request createRequest(String method) throws SipException {
        if (method.equals("ACK") || method.equals(Request.PRACK)) {
            throw new SipException("Invalid method specified for createRequest:" + method);
        }
        if (this.lastResponse != null) {
            return createRequest(method, this.lastResponse);
        }
        throw new SipException("Dialog not yet established -- no response!");
    }

    private Request createRequest(String method, SIPResponse sipResponse) throws SipException {
        SipUri sipUri;
        if (method == null || sipResponse == null) {
            throw new NullPointerException("null argument");
        }
        if (method.equals(Request.CANCEL)) {
            throw new SipException("Dialog.createRequest(): Invalid request");
        }
        if (getState() == null || ((getState().getValue() == TERMINATED_STATE && !method.equalsIgnoreCase("BYE")) || (isServer() && getState().getValue() == EARLY_STATE && method.equalsIgnoreCase("BYE")))) {
            throw new SipException("Dialog  " + getDialogId() + " not yet established or terminated " + getState());
        }
        if (getRemoteTarget() != null) {
            sipUri = (SipUri) getRemoteTarget().getURI().clone();
        } else {
            sipUri = (SipUri) getRemoteParty().getURI().clone();
            sipUri.clearUriParms();
        }
        CSeq cseq = new CSeq();
        try {
            cseq.setMethod(method);
            cseq.setSeqNumber(getLocalSeqNumber());
        } catch (Exception ex) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logError("Unexpected error");
            }
            InternalErrorHandler.handleException(ex);
        }
        ListeningPointImpl lp = (ListeningPointImpl) this.sipProvider.getListeningPoint(sipResponse.getTopmostVia().getTransport());
        if (lp == null) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logError("Cannot find listening point for transport " + sipResponse.getTopmostVia().getTransport());
            }
            throw new SipException("Cannot find listening point for transport " + sipResponse.getTopmostVia().getTransport());
        }
        Via via = lp.getViaHeader();
        From from = new From();
        from.setAddress(this.localParty);
        To to = new To();
        to.setAddress(this.remoteParty);
        SIPRequest sipRequest = sipResponse.createRequest(sipUri, via, cseq, from, to);
        if (SIPRequest.isTargetRefresh(method)) {
            ContactHeader contactHeader = ((ListeningPointImpl) this.sipProvider.getListeningPoint(lp.getTransport())).createContactHeader();
            ((SipURI) contactHeader.getAddress().getURI()).setSecure(isSecure());
            sipRequest.setHeader(contactHeader);
        }
        try {
            ((CSeq) sipRequest.getCSeq()).setSeqNumber(this.localSequenceNumber + 1);
        } catch (InvalidArgumentException ex2) {
            InternalErrorHandler.handleException(ex2);
        }
        if (method.equals("SUBSCRIBE") && this.eventHeader != null) {
            sipRequest.addHeader(this.eventHeader);
        }
        try {
            if (getLocalTag() != null) {
                from.setTag(getLocalTag());
            } else {
                from.removeTag();
            }
            if (getRemoteTag() != null) {
                to.setTag(getRemoteTag());
            } else {
                to.removeTag();
            }
        } catch (ParseException ex3) {
            InternalErrorHandler.handleException(ex3);
        }
        updateRequest(sipRequest);
        return sipRequest;
    }

    @Override // javax.sip.Dialog
    public void sendRequest(ClientTransaction clientTransactionId) throws TransactionDoesNotExistException, SipException {
        sendRequest(clientTransactionId, !this.isBackToBackUserAgent);
    }

    public void sendRequest(ClientTransaction clientTransactionId, boolean allowInterleaving) throws TransactionDoesNotExistException, SipException {
        if (!allowInterleaving && clientTransactionId.getRequest().getMethod().equals("INVITE")) {
            new Thread(new ReInviteSender(clientTransactionId)).start();
            return;
        }
        SIPRequest dialogRequest = ((SIPClientTransaction) clientTransactionId).getOriginalRequest();
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("dialog.sendRequest  dialog = " + this + "\ndialogRequest = \n" + dialogRequest);
        }
        if (clientTransactionId == null) {
            throw new NullPointerException("null parameter");
        }
        if (dialogRequest.getMethod().equals("ACK") || dialogRequest.getMethod().equals(Request.CANCEL)) {
            throw new SipException("Bad Request Method. " + dialogRequest.getMethod());
        }
        if (this.byeSent && isTerminatedOnBye() && !dialogRequest.getMethod().equals("BYE")) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logError("BYE already sent for " + this);
            }
            throw new SipException("Cannot send request; BYE already sent");
        }
        if (dialogRequest.getTopmostVia() == null) {
            Via via = ((SIPClientTransaction) clientTransactionId).getOutgoingViaHeader();
            dialogRequest.addHeader(via);
        }
        if (!getCallId().getCallId().equalsIgnoreCase(dialogRequest.getCallId().getCallId())) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logError("CallID " + getCallId());
                this.sipStack.getStackLogger().logError("RequestCallID = " + dialogRequest.getCallId().getCallId());
                this.sipStack.getStackLogger().logError("dialog =  " + this);
            }
            throw new SipException("Bad call ID in request");
        }
        ((SIPClientTransaction) clientTransactionId).setDialog(this, this.dialogId);
        addTransaction((SIPTransaction) clientTransactionId);
        ((SIPClientTransaction) clientTransactionId).isMapped = true;
        From from = (From) dialogRequest.getFrom();
        To to = (To) dialogRequest.getTo();
        if (getLocalTag() != null && from.getTag() != null && !from.getTag().equals(getLocalTag())) {
            throw new SipException("From tag mismatch expecting  " + getLocalTag());
        }
        if (getRemoteTag() != null && to.getTag() != null && !to.getTag().equals(getRemoteTag()) && this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logWarning("To header tag mismatch expecting " + getRemoteTag());
        }
        if (getLocalTag() == null && dialogRequest.getMethod().equals("NOTIFY")) {
            if (!getMethod().equals("SUBSCRIBE")) {
                throw new SipException("Trying to send NOTIFY without SUBSCRIBE Dialog!");
            }
            setLocalTag(from.getTag());
        }
        try {
            if (getLocalTag() != null) {
                from.setTag(getLocalTag());
            }
            if (getRemoteTag() != null) {
                to.setTag(getRemoteTag());
            }
        } catch (ParseException ex) {
            InternalErrorHandler.handleException(ex);
        }
        Hop hop = ((SIPClientTransaction) clientTransactionId).getNextHop();
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Using hop = " + hop.getHost() + " : " + hop.getPort());
        }
        try {
            MessageChannel messageChannel = this.sipStack.createRawMessageChannel(getSipProvider().getListeningPoint(hop.getTransport()).getIPAddress(), this.firstTransactionPort, hop);
            MessageChannel oldChannel = ((SIPClientTransaction) clientTransactionId).getMessageChannel();
            oldChannel.uncache();
            if (!this.sipStack.cacheClientConnections) {
                oldChannel.useCount--;
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("oldChannel: useCount " + oldChannel.useCount);
                }
            }
            if (messageChannel == null) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("Null message channel using outbound proxy !");
                }
                Hop outboundProxy = this.sipStack.getRouter(dialogRequest).getOutboundProxy();
                if (outboundProxy == null) {
                    throw new SipException("No route found! hop=" + hop);
                }
                messageChannel = this.sipStack.createRawMessageChannel(getSipProvider().getListeningPoint(outboundProxy.getTransport()).getIPAddress(), this.firstTransactionPort, outboundProxy);
                if (messageChannel != null) {
                    ((SIPClientTransaction) clientTransactionId).setEncapsulatedChannel(messageChannel);
                }
            } else {
                ((SIPClientTransaction) clientTransactionId).setEncapsulatedChannel(messageChannel);
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("using message channel " + messageChannel);
                }
            }
            if (messageChannel != null) {
                messageChannel.useCount++;
            }
            if (!this.sipStack.cacheClientConnections && oldChannel != null && oldChannel.useCount <= 0) {
                oldChannel.close();
            }
            try {
                this.localSequenceNumber++;
                dialogRequest.getCSeq().setSeqNumber(getLocalSeqNumber());
            } catch (InvalidArgumentException ex2) {
                this.sipStack.getStackLogger().logFatalError(ex2.getMessage());
            }
            try {
                ((SIPClientTransaction) clientTransactionId).sendMessage(dialogRequest);
                if (dialogRequest.getMethod().equals("BYE")) {
                    this.byeSent = true;
                    if (isTerminatedOnBye()) {
                        setState(DialogState._TERMINATED);
                    }
                }
            } catch (IOException ex3) {
                throw new SipException("error sending message", ex3);
            }
        } catch (Exception ex4) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logException(ex4);
            }
            throw new SipException("Could not create message channel", ex4);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public boolean toRetransmitFinalResponse(int T2) {
        int i = this.retransmissionTicksLeft - 1;
        this.retransmissionTicksLeft = i;
        if (i == 0) {
            if (2 * this.prevRetransmissionTicks <= T2) {
                this.retransmissionTicksLeft = 2 * this.prevRetransmissionTicks;
            } else {
                this.retransmissionTicksLeft = this.prevRetransmissionTicks;
            }
            this.prevRetransmissionTicks = this.retransmissionTicksLeft;
            return true;
        }
        return false;
    }

    protected void setRetransmissionTicks() {
        this.retransmissionTicksLeft = 1;
        this.prevRetransmissionTicks = 1;
    }

    public void resendAck() throws SipException {
        if (getLastAckSent() != null) {
            if (getLastAckSent().getHeader("Timestamp") != null && this.sipStack.generateTimeStampHeader) {
                TimeStamp ts = new TimeStamp();
                try {
                    ts.setTimeStamp((float) System.currentTimeMillis());
                    getLastAckSent().setHeader(ts);
                } catch (InvalidArgumentException e) {
                }
            }
            sendAck(getLastAckSent(), false);
        }
    }

    public String getMethod() {
        return this.method;
    }

    @Override // javax.sip.Dialog
    public Request createPrack(Response relResponse) throws DialogDoesNotExistException, SipException {
        if (getState() == null || getState().equals(DialogState.TERMINATED)) {
            throw new DialogDoesNotExistException("Dialog not initialized or terminated");
        }
        if (((RSeq) relResponse.getHeader("RSeq")) == null) {
            throw new SipException("Missing RSeq Header");
        }
        try {
            SIPResponse sipResponse = (SIPResponse) relResponse;
            SIPRequest sipRequest = (SIPRequest) createRequest(Request.PRACK, (SIPResponse) relResponse);
            String toHeaderTag = sipResponse.getTo().getTag();
            sipRequest.setToTag(toHeaderTag);
            RAck rack = new RAck();
            RSeq rseq = (RSeq) relResponse.getHeader("RSeq");
            rack.setMethod(sipResponse.getCSeq().getMethod());
            rack.setCSequenceNumber((int) sipResponse.getCSeq().getSeqNumber());
            rack.setRSequenceNumber(rseq.getSeqNumber());
            sipRequest.setHeader(rack);
            return sipRequest;
        } catch (Exception ex) {
            InternalErrorHandler.handleException(ex);
            return null;
        }
    }

    private void updateRequest(SIPRequest sipRequest) {
        RouteList rl = getRouteList();
        if (rl.size() > 0) {
            sipRequest.setHeader((Header) rl);
        } else {
            sipRequest.removeHeader("Route");
        }
        if (MessageFactoryImpl.getDefaultUserAgentHeader() != null) {
            sipRequest.setHeader(MessageFactoryImpl.getDefaultUserAgentHeader());
        }
    }

    @Override // javax.sip.Dialog
    public Request createAck(long cseqno) throws InvalidArgumentException, SipException {
        SipURI uri4transport;
        Authorization authorization;
        NameValueList originalRequestParameters;
        if (!this.method.equals("INVITE")) {
            throw new SipException("Dialog was not created with an INVITE" + this.method);
        }
        if (cseqno <= 0) {
            throw new InvalidArgumentException("bad cseq <= 0 ");
        }
        if (cseqno > ExpandableListView.PACKED_POSITION_VALUE_NULL) {
            throw new InvalidArgumentException("bad cseq > 4294967295");
        }
        if (this.remoteTarget == null) {
            throw new SipException("Cannot create ACK - no remote Target!");
        }
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("createAck " + this + " cseqno " + cseqno);
        }
        if (this.lastInviteOkReceived < cseqno) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("WARNING : Attempt to crete ACK without OK " + this);
                this.sipStack.getStackLogger().logDebug("LAST RESPONSE = " + this.lastResponse);
            }
            throw new SipException("Dialog not yet established -- no OK response!");
        }
        try {
            if (this.routeList != null && !this.routeList.isEmpty()) {
                Route r = (Route) this.routeList.getFirst();
                uri4transport = (SipURI) r.getAddress().getURI();
            } else {
                uri4transport = (SipURI) this.remoteTarget.getURI();
            }
            String transport = uri4transport.getTransportParam();
            if (transport == null) {
                transport = uri4transport.isSecure() ? "TLS" : ListeningPoint.UDP;
            }
            ListeningPointImpl lp = (ListeningPointImpl) this.sipProvider.getListeningPoint(transport);
            if (lp == null) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logError("remoteTargetURI " + this.remoteTarget.getURI());
                    this.sipStack.getStackLogger().logError("uri4transport = " + uri4transport);
                    this.sipStack.getStackLogger().logError("No LP found for transport=" + transport);
                }
                throw new SipException("Cannot create ACK - no ListeningPoint for transport towards next hop found:" + transport);
            }
            SIPRequest sipRequest = new SIPRequest();
            sipRequest.setMethod("ACK");
            sipRequest.setRequestURI((SipUri) getRemoteTarget().getURI().clone());
            sipRequest.setCallId(this.callIdHeader);
            sipRequest.setCSeq(new CSeq(cseqno, "ACK"));
            List<Via> vias = new ArrayList<>();
            Via via = this.lastResponse.getTopmostVia();
            via.removeParameters();
            if (this.originalRequest != null && this.originalRequest.getTopmostVia() != null && (originalRequestParameters = this.originalRequest.getTopmostVia().getParameters()) != null && originalRequestParameters.size() > 0) {
                via.setParameters((NameValueList) originalRequestParameters.clone());
            }
            via.setBranch(Utils.getInstance().generateBranchId());
            vias.add(via);
            sipRequest.setVia(vias);
            From from = new From();
            from.setAddress(this.localParty);
            from.setTag(this.myTag);
            sipRequest.setFrom(from);
            To to = new To();
            to.setAddress(this.remoteParty);
            if (this.hisTag != null) {
                to.setTag(this.hisTag);
            }
            sipRequest.setTo(to);
            sipRequest.setMaxForwards(new MaxForwards(70));
            if (this.originalRequest != null && (authorization = this.originalRequest.getAuthorization()) != null) {
                sipRequest.setHeader(authorization);
            }
            updateRequest(sipRequest);
            return sipRequest;
        } catch (Exception ex) {
            InternalErrorHandler.handleException(ex);
            throw new SipException("unexpected exception ", ex);
        }
    }

    @Override // javax.sip.Dialog
    public SipProviderImpl getSipProvider() {
        return this.sipProvider;
    }

    public void setSipProvider(SipProviderImpl sipProvider) {
        this.sipProvider = sipProvider;
    }

    public void setResponseTags(SIPResponse sipResponse) {
        if (getLocalTag() != null || getRemoteTag() != null) {
            return;
        }
        String responseFromTag = sipResponse.getFromTag();
        if (responseFromTag != null) {
            if (responseFromTag.equals(getLocalTag())) {
                sipResponse.setToTag(getRemoteTag());
            } else if (responseFromTag.equals(getRemoteTag())) {
                sipResponse.setToTag(getLocalTag());
            }
        } else if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logWarning("No from tag in response! Not RFC 3261 compatible.");
        }
    }

    public void setLastResponse(SIPTransaction transaction, SIPResponse sipResponse) {
        RecordRouteList rrList;
        this.callIdHeader = sipResponse.getCallId();
        int statusCode = sipResponse.getStatusCode();
        if (statusCode == 100) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logWarning("Invalid status code - 100 in setLastResponse - ignoring");
                return;
            }
            return;
        }
        this.lastResponse = sipResponse;
        setAssigned();
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("sipDialog: setLastResponse:" + this + " lastResponse = " + this.lastResponse.getFirstLine());
        }
        if (getState() == DialogState.TERMINATED) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("sipDialog: setLastResponse -- dialog is terminated - ignoring ");
            }
            if (sipResponse.getCSeq().getMethod().equals("INVITE") && statusCode == 200) {
                this.lastInviteOkReceived = Math.max(sipResponse.getCSeq().getSeqNumber(), this.lastInviteOkReceived);
                return;
            }
            return;
        }
        String cseqMethod = sipResponse.getCSeq().getMethod();
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logStackTrace();
            this.sipStack.getStackLogger().logDebug("cseqMethod = " + cseqMethod);
            this.sipStack.getStackLogger().logDebug("dialogState = " + getState());
            this.sipStack.getStackLogger().logDebug("method = " + getMethod());
            this.sipStack.getStackLogger().logDebug("statusCode = " + statusCode);
            this.sipStack.getStackLogger().logDebug("transaction = " + transaction);
        }
        if (transaction == null || (transaction instanceof ClientTransaction)) {
            SIPTransactionStack sIPTransactionStack = this.sipStack;
            if (SIPTransactionStack.isDialogCreated(cseqMethod)) {
                if (getState() == null && statusCode / 100 == 1) {
                    setState(EARLY_STATE);
                    if ((sipResponse.getToTag() != null || this.sipStack.rfc2543Supported) && getRemoteTag() == null) {
                        setRemoteTag(sipResponse.getToTag());
                        setDialogId(sipResponse.getDialogId(false));
                        this.sipStack.putDialog(this);
                        addRoute(sipResponse);
                    }
                } else if (getState() != null && getState().equals(DialogState.EARLY) && statusCode / 100 == 1) {
                    if (cseqMethod.equals(getMethod()) && transaction != null && (sipResponse.getToTag() != null || this.sipStack.rfc2543Supported)) {
                        setRemoteTag(sipResponse.getToTag());
                        setDialogId(sipResponse.getDialogId(false));
                        this.sipStack.putDialog(this);
                        addRoute(sipResponse);
                    }
                } else if (statusCode / 100 == 2) {
                    if (cseqMethod.equals(getMethod()) && ((sipResponse.getToTag() != null || this.sipStack.rfc2543Supported) && getState() != DialogState.CONFIRMED)) {
                        setRemoteTag(sipResponse.getToTag());
                        setDialogId(sipResponse.getDialogId(false));
                        this.sipStack.putDialog(this);
                        addRoute(sipResponse);
                        setState(CONFIRMED_STATE);
                    }
                    if (cseqMethod.equals("INVITE")) {
                        this.lastInviteOkReceived = Math.max(sipResponse.getCSeq().getSeqNumber(), this.lastInviteOkReceived);
                    }
                } else if (statusCode >= 300 && statusCode <= 699 && (getState() == null || (cseqMethod.equals(getMethod()) && getState().getValue() == EARLY_STATE))) {
                    setState(TERMINATED_STATE);
                }
                if (getState() != DialogState.CONFIRMED && getState() != DialogState.TERMINATED && this.originalRequest != null && (rrList = this.originalRequest.getRecordRouteHeaders()) != null) {
                    ListIterator<RecordRoute> it = rrList.listIterator(rrList.size());
                    while (it.hasPrevious()) {
                        RecordRoute rr = it.previous();
                        Route route = (Route) this.routeList.getFirst();
                        if (route != null && rr.getAddress().equals(route.getAddress())) {
                            this.routeList.removeFirst();
                        } else {
                            return;
                        }
                    }
                }
            } else if (cseqMethod.equals("NOTIFY") && ((getMethod().equals("SUBSCRIBE") || getMethod().equals(Request.REFER)) && sipResponse.getStatusCode() / 100 == 2 && getState() == null)) {
                setDialogId(sipResponse.getDialogId(true));
                this.sipStack.putDialog(this);
                setState(CONFIRMED_STATE);
            } else if (cseqMethod.equals("BYE") && statusCode / 100 == 2 && isTerminatedOnBye()) {
                setState(TERMINATED_STATE);
            }
        } else if (cseqMethod.equals("BYE") && statusCode / 100 == 2 && isTerminatedOnBye()) {
            setState(TERMINATED_STATE);
        } else {
            boolean doPutDialog = false;
            if (getLocalTag() == null && sipResponse.getTo().getTag() != null) {
                SIPTransactionStack sIPTransactionStack2 = this.sipStack;
                if (SIPTransactionStack.isDialogCreated(cseqMethod) && cseqMethod.equals(getMethod())) {
                    setLocalTag(sipResponse.getTo().getTag());
                    doPutDialog = true;
                }
            }
            if (statusCode / 100 != 2) {
                if (statusCode / 100 == 1) {
                    if (doPutDialog) {
                        setState(EARLY_STATE);
                        setDialogId(sipResponse.getDialogId(true));
                        this.sipStack.putDialog(this);
                        return;
                    }
                    return;
                } else if (statusCode == 489 && (cseqMethod.equals("NOTIFY") || cseqMethod.equals("SUBSCRIBE"))) {
                    if (this.sipStack.isLoggingEnabled()) {
                        this.sipStack.getStackLogger().logDebug("RFC 3265 : Not setting dialog to TERMINATED for 489");
                        return;
                    }
                    return;
                } else if (!isReInvite() && getState() != DialogState.CONFIRMED) {
                    setState(TERMINATED_STATE);
                    return;
                } else {
                    return;
                }
            }
            if (this.dialogState <= EARLY_STATE && (cseqMethod.equals("INVITE") || cseqMethod.equals("SUBSCRIBE") || cseqMethod.equals(Request.REFER))) {
                setState(CONFIRMED_STATE);
            }
            if (doPutDialog) {
                setDialogId(sipResponse.getDialogId(true));
                this.sipStack.putDialog(this);
            }
            if (transaction.getState() != TransactionState.TERMINATED && sipResponse.getStatusCode() == 200 && cseqMethod.equals("INVITE") && this.isBackToBackUserAgent && !takeAckSem()) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("Delete dialog -- cannot acquire ackSem");
                }
                delete();
            }
        }
    }

    public void startRetransmitTimer(SIPServerTransaction sipServerTx, Response response) {
        if (sipServerTx.getRequest().getMethod().equals("INVITE") && response.getStatusCode() / 100 == 2) {
            startTimer(sipServerTx);
        }
    }

    public SIPResponse getLastResponse() {
        return this.lastResponse;
    }

    private void doTargetRefresh(SIPMessage sipMessage) {
        ContactList contactList = sipMessage.getContactHeaders();
        if (contactList != null) {
            Contact contact = (Contact) contactList.getFirst();
            setRemoteTarget(contact);
        }
    }

    private static final boolean optionPresent(ListIterator l, String option) {
        while (l.hasNext()) {
            OptionTag opt = (OptionTag) l.next();
            if (opt != null && option.equalsIgnoreCase(opt.getOptionTag())) {
                return true;
            }
        }
        return false;
    }

    @Override // javax.sip.Dialog
    public Response createReliableProvisionalResponse(int statusCode) throws InvalidArgumentException, SipException {
        ListIterator<SIPHeader> list;
        if (!this.firstTransactionIsServerTransaction) {
            throw new SipException("Not a Server Dialog!");
        }
        if (statusCode <= 100 || statusCode > 199) {
            throw new InvalidArgumentException("Bad status code ");
        }
        SIPRequest request = this.originalRequest;
        if (!request.getMethod().equals("INVITE")) {
            throw new SipException("Bad method");
        }
        ListIterator<SIPHeader> list2 = request.getHeaders("Supported");
        if ((list2 == null || !optionPresent(list2, "100rel")) && ((list = request.getHeaders("Require")) == null || !optionPresent(list, "100rel"))) {
            throw new SipException("No Supported/Require 100rel header in the request");
        }
        SIPResponse response = request.createResponse(statusCode);
        Require require = new Require();
        try {
            require.setOptionTag("100rel");
        } catch (Exception ex) {
            InternalErrorHandler.handleException(ex);
        }
        response.addHeader(require);
        RSeq rseq = new RSeq();
        rseq.setSeqNumber(1L);
        RecordRouteList rrl = request.getRecordRouteHeaders();
        if (rrl != null) {
            RecordRouteList rrlclone = (RecordRouteList) rrl.clone();
            response.setHeader((Header) rrlclone);
        }
        return response;
    }

    public boolean handlePrack(SIPRequest prackRequest) {
        if (!isServer()) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("Dropping Prack -- not a server Dialog");
                return false;
            }
            return false;
        }
        SIPServerTransaction sipServerTransaction = (SIPServerTransaction) getFirstTransaction();
        SIPResponse sipResponse = sipServerTransaction.getReliableProvisionalResponse();
        if (sipResponse == null) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("Dropping Prack -- ReliableResponse not found");
                return false;
            }
            return false;
        }
        RAck rack = (RAck) prackRequest.getHeader("RAck");
        if (rack == null) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("Dropping Prack -- rack header not found");
                return false;
            }
            return false;
        }
        CSeq cseq = (CSeq) sipResponse.getCSeq();
        if (!rack.getMethod().equals(cseq.getMethod())) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("Dropping Prack -- CSeq Header does not match PRACK");
                return false;
            }
            return false;
        } else if (rack.getCSeqNumberLong() != cseq.getSeqNumber()) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("Dropping Prack -- CSeq Header does not match PRACK");
                return false;
            }
            return false;
        } else {
            RSeq rseq = (RSeq) sipResponse.getHeader("RSeq");
            if (rack.getRSequenceNumber() != rseq.getSeqNumber()) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("Dropping Prack -- RSeq Header does not match PRACK");
                    return false;
                }
                return false;
            }
            return sipServerTransaction.prackRecieved();
        }
    }

    @Override // javax.sip.Dialog
    public void sendReliableProvisionalResponse(Response relResponse) throws SipException {
        if (!isServer()) {
            throw new SipException("Not a Server Dialog");
        }
        SIPResponse sipResponse = (SIPResponse) relResponse;
        if (relResponse.getStatusCode() == 100) {
            throw new SipException("Cannot send 100 as a reliable provisional response");
        }
        if (relResponse.getStatusCode() / 100 > 2) {
            throw new SipException("Response code is not a 1xx response - should be in the range 101 to 199 ");
        }
        if (sipResponse.getToTag() == null) {
            throw new SipException("Badly formatted response -- To tag mandatory for Reliable Provisional Response");
        }
        ListIterator requireList = relResponse.getHeaders("Require");
        boolean found = false;
        if (requireList != null) {
            while (requireList.hasNext() && !found) {
                RequireHeader rh = (RequireHeader) requireList.next();
                if (rh.getOptionTag().equalsIgnoreCase("100rel")) {
                    found = true;
                }
            }
        }
        if (!found) {
            Require require = new Require("100rel");
            relResponse.addHeader(require);
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("Require header with optionTag 100rel is needed -- adding one");
            }
        }
        SIPServerTransaction serverTransaction = (SIPServerTransaction) getFirstTransaction();
        setLastResponse(serverTransaction, sipResponse);
        setDialogId(sipResponse.getDialogId(true));
        serverTransaction.sendReliableProvisionalResponse(relResponse);
        startRetransmitTimer(serverTransaction, relResponse);
    }

    @Override // javax.sip.Dialog
    public void terminateOnBye(boolean terminateFlag) throws SipException {
        this.terminateOnBye = terminateFlag;
    }

    public void setAssigned() {
        this.isAssigned = true;
    }

    public boolean isAssigned() {
        return this.isAssigned;
    }

    public Contact getMyContactHeader() {
        return this.contactHeader;
    }

    void setEarlyDialogId(String earlyDialogId) {
        this.earlyDialogId = earlyDialogId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public String getEarlyDialogId() {
        return this.earlyDialogId;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void releaseAckSem() {
        if (this.isBackToBackUserAgent) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("releaseAckSem]" + this);
            }
            this.ackSem.release();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public boolean takeAckSem() {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("[takeAckSem " + this);
        }
        try {
            if (!this.ackSem.tryAcquire(2L, TimeUnit.SECONDS)) {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logError("Cannot aquire ACK semaphore");
                }
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("Semaphore previously acquired at " + this.stackTrace);
                    this.sipStack.getStackLogger().logStackTrace();
                    return false;
                }
                return false;
            }
            if (this.sipStack.isLoggingEnabled()) {
                recordStackTrace();
            }
            return true;
        } catch (InterruptedException e) {
            this.sipStack.getStackLogger().logError("Cannot aquire ACK semaphore");
            return false;
        }
    }

    private void setLastAckReceived(SIPRequest lastAckReceived) {
        this.lastAckReceived = lastAckReceived;
    }

    protected SIPRequest getLastAckReceived() {
        return this.lastAckReceived;
    }

    private void setLastAckSent(SIPRequest lastAckSent) {
        this.lastAckSent = lastAckSent;
    }

    public boolean isAtleastOneAckSent() {
        return this.isAcknowledged;
    }

    public boolean isBackToBackUserAgent() {
        return this.isBackToBackUserAgent;
    }

    public synchronized void doDeferredDeleteIfNoAckSent(long seqno) {
        if (this.sipStack.getTimer() == null) {
            setState(TERMINATED_STATE);
        } else if (this.dialogDeleteIfNoAckSentTask == null) {
            this.dialogDeleteIfNoAckSentTask = new DialogDeleteIfNoAckSentTask(seqno);
            this.sipStack.getTimer().schedule(this.dialogDeleteIfNoAckSentTask, 32000L);
        }
    }

    @Override // javax.sip.Dialog
    public void setBackToBackUserAgent() {
        this.isBackToBackUserAgent = true;
    }

    EventHeader getEventHeader() {
        return this.eventHeader;
    }

    void setEventHeader(EventHeader eventHeader) {
        this.eventHeader = eventHeader;
    }

    void setServerTransactionFlag(boolean serverTransactionFlag) {
        this.serverTransactionFlag = serverTransactionFlag;
    }

    void setReInviteFlag(boolean reInviteFlag) {
        this.reInviteFlag = reInviteFlag;
    }

    public boolean isSequnceNumberValidation() {
        return this.sequenceNumberValidation;
    }

    @Override // gov.nist.javax.sip.DialogExt
    public void disableSequenceNumberValidation() {
        this.sequenceNumberValidation = false;
    }

    public void acquireTimerTaskSem() {
        boolean acquired;
        try {
            acquired = this.timerTaskLock.tryAcquire(10L, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            acquired = false;
        }
        if (!acquired) {
            throw new IllegalStateException("Impossible to acquire the dialog timer task lock");
        }
    }

    public void releaseTimerTaskSem() {
        this.timerTaskLock.release();
    }
}