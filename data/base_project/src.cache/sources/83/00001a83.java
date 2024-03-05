package com.android.internal.telephony;

import android.Manifest;
import android.os.AsyncResult;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.ServiceManager;
import com.android.internal.telephony.IIccPhoneBook;
import com.android.internal.telephony.uicc.AdnRecord;
import com.android.internal.telephony.uicc.AdnRecordCache;
import com.android.internal.telephony.uicc.IccCardApplicationStatus;
import com.android.internal.telephony.uicc.IccRecords;
import gov.nist.core.Separators;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/* loaded from: IccPhoneBookInterfaceManager.class */
public abstract class IccPhoneBookInterfaceManager extends IIccPhoneBook.Stub {
    protected static final boolean DBG = true;
    protected PhoneBase mPhone;
    protected AdnRecordCache mAdnCache;
    protected int[] mRecordSize;
    protected boolean mSuccess;
    protected List<AdnRecord> mRecords;
    protected static final boolean ALLOW_SIM_OP_IN_UI_THREAD = false;
    protected static final int EVENT_GET_SIZE_DONE = 1;
    protected static final int EVENT_LOAD_DONE = 2;
    protected static final int EVENT_UPDATE_DONE = 3;
    protected final Object mLock = new Object();
    protected Handler mBaseHandler = new Handler() { // from class: com.android.internal.telephony.IccPhoneBookInterfaceManager.1
        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    AsyncResult ar = (AsyncResult) msg.obj;
                    synchronized (IccPhoneBookInterfaceManager.this.mLock) {
                        if (ar.exception == null) {
                            IccPhoneBookInterfaceManager.this.mRecordSize = (int[]) ar.result;
                            IccPhoneBookInterfaceManager.this.logd("GET_RECORD_SIZE Size " + IccPhoneBookInterfaceManager.this.mRecordSize[0] + " total " + IccPhoneBookInterfaceManager.this.mRecordSize[1] + " #record " + IccPhoneBookInterfaceManager.this.mRecordSize[2]);
                        }
                        notifyPending(ar);
                    }
                    return;
                case 2:
                    AsyncResult ar2 = (AsyncResult) msg.obj;
                    synchronized (IccPhoneBookInterfaceManager.this.mLock) {
                        if (ar2.exception == null) {
                            IccPhoneBookInterfaceManager.this.mRecords = (List) ar2.result;
                        } else {
                            IccPhoneBookInterfaceManager.this.logd("Cannot load ADN records");
                            if (IccPhoneBookInterfaceManager.this.mRecords != null) {
                                IccPhoneBookInterfaceManager.this.mRecords.clear();
                            }
                        }
                        notifyPending(ar2);
                    }
                    return;
                case 3:
                    AsyncResult ar3 = (AsyncResult) msg.obj;
                    synchronized (IccPhoneBookInterfaceManager.this.mLock) {
                        IccPhoneBookInterfaceManager.this.mSuccess = ar3.exception == null;
                        notifyPending(ar3);
                    }
                    return;
                default:
                    return;
            }
        }

        private void notifyPending(AsyncResult ar) {
            if (ar.userObj == null) {
                return;
            }
            AtomicBoolean status = (AtomicBoolean) ar.userObj;
            status.set(true);
            IccPhoneBookInterfaceManager.this.mLock.notifyAll();
        }
    };

    protected abstract void logd(String str);

    protected abstract void loge(String str);

    @Override // com.android.internal.telephony.IIccPhoneBook
    public abstract int[] getAdnRecordsSize(int i);

    public IccPhoneBookInterfaceManager(PhoneBase phone) {
        this.mPhone = phone;
        IccRecords r = phone.mIccRecords.get();
        if (r != null) {
            this.mAdnCache = r.getAdnCache();
        }
    }

    public void dispose() {
    }

    public void updateIccRecords(IccRecords iccRecords) {
        if (iccRecords != null) {
            this.mAdnCache = iccRecords.getAdnCache();
        } else {
            this.mAdnCache = null;
        }
    }

    protected void publish() {
        ServiceManager.addService("simphonebook", this);
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public boolean updateAdnRecordsInEfBySearch(int efid, String oldTag, String oldPhoneNumber, String newTag, String newPhoneNumber, String pin2) {
        if (this.mPhone.getContext().checkCallingOrSelfPermission(Manifest.permission.WRITE_CONTACTS) != 0) {
            throw new SecurityException("Requires android.permission.WRITE_CONTACTS permission");
        }
        logd("updateAdnRecordsInEfBySearch: efid=" + efid + " (" + oldTag + Separators.COMMA + oldPhoneNumber + Separators.RPAREN + "==> (" + newTag + Separators.COMMA + newPhoneNumber + Separators.RPAREN + " pin2=" + pin2);
        int efid2 = updateEfForIccType(efid);
        synchronized (this.mLock) {
            checkThread();
            this.mSuccess = false;
            AtomicBoolean status = new AtomicBoolean(false);
            Message response = this.mBaseHandler.obtainMessage(3, status);
            AdnRecord oldAdn = new AdnRecord(oldTag, oldPhoneNumber);
            AdnRecord newAdn = new AdnRecord(newTag, newPhoneNumber);
            if (this.mAdnCache != null) {
                this.mAdnCache.updateAdnBySearch(efid2, oldAdn, newAdn, pin2, response);
                waitForResult(status);
            } else {
                loge("Failure while trying to update by search due to uninitialised adncache");
            }
        }
        return this.mSuccess;
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public boolean updateAdnRecordsInEfByIndex(int efid, String newTag, String newPhoneNumber, int index, String pin2) {
        if (this.mPhone.getContext().checkCallingOrSelfPermission(Manifest.permission.WRITE_CONTACTS) != 0) {
            throw new SecurityException("Requires android.permission.WRITE_CONTACTS permission");
        }
        logd("updateAdnRecordsInEfByIndex: efid=" + efid + " Index=" + index + " ==> " + Separators.LPAREN + newTag + Separators.COMMA + newPhoneNumber + Separators.RPAREN + " pin2=" + pin2);
        synchronized (this.mLock) {
            checkThread();
            this.mSuccess = false;
            AtomicBoolean status = new AtomicBoolean(false);
            Message response = this.mBaseHandler.obtainMessage(3, status);
            AdnRecord newAdn = new AdnRecord(newTag, newPhoneNumber);
            if (this.mAdnCache != null) {
                this.mAdnCache.updateAdnByIndex(efid, newAdn, index, pin2, response);
                waitForResult(status);
            } else {
                loge("Failure while trying to update by index due to uninitialised adncache");
            }
        }
        return this.mSuccess;
    }

    @Override // com.android.internal.telephony.IIccPhoneBook
    public List<AdnRecord> getAdnRecordsInEf(int efid) {
        if (this.mPhone.getContext().checkCallingOrSelfPermission(Manifest.permission.READ_CONTACTS) != 0) {
            throw new SecurityException("Requires android.permission.READ_CONTACTS permission");
        }
        int efid2 = updateEfForIccType(efid);
        logd("getAdnRecordsInEF: efid=" + efid2);
        synchronized (this.mLock) {
            checkThread();
            AtomicBoolean status = new AtomicBoolean(false);
            Message response = this.mBaseHandler.obtainMessage(2, status);
            if (this.mAdnCache != null) {
                this.mAdnCache.requestLoadAllAdnLike(efid2, this.mAdnCache.extensionEfForEf(efid2), response);
                waitForResult(status);
            } else {
                loge("Failure while trying to load from SIM due to uninitialised adncache");
            }
        }
        return this.mRecords;
    }

    protected void checkThread() {
        if (this.mBaseHandler.getLooper().equals(Looper.myLooper())) {
            loge("query() called on the main UI thread!");
            throw new IllegalStateException("You cannot call query on this provder from the main UI thread.");
        }
    }

    protected void waitForResult(AtomicBoolean status) {
        while (!status.get()) {
            try {
                this.mLock.wait();
            } catch (InterruptedException e) {
                logd("interrupted while trying to update by search");
            }
        }
    }

    private int updateEfForIccType(int efid) {
        if (efid == 28474 && this.mPhone.getCurrentUiccAppType() == IccCardApplicationStatus.AppType.APPTYPE_USIM) {
            return 20272;
        }
        return efid;
    }
}