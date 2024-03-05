package gov.nist.javax.sip.stack;

import android.bluetooth.BluetoothInputDevice;
import gov.nist.core.Separators;
import gov.nist.javax.sip.SIPConstants;
import gov.nist.javax.sip.SipProviderImpl;
import gov.nist.javax.sip.TransactionExt;
import gov.nist.javax.sip.header.CallID;
import gov.nist.javax.sip.header.Event;
import gov.nist.javax.sip.header.From;
import gov.nist.javax.sip.header.To;
import gov.nist.javax.sip.header.Via;
import gov.nist.javax.sip.header.ViaList;
import gov.nist.javax.sip.message.SIPMessage;
import gov.nist.javax.sip.message.SIPRequest;
import gov.nist.javax.sip.message.SIPResponse;
import java.io.IOException;
import java.net.InetAddress;
import java.security.cert.Certificate;
import java.text.ParseException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.sip.Dialog;
import javax.sip.IOExceptionEvent;
import javax.sip.ServerTransaction;
import javax.sip.Transaction;
import javax.sip.TransactionState;
import javax.sip.message.Request;
import javax.sip.message.Response;

/* loaded from: SIPTransaction.class */
public abstract class SIPTransaction extends MessageChannel implements Transaction, TransactionExt {
    protected boolean toListener;
    protected static final int T1 = 1;
    protected static final int TIMER_A = 1;
    protected static final int TIMER_B = 64;
    protected static final int TIMER_J = 64;
    protected static final int TIMER_F = 64;
    protected static final int TIMER_H = 64;
    protected transient Object applicationData;
    protected SIPResponse lastResponse;
    protected boolean isMapped;
    protected boolean isSemaphoreAquired;
    protected String transactionId;
    public static final TransactionState INITIAL_STATE = null;
    public static final TransactionState TRYING_STATE = TransactionState.TRYING;
    public static final TransactionState CALLING_STATE = TransactionState.CALLING;
    public static final TransactionState PROCEEDING_STATE = TransactionState.PROCEEDING;
    public static final TransactionState COMPLETED_STATE = TransactionState.COMPLETED;
    public static final TransactionState CONFIRMED_STATE = TransactionState.CONFIRMED;
    public static final TransactionState TERMINATED_STATE = TransactionState.TERMINATED;
    protected static final int MAXIMUM_RETRANSMISSION_TICK_COUNT = 8;
    protected transient SIPTransactionStack sipStack;
    protected SIPRequest originalRequest;
    private transient MessageChannel encapsulatedChannel;
    protected int peerPort;
    protected InetAddress peerInetAddress;
    protected String peerAddress;
    protected String peerProtocol;
    protected int peerPacketSourcePort;
    protected InetAddress peerPacketSourceAddress;
    private String branch;
    private String method;
    private long cSeq;
    private TransactionState currentState;
    private transient int retransmissionTimerLastTickCount;
    private transient int retransmissionTimerTicksLeft;
    protected int timeoutTimerTicksLeft;
    private transient Set<SIPTransactionEventListener> eventListeners;
    protected From from;
    protected To to;
    protected Event event;
    protected CallID callId;
    protected int collectionTime;
    protected String toTag;
    protected String fromTag;
    private boolean terminatedEventDelivered;
    protected int BASE_TIMER_INTERVAL = 500;
    protected int T4 = BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED / this.BASE_TIMER_INTERVAL;
    protected int T2 = 4000 / this.BASE_TIMER_INTERVAL;
    protected int TIMER_I = this.T4;
    protected int TIMER_K = this.T4;
    protected int TIMER_D = 32000 / this.BASE_TIMER_INTERVAL;
    public long auditTag = 0;
    protected AtomicBoolean transactionTimerStarted = new AtomicBoolean(false);
    private Semaphore semaphore = new Semaphore(1, true);

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPTransaction.sendMessage(gov.nist.javax.sip.message.SIPMessage):void, file: SIPTransaction.class
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
    @Override // gov.nist.javax.sip.stack.MessageChannel
    public void sendMessage(gov.nist.javax.sip.message.SIPMessage r1) throws java.io.IOException {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPTransaction.sendMessage(gov.nist.javax.sip.message.SIPMessage):void, file: SIPTransaction.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.SIPTransaction.sendMessage(gov.nist.javax.sip.message.SIPMessage):void");
    }

    public abstract Dialog getDialog();

    public abstract void setDialog(SIPDialog sIPDialog, String str);

    /*  JADX ERROR: Method load error
        jadx.core.utils.exceptions.DecodeException: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPTransaction.acquireSem():boolean, file: SIPTransaction.class
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
    public boolean acquireSem() {
        /*
        // Can't load method instructions: Load method exception: JavaClassParseException: Unknown opcode: 0xa8 in method: gov.nist.javax.sip.stack.SIPTransaction.acquireSem():boolean, file: SIPTransaction.class
        */
        throw new UnsupportedOperationException("Method not decompiled: gov.nist.javax.sip.stack.SIPTransaction.acquireSem():boolean");
    }

    protected abstract void startTransactionTimer();

    public abstract boolean isMessagePartOfTransaction(SIPMessage sIPMessage);

    protected abstract void fireRetransmissionTimer();

    protected abstract void fireTimeoutTimer();

    @Override // javax.sip.Transaction
    public String getBranchId() {
        return this.branch;
    }

    /* loaded from: SIPTransaction$LingerTimer.class */
    class LingerTimer extends SIPStackTimerTask {
        public LingerTimer() {
            if (SIPTransaction.this.sipStack.isLoggingEnabled()) {
                SIPTransaction.this.sipStack.getStackLogger().logDebug("LingerTimer : " + SIPTransaction.this.getTransactionId());
            }
        }

        @Override // gov.nist.javax.sip.stack.SIPStackTimerTask
        protected void runTask() {
            SIPTransaction transaction = SIPTransaction.this;
            SIPTransactionStack sipStack = transaction.getSIPStack();
            if (sipStack.isLoggingEnabled()) {
                sipStack.getStackLogger().logDebug("LingerTimer: run() : " + SIPTransaction.this.getTransactionId());
            }
            if (transaction instanceof SIPClientTransaction) {
                sipStack.removeTransaction(transaction);
                transaction.close();
            } else if (transaction instanceof ServerTransaction) {
                if (sipStack.isLoggingEnabled()) {
                    sipStack.getStackLogger().logDebug("removing" + transaction);
                }
                sipStack.removeTransaction(transaction);
                if (!sipStack.cacheServerConnections) {
                    MessageChannel messageChannel = transaction.encapsulatedChannel;
                    int i = messageChannel.useCount - 1;
                    messageChannel.useCount = i;
                    if (i <= 0) {
                        transaction.close();
                        return;
                    }
                }
                if (sipStack.isLoggingEnabled() && !sipStack.cacheServerConnections && transaction.isReliable()) {
                    int useCount = transaction.encapsulatedChannel.useCount;
                    sipStack.getStackLogger().logDebug("Use Count = " + useCount);
                }
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public SIPTransaction(SIPTransactionStack newParentStack, MessageChannel newEncapsulatedChannel) {
        this.sipStack = newParentStack;
        this.encapsulatedChannel = newEncapsulatedChannel;
        this.peerPort = newEncapsulatedChannel.getPeerPort();
        this.peerAddress = newEncapsulatedChannel.getPeerAddress();
        this.peerInetAddress = newEncapsulatedChannel.getPeerInetAddress();
        this.peerPacketSourcePort = newEncapsulatedChannel.getPeerPacketSourcePort();
        this.peerPacketSourceAddress = newEncapsulatedChannel.getPeerPacketSourceAddress();
        this.peerProtocol = newEncapsulatedChannel.getPeerProtocol();
        if (isReliable()) {
            this.encapsulatedChannel.useCount++;
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("use count for encapsulated channel" + this + Separators.SP + this.encapsulatedChannel.useCount);
            }
        }
        this.currentState = null;
        disableRetransmissionTimer();
        disableTimeoutTimer();
        this.eventListeners = Collections.synchronizedSet(new HashSet());
        addEventListener(newParentStack);
    }

    public void setOriginalRequest(SIPRequest newOriginalRequest) {
        if (this.originalRequest != null && !this.originalRequest.getTransactionId().equals(newOriginalRequest.getTransactionId())) {
            this.sipStack.removeTransactionHash(this);
        }
        this.originalRequest = newOriginalRequest;
        this.method = newOriginalRequest.getMethod();
        this.from = (From) newOriginalRequest.getFrom();
        this.to = (To) newOriginalRequest.getTo();
        this.toTag = this.to.getTag();
        this.fromTag = this.from.getTag();
        this.callId = (CallID) newOriginalRequest.getCallId();
        this.cSeq = newOriginalRequest.getCSeq().getSeqNumber();
        this.event = (Event) newOriginalRequest.getHeader("Event");
        this.transactionId = newOriginalRequest.getTransactionId();
        this.originalRequest.setTransaction(this);
        String newBranch = ((Via) newOriginalRequest.getViaHeaders().getFirst()).getBranch();
        if (newBranch != null) {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("Setting Branch id : " + newBranch);
            }
            setBranch(newBranch);
            return;
        }
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Branch id is null - compute TID!" + newOriginalRequest.encode());
        }
        setBranch(newOriginalRequest.getTransactionId());
    }

    public SIPRequest getOriginalRequest() {
        return this.originalRequest;
    }

    @Override // javax.sip.Transaction
    public Request getRequest() {
        return this.originalRequest;
    }

    public final boolean isInviteTransaction() {
        return getMethod().equals("INVITE");
    }

    public final boolean isCancelTransaction() {
        return getMethod().equals(Request.CANCEL);
    }

    public final boolean isByeTransaction() {
        return getMethod().equals("BYE");
    }

    public MessageChannel getMessageChannel() {
        return this.encapsulatedChannel;
    }

    public final void setBranch(String newBranch) {
        this.branch = newBranch;
    }

    public final String getBranch() {
        if (this.branch == null) {
            this.branch = getOriginalRequest().getTopmostVia().getBranch();
        }
        return this.branch;
    }

    public final String getMethod() {
        return this.method;
    }

    public final long getCSeq() {
        return this.cSeq;
    }

    public void setState(TransactionState newState) {
        if (this.currentState == TransactionState.COMPLETED && newState != TransactionState.TERMINATED && newState != TransactionState.CONFIRMED) {
            newState = TransactionState.COMPLETED;
        }
        if (this.currentState == TransactionState.CONFIRMED && newState != TransactionState.TERMINATED) {
            newState = TransactionState.CONFIRMED;
        }
        if (this.currentState != TransactionState.TERMINATED) {
            this.currentState = newState;
        } else {
            newState = this.currentState;
        }
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Transaction:setState " + newState + Separators.SP + this + " branchID = " + getBranch() + " isClient = " + (this instanceof SIPClientTransaction));
            this.sipStack.getStackLogger().logStackTrace();
        }
    }

    public TransactionState getState() {
        return this.currentState;
    }

    protected final void enableRetransmissionTimer() {
        enableRetransmissionTimer(1);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void enableRetransmissionTimer(int tickCount) {
        if (isInviteTransaction() && (this instanceof SIPClientTransaction)) {
            this.retransmissionTimerTicksLeft = tickCount;
        } else {
            this.retransmissionTimerTicksLeft = Math.min(tickCount, 8);
        }
        this.retransmissionTimerLastTickCount = this.retransmissionTimerTicksLeft;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void disableRetransmissionTimer() {
        this.retransmissionTimerTicksLeft = -1;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void enableTimeoutTimer(int tickCount) {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("enableTimeoutTimer " + this + " tickCount " + tickCount + " currentTickCount = " + this.timeoutTimerTicksLeft);
        }
        this.timeoutTimerTicksLeft = tickCount;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public final void disableTimeoutTimer() {
        this.timeoutTimerTicksLeft = -1;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public final void fireTimer() {
        if (this.timeoutTimerTicksLeft != -1) {
            int i = this.timeoutTimerTicksLeft - 1;
            this.timeoutTimerTicksLeft = i;
            if (i == 0) {
                fireTimeoutTimer();
            }
        }
        if (this.retransmissionTimerTicksLeft != -1) {
            int i2 = this.retransmissionTimerTicksLeft - 1;
            this.retransmissionTimerTicksLeft = i2;
            if (i2 == 0) {
                enableRetransmissionTimer(this.retransmissionTimerLastTickCount * 2);
                fireRetransmissionTimer();
            }
        }
    }

    public final boolean isTerminated() {
        return getState() == TERMINATED_STATE;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel, javax.sip.Transaction
    public String getHost() {
        return this.encapsulatedChannel.getHost();
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public String getKey() {
        return this.encapsulatedChannel.getKey();
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel, javax.sip.Transaction
    public int getPort() {
        return this.encapsulatedChannel.getPort();
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public SIPTransactionStack getSIPStack() {
        return this.sipStack;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel, javax.sip.Transaction
    public String getPeerAddress() {
        return this.peerAddress;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel, javax.sip.Transaction
    public int getPeerPort() {
        return this.peerPort;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public int getPeerPacketSourcePort() {
        return this.peerPacketSourcePort;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public InetAddress getPeerPacketSourceAddress() {
        return this.peerPacketSourceAddress;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    protected InetAddress getPeerInetAddress() {
        return this.peerInetAddress;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    protected String getPeerProtocol() {
        return this.peerProtocol;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel, javax.sip.Transaction
    public String getTransport() {
        return this.encapsulatedChannel.getTransport();
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public boolean isReliable() {
        return this.encapsulatedChannel.isReliable();
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public Via getViaHeader() {
        Via channelViaHeader = super.getViaHeader();
        try {
            channelViaHeader.setBranch(this.branch);
        } catch (ParseException e) {
        }
        return channelViaHeader;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    protected void sendMessage(byte[] messageBytes, InetAddress receiverAddress, int receiverPort, boolean retry) throws IOException {
        throw new IOException("Cannot send unparsed message through Transaction Channel!");
    }

    public void addEventListener(SIPTransactionEventListener newListener) {
        this.eventListeners.add(newListener);
    }

    public void removeEventListener(SIPTransactionEventListener oldListener) {
        this.eventListeners.remove(oldListener);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void raiseErrorEvent(int errorEventID) {
        SIPTransactionErrorEvent newErrorEvent = new SIPTransactionErrorEvent(this, errorEventID);
        synchronized (this.eventListeners) {
            for (SIPTransactionEventListener nextListener : this.eventListeners) {
                nextListener.transactionErrorEvent(newErrorEvent);
            }
        }
        if (errorEventID != 3) {
            this.eventListeners.clear();
            setState(TransactionState.TERMINATED);
            if ((this instanceof SIPServerTransaction) && isByeTransaction() && getDialog() != null) {
                ((SIPDialog) getDialog()).setState(SIPDialog.TERMINATED_STATE);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public boolean isServerTransaction() {
        return this instanceof SIPServerTransaction;
    }

    @Override // javax.sip.Transaction
    public int getRetransmitTimer() {
        return 500;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public String getViaHost() {
        return getViaHeader().getHost();
    }

    public SIPResponse getLastResponse() {
        return this.lastResponse;
    }

    public Response getResponse() {
        return this.lastResponse;
    }

    public String getTransactionId() {
        return this.transactionId;
    }

    public int hashCode() {
        if (this.transactionId == null) {
            return -1;
        }
        return this.transactionId.hashCode();
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public int getViaPort() {
        return getViaHeader().getPort();
    }

    public boolean doesCancelMatchTransaction(SIPRequest requestToTest) {
        boolean transactionMatches = false;
        if (getOriginalRequest() == null || getOriginalRequest().getMethod().equals(Request.CANCEL)) {
            return false;
        }
        ViaList viaHeaders = requestToTest.getViaHeaders();
        if (viaHeaders != null) {
            Via topViaHeader = (Via) viaHeaders.getFirst();
            String messageBranch = topViaHeader.getBranch();
            if (messageBranch != null && !messageBranch.toLowerCase().startsWith(SIPConstants.BRANCH_MAGIC_COOKIE_LOWER_CASE)) {
                messageBranch = null;
            }
            if (messageBranch != null && getBranch() != null) {
                if (getBranch().equalsIgnoreCase(messageBranch) && topViaHeader.getSentBy().equals(((Via) getOriginalRequest().getViaHeaders().getFirst()).getSentBy())) {
                    transactionMatches = true;
                    if (this.sipStack.isLoggingEnabled()) {
                        this.sipStack.getStackLogger().logDebug("returning  true");
                    }
                }
            } else {
                if (this.sipStack.isLoggingEnabled()) {
                    this.sipStack.getStackLogger().logDebug("testing against " + getOriginalRequest());
                }
                if (getOriginalRequest().getRequestURI().equals(requestToTest.getRequestURI()) && getOriginalRequest().getTo().equals(requestToTest.getTo()) && getOriginalRequest().getFrom().equals(requestToTest.getFrom()) && getOriginalRequest().getCallId().getCallId().equals(requestToTest.getCallId().getCallId()) && getOriginalRequest().getCSeq().getSeqNumber() == requestToTest.getCSeq().getSeqNumber() && topViaHeader.equals(getOriginalRequest().getViaHeaders().getFirst())) {
                    transactionMatches = true;
                }
            }
        }
        if (transactionMatches) {
            setPassToListener();
        }
        return transactionMatches;
    }

    @Override // javax.sip.Transaction
    public void setRetransmitTimer(int retransmitTimer) {
        if (retransmitTimer <= 0) {
            throw new IllegalArgumentException("Retransmit timer must be positive!");
        }
        if (this.transactionTimerStarted.get()) {
            throw new IllegalStateException("Transaction timer is already started");
        }
        this.BASE_TIMER_INTERVAL = retransmitTimer;
        this.T4 = BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED / this.BASE_TIMER_INTERVAL;
        this.T2 = 4000 / this.BASE_TIMER_INTERVAL;
        this.TIMER_I = this.T4;
        this.TIMER_K = this.T4;
        this.TIMER_D = 32000 / this.BASE_TIMER_INTERVAL;
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public void close() {
        this.encapsulatedChannel.close();
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("Closing " + this.encapsulatedChannel);
        }
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public boolean isSecure() {
        return this.encapsulatedChannel.isSecure();
    }

    @Override // gov.nist.javax.sip.stack.MessageChannel
    public MessageProcessor getMessageProcessor() {
        return this.encapsulatedChannel.getMessageProcessor();
    }

    @Override // javax.sip.Transaction
    public void setApplicationData(Object applicationData) {
        this.applicationData = applicationData;
    }

    @Override // javax.sip.Transaction
    public Object getApplicationData() {
        return this.applicationData;
    }

    public void setEncapsulatedChannel(MessageChannel messageChannel) {
        this.encapsulatedChannel = messageChannel;
        this.peerInetAddress = messageChannel.getPeerInetAddress();
        this.peerPort = messageChannel.getPeerPort();
    }

    @Override // javax.sip.Transaction
    public SipProviderImpl getSipProvider() {
        return getMessageProcessor().getListeningPoint().getProvider();
    }

    public void raiseIOExceptionEvent() {
        setState(TransactionState.TERMINATED);
        String host = getPeerAddress();
        int port = getPeerPort();
        String transport = getTransport();
        IOExceptionEvent exceptionEvent = new IOExceptionEvent(this, host, port, transport);
        getSipProvider().handleEvent(exceptionEvent, this);
    }

    public void releaseSem() {
        try {
            this.toListener = false;
            semRelease();
        } catch (Exception ex) {
            this.sipStack.getStackLogger().logError("Unexpected exception releasing sem", ex);
        }
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public void semRelease() {
        try {
            if (this.sipStack.isLoggingEnabled()) {
                this.sipStack.getStackLogger().logDebug("semRelease ]]]]" + this);
                this.sipStack.getStackLogger().logStackTrace();
            }
            this.isSemaphoreAquired = false;
            this.semaphore.release();
        } catch (Exception ex) {
            this.sipStack.getStackLogger().logError("Unexpected exception releasing sem", ex);
        }
    }

    public boolean passToListener() {
        return this.toListener;
    }

    public void setPassToListener() {
        if (this.sipStack.isLoggingEnabled()) {
            this.sipStack.getStackLogger().logDebug("setPassToListener()");
        }
        this.toListener = true;
    }

    /* JADX INFO: Access modifiers changed from: protected */
    public synchronized boolean testAndSetTransactionTerminatedEvent() {
        boolean retval = !this.terminatedEventDelivered;
        this.terminatedEventDelivered = true;
        return retval;
    }

    @Override // gov.nist.javax.sip.TransactionExt
    public String getCipherSuite() throws UnsupportedOperationException {
        if (getMessageChannel() instanceof TLSMessageChannel) {
            if (((TLSMessageChannel) getMessageChannel()).getHandshakeCompletedListener() == null || ((TLSMessageChannel) getMessageChannel()).getHandshakeCompletedListener().getHandshakeCompletedEvent() == null) {
                return null;
            }
            return ((TLSMessageChannel) getMessageChannel()).getHandshakeCompletedListener().getHandshakeCompletedEvent().getCipherSuite();
        }
        throw new UnsupportedOperationException("Not a TLS channel");
    }

    @Override // gov.nist.javax.sip.TransactionExt
    public Certificate[] getLocalCertificates() throws UnsupportedOperationException {
        if (getMessageChannel() instanceof TLSMessageChannel) {
            if (((TLSMessageChannel) getMessageChannel()).getHandshakeCompletedListener() == null || ((TLSMessageChannel) getMessageChannel()).getHandshakeCompletedListener().getHandshakeCompletedEvent() == null) {
                return null;
            }
            return ((TLSMessageChannel) getMessageChannel()).getHandshakeCompletedListener().getHandshakeCompletedEvent().getLocalCertificates();
        }
        throw new UnsupportedOperationException("Not a TLS channel");
    }

    @Override // gov.nist.javax.sip.TransactionExt
    public Certificate[] getPeerCertificates() throws SSLPeerUnverifiedException {
        if (getMessageChannel() instanceof TLSMessageChannel) {
            if (((TLSMessageChannel) getMessageChannel()).getHandshakeCompletedListener() == null || ((TLSMessageChannel) getMessageChannel()).getHandshakeCompletedListener().getHandshakeCompletedEvent() == null) {
                return null;
            }
            return ((TLSMessageChannel) getMessageChannel()).getHandshakeCompletedListener().getHandshakeCompletedEvent().getPeerCertificates();
        }
        throw new UnsupportedOperationException("Not a TLS channel");
    }
}