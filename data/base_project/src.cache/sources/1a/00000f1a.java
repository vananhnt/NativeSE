package android.support.v4.app;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.BackStackRecord;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.DebugUtils;
import android.support.v4.util.LogWriter;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import gov.nist.core.Separators;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* JADX INFO: Access modifiers changed from: package-private */
/* loaded from: FragmentManagerImpl.class */
public final class FragmentManagerImpl extends FragmentManager implements LayoutInflater.Factory {
    static final Interpolator ACCELERATE_CUBIC;
    static final Interpolator ACCELERATE_QUINT;
    static final int ANIM_DUR = 220;
    public static final int ANIM_STYLE_CLOSE_ENTER = 3;
    public static final int ANIM_STYLE_CLOSE_EXIT = 4;
    public static final int ANIM_STYLE_FADE_ENTER = 5;
    public static final int ANIM_STYLE_FADE_EXIT = 6;
    public static final int ANIM_STYLE_OPEN_ENTER = 1;
    public static final int ANIM_STYLE_OPEN_EXIT = 2;
    static boolean DEBUG = false;
    static final Interpolator DECELERATE_CUBIC;
    static final Interpolator DECELERATE_QUINT;
    static final boolean HONEYCOMB;
    static final String TAG = "FragmentManager";
    static final String TARGET_REQUEST_CODE_STATE_TAG = "android:target_req_state";
    static final String TARGET_STATE_TAG = "android:target_state";
    static final String USER_VISIBLE_HINT_TAG = "android:user_visible_hint";
    static final String VIEW_STATE_TAG = "android:view_state";
    ArrayList<Fragment> mActive;
    FragmentActivity mActivity;
    ArrayList<Fragment> mAdded;
    ArrayList<Integer> mAvailBackStackIndices;
    ArrayList<Integer> mAvailIndices;
    ArrayList<BackStackRecord> mBackStack;
    ArrayList<FragmentManager.OnBackStackChangedListener> mBackStackChangeListeners;
    ArrayList<BackStackRecord> mBackStackIndices;
    FragmentContainer mContainer;
    ArrayList<Fragment> mCreatedMenus;
    boolean mDestroyed;
    boolean mExecutingActions;
    boolean mHavePendingDeferredStart;
    boolean mNeedMenuInvalidate;
    String mNoTransactionsBecause;
    Fragment mParent;
    ArrayList<Runnable> mPendingActions;
    boolean mStateSaved;
    Runnable[] mTmpActions;
    int mCurState = 0;
    Bundle mStateBundle = null;
    SparseArray<Parcelable> mStateArray = null;
    Runnable mExecCommit = new Runnable(this) { // from class: android.support.v4.app.FragmentManagerImpl.1
        final FragmentManagerImpl this$0;

        {
            this.this$0 = this;
        }

        @Override // java.lang.Runnable
        public void run() {
            this.this$0.execPendingActions();
        }
    };

    /* loaded from: FragmentManagerImpl$FragmentTag.class */
    static class FragmentTag {
        public static final int[] Fragment = {16842755, 16842960, 16842961};
        public static final int Fragment_id = 1;
        public static final int Fragment_name = 0;
        public static final int Fragment_tag = 2;

        FragmentTag() {
        }
    }

    static {
        boolean z = false;
        if (Build.VERSION.SDK_INT >= 11) {
            z = true;
        }
        HONEYCOMB = z;
        DECELERATE_QUINT = new DecelerateInterpolator(2.5f);
        DECELERATE_CUBIC = new DecelerateInterpolator(1.5f);
        ACCELERATE_QUINT = new AccelerateInterpolator(2.5f);
        ACCELERATE_CUBIC = new AccelerateInterpolator(1.5f);
    }

    private void checkStateLoss() {
        if (this.mStateSaved) {
            throw new IllegalStateException("Can not perform this action after onSaveInstanceState");
        }
        if (this.mNoTransactionsBecause == null) {
            return;
        }
        throw new IllegalStateException("Can not perform this action inside of " + this.mNoTransactionsBecause);
    }

    static Animation makeFadeAnimation(Context context, float f, float f2) {
        AlphaAnimation alphaAnimation = new AlphaAnimation(f, f2);
        alphaAnimation.setInterpolator(DECELERATE_CUBIC);
        alphaAnimation.setDuration(220L);
        return alphaAnimation;
    }

    static Animation makeOpenCloseAnimation(Context context, float f, float f2, float f3, float f4) {
        AnimationSet animationSet = new AnimationSet(false);
        ScaleAnimation scaleAnimation = new ScaleAnimation(f, f2, f, f2, 1, 0.5f, 1, 0.5f);
        scaleAnimation.setInterpolator(DECELERATE_QUINT);
        scaleAnimation.setDuration(220L);
        animationSet.addAnimation(scaleAnimation);
        AlphaAnimation alphaAnimation = new AlphaAnimation(f3, f4);
        alphaAnimation.setInterpolator(DECELERATE_CUBIC);
        alphaAnimation.setDuration(220L);
        animationSet.addAnimation(alphaAnimation);
        return animationSet;
    }

    public static int reverseTransit(int i) {
        return i != 4097 ? i != 4099 ? i != 8194 ? 0 : 4097 : 4099 : 8194;
    }

    private void throwException(RuntimeException runtimeException) {
        Log.e(TAG, runtimeException.getMessage());
        Log.e(TAG, "Activity state:");
        PrintWriter printWriter = new PrintWriter(new LogWriter(TAG));
        FragmentActivity fragmentActivity = this.mActivity;
        if (fragmentActivity != null) {
            try {
                fragmentActivity.dump("  ", null, printWriter, new String[0]);
            } catch (Exception e) {
                Log.e(TAG, "Failed dumping state", e);
            }
        } else {
            try {
                dump("  ", null, printWriter, new String[0]);
            } catch (Exception e2) {
                Log.e(TAG, "Failed dumping state", e2);
            }
        }
        throw runtimeException;
    }

    public static int transitToStyleIndex(int i, boolean z) {
        return i != 4097 ? i != 4099 ? i != 8194 ? -1 : z ? 3 : 4 : z ? 5 : 6 : z ? 1 : 2;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void addBackStackState(BackStackRecord backStackRecord) {
        if (this.mBackStack == null) {
            this.mBackStack = new ArrayList<>();
        }
        this.mBackStack.add(backStackRecord);
        reportBackStackChanged();
    }

    public void addFragment(Fragment fragment, boolean z) {
        if (this.mAdded == null) {
            this.mAdded = new ArrayList<>();
        }
        if (DEBUG) {
            Log.v(TAG, "add: " + fragment);
        }
        makeActive(fragment);
        if (fragment.mDetached) {
            return;
        }
        if (this.mAdded.contains(fragment)) {
            throw new IllegalStateException("Fragment already added: " + fragment);
        }
        this.mAdded.add(fragment);
        fragment.mAdded = true;
        fragment.mRemoving = false;
        if (fragment.mHasMenu && fragment.mMenuVisible) {
            this.mNeedMenuInvalidate = true;
        }
        if (z) {
            moveToState(fragment);
        }
    }

    @Override // android.support.v4.app.FragmentManager
    public void addOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener onBackStackChangedListener) {
        if (this.mBackStackChangeListeners == null) {
            this.mBackStackChangeListeners = new ArrayList<>();
        }
        this.mBackStackChangeListeners.add(onBackStackChangedListener);
    }

    public int allocBackStackIndex(BackStackRecord backStackRecord) {
        synchronized (this) {
            if (this.mAvailBackStackIndices != null && this.mAvailBackStackIndices.size() > 0) {
                int intValue = this.mAvailBackStackIndices.remove(this.mAvailBackStackIndices.size() - 1).intValue();
                if (DEBUG) {
                    Log.v(TAG, "Adding back stack index " + intValue + " with " + backStackRecord);
                }
                this.mBackStackIndices.set(intValue, backStackRecord);
                return intValue;
            }
            if (this.mBackStackIndices == null) {
                this.mBackStackIndices = new ArrayList<>();
            }
            int size = this.mBackStackIndices.size();
            if (DEBUG) {
                Log.v(TAG, "Setting back stack index " + size + " to " + backStackRecord);
            }
            this.mBackStackIndices.add(backStackRecord);
            return size;
        }
    }

    public void attachActivity(FragmentActivity fragmentActivity, FragmentContainer fragmentContainer, Fragment fragment) {
        if (this.mActivity != null) {
            throw new IllegalStateException("Already attached");
        }
        this.mActivity = fragmentActivity;
        this.mContainer = fragmentContainer;
        this.mParent = fragment;
    }

    public void attachFragment(Fragment fragment, int i, int i2) {
        if (DEBUG) {
            Log.v(TAG, "attach: " + fragment);
        }
        if (fragment.mDetached) {
            fragment.mDetached = false;
            if (fragment.mAdded) {
                return;
            }
            if (this.mAdded == null) {
                this.mAdded = new ArrayList<>();
            }
            if (this.mAdded.contains(fragment)) {
                throw new IllegalStateException("Fragment already added: " + fragment);
            }
            if (DEBUG) {
                Log.v(TAG, "add from attach: " + fragment);
            }
            this.mAdded.add(fragment);
            fragment.mAdded = true;
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            moveToState(fragment, this.mCurState, i, i2, false);
        }
    }

    @Override // android.support.v4.app.FragmentManager
    public FragmentTransaction beginTransaction() {
        return new BackStackRecord(this);
    }

    public void detachFragment(Fragment fragment, int i, int i2) {
        if (DEBUG) {
            Log.v(TAG, "detach: " + fragment);
        }
        if (fragment.mDetached) {
            return;
        }
        fragment.mDetached = true;
        if (fragment.mAdded) {
            if (this.mAdded != null) {
                if (DEBUG) {
                    Log.v(TAG, "remove from detach: " + fragment);
                }
                this.mAdded.remove(fragment);
            }
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.mAdded = false;
            moveToState(fragment, 1, i, i2, false);
        }
    }

    public void dispatchActivityCreated() {
        this.mStateSaved = false;
        moveToState(2, false);
    }

    public void dispatchConfigurationChanged(Configuration configuration) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performConfigurationChanged(configuration);
                }
            }
        }
    }

    public boolean dispatchContextItemSelected(MenuItem menuItem) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = this.mAdded.get(i);
                if (fragment != null && fragment.performContextItemSelected(menuItem)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public void dispatchCreate() {
        this.mStateSaved = false;
        moveToState(1, false);
    }

    public boolean dispatchCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        boolean z = false;
        ArrayList<Fragment> arrayList = null;
        if (this.mAdded != null) {
            arrayList = null;
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = this.mAdded.get(i);
                if (fragment != null && fragment.performCreateOptionsMenu(menu, menuInflater)) {
                    z = true;
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(fragment);
                }
            }
        } else {
            z = false;
        }
        if (this.mCreatedMenus != null) {
            for (int i2 = 0; i2 < this.mCreatedMenus.size(); i2++) {
                Fragment fragment2 = this.mCreatedMenus.get(i2);
                if (arrayList == null || !arrayList.contains(fragment2)) {
                    fragment2.onDestroyOptionsMenu();
                }
            }
        }
        this.mCreatedMenus = arrayList;
        return z;
    }

    public void dispatchDestroy() {
        this.mDestroyed = true;
        execPendingActions();
        moveToState(0, false);
        this.mActivity = null;
        this.mContainer = null;
        this.mParent = null;
    }

    public void dispatchDestroyView() {
        moveToState(1, false);
    }

    public void dispatchLowMemory() {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performLowMemory();
                }
            }
        }
    }

    public boolean dispatchOptionsItemSelected(MenuItem menuItem) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = this.mAdded.get(i);
                if (fragment != null && fragment.performOptionsItemSelected(menuItem)) {
                    return true;
                }
            }
            return false;
        }
        return false;
    }

    public void dispatchOptionsMenuClosed(Menu menu) {
        if (this.mAdded != null) {
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = this.mAdded.get(i);
                if (fragment != null) {
                    fragment.performOptionsMenuClosed(menu);
                }
            }
        }
    }

    public void dispatchPause() {
        moveToState(4, false);
    }

    public boolean dispatchPrepareOptionsMenu(Menu menu) {
        boolean z = false;
        if (this.mAdded != null) {
            z = false;
            for (int i = 0; i < this.mAdded.size(); i++) {
                Fragment fragment = this.mAdded.get(i);
                if (fragment != null && fragment.performPrepareOptionsMenu(menu)) {
                    z = true;
                }
            }
        }
        return z;
    }

    public void dispatchReallyStop() {
        moveToState(2, false);
    }

    public void dispatchResume() {
        this.mStateSaved = false;
        moveToState(5, false);
    }

    public void dispatchStart() {
        this.mStateSaved = false;
        moveToState(4, false);
    }

    public void dispatchStop() {
        this.mStateSaved = true;
        moveToState(3, false);
    }

    @Override // android.support.v4.app.FragmentManager
    public void dump(String str, FileDescriptor fileDescriptor, PrintWriter printWriter, String[] strArr) {
        int size;
        int size2;
        int size3;
        int size4;
        int size5;
        int size6;
        String str2 = str + "    ";
        ArrayList<Fragment> arrayList = this.mActive;
        if (arrayList != null && (size6 = arrayList.size()) > 0) {
            printWriter.print(str);
            printWriter.print("Active Fragments in ");
            printWriter.print(Integer.toHexString(System.identityHashCode(this)));
            printWriter.println(Separators.COLON);
            for (int i = 0; i < size6; i++) {
                Fragment fragment = this.mActive.get(i);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i);
                printWriter.print(": ");
                printWriter.println(fragment);
                if (fragment != null) {
                    fragment.dump(str2, fileDescriptor, printWriter, strArr);
                }
            }
        }
        ArrayList<Fragment> arrayList2 = this.mAdded;
        if (arrayList2 != null && (size5 = arrayList2.size()) > 0) {
            printWriter.print(str);
            printWriter.println("Added Fragments:");
            for (int i2 = 0; i2 < size5; i2++) {
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i2);
                printWriter.print(": ");
                printWriter.println(this.mAdded.get(i2).toString());
            }
        }
        ArrayList<Fragment> arrayList3 = this.mCreatedMenus;
        if (arrayList3 != null && (size4 = arrayList3.size()) > 0) {
            printWriter.print(str);
            printWriter.println("Fragments Created Menus:");
            for (int i3 = 0; i3 < size4; i3++) {
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i3);
                printWriter.print(": ");
                printWriter.println(this.mCreatedMenus.get(i3).toString());
            }
        }
        ArrayList<BackStackRecord> arrayList4 = this.mBackStack;
        if (arrayList4 != null && (size3 = arrayList4.size()) > 0) {
            printWriter.print(str);
            printWriter.println("Back Stack:");
            for (int i4 = 0; i4 < size3; i4++) {
                BackStackRecord backStackRecord = this.mBackStack.get(i4);
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i4);
                printWriter.print(": ");
                printWriter.println(backStackRecord.toString());
                backStackRecord.dump(str2, fileDescriptor, printWriter, strArr);
            }
        }
        synchronized (this) {
            if (this.mBackStackIndices != null && (size2 = this.mBackStackIndices.size()) > 0) {
                printWriter.print(str);
                printWriter.println("Back Stack Indices:");
                for (int i5 = 0; i5 < size2; i5++) {
                    printWriter.print(str);
                    printWriter.print("  #");
                    printWriter.print(i5);
                    printWriter.print(": ");
                    printWriter.println((BackStackRecord) this.mBackStackIndices.get(i5));
                }
            }
            if (this.mAvailBackStackIndices != null && this.mAvailBackStackIndices.size() > 0) {
                printWriter.print(str);
                printWriter.print("mAvailBackStackIndices: ");
                printWriter.println(Arrays.toString(this.mAvailBackStackIndices.toArray()));
            }
        }
        ArrayList<Runnable> arrayList5 = this.mPendingActions;
        if (arrayList5 != null && (size = arrayList5.size()) > 0) {
            printWriter.print(str);
            printWriter.println("Pending Actions:");
            for (int i6 = 0; i6 < size; i6++) {
                printWriter.print(str);
                printWriter.print("  #");
                printWriter.print(i6);
                printWriter.print(": ");
                printWriter.println((Runnable) this.mPendingActions.get(i6));
            }
        }
        printWriter.print(str);
        printWriter.println("FragmentManager misc state:");
        printWriter.print(str);
        printWriter.print("  mActivity=");
        printWriter.println(this.mActivity);
        printWriter.print(str);
        printWriter.print("  mContainer=");
        printWriter.println(this.mContainer);
        if (this.mParent != null) {
            printWriter.print(str);
            printWriter.print("  mParent=");
            printWriter.println(this.mParent);
        }
        printWriter.print(str);
        printWriter.print("  mCurState=");
        printWriter.print(this.mCurState);
        printWriter.print(" mStateSaved=");
        printWriter.print(this.mStateSaved);
        printWriter.print(" mDestroyed=");
        printWriter.println(this.mDestroyed);
        if (this.mNeedMenuInvalidate) {
            printWriter.print(str);
            printWriter.print("  mNeedMenuInvalidate=");
            printWriter.println(this.mNeedMenuInvalidate);
        }
        if (this.mNoTransactionsBecause != null) {
            printWriter.print(str);
            printWriter.print("  mNoTransactionsBecause=");
            printWriter.println(this.mNoTransactionsBecause);
        }
        ArrayList<Integer> arrayList6 = this.mAvailIndices;
        if (arrayList6 == null || arrayList6.size() <= 0) {
            return;
        }
        printWriter.print(str);
        printWriter.print("  mAvailIndices: ");
        printWriter.println(Arrays.toString(this.mAvailIndices.toArray()));
    }

    public void enqueueAction(Runnable runnable, boolean z) {
        if (!z) {
            checkStateLoss();
        }
        synchronized (this) {
            if (this.mDestroyed || this.mActivity == null) {
                throw new IllegalStateException("Activity has been destroyed");
            }
            if (this.mPendingActions == null) {
                this.mPendingActions = new ArrayList<>();
            }
            this.mPendingActions.add(runnable);
            if (this.mPendingActions.size() == 1) {
                this.mActivity.mHandler.removeCallbacks(this.mExecCommit);
                this.mActivity.mHandler.post(this.mExecCommit);
            }
        }
    }

    /* JADX WARN: Code restructure failed: missing block: B:25:0x0079, code lost:
        r4.mExecutingActions = true;
        r7 = 0;
     */
    /* JADX WARN: Code restructure failed: missing block: B:27:0x0081, code lost:
        if (r7 >= r0) goto L26;
     */
    /* JADX WARN: Code restructure failed: missing block: B:28:0x0084, code lost:
        r4.mTmpActions[r7].run();
        r4.mTmpActions[r7] = null;
        r7 = r7 + 1;
     */
    /* JADX WARN: Removed duplicated region for block: B:35:0x00af  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    public boolean execPendingActions() {
        /*
            Method dump skipped, instructions count: 286
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentManagerImpl.execPendingActions():boolean");
    }

    @Override // android.support.v4.app.FragmentManager
    public boolean executePendingTransactions() {
        return execPendingActions();
    }

    @Override // android.support.v4.app.FragmentManager
    public Fragment findFragmentById(int i) {
        ArrayList<Fragment> arrayList = this.mAdded;
        if (arrayList != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                Fragment fragment = this.mAdded.get(size);
                if (fragment != null && fragment.mFragmentId == i) {
                    return fragment;
                }
            }
        }
        ArrayList<Fragment> arrayList2 = this.mActive;
        if (arrayList2 != null) {
            for (int size2 = arrayList2.size() - 1; size2 >= 0; size2--) {
                Fragment fragment2 = this.mActive.get(size2);
                if (fragment2 != null && fragment2.mFragmentId == i) {
                    return fragment2;
                }
            }
            return null;
        }
        return null;
    }

    @Override // android.support.v4.app.FragmentManager
    public Fragment findFragmentByTag(String str) {
        ArrayList<Fragment> arrayList = this.mAdded;
        if (arrayList != null && str != null) {
            for (int size = arrayList.size() - 1; size >= 0; size--) {
                Fragment fragment = this.mAdded.get(size);
                if (fragment != null && str.equals(fragment.mTag)) {
                    return fragment;
                }
            }
        }
        ArrayList<Fragment> arrayList2 = this.mActive;
        if (arrayList2 == null || str == null) {
            return null;
        }
        for (int size2 = arrayList2.size() - 1; size2 >= 0; size2--) {
            Fragment fragment2 = this.mActive.get(size2);
            if (fragment2 != null && str.equals(fragment2.mTag)) {
                return fragment2;
            }
        }
        return null;
    }

    public Fragment findFragmentByWho(String str) {
        Fragment findFragmentByWho;
        ArrayList<Fragment> arrayList = this.mActive;
        if (arrayList == null || str == null) {
            return null;
        }
        for (int size = arrayList.size() - 1; size >= 0; size--) {
            Fragment fragment = this.mActive.get(size);
            if (fragment != null && (findFragmentByWho = fragment.findFragmentByWho(str)) != null) {
                return findFragmentByWho;
            }
        }
        return null;
    }

    public void freeBackStackIndex(int i) {
        synchronized (this) {
            this.mBackStackIndices.set(i, null);
            if (this.mAvailBackStackIndices == null) {
                this.mAvailBackStackIndices = new ArrayList<>();
            }
            if (DEBUG) {
                Log.v(TAG, "Freeing back stack index " + i);
            }
            this.mAvailBackStackIndices.add(Integer.valueOf(i));
        }
    }

    @Override // android.support.v4.app.FragmentManager
    public FragmentManager.BackStackEntry getBackStackEntryAt(int i) {
        return this.mBackStack.get(i);
    }

    @Override // android.support.v4.app.FragmentManager
    public int getBackStackEntryCount() {
        ArrayList<BackStackRecord> arrayList = this.mBackStack;
        return arrayList != null ? arrayList.size() : 0;
    }

    @Override // android.support.v4.app.FragmentManager
    public Fragment getFragment(Bundle bundle, String str) {
        int i = bundle.getInt(str, -1);
        if (i == -1) {
            return null;
        }
        if (i >= this.mActive.size()) {
            throwException(new IllegalStateException("Fragment no longer exists for key " + str + ": index " + i));
        }
        Fragment fragment = this.mActive.get(i);
        if (fragment == null) {
            throwException(new IllegalStateException("Fragment no longer exists for key " + str + ": index " + i));
        }
        return fragment;
    }

    @Override // android.support.v4.app.FragmentManager
    public List<Fragment> getFragments() {
        return this.mActive;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public LayoutInflater.Factory getLayoutInflaterFactory() {
        return this;
    }

    public void hideFragment(Fragment fragment, int i, int i2) {
        if (DEBUG) {
            Log.v(TAG, "hide: " + fragment);
        }
        if (fragment.mHidden) {
            return;
        }
        fragment.mHidden = true;
        if (fragment.mView != null) {
            Animation loadAnimation = loadAnimation(fragment, i, false, i2);
            if (loadAnimation != null) {
                fragment.mView.startAnimation(loadAnimation);
            }
            fragment.mView.setVisibility(8);
        }
        if (fragment.mAdded && fragment.mHasMenu && fragment.mMenuVisible) {
            this.mNeedMenuInvalidate = true;
        }
        fragment.onHiddenChanged(true);
    }

    @Override // android.support.v4.app.FragmentManager
    public boolean isDestroyed() {
        return this.mDestroyed;
    }

    Animation loadAnimation(Fragment fragment, int i, boolean z, int i2) {
        int transitToStyleIndex;
        Animation loadAnimation;
        Animation onCreateAnimation = fragment.onCreateAnimation(i, z, fragment.mNextAnim);
        if (onCreateAnimation != null) {
            return onCreateAnimation;
        }
        if (fragment.mNextAnim == 0 || (loadAnimation = AnimationUtils.loadAnimation(this.mActivity, fragment.mNextAnim)) == null) {
            if (i != 0 && (transitToStyleIndex = transitToStyleIndex(i, z)) >= 0) {
                switch (transitToStyleIndex) {
                    case 1:
                        return makeOpenCloseAnimation(this.mActivity, 1.125f, 1.0f, 0.0f, 1.0f);
                    case 2:
                        return makeOpenCloseAnimation(this.mActivity, 1.0f, 0.975f, 1.0f, 0.0f);
                    case 3:
                        return makeOpenCloseAnimation(this.mActivity, 0.975f, 1.0f, 0.0f, 1.0f);
                    case 4:
                        return makeOpenCloseAnimation(this.mActivity, 1.0f, 1.075f, 1.0f, 0.0f);
                    case 5:
                        return makeFadeAnimation(this.mActivity, 0.0f, 1.0f);
                    case 6:
                        return makeFadeAnimation(this.mActivity, 1.0f, 0.0f);
                    default:
                        if (i2 == 0 && this.mActivity.getWindow() != null) {
                            i2 = this.mActivity.getWindow().getAttributes().windowAnimations;
                        }
                        return i2 == 0 ? null : null;
                }
            }
            return null;
        }
        return loadAnimation;
    }

    void makeActive(Fragment fragment) {
        if (fragment.mIndex >= 0) {
            return;
        }
        ArrayList<Integer> arrayList = this.mAvailIndices;
        if (arrayList == null || arrayList.size() <= 0) {
            if (this.mActive == null) {
                this.mActive = new ArrayList<>();
            }
            fragment.setIndex(this.mActive.size(), this.mParent);
            this.mActive.add(fragment);
        } else {
            ArrayList<Integer> arrayList2 = this.mAvailIndices;
            fragment.setIndex(arrayList2.remove(arrayList2.size() - 1).intValue(), this.mParent);
            this.mActive.set(fragment.mIndex, fragment);
        }
        if (DEBUG) {
            Log.v(TAG, "Allocated fragment index " + fragment);
        }
    }

    void makeInactive(Fragment fragment) {
        if (fragment.mIndex < 0) {
            return;
        }
        if (DEBUG) {
            Log.v(TAG, "Freeing fragment index " + fragment);
        }
        this.mActive.set(fragment.mIndex, null);
        if (this.mAvailIndices == null) {
            this.mAvailIndices = new ArrayList<>();
        }
        this.mAvailIndices.add(Integer.valueOf(fragment.mIndex));
        this.mActivity.invalidateSupportFragment(fragment.mWho);
        fragment.initState();
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void moveToState(int i, int i2, int i3, boolean z) {
        FragmentActivity fragmentActivity;
        if (this.mActivity == null && i != 0) {
            throw new IllegalStateException("No activity");
        }
        if (z || this.mCurState != i) {
            this.mCurState = i;
            if (this.mActive != null) {
                boolean z2 = false;
                for (int i4 = 0; i4 < this.mActive.size(); i4++) {
                    Fragment fragment = this.mActive.get(i4);
                    if (fragment != null) {
                        moveToState(fragment, i, i2, i3, false);
                        if (fragment.mLoaderManager != null) {
                            z2 |= fragment.mLoaderManager.hasRunningLoaders();
                        }
                    }
                }
                if (!z2) {
                    startPendingDeferredFragments();
                }
                if (this.mNeedMenuInvalidate && (fragmentActivity = this.mActivity) != null && this.mCurState == 5) {
                    fragmentActivity.supportInvalidateOptionsMenu();
                    this.mNeedMenuInvalidate = false;
                }
            }
        }
    }

    void moveToState(int i, boolean z) {
        moveToState(i, 0, 0, z);
    }

    void moveToState(Fragment fragment) {
        moveToState(fragment, this.mCurState, 0, 0, false);
    }

    /* JADX WARN: Removed duplicated region for block: B:137:0x04aa  */
    /* JADX WARN: Removed duplicated region for block: B:144:0x04e3  */
    /* JADX WARN: Removed duplicated region for block: B:151:0x051c  */
    /* JADX WARN: Removed duplicated region for block: B:179:0x05ee  */
    /*
        Code decompiled incorrectly, please refer to instructions dump.
        To view partially-correct code enable 'Show inconsistent code' option in preferences
    */
    void moveToState(android.support.v4.app.Fragment r8, int r9, int r10, int r11, boolean r12) {
        /*
            Method dump skipped, instructions count: 1745
            To view this dump change 'Code comments level' option to 'DEBUG'
        */
        throw new UnsupportedOperationException("Method not decompiled: android.support.v4.app.FragmentManagerImpl.moveToState(android.support.v4.app.Fragment, int, int, int, boolean):void");
    }

    public void noteStateNotSaved() {
        this.mStateSaved = false;
    }

    @Override // android.view.LayoutInflater.Factory
    public View onCreateView(String str, Context context, AttributeSet attributeSet) {
        boolean equals = "fragment".equals(str);
        Fragment fragment = null;
        if (equals) {
            String attributeValue = attributeSet.getAttributeValue(null, "class");
            TypedArray obtainStyledAttributes = context.obtainStyledAttributes(attributeSet, FragmentTag.Fragment);
            if (attributeValue == null) {
                attributeValue = obtainStyledAttributes.getString(0);
            }
            int resourceId = obtainStyledAttributes.getResourceId(1, -1);
            String string = obtainStyledAttributes.getString(2);
            obtainStyledAttributes.recycle();
            if (Fragment.isSupportFragmentClass(this.mActivity, attributeValue)) {
                if (0 != 0) {
                    throw new NullPointerException();
                }
                if (-1 == 0 && resourceId == -1 && string == null) {
                    throw new IllegalArgumentException(attributeSet.getPositionDescription() + ": Must specify unique android:id, android:tag, or have a parent with an id for " + attributeValue);
                }
                if (resourceId != -1) {
                    fragment = findFragmentById(resourceId);
                }
                if (fragment == null && string != null) {
                    fragment = findFragmentByTag(string);
                }
                if (fragment == null && -1 != 0) {
                    fragment = findFragmentById(0);
                }
                if (DEBUG) {
                    Log.v(TAG, "onCreateView: id=0x" + Integer.toHexString(resourceId) + " fname=" + attributeValue + " existing=" + fragment);
                }
                if (fragment == null) {
                    fragment = Fragment.instantiate(context, attributeValue);
                    fragment.mFromLayout = true;
                    fragment.mFragmentId = resourceId != 0 ? resourceId : 0;
                    fragment.mContainerId = 0;
                    fragment.mTag = string;
                    fragment.mInLayout = true;
                    fragment.mFragmentManager = this;
                    fragment.onInflate(this.mActivity, attributeSet, fragment.mSavedFragmentState);
                    addFragment(fragment, true);
                } else if (fragment.mInLayout) {
                    throw new IllegalArgumentException(attributeSet.getPositionDescription() + ": Duplicate id 0x" + Integer.toHexString(resourceId) + ", tag " + string + ", or parent id 0x" + Integer.toHexString(0) + " with another fragment for " + attributeValue);
                } else {
                    fragment.mInLayout = true;
                    if (!fragment.mRetaining) {
                        fragment.onInflate(this.mActivity, attributeSet, fragment.mSavedFragmentState);
                    }
                }
                if (this.mCurState >= 1 || !fragment.mFromLayout) {
                    moveToState(fragment);
                } else {
                    moveToState(fragment, 1, 0, 0, false);
                }
                if (fragment.mView != null) {
                    if (resourceId != 0) {
                        fragment.mView.setId(resourceId);
                    }
                    if (fragment.mView.getTag() == null) {
                        fragment.mView.setTag(string);
                    }
                    return fragment.mView;
                }
                throw new IllegalStateException("Fragment " + attributeValue + " did not create a view.");
            }
            return null;
        }
        return null;
    }

    public void performPendingDeferredStart(Fragment fragment) {
        if (fragment.mDeferStart) {
            if (this.mExecutingActions) {
                this.mHavePendingDeferredStart = true;
                return;
            }
            fragment.mDeferStart = false;
            moveToState(fragment, this.mCurState, 0, 0, false);
        }
    }

    @Override // android.support.v4.app.FragmentManager
    public void popBackStack() {
        enqueueAction(new Runnable(this) { // from class: android.support.v4.app.FragmentManagerImpl.2
            final FragmentManagerImpl this$0;

            {
                this.this$0 = this;
            }

            @Override // java.lang.Runnable
            public void run() {
                FragmentManagerImpl fragmentManagerImpl = this.this$0;
                fragmentManagerImpl.popBackStackState(fragmentManagerImpl.mActivity.mHandler, null, -1, 0);
            }
        }, false);
    }

    @Override // android.support.v4.app.FragmentManager
    public void popBackStack(int i, int i2) {
        if (i >= 0) {
            enqueueAction(new Runnable(this, i, i2) { // from class: android.support.v4.app.FragmentManagerImpl.4
                final FragmentManagerImpl this$0;
                final int val$flags;
                final int val$id;

                {
                    this.this$0 = this;
                    this.val$id = i;
                    this.val$flags = i2;
                }

                @Override // java.lang.Runnable
                public void run() {
                    FragmentManagerImpl fragmentManagerImpl = this.this$0;
                    fragmentManagerImpl.popBackStackState(fragmentManagerImpl.mActivity.mHandler, null, this.val$id, this.val$flags);
                }
            }, false);
            return;
        }
        throw new IllegalArgumentException("Bad id: " + i);
    }

    @Override // android.support.v4.app.FragmentManager
    public void popBackStack(String str, int i) {
        enqueueAction(new Runnable(this, str, i) { // from class: android.support.v4.app.FragmentManagerImpl.3
            final FragmentManagerImpl this$0;
            final int val$flags;
            final String val$name;

            {
                this.this$0 = this;
                this.val$name = str;
                this.val$flags = i;
            }

            @Override // java.lang.Runnable
            public void run() {
                FragmentManagerImpl fragmentManagerImpl = this.this$0;
                fragmentManagerImpl.popBackStackState(fragmentManagerImpl.mActivity.mHandler, this.val$name, -1, this.val$flags);
            }
        }, false);
    }

    @Override // android.support.v4.app.FragmentManager
    public boolean popBackStackImmediate() {
        checkStateLoss();
        executePendingTransactions();
        return popBackStackState(this.mActivity.mHandler, null, -1, 0);
    }

    @Override // android.support.v4.app.FragmentManager
    public boolean popBackStackImmediate(int i, int i2) {
        checkStateLoss();
        executePendingTransactions();
        if (i >= 0) {
            return popBackStackState(this.mActivity.mHandler, null, i, i2);
        }
        throw new IllegalArgumentException("Bad id: " + i);
    }

    @Override // android.support.v4.app.FragmentManager
    public boolean popBackStackImmediate(String str, int i) {
        checkStateLoss();
        executePendingTransactions();
        return popBackStackState(this.mActivity.mHandler, str, -1, i);
    }

    boolean popBackStackState(Handler handler, String str, int i, int i2) {
        int i3;
        ArrayList<BackStackRecord> arrayList = this.mBackStack;
        if (arrayList == null) {
            return false;
        }
        if (str == null && i < 0 && (i2 & 1) == 0) {
            int size = arrayList.size() - 1;
            if (size < 0) {
                return false;
            }
            BackStackRecord remove = this.mBackStack.remove(size);
            SparseArray<Fragment> sparseArray = new SparseArray<>();
            SparseArray<Fragment> sparseArray2 = new SparseArray<>();
            remove.calculateBackFragments(sparseArray, sparseArray2);
            remove.popFromBackStack(true, null, sparseArray, sparseArray2);
            reportBackStackChanged();
            return true;
        }
        if (str != null || i >= 0) {
            int size2 = this.mBackStack.size() - 1;
            while (size2 >= 0) {
                BackStackRecord backStackRecord = this.mBackStack.get(size2);
                if ((str != null && str.equals(backStackRecord.getName())) || (i >= 0 && i == backStackRecord.mIndex)) {
                    break;
                }
                size2--;
            }
            if (size2 < 0) {
                return false;
            }
            if ((i2 & 1) != 0) {
                int i4 = size2 - 1;
                while (i4 >= 0) {
                    BackStackRecord backStackRecord2 = this.mBackStack.get(i4);
                    if ((str == null || !str.equals(backStackRecord2.getName())) && (i < 0 || i != backStackRecord2.mIndex)) {
                        i3 = i4;
                        break;
                    }
                    i4--;
                }
                i3 = i4;
            } else {
                i3 = size2;
            }
        } else {
            i3 = -1;
        }
        if (i3 == this.mBackStack.size() - 1) {
            return false;
        }
        ArrayList arrayList2 = new ArrayList();
        for (int size3 = this.mBackStack.size() - 1; size3 > i3; size3--) {
            arrayList2.add(this.mBackStack.remove(size3));
        }
        int size4 = arrayList2.size() - 1;
        SparseArray<Fragment> sparseArray3 = new SparseArray<>();
        SparseArray<Fragment> sparseArray4 = new SparseArray<>();
        for (int i5 = 0; i5 <= size4; i5++) {
            ((BackStackRecord) arrayList2.get(i5)).calculateBackFragments(sparseArray3, sparseArray4);
        }
        BackStackRecord.TransitionState transitionState = null;
        int i6 = 0;
        while (i6 <= size4) {
            if (DEBUG) {
                Log.v(TAG, "Popping back stack state: " + arrayList2.get(i6));
            }
            transitionState = ((BackStackRecord) arrayList2.get(i6)).popFromBackStack(i6 == size4, transitionState, sparseArray3, sparseArray4);
            i6++;
        }
        reportBackStackChanged();
        return true;
    }

    @Override // android.support.v4.app.FragmentManager
    public void putFragment(Bundle bundle, String str, Fragment fragment) {
        if (fragment.mIndex < 0) {
            throwException(new IllegalStateException("Fragment " + fragment + " is not currently in the FragmentManager"));
        }
        bundle.putInt(str, fragment.mIndex);
    }

    public void removeFragment(Fragment fragment, int i, int i2) {
        if (DEBUG) {
            Log.v(TAG, "remove: " + fragment + " nesting=" + fragment.mBackStackNesting);
        }
        boolean z = !fragment.isInBackStack();
        if (!fragment.mDetached || z) {
            ArrayList<Fragment> arrayList = this.mAdded;
            if (arrayList != null) {
                arrayList.remove(fragment);
            }
            if (fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.mAdded = false;
            fragment.mRemoving = true;
            moveToState(fragment, z ? 0 : 1, i, i2, false);
        }
    }

    @Override // android.support.v4.app.FragmentManager
    public void removeOnBackStackChangedListener(FragmentManager.OnBackStackChangedListener onBackStackChangedListener) {
        ArrayList<FragmentManager.OnBackStackChangedListener> arrayList = this.mBackStackChangeListeners;
        if (arrayList != null) {
            arrayList.remove(onBackStackChangedListener);
        }
    }

    void reportBackStackChanged() {
        if (this.mBackStackChangeListeners != null) {
            for (int i = 0; i < this.mBackStackChangeListeners.size(); i++) {
                this.mBackStackChangeListeners.get(i).onBackStackChanged();
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void restoreAllState(Parcelable parcelable, ArrayList<Fragment> arrayList) {
        if (parcelable == null) {
            return;
        }
        FragmentManagerState fragmentManagerState = (FragmentManagerState) parcelable;
        if (fragmentManagerState.mActive == null) {
            return;
        }
        if (arrayList != null) {
            for (int i = 0; i < arrayList.size(); i++) {
                Fragment fragment = arrayList.get(i);
                if (DEBUG) {
                    Log.v(TAG, "restoreAllState: re-attaching retained " + fragment);
                }
                FragmentState fragmentState = fragmentManagerState.mActive[fragment.mIndex];
                fragmentState.mInstance = fragment;
                fragment.mSavedViewState = null;
                fragment.mBackStackNesting = 0;
                fragment.mInLayout = false;
                fragment.mAdded = false;
                fragment.mTarget = null;
                if (fragmentState.mSavedFragmentState != null) {
                    fragmentState.mSavedFragmentState.setClassLoader(this.mActivity.getClassLoader());
                    fragment.mSavedViewState = fragmentState.mSavedFragmentState.getSparseParcelableArray(VIEW_STATE_TAG);
                    fragment.mSavedFragmentState = fragmentState.mSavedFragmentState;
                }
            }
        }
        this.mActive = new ArrayList<>(fragmentManagerState.mActive.length);
        ArrayList<Integer> arrayList2 = this.mAvailIndices;
        if (arrayList2 != null) {
            arrayList2.clear();
        }
        for (int i2 = 0; i2 < fragmentManagerState.mActive.length; i2++) {
            FragmentState fragmentState2 = fragmentManagerState.mActive[i2];
            if (fragmentState2 != null) {
                Fragment instantiate = fragmentState2.instantiate(this.mActivity, this.mParent);
                if (DEBUG) {
                    Log.v(TAG, "restoreAllState: active #" + i2 + ": " + instantiate);
                }
                this.mActive.add(instantiate);
                fragmentState2.mInstance = null;
            } else {
                this.mActive.add(null);
                if (this.mAvailIndices == null) {
                    this.mAvailIndices = new ArrayList<>();
                }
                if (DEBUG) {
                    Log.v(TAG, "restoreAllState: avail #" + i2);
                }
                this.mAvailIndices.add(Integer.valueOf(i2));
            }
        }
        if (arrayList != null) {
            for (int i3 = 0; i3 < arrayList.size(); i3++) {
                Fragment fragment2 = arrayList.get(i3);
                if (fragment2.mTargetIndex >= 0) {
                    if (fragment2.mTargetIndex < this.mActive.size()) {
                        fragment2.mTarget = this.mActive.get(fragment2.mTargetIndex);
                    } else {
                        Log.w(TAG, "Re-attaching retained fragment " + fragment2 + " target no longer exists: " + fragment2.mTargetIndex);
                        fragment2.mTarget = null;
                    }
                }
            }
        }
        if (fragmentManagerState.mAdded != null) {
            this.mAdded = new ArrayList<>(fragmentManagerState.mAdded.length);
            for (int i4 = 0; i4 < fragmentManagerState.mAdded.length; i4++) {
                Fragment fragment3 = this.mActive.get(fragmentManagerState.mAdded[i4]);
                if (fragment3 == null) {
                    throwException(new IllegalStateException("No instantiated fragment for index #" + fragmentManagerState.mAdded[i4]));
                }
                fragment3.mAdded = true;
                if (DEBUG) {
                    Log.v(TAG, "restoreAllState: added #" + i4 + ": " + fragment3);
                }
                if (this.mAdded.contains(fragment3)) {
                    throw new IllegalStateException("Already added!");
                }
                this.mAdded.add(fragment3);
            }
        } else {
            this.mAdded = null;
        }
        if (fragmentManagerState.mBackStack == null) {
            this.mBackStack = null;
            return;
        }
        this.mBackStack = new ArrayList<>(fragmentManagerState.mBackStack.length);
        for (int i5 = 0; i5 < fragmentManagerState.mBackStack.length; i5++) {
            BackStackRecord instantiate2 = fragmentManagerState.mBackStack[i5].instantiate(this);
            if (DEBUG) {
                Log.v(TAG, "restoreAllState: back stack #" + i5 + " (index " + instantiate2.mIndex + "): " + instantiate2);
                instantiate2.dump("  ", new PrintWriter(new LogWriter(TAG)), false);
            }
            this.mBackStack.add(instantiate2);
            if (instantiate2.mIndex >= 0) {
                setBackStackIndex(instantiate2.mIndex, instantiate2);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public ArrayList<Fragment> retainNonConfig() {
        ArrayList<Fragment> arrayList = null;
        if (this.mActive != null) {
            arrayList = null;
            for (int i = 0; i < this.mActive.size(); i++) {
                Fragment fragment = this.mActive.get(i);
                if (fragment != null && fragment.mRetainInstance) {
                    if (arrayList == null) {
                        arrayList = new ArrayList<>();
                    }
                    arrayList.add(fragment);
                    fragment.mRetaining = true;
                    fragment.mTargetIndex = fragment.mTarget != null ? fragment.mTarget.mIndex : -1;
                    if (DEBUG) {
                        Log.v(TAG, "retainNonConfig: keeping retained " + fragment);
                    }
                }
            }
        }
        return arrayList;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Parcelable saveAllState() {
        int size;
        int size2;
        execPendingActions();
        if (HONEYCOMB) {
            this.mStateSaved = true;
        }
        ArrayList<Fragment> arrayList = this.mActive;
        if (arrayList == null || arrayList.size() <= 0) {
            return null;
        }
        int size3 = this.mActive.size();
        FragmentState[] fragmentStateArr = new FragmentState[size3];
        boolean z = false;
        for (int i = 0; i < size3; i++) {
            Fragment fragment = this.mActive.get(i);
            if (fragment != null) {
                if (fragment.mIndex < 0) {
                    throwException(new IllegalStateException("Failure saving state: active " + fragment + " has cleared index: " + fragment.mIndex));
                }
                z = true;
                FragmentState fragmentState = new FragmentState(fragment);
                fragmentStateArr[i] = fragmentState;
                if (fragment.mState <= 0 || fragmentState.mSavedFragmentState != null) {
                    fragmentState.mSavedFragmentState = fragment.mSavedFragmentState;
                } else {
                    fragmentState.mSavedFragmentState = saveFragmentBasicState(fragment);
                    if (fragment.mTarget != null) {
                        if (fragment.mTarget.mIndex < 0) {
                            throwException(new IllegalStateException("Failure saving state: " + fragment + " has target not in fragment manager: " + fragment.mTarget));
                        }
                        if (fragmentState.mSavedFragmentState == null) {
                            fragmentState.mSavedFragmentState = new Bundle();
                        }
                        putFragment(fragmentState.mSavedFragmentState, TARGET_STATE_TAG, fragment.mTarget);
                        if (fragment.mTargetRequestCode != 0) {
                            fragmentState.mSavedFragmentState.putInt(TARGET_REQUEST_CODE_STATE_TAG, fragment.mTargetRequestCode);
                        }
                    }
                }
                if (DEBUG) {
                    Log.v(TAG, "Saved state of " + fragment + ": " + fragmentState.mSavedFragmentState);
                }
            }
        }
        if (!z) {
            if (DEBUG) {
                Log.v(TAG, "saveAllState: no fragments!");
                return null;
            }
            return null;
        }
        int[] iArr = null;
        BackStackState[] backStackStateArr = null;
        ArrayList<Fragment> arrayList2 = this.mAdded;
        if (arrayList2 != null && (size2 = arrayList2.size()) > 0) {
            iArr = new int[size2];
            for (int i2 = 0; i2 < size2; i2++) {
                iArr[i2] = this.mAdded.get(i2).mIndex;
                if (iArr[i2] < 0) {
                    throwException(new IllegalStateException("Failure saving state: active " + this.mAdded.get(i2) + " has cleared index: " + iArr[i2]));
                }
                if (DEBUG) {
                    Log.v(TAG, "saveAllState: adding fragment #" + i2 + ": " + this.mAdded.get(i2));
                }
            }
        }
        ArrayList<BackStackRecord> arrayList3 = this.mBackStack;
        if (arrayList3 != null && (size = arrayList3.size()) > 0) {
            backStackStateArr = new BackStackState[size];
            for (int i3 = 0; i3 < size; i3++) {
                backStackStateArr[i3] = new BackStackState(this, this.mBackStack.get(i3));
                if (DEBUG) {
                    Log.v(TAG, "saveAllState: adding back stack #" + i3 + ": " + this.mBackStack.get(i3));
                }
            }
        }
        FragmentManagerState fragmentManagerState = new FragmentManagerState();
        fragmentManagerState.mActive = fragmentStateArr;
        fragmentManagerState.mAdded = iArr;
        fragmentManagerState.mBackStack = backStackStateArr;
        return fragmentManagerState;
    }

    Bundle saveFragmentBasicState(Fragment fragment) {
        Bundle bundle = null;
        if (this.mStateBundle == null) {
            this.mStateBundle = new Bundle();
        }
        fragment.performSaveInstanceState(this.mStateBundle);
        if (!this.mStateBundle.isEmpty()) {
            bundle = this.mStateBundle;
            this.mStateBundle = null;
        }
        if (fragment.mView != null) {
            saveFragmentViewState(fragment);
        }
        if (fragment.mSavedViewState != null) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putSparseParcelableArray(VIEW_STATE_TAG, fragment.mSavedViewState);
        }
        if (!fragment.mUserVisibleHint) {
            if (bundle == null) {
                bundle = new Bundle();
            }
            bundle.putBoolean(USER_VISIBLE_HINT_TAG, fragment.mUserVisibleHint);
        }
        return bundle;
    }

    @Override // android.support.v4.app.FragmentManager
    public Fragment.SavedState saveFragmentInstanceState(Fragment fragment) {
        if (fragment.mIndex < 0) {
            throwException(new IllegalStateException("Fragment " + fragment + " is not currently in the FragmentManager"));
        }
        if (fragment.mState > 0) {
            Bundle saveFragmentBasicState = saveFragmentBasicState(fragment);
            Fragment.SavedState savedState = null;
            if (saveFragmentBasicState != null) {
                savedState = new Fragment.SavedState(saveFragmentBasicState);
            }
            return savedState;
        }
        return null;
    }

    void saveFragmentViewState(Fragment fragment) {
        if (fragment.mInnerView == null) {
            return;
        }
        SparseArray<Parcelable> sparseArray = this.mStateArray;
        if (sparseArray == null) {
            this.mStateArray = new SparseArray<>();
        } else {
            sparseArray.clear();
        }
        fragment.mInnerView.saveHierarchyState(this.mStateArray);
        if (this.mStateArray.size() > 0) {
            fragment.mSavedViewState = this.mStateArray;
            this.mStateArray = null;
        }
    }

    public void setBackStackIndex(int i, BackStackRecord backStackRecord) {
        synchronized (this) {
            if (this.mBackStackIndices == null) {
                this.mBackStackIndices = new ArrayList<>();
            }
            int size = this.mBackStackIndices.size();
            if (i < size) {
                if (DEBUG) {
                    Log.v(TAG, "Setting back stack index " + i + " to " + backStackRecord);
                }
                this.mBackStackIndices.set(i, backStackRecord);
            } else {
                while (size < i) {
                    this.mBackStackIndices.add(null);
                    if (this.mAvailBackStackIndices == null) {
                        this.mAvailBackStackIndices = new ArrayList<>();
                    }
                    if (DEBUG) {
                        Log.v(TAG, "Adding available back stack index " + size);
                    }
                    this.mAvailBackStackIndices.add(Integer.valueOf(size));
                    size++;
                }
                if (DEBUG) {
                    Log.v(TAG, "Adding back stack index " + i + " with " + backStackRecord);
                }
                this.mBackStackIndices.add(backStackRecord);
            }
        }
    }

    public void showFragment(Fragment fragment, int i, int i2) {
        if (DEBUG) {
            Log.v(TAG, "show: " + fragment);
        }
        if (fragment.mHidden) {
            fragment.mHidden = false;
            if (fragment.mView != null) {
                Animation loadAnimation = loadAnimation(fragment, i, true, i2);
                if (loadAnimation != null) {
                    fragment.mView.startAnimation(loadAnimation);
                }
                fragment.mView.setVisibility(0);
            }
            if (fragment.mAdded && fragment.mHasMenu && fragment.mMenuVisible) {
                this.mNeedMenuInvalidate = true;
            }
            fragment.onHiddenChanged(false);
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void startPendingDeferredFragments() {
        if (this.mActive == null) {
            return;
        }
        for (int i = 0; i < this.mActive.size(); i++) {
            Fragment fragment = this.mActive.get(i);
            if (fragment != null) {
                performPendingDeferredStart(fragment);
            }
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(128);
        sb.append("FragmentManager{");
        sb.append(Integer.toHexString(System.identityHashCode(this)));
        sb.append(" in ");
        Fragment fragment = this.mParent;
        if (fragment != null) {
            DebugUtils.buildShortClassTag(fragment, sb);
        } else {
            DebugUtils.buildShortClassTag(this.mActivity, sb);
        }
        sb.append("}}");
        return sb.toString();
    }
}