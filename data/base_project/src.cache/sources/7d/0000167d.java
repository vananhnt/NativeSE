package android.view.textservice;

import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import com.android.internal.textservice.ISpellCheckerSession;
import com.android.internal.textservice.ISpellCheckerSessionListener;
import com.android.internal.textservice.ITextServicesManager;
import com.android.internal.textservice.ITextServicesSessionListener;
import java.util.LinkedList;
import java.util.Queue;

/* loaded from: SpellCheckerSession.class */
public class SpellCheckerSession {
    private static final String TAG = SpellCheckerSession.class.getSimpleName();
    private static final boolean DBG = false;
    public static final String SERVICE_META_DATA = "android.view.textservice.scs";
    private static final int MSG_ON_GET_SUGGESTION_MULTIPLE = 1;
    private static final int MSG_ON_GET_SUGGESTION_MULTIPLE_FOR_SENTENCE = 2;
    private final InternalListener mInternalListener;
    private final ITextServicesManager mTextServicesManager;
    private final SpellCheckerInfo mSpellCheckerInfo;
    private final SpellCheckerSessionListenerImpl mSpellCheckerSessionListenerImpl;
    private final SpellCheckerSubtype mSubtype;
    private boolean mIsUsed;
    private SpellCheckerSessionListener mSpellCheckerSessionListener;
    private final Handler mHandler = new Handler() { // from class: android.view.textservice.SpellCheckerSession.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    SpellCheckerSession.this.handleOnGetSuggestionsMultiple((SuggestionsInfo[]) msg.obj);
                    return;
                case 2:
                    SpellCheckerSession.this.handleOnGetSentenceSuggestionsMultiple((SentenceSuggestionsInfo[]) msg.obj);
                    return;
                default:
                    return;
            }
        }
    };

    /* loaded from: SpellCheckerSession$SpellCheckerSessionListener.class */
    public interface SpellCheckerSessionListener {
        void onGetSuggestions(SuggestionsInfo[] suggestionsInfoArr);

        void onGetSentenceSuggestions(SentenceSuggestionsInfo[] sentenceSuggestionsInfoArr);
    }

    public SpellCheckerSession(SpellCheckerInfo info, ITextServicesManager tsm, SpellCheckerSessionListener listener, SpellCheckerSubtype subtype) {
        if (info == null || listener == null || tsm == null) {
            throw new NullPointerException();
        }
        this.mSpellCheckerInfo = info;
        this.mSpellCheckerSessionListenerImpl = new SpellCheckerSessionListenerImpl(this.mHandler);
        this.mInternalListener = new InternalListener(this.mSpellCheckerSessionListenerImpl);
        this.mTextServicesManager = tsm;
        this.mIsUsed = true;
        this.mSpellCheckerSessionListener = listener;
        this.mSubtype = subtype;
    }

    public boolean isSessionDisconnected() {
        return this.mSpellCheckerSessionListenerImpl.isDisconnected();
    }

    public SpellCheckerInfo getSpellChecker() {
        return this.mSpellCheckerInfo;
    }

    public void cancel() {
        this.mSpellCheckerSessionListenerImpl.cancel();
    }

    public void close() {
        this.mIsUsed = false;
        try {
            this.mSpellCheckerSessionListenerImpl.close();
            this.mTextServicesManager.finishSpellCheckerService(this.mSpellCheckerSessionListenerImpl);
        } catch (RemoteException e) {
        }
    }

    public void getSentenceSuggestions(TextInfo[] textInfos, int suggestionsLimit) {
        this.mSpellCheckerSessionListenerImpl.getSentenceSuggestionsMultiple(textInfos, suggestionsLimit);
    }

    @Deprecated
    public void getSuggestions(TextInfo textInfo, int suggestionsLimit) {
        getSuggestions(new TextInfo[]{textInfo}, suggestionsLimit, false);
    }

    @Deprecated
    public void getSuggestions(TextInfo[] textInfos, int suggestionsLimit, boolean sequentialWords) {
        this.mSpellCheckerSessionListenerImpl.getSuggestionsMultiple(textInfos, suggestionsLimit, sequentialWords);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnGetSuggestionsMultiple(SuggestionsInfo[] suggestionInfos) {
        this.mSpellCheckerSessionListener.onGetSuggestions(suggestionInfos);
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void handleOnGetSentenceSuggestionsMultiple(SentenceSuggestionsInfo[] suggestionInfos) {
        this.mSpellCheckerSessionListener.onGetSentenceSuggestions(suggestionInfos);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SpellCheckerSession$SpellCheckerSessionListenerImpl.class */
    public static class SpellCheckerSessionListenerImpl extends ISpellCheckerSessionListener.Stub {
        private static final int TASK_CANCEL = 1;
        private static final int TASK_GET_SUGGESTIONS_MULTIPLE = 2;
        private static final int TASK_CLOSE = 3;
        private static final int TASK_GET_SUGGESTIONS_MULTIPLE_FOR_SENTENCE = 4;
        private Handler mHandler;
        private ISpellCheckerSession mISpellCheckerSession;
        private HandlerThread mThread;
        private Handler mAsyncHandler;
        private final Queue<SpellCheckerParams> mPendingTasks = new LinkedList();
        private boolean mOpened = false;

        public SpellCheckerSessionListenerImpl(Handler handler) {
            this.mHandler = handler;
        }

        /* JADX INFO: Access modifiers changed from: private */
        /* loaded from: SpellCheckerSession$SpellCheckerSessionListenerImpl$SpellCheckerParams.class */
        public static class SpellCheckerParams {
            public final int mWhat;
            public final TextInfo[] mTextInfos;
            public final int mSuggestionsLimit;
            public final boolean mSequentialWords;
            public ISpellCheckerSession mSession;

            public SpellCheckerParams(int what, TextInfo[] textInfos, int suggestionsLimit, boolean sequentialWords) {
                this.mWhat = what;
                this.mTextInfos = textInfos;
                this.mSuggestionsLimit = suggestionsLimit;
                this.mSequentialWords = sequentialWords;
            }
        }

        /* JADX INFO: Access modifiers changed from: private */
        public void processTask(ISpellCheckerSession session, SpellCheckerParams scp, boolean async) {
            if (async || this.mAsyncHandler == null) {
                switch (scp.mWhat) {
                    case 1:
                        try {
                            session.onCancel();
                            break;
                        } catch (RemoteException e) {
                            Log.e(SpellCheckerSession.TAG, "Failed to cancel " + e);
                            break;
                        }
                    case 2:
                        try {
                            session.onGetSuggestionsMultiple(scp.mTextInfos, scp.mSuggestionsLimit, scp.mSequentialWords);
                            break;
                        } catch (RemoteException e2) {
                            Log.e(SpellCheckerSession.TAG, "Failed to get suggestions " + e2);
                            break;
                        }
                    case 3:
                        try {
                            session.onClose();
                            break;
                        } catch (RemoteException e3) {
                            Log.e(SpellCheckerSession.TAG, "Failed to close " + e3);
                            break;
                        }
                    case 4:
                        try {
                            session.onGetSentenceSuggestionsMultiple(scp.mTextInfos, scp.mSuggestionsLimit);
                            break;
                        } catch (RemoteException e4) {
                            Log.e(SpellCheckerSession.TAG, "Failed to get suggestions " + e4);
                            break;
                        }
                }
            } else {
                scp.mSession = session;
                this.mAsyncHandler.sendMessage(Message.obtain(this.mAsyncHandler, 1, scp));
            }
            if (scp.mWhat == 3) {
                synchronized (this) {
                    this.mISpellCheckerSession = null;
                    this.mHandler = null;
                    if (this.mThread != null) {
                        this.mThread.quit();
                    }
                    this.mThread = null;
                    this.mAsyncHandler = null;
                }
            }
        }

        public synchronized void onServiceConnected(ISpellCheckerSession session) {
            this.mISpellCheckerSession = session;
            if ((session.asBinder() instanceof Binder) && this.mThread == null) {
                this.mThread = new HandlerThread("SpellCheckerSession", 10);
                this.mThread.start();
                this.mAsyncHandler = new Handler(this.mThread.getLooper()) { // from class: android.view.textservice.SpellCheckerSession.SpellCheckerSessionListenerImpl.1
                    @Override // android.os.Handler
                    public void handleMessage(Message msg) {
                        SpellCheckerParams scp = (SpellCheckerParams) msg.obj;
                        SpellCheckerSessionListenerImpl.this.processTask(scp.mSession, scp, true);
                    }
                };
            }
            this.mOpened = true;
            while (!this.mPendingTasks.isEmpty()) {
                processTask(session, this.mPendingTasks.poll(), false);
            }
        }

        public void cancel() {
            processOrEnqueueTask(new SpellCheckerParams(1, null, 0, false));
        }

        public void getSuggestionsMultiple(TextInfo[] textInfos, int suggestionsLimit, boolean sequentialWords) {
            processOrEnqueueTask(new SpellCheckerParams(2, textInfos, suggestionsLimit, sequentialWords));
        }

        public void getSentenceSuggestionsMultiple(TextInfo[] textInfos, int suggestionsLimit) {
            processOrEnqueueTask(new SpellCheckerParams(4, textInfos, suggestionsLimit, false));
        }

        public void close() {
            processOrEnqueueTask(new SpellCheckerParams(3, null, 0, false));
        }

        public boolean isDisconnected() {
            return this.mOpened && this.mISpellCheckerSession == null;
        }

        private void processOrEnqueueTask(SpellCheckerParams scp) {
            synchronized (this) {
                ISpellCheckerSession session = this.mISpellCheckerSession;
                if (session == null) {
                    SpellCheckerParams closeTask = null;
                    if (scp.mWhat == 1) {
                        while (!this.mPendingTasks.isEmpty()) {
                            SpellCheckerParams tmp = this.mPendingTasks.poll();
                            if (tmp.mWhat == 3) {
                                closeTask = tmp;
                            }
                        }
                    }
                    this.mPendingTasks.offer(scp);
                    if (closeTask != null) {
                        this.mPendingTasks.offer(closeTask);
                    }
                    return;
                }
                processTask(session, scp, false);
            }
        }

        @Override // com.android.internal.textservice.ISpellCheckerSessionListener
        public void onGetSuggestions(SuggestionsInfo[] results) {
            synchronized (this) {
                if (this.mHandler != null) {
                    this.mHandler.sendMessage(Message.obtain(this.mHandler, 1, results));
                }
            }
        }

        @Override // com.android.internal.textservice.ISpellCheckerSessionListener
        public void onGetSentenceSuggestions(SentenceSuggestionsInfo[] results) {
            this.mHandler.sendMessage(Message.obtain(this.mHandler, 2, results));
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: SpellCheckerSession$InternalListener.class */
    public static class InternalListener extends ITextServicesSessionListener.Stub {
        private final SpellCheckerSessionListenerImpl mParentSpellCheckerSessionListenerImpl;

        public InternalListener(SpellCheckerSessionListenerImpl spellCheckerSessionListenerImpl) {
            this.mParentSpellCheckerSessionListenerImpl = spellCheckerSessionListenerImpl;
        }

        @Override // com.android.internal.textservice.ITextServicesSessionListener
        public void onServiceConnected(ISpellCheckerSession session) {
            this.mParentSpellCheckerSessionListenerImpl.onServiceConnected(session);
        }
    }

    protected void finalize() throws Throwable {
        super.finalize();
        if (this.mIsUsed) {
            Log.e(TAG, "SpellCheckerSession was not finished properly.You should call finishShession() when you finished to use a spell checker.");
            close();
        }
    }

    public ITextServicesSessionListener getTextServicesSessionListener() {
        return this.mInternalListener;
    }

    public ISpellCheckerSessionListener getSpellCheckerSessionListener() {
        return this.mSpellCheckerSessionListenerImpl;
    }
}