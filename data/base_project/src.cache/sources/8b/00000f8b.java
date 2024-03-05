package android.support.v4.app;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import gov.nist.core.Separators;
import java.util.ArrayList;

/* loaded from: ShareCompat.class */
public class ShareCompat {
    public static final String EXTRA_CALLING_ACTIVITY = "android.support.v4.app.EXTRA_CALLING_ACTIVITY";
    public static final String EXTRA_CALLING_PACKAGE = "android.support.v4.app.EXTRA_CALLING_PACKAGE";
    private static ShareCompatImpl IMPL;

    /* loaded from: ShareCompat$IntentBuilder.class */
    public static class IntentBuilder {
        private Activity mActivity;
        private ArrayList<String> mBccAddresses;
        private ArrayList<String> mCcAddresses;
        private CharSequence mChooserTitle;
        private Intent mIntent = new Intent().setAction(Intent.ACTION_SEND);
        private ArrayList<Uri> mStreams;
        private ArrayList<String> mToAddresses;

        private IntentBuilder(Activity activity) {
            this.mActivity = activity;
            this.mIntent.putExtra(ShareCompat.EXTRA_CALLING_PACKAGE, activity.getPackageName());
            this.mIntent.putExtra(ShareCompat.EXTRA_CALLING_ACTIVITY, activity.getComponentName());
            this.mIntent.addFlags(524288);
        }

        private void combineArrayExtra(String str, ArrayList<String> arrayList) {
            String[] stringArrayExtra = this.mIntent.getStringArrayExtra(str);
            int length = stringArrayExtra != null ? stringArrayExtra.length : 0;
            String[] strArr = new String[arrayList.size() + length];
            arrayList.toArray(strArr);
            if (stringArrayExtra != null) {
                System.arraycopy(stringArrayExtra, 0, strArr, arrayList.size(), length);
            }
            this.mIntent.putExtra(str, strArr);
        }

        private void combineArrayExtra(String str, String[] strArr) {
            Intent intent = getIntent();
            String[] stringArrayExtra = intent.getStringArrayExtra(str);
            int length = stringArrayExtra != null ? stringArrayExtra.length : 0;
            String[] strArr2 = new String[strArr.length + length];
            if (stringArrayExtra != null) {
                System.arraycopy(stringArrayExtra, 0, strArr2, 0, length);
            }
            System.arraycopy(strArr, 0, strArr2, length, strArr.length);
            intent.putExtra(str, strArr2);
        }

        public static IntentBuilder from(Activity activity) {
            return new IntentBuilder(activity);
        }

        public IntentBuilder addEmailBcc(String str) {
            if (this.mBccAddresses == null) {
                this.mBccAddresses = new ArrayList<>();
            }
            this.mBccAddresses.add(str);
            return this;
        }

        public IntentBuilder addEmailBcc(String[] strArr) {
            combineArrayExtra(Intent.EXTRA_BCC, strArr);
            return this;
        }

        public IntentBuilder addEmailCc(String str) {
            if (this.mCcAddresses == null) {
                this.mCcAddresses = new ArrayList<>();
            }
            this.mCcAddresses.add(str);
            return this;
        }

        public IntentBuilder addEmailCc(String[] strArr) {
            combineArrayExtra(Intent.EXTRA_CC, strArr);
            return this;
        }

        public IntentBuilder addEmailTo(String str) {
            if (this.mToAddresses == null) {
                this.mToAddresses = new ArrayList<>();
            }
            this.mToAddresses.add(str);
            return this;
        }

        public IntentBuilder addEmailTo(String[] strArr) {
            combineArrayExtra(Intent.EXTRA_EMAIL, strArr);
            return this;
        }

        public IntentBuilder addStream(Uri uri) {
            Uri uri2 = (Uri) this.mIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            if (uri2 == null) {
                return setStream(uri);
            }
            if (this.mStreams == null) {
                this.mStreams = new ArrayList<>();
            }
            if (uri2 != null) {
                this.mIntent.removeExtra(Intent.EXTRA_STREAM);
                this.mStreams.add(uri2);
            }
            this.mStreams.add(uri);
            return this;
        }

        public Intent createChooserIntent() {
            return Intent.createChooser(getIntent(), this.mChooserTitle);
        }

        Activity getActivity() {
            return this.mActivity;
        }

        public Intent getIntent() {
            ArrayList<String> arrayList = this.mToAddresses;
            if (arrayList != null) {
                combineArrayExtra(Intent.EXTRA_EMAIL, arrayList);
                this.mToAddresses = null;
            }
            ArrayList<String> arrayList2 = this.mCcAddresses;
            if (arrayList2 != null) {
                combineArrayExtra(Intent.EXTRA_CC, arrayList2);
                this.mCcAddresses = null;
            }
            ArrayList<String> arrayList3 = this.mBccAddresses;
            if (arrayList3 != null) {
                combineArrayExtra(Intent.EXTRA_BCC, arrayList3);
                this.mBccAddresses = null;
            }
            ArrayList<Uri> arrayList4 = this.mStreams;
            boolean z = true;
            if (arrayList4 == null || arrayList4.size() <= 1) {
                z = false;
            }
            boolean equals = this.mIntent.getAction().equals(Intent.ACTION_SEND_MULTIPLE);
            if (!z && equals) {
                this.mIntent.setAction(Intent.ACTION_SEND);
                ArrayList<Uri> arrayList5 = this.mStreams;
                if (arrayList5 == null || arrayList5.isEmpty()) {
                    this.mIntent.removeExtra(Intent.EXTRA_STREAM);
                } else {
                    this.mIntent.putExtra(Intent.EXTRA_STREAM, this.mStreams.get(0));
                }
                this.mStreams = null;
            }
            if (z && !equals) {
                this.mIntent.setAction(Intent.ACTION_SEND_MULTIPLE);
                ArrayList<Uri> arrayList6 = this.mStreams;
                if (arrayList6 == null || arrayList6.isEmpty()) {
                    this.mIntent.removeExtra(Intent.EXTRA_STREAM);
                } else {
                    this.mIntent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, this.mStreams);
                }
            }
            return this.mIntent;
        }

        public IntentBuilder setChooserTitle(int i) {
            return setChooserTitle(this.mActivity.getText(i));
        }

        public IntentBuilder setChooserTitle(CharSequence charSequence) {
            this.mChooserTitle = charSequence;
            return this;
        }

        public IntentBuilder setEmailBcc(String[] strArr) {
            this.mIntent.putExtra(Intent.EXTRA_BCC, strArr);
            return this;
        }

        public IntentBuilder setEmailCc(String[] strArr) {
            this.mIntent.putExtra(Intent.EXTRA_CC, strArr);
            return this;
        }

        public IntentBuilder setEmailTo(String[] strArr) {
            if (this.mToAddresses != null) {
                this.mToAddresses = null;
            }
            this.mIntent.putExtra(Intent.EXTRA_EMAIL, strArr);
            return this;
        }

        public IntentBuilder setHtmlText(String str) {
            this.mIntent.putExtra("android.intent.extra.HTML_TEXT", str);
            if (!this.mIntent.hasExtra(Intent.EXTRA_TEXT)) {
                setText(Html.fromHtml(str));
            }
            return this;
        }

        public IntentBuilder setStream(Uri uri) {
            if (!this.mIntent.getAction().equals(Intent.ACTION_SEND)) {
                this.mIntent.setAction(Intent.ACTION_SEND);
            }
            this.mStreams = null;
            this.mIntent.putExtra(Intent.EXTRA_STREAM, uri);
            return this;
        }

        public IntentBuilder setSubject(String str) {
            this.mIntent.putExtra(Intent.EXTRA_SUBJECT, str);
            return this;
        }

        public IntentBuilder setText(CharSequence charSequence) {
            this.mIntent.putExtra(Intent.EXTRA_TEXT, charSequence);
            return this;
        }

        public IntentBuilder setType(String str) {
            this.mIntent.setType(str);
            return this;
        }

        public void startChooser() {
            this.mActivity.startActivity(createChooserIntent());
        }
    }

    /* loaded from: ShareCompat$IntentReader.class */
    public static class IntentReader {
        private static final String TAG = "IntentReader";
        private Activity mActivity;
        private ComponentName mCallingActivity;
        private String mCallingPackage;
        private Intent mIntent;
        private ArrayList<Uri> mStreams;

        private IntentReader(Activity activity) {
            this.mActivity = activity;
            this.mIntent = activity.getIntent();
            this.mCallingPackage = ShareCompat.getCallingPackage(activity);
            this.mCallingActivity = ShareCompat.getCallingActivity(activity);
        }

        public static IntentReader from(Activity activity) {
            return new IntentReader(activity);
        }

        public ComponentName getCallingActivity() {
            return this.mCallingActivity;
        }

        public Drawable getCallingActivityIcon() {
            if (this.mCallingActivity == null) {
                return null;
            }
            try {
                return this.mActivity.getPackageManager().getActivityIcon(this.mCallingActivity);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Could not retrieve icon for calling activity", e);
                return null;
            }
        }

        public Drawable getCallingApplicationIcon() {
            if (this.mCallingPackage == null) {
                return null;
            }
            try {
                return this.mActivity.getPackageManager().getApplicationIcon(this.mCallingPackage);
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Could not retrieve icon for calling application", e);
                return null;
            }
        }

        public CharSequence getCallingApplicationLabel() {
            if (this.mCallingPackage == null) {
                return null;
            }
            PackageManager packageManager = this.mActivity.getPackageManager();
            try {
                return packageManager.getApplicationLabel(packageManager.getApplicationInfo(this.mCallingPackage, 0));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Could not retrieve label for calling application", e);
                return null;
            }
        }

        public String getCallingPackage() {
            return this.mCallingPackage;
        }

        public String[] getEmailBcc() {
            return this.mIntent.getStringArrayExtra(Intent.EXTRA_BCC);
        }

        public String[] getEmailCc() {
            return this.mIntent.getStringArrayExtra(Intent.EXTRA_CC);
        }

        public String[] getEmailTo() {
            return this.mIntent.getStringArrayExtra(Intent.EXTRA_EMAIL);
        }

        public String getHtmlText() {
            String stringExtra = this.mIntent.getStringExtra("android.intent.extra.HTML_TEXT");
            if (stringExtra == null) {
                CharSequence text = getText();
                if (text instanceof Spanned) {
                    stringExtra = Html.toHtml((Spanned) text);
                } else if (text != null) {
                    stringExtra = ShareCompat.IMPL.escapeHtml(text);
                }
            }
            return stringExtra;
        }

        public Uri getStream() {
            return (Uri) this.mIntent.getParcelableExtra(Intent.EXTRA_STREAM);
        }

        public Uri getStream(int i) {
            if (this.mStreams == null && isMultipleShare()) {
                this.mStreams = this.mIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            }
            ArrayList<Uri> arrayList = this.mStreams;
            if (arrayList != null) {
                return arrayList.get(i);
            }
            if (i == 0) {
                return (Uri) this.mIntent.getParcelableExtra(Intent.EXTRA_STREAM);
            }
            throw new IndexOutOfBoundsException("Stream items available: " + getStreamCount() + " index requested: " + i);
        }

        public int getStreamCount() {
            if (this.mStreams == null && isMultipleShare()) {
                this.mStreams = this.mIntent.getParcelableArrayListExtra(Intent.EXTRA_STREAM);
            }
            ArrayList<Uri> arrayList = this.mStreams;
            return arrayList != null ? arrayList.size() : this.mIntent.hasExtra(Intent.EXTRA_STREAM) ? 1 : 0;
        }

        public String getSubject() {
            return this.mIntent.getStringExtra(Intent.EXTRA_SUBJECT);
        }

        public CharSequence getText() {
            return this.mIntent.getCharSequenceExtra(Intent.EXTRA_TEXT);
        }

        public String getType() {
            return this.mIntent.getType();
        }

        public boolean isMultipleShare() {
            return Intent.ACTION_SEND_MULTIPLE.equals(this.mIntent.getAction());
        }

        public boolean isShareIntent() {
            String action = this.mIntent.getAction();
            return Intent.ACTION_SEND.equals(action) || Intent.ACTION_SEND_MULTIPLE.equals(action);
        }

        public boolean isSingleShare() {
            return Intent.ACTION_SEND.equals(this.mIntent.getAction());
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    /* loaded from: ShareCompat$ShareCompatImpl.class */
    public interface ShareCompatImpl {
        void configureMenuItem(MenuItem menuItem, IntentBuilder intentBuilder);

        String escapeHtml(CharSequence charSequence);
    }

    /* loaded from: ShareCompat$ShareCompatImplBase.class */
    static class ShareCompatImplBase implements ShareCompatImpl {
        ShareCompatImplBase() {
        }

        private static void withinStyle(StringBuilder sb, CharSequence charSequence, int i, int i2) {
            while (i < i2) {
                char charAt = charSequence.charAt(i);
                if (charAt == '<') {
                    sb.append("&lt;");
                } else if (charAt == '>') {
                    sb.append("&gt;");
                } else if (charAt == '&') {
                    sb.append("&amp;");
                } else if (charAt > '~' || charAt < ' ') {
                    sb.append("&#" + ((int) charAt) + Separators.SEMICOLON);
                } else if (charAt == ' ') {
                    while (i + 1 < i2 && charSequence.charAt(i + 1) == ' ') {
                        sb.append("&nbsp;");
                        i++;
                    }
                    sb.append(' ');
                } else {
                    sb.append(charAt);
                }
                i++;
            }
        }

        @Override // android.support.v4.app.ShareCompat.ShareCompatImpl
        public void configureMenuItem(MenuItem menuItem, IntentBuilder intentBuilder) {
            menuItem.setIntent(intentBuilder.createChooserIntent());
        }

        @Override // android.support.v4.app.ShareCompat.ShareCompatImpl
        public String escapeHtml(CharSequence charSequence) {
            StringBuilder sb = new StringBuilder();
            withinStyle(sb, charSequence, 0, charSequence.length());
            return sb.toString();
        }
    }

    /* loaded from: ShareCompat$ShareCompatImplICS.class */
    static class ShareCompatImplICS extends ShareCompatImplBase {
        ShareCompatImplICS() {
        }

        @Override // android.support.v4.app.ShareCompat.ShareCompatImplBase, android.support.v4.app.ShareCompat.ShareCompatImpl
        public void configureMenuItem(MenuItem menuItem, IntentBuilder intentBuilder) {
            ShareCompatICS.configureMenuItem(menuItem, intentBuilder.getActivity(), intentBuilder.getIntent());
            if (shouldAddChooserIntent(menuItem)) {
                menuItem.setIntent(intentBuilder.createChooserIntent());
            }
        }

        boolean shouldAddChooserIntent(MenuItem menuItem) {
            return !menuItem.hasSubMenu();
        }
    }

    /* loaded from: ShareCompat$ShareCompatImplJB.class */
    static class ShareCompatImplJB extends ShareCompatImplICS {
        ShareCompatImplJB() {
        }

        @Override // android.support.v4.app.ShareCompat.ShareCompatImplBase, android.support.v4.app.ShareCompat.ShareCompatImpl
        public String escapeHtml(CharSequence charSequence) {
            return ShareCompatJB.escapeHtml(charSequence);
        }

        @Override // android.support.v4.app.ShareCompat.ShareCompatImplICS
        boolean shouldAddChooserIntent(MenuItem menuItem) {
            return false;
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 16) {
            IMPL = new ShareCompatImplJB();
        } else if (Build.VERSION.SDK_INT >= 14) {
            IMPL = new ShareCompatImplICS();
        } else {
            IMPL = new ShareCompatImplBase();
        }
    }

    public static void configureMenuItem(Menu menu, int i, IntentBuilder intentBuilder) {
        MenuItem findItem = menu.findItem(i);
        if (findItem != null) {
            configureMenuItem(findItem, intentBuilder);
            return;
        }
        throw new IllegalArgumentException("Could not find menu item with id " + i + " in the supplied menu");
    }

    public static void configureMenuItem(MenuItem menuItem, IntentBuilder intentBuilder) {
        IMPL.configureMenuItem(menuItem, intentBuilder);
    }

    public static ComponentName getCallingActivity(Activity activity) {
        ComponentName callingActivity = activity.getCallingActivity();
        return callingActivity == null ? (ComponentName) activity.getIntent().getParcelableExtra(EXTRA_CALLING_ACTIVITY) : callingActivity;
    }

    public static String getCallingPackage(Activity activity) {
        String callingPackage = activity.getCallingPackage();
        return callingPackage == null ? activity.getIntent().getStringExtra(EXTRA_CALLING_PACKAGE) : callingPackage;
    }
}