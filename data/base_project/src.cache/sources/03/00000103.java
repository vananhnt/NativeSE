package android.app;

import android.app.BackStackRecord;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import java.util.ArrayList;

/* JADX INFO: Access modifiers changed from: package-private */
/* compiled from: BackStackRecord.java */
/* loaded from: BackStackState.class */
public final class BackStackState implements Parcelable {
    final int[] mOps;
    final int mTransition;
    final int mTransitionStyle;
    final String mName;
    final int mIndex;
    final int mBreadCrumbTitleRes;
    final CharSequence mBreadCrumbTitleText;
    final int mBreadCrumbShortTitleRes;
    final CharSequence mBreadCrumbShortTitleText;
    public static final Parcelable.Creator<BackStackState> CREATOR = new Parcelable.Creator<BackStackState>() { // from class: android.app.BackStackState.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BackStackState createFromParcel(Parcel in) {
            return new BackStackState(in);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public BackStackState[] newArray(int size) {
            return new BackStackState[size];
        }
    };

    public BackStackState(FragmentManagerImpl fm, BackStackRecord bse) {
        int numRemoved = 0;
        BackStackRecord.Op op = bse.mHead;
        while (true) {
            BackStackRecord.Op op2 = op;
            if (op2 == null) {
                break;
            }
            if (op2.removed != null) {
                numRemoved += op2.removed.size();
            }
            op = op2.next;
        }
        this.mOps = new int[(bse.mNumOp * 7) + numRemoved];
        if (!bse.mAddToBackStack) {
            throw new IllegalStateException("Not on back stack");
        }
        int pos = 0;
        for (BackStackRecord.Op op3 = bse.mHead; op3 != null; op3 = op3.next) {
            int i = pos;
            int pos2 = pos + 1;
            this.mOps[i] = op3.cmd;
            int pos3 = pos2 + 1;
            this.mOps[pos2] = op3.fragment != null ? op3.fragment.mIndex : -1;
            int pos4 = pos3 + 1;
            this.mOps[pos3] = op3.enterAnim;
            int pos5 = pos4 + 1;
            this.mOps[pos4] = op3.exitAnim;
            int pos6 = pos5 + 1;
            this.mOps[pos5] = op3.popEnterAnim;
            int pos7 = pos6 + 1;
            this.mOps[pos6] = op3.popExitAnim;
            if (op3.removed != null) {
                int N = op3.removed.size();
                pos = pos7 + 1;
                this.mOps[pos7] = N;
                for (int i2 = 0; i2 < N; i2++) {
                    int i3 = pos;
                    pos++;
                    this.mOps[i3] = op3.removed.get(i2).mIndex;
                }
            } else {
                pos = pos7 + 1;
                this.mOps[pos7] = 0;
            }
        }
        this.mTransition = bse.mTransition;
        this.mTransitionStyle = bse.mTransitionStyle;
        this.mName = bse.mName;
        this.mIndex = bse.mIndex;
        this.mBreadCrumbTitleRes = bse.mBreadCrumbTitleRes;
        this.mBreadCrumbTitleText = bse.mBreadCrumbTitleText;
        this.mBreadCrumbShortTitleRes = bse.mBreadCrumbShortTitleRes;
        this.mBreadCrumbShortTitleText = bse.mBreadCrumbShortTitleText;
    }

    public BackStackState(Parcel in) {
        this.mOps = in.createIntArray();
        this.mTransition = in.readInt();
        this.mTransitionStyle = in.readInt();
        this.mName = in.readString();
        this.mIndex = in.readInt();
        this.mBreadCrumbTitleRes = in.readInt();
        this.mBreadCrumbTitleText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
        this.mBreadCrumbShortTitleRes = in.readInt();
        this.mBreadCrumbShortTitleText = TextUtils.CHAR_SEQUENCE_CREATOR.createFromParcel(in);
    }

    public BackStackRecord instantiate(FragmentManagerImpl fm) {
        BackStackRecord bse = new BackStackRecord(fm);
        int pos = 0;
        int num = 0;
        while (pos < this.mOps.length) {
            BackStackRecord.Op op = new BackStackRecord.Op();
            int i = pos;
            int pos2 = pos + 1;
            op.cmd = this.mOps[i];
            if (FragmentManagerImpl.DEBUG) {
                Log.v("FragmentManager", "Instantiate " + bse + " op #" + num + " base fragment #" + this.mOps[pos2]);
            }
            int pos3 = pos2 + 1;
            int findex = this.mOps[pos2];
            if (findex >= 0) {
                Fragment f = fm.mActive.get(findex);
                op.fragment = f;
            } else {
                op.fragment = null;
            }
            int pos4 = pos3 + 1;
            op.enterAnim = this.mOps[pos3];
            int pos5 = pos4 + 1;
            op.exitAnim = this.mOps[pos4];
            int pos6 = pos5 + 1;
            op.popEnterAnim = this.mOps[pos5];
            int pos7 = pos6 + 1;
            op.popExitAnim = this.mOps[pos6];
            pos = pos7 + 1;
            int N = this.mOps[pos7];
            if (N > 0) {
                op.removed = new ArrayList<>(N);
                for (int i2 = 0; i2 < N; i2++) {
                    if (FragmentManagerImpl.DEBUG) {
                        Log.v("FragmentManager", "Instantiate " + bse + " set remove fragment #" + this.mOps[pos]);
                    }
                    int i3 = pos;
                    pos++;
                    Fragment r = fm.mActive.get(this.mOps[i3]);
                    op.removed.add(r);
                }
            }
            bse.addOp(op);
            num++;
        }
        bse.mTransition = this.mTransition;
        bse.mTransitionStyle = this.mTransitionStyle;
        bse.mName = this.mName;
        bse.mIndex = this.mIndex;
        bse.mAddToBackStack = true;
        bse.mBreadCrumbTitleRes = this.mBreadCrumbTitleRes;
        bse.mBreadCrumbTitleText = this.mBreadCrumbTitleText;
        bse.mBreadCrumbShortTitleRes = this.mBreadCrumbShortTitleRes;
        bse.mBreadCrumbShortTitleText = this.mBreadCrumbShortTitleText;
        bse.bumpBackStackNesting(1);
        return bse;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeIntArray(this.mOps);
        dest.writeInt(this.mTransition);
        dest.writeInt(this.mTransitionStyle);
        dest.writeString(this.mName);
        dest.writeInt(this.mIndex);
        dest.writeInt(this.mBreadCrumbTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbTitleText, dest, 0);
        dest.writeInt(this.mBreadCrumbShortTitleRes);
        TextUtils.writeToParcel(this.mBreadCrumbShortTitleText, dest, 0);
    }
}