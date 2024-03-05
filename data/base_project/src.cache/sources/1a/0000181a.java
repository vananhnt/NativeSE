package android.widget;

import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.AttributeSet;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import com.android.internal.R;

/* loaded from: QuickContactBadge.class */
public class QuickContactBadge extends ImageView implements View.OnClickListener {
    private Uri mContactUri;
    private String mContactEmail;
    private String mContactPhone;
    private Drawable mOverlay;
    private QueryHandler mQueryHandler;
    private Drawable mDefaultAvatar;
    private Bundle mExtras;
    protected String[] mExcludeMimes;
    private static final int TOKEN_EMAIL_LOOKUP = 0;
    private static final int TOKEN_PHONE_LOOKUP = 1;
    private static final int TOKEN_EMAIL_LOOKUP_AND_TRIGGER = 2;
    private static final int TOKEN_PHONE_LOOKUP_AND_TRIGGER = 3;
    private static final String EXTRA_URI_CONTENT = "uri_content";
    static final int EMAIL_ID_COLUMN_INDEX = 0;
    static final int EMAIL_LOOKUP_STRING_COLUMN_INDEX = 1;
    static final int PHONE_ID_COLUMN_INDEX = 0;
    static final int PHONE_LOOKUP_STRING_COLUMN_INDEX = 1;
    static final String[] EMAIL_LOOKUP_PROJECTION = {"contact_id", ContactsContract.ContactsColumns.LOOKUP_KEY};
    static final String[] PHONE_LOOKUP_PROJECTION = {"_id", ContactsContract.ContactsColumns.LOOKUP_KEY};

    public QuickContactBadge(Context context) {
        this(context, null);
    }

    public QuickContactBadge(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public QuickContactBadge(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mExtras = null;
        this.mExcludeMimes = null;
        TypedArray styledAttributes = this.mContext.obtainStyledAttributes(R.styleable.Theme);
        this.mOverlay = styledAttributes.getDrawable(236);
        styledAttributes.recycle();
        this.mQueryHandler = new QueryHandler(this.mContext.getContentResolver());
        setOnClickListener(this);
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ImageView, android.view.View
    public void drawableStateChanged() {
        super.drawableStateChanged();
        if (this.mOverlay != null && this.mOverlay.isStateful()) {
            this.mOverlay.setState(getDrawableState());
            invalidate();
        }
    }

    public void setMode(int size) {
    }

    /* JADX INFO: Access modifiers changed from: protected */
    @Override // android.widget.ImageView, android.view.View
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isEnabled() || this.mOverlay == null || this.mOverlay.getIntrinsicWidth() == 0 || this.mOverlay.getIntrinsicHeight() == 0) {
            return;
        }
        this.mOverlay.setBounds(0, 0, getWidth(), getHeight());
        if (this.mPaddingTop == 0 && this.mPaddingLeft == 0) {
            this.mOverlay.draw(canvas);
            return;
        }
        int saveCount = canvas.getSaveCount();
        canvas.save();
        canvas.translate(this.mPaddingLeft, this.mPaddingTop);
        this.mOverlay.draw(canvas);
        canvas.restoreToCount(saveCount);
    }

    private boolean isAssigned() {
        return (this.mContactUri == null && this.mContactEmail == null && this.mContactPhone == null) ? false : true;
    }

    public void setImageToDefault() {
        if (this.mDefaultAvatar == null) {
            this.mDefaultAvatar = getResources().getDrawable(R.drawable.ic_contact_picture);
        }
        setImageDrawable(this.mDefaultAvatar);
    }

    public void assignContactUri(Uri contactUri) {
        this.mContactUri = contactUri;
        this.mContactEmail = null;
        this.mContactPhone = null;
        onContactUriChanged();
    }

    public void assignContactFromEmail(String emailAddress, boolean lazyLookup) {
        assignContactFromEmail(emailAddress, lazyLookup, null);
    }

    public void assignContactFromEmail(String emailAddress, boolean lazyLookup, Bundle extras) {
        this.mContactEmail = emailAddress;
        this.mExtras = extras;
        if (!lazyLookup) {
            this.mQueryHandler.startQuery(0, null, Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode(this.mContactEmail)), EMAIL_LOOKUP_PROJECTION, null, null, null);
            return;
        }
        this.mContactUri = null;
        onContactUriChanged();
    }

    public void assignContactFromPhone(String phoneNumber, boolean lazyLookup) {
        assignContactFromPhone(phoneNumber, lazyLookup, new Bundle());
    }

    public void assignContactFromPhone(String phoneNumber, boolean lazyLookup, Bundle extras) {
        this.mContactPhone = phoneNumber;
        this.mExtras = extras;
        if (!lazyLookup) {
            this.mQueryHandler.startQuery(1, null, Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, this.mContactPhone), PHONE_LOOKUP_PROJECTION, null, null, null);
            return;
        }
        this.mContactUri = null;
        onContactUriChanged();
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void onContactUriChanged() {
        setEnabled(isAssigned());
    }

    @Override // android.view.View.OnClickListener
    public void onClick(View v) {
        Bundle extras = this.mExtras == null ? new Bundle() : this.mExtras;
        if (this.mContactUri != null) {
            ContactsContract.QuickContact.showQuickContact(getContext(), this, this.mContactUri, 3, this.mExcludeMimes);
        } else if (this.mContactEmail != null) {
            extras.putString(EXTRA_URI_CONTENT, this.mContactEmail);
            this.mQueryHandler.startQuery(2, extras, Uri.withAppendedPath(ContactsContract.CommonDataKinds.Email.CONTENT_LOOKUP_URI, Uri.encode(this.mContactEmail)), EMAIL_LOOKUP_PROJECTION, null, null, null);
        } else if (this.mContactPhone != null) {
            extras.putString(EXTRA_URI_CONTENT, this.mContactPhone);
            this.mQueryHandler.startQuery(3, extras, Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, this.mContactPhone), PHONE_LOOKUP_PROJECTION, null, null, null);
        }
    }

    @Override // android.widget.ImageView, android.view.View
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(QuickContactBadge.class.getName());
    }

    @Override // android.widget.ImageView, android.view.View
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(QuickContactBadge.class.getName());
    }

    public void setExcludeMimes(String[] excludeMimes) {
        this.mExcludeMimes = excludeMimes;
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: QuickContactBadge$QueryHandler.class */
    public class QueryHandler extends AsyncQueryHandler {
        public QueryHandler(ContentResolver cr) {
            super(cr);
        }

        /* JADX WARN: Can't fix incorrect switch cases order, some code will duplicate */
        @Override // android.content.AsyncQueryHandler
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            Uri lookupUri = null;
            Uri createUri = null;
            boolean trigger = false;
            Bundle extras = cookie != null ? (Bundle) cookie : new Bundle();
            try {
                switch (token) {
                    case 0:
                        if (cursor != null && cursor.moveToFirst()) {
                            long contactId = cursor.getLong(0);
                            String lookupKey = cursor.getString(1);
                            lookupUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey);
                            break;
                        }
                        break;
                    case 1:
                        if (cursor != null && cursor.moveToFirst()) {
                            long contactId2 = cursor.getLong(0);
                            String lookupKey2 = cursor.getString(1);
                            lookupUri = ContactsContract.Contacts.getLookupUri(contactId2, lookupKey2);
                            break;
                        }
                        break;
                    case 2:
                        trigger = true;
                        createUri = Uri.fromParts("mailto", extras.getString(QuickContactBadge.EXTRA_URI_CONTENT), null);
                        if (cursor != null) {
                            long contactId3 = cursor.getLong(0);
                            String lookupKey3 = cursor.getString(1);
                            lookupUri = ContactsContract.Contacts.getLookupUri(contactId3, lookupKey3);
                            break;
                        }
                        break;
                    case 3:
                        trigger = true;
                        createUri = Uri.fromParts("tel", extras.getString(QuickContactBadge.EXTRA_URI_CONTENT), null);
                        if (cursor != null) {
                            long contactId22 = cursor.getLong(0);
                            String lookupKey22 = cursor.getString(1);
                            lookupUri = ContactsContract.Contacts.getLookupUri(contactId22, lookupKey22);
                            break;
                        }
                        break;
                }
                QuickContactBadge.this.mContactUri = lookupUri;
                QuickContactBadge.this.onContactUriChanged();
                if (trigger && lookupUri != null) {
                    ContactsContract.QuickContact.showQuickContact(QuickContactBadge.this.getContext(), QuickContactBadge.this, lookupUri, 3, QuickContactBadge.this.mExcludeMimes);
                } else if (createUri != null) {
                    Intent intent = new Intent("com.android.contacts.action.SHOW_OR_CREATE_CONTACT", createUri);
                    if (extras != null) {
                        extras.remove(QuickContactBadge.EXTRA_URI_CONTENT);
                        intent.putExtras(extras);
                    }
                    QuickContactBadge.this.getContext().startActivity(intent);
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }
}