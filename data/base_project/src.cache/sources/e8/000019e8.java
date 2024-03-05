package com.android.internal.notification;

import android.app.Notification;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.text.SpannableString;
import android.util.Slog;
import gov.nist.core.Separators;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/* loaded from: DemoContactNotificationScorer.class */
public class DemoContactNotificationScorer implements NotificationScorer {
    private static final String TAG = "DemoContactNotificationScorer";
    private static final boolean DBG = false;
    protected static final boolean ENABLE_CONTACT_SCORER = true;
    private static final String SETTING_ENABLE_SCORER = "contact_scorer_enabled";
    protected boolean mEnabled;
    private static final int NOTIFICATION_PRIORITY_MULTIPLIER = 10;
    private Context mContext;
    private static final List<String> RELEVANT_KEYS_LIST = Arrays.asList("android.infoText", "android.text", "android.textLines", "android.subText", "android.title");
    private static final String[] PROJECTION = {"_id", "display_name"};
    private static final Uri CONTACTS_URI = ContactsContract.Contacts.CONTENT_URI;

    private static List<String> extractSpannedStrings(CharSequence charSequence) {
        if (charSequence == null) {
            return Collections.emptyList();
        }
        if (charSequence instanceof SpannableString) {
            SpannableString spannableString = (SpannableString) charSequence;
            Object[] ssArr = spannableString.getSpans(0, spannableString.length(), Object.class);
            ArrayList<String> sss = new ArrayList<>();
            for (Object spanObj : ssArr) {
                try {
                    sss.add(spannableString.subSequence(spannableString.getSpanStart(spanObj), spannableString.getSpanEnd(spanObj)).toString());
                } catch (StringIndexOutOfBoundsException e) {
                    Slog.e(TAG, "Bad indices when extracting spanned subsequence", e);
                }
            }
            return sss;
        }
        return Arrays.asList(charSequence.toString());
    }

    private static String getQuestionMarksInParens(int n) {
        StringBuilder sb = new StringBuilder(Separators.LPAREN);
        for (int i = 0; i < n; i++) {
            if (sb.length() > 1) {
                sb.append(',');
            }
            sb.append('?');
        }
        sb.append(Separators.RPAREN);
        return sb.toString();
    }

    private boolean hasStarredContact(Bundle extras) {
        if (extras == null) {
            return false;
        }
        ArrayList<String> qStrings = new ArrayList<>();
        for (String rk : RELEVANT_KEYS_LIST) {
            if (extras.get(rk) != null) {
                if (extras.get(rk) instanceof CharSequence) {
                    qStrings.addAll(extractSpannedStrings((CharSequence) extras.get(rk)));
                } else if (extras.get(rk) instanceof CharSequence[]) {
                    CharSequence[] arr$ = (CharSequence[]) extras.get(rk);
                    for (CharSequence line : arr$) {
                        qStrings.addAll(extractSpannedStrings(line));
                    }
                } else {
                    Slog.w(TAG, "Strange, the extra " + rk + " is of unexpected type.");
                }
            }
        }
        if (qStrings.isEmpty()) {
            return false;
        }
        String[] qStringsArr = (String[]) qStrings.toArray(new String[qStrings.size()]);
        String selection = "display_name IN " + getQuestionMarksInParens(qStringsArr.length) + " AND starred ='1'";
        Cursor c = null;
        try {
            c = this.mContext.getContentResolver().query(CONTACTS_URI, PROJECTION, selection, qStringsArr, null);
            if (c != null) {
                boolean z = c.getCount() > 0;
                if (c != null) {
                    c.close();
                }
                return z;
            } else if (c != null) {
                c.close();
                return false;
            } else {
                return false;
            }
        } catch (Throwable t) {
            try {
                Slog.w(TAG, "Problem getting content resolver or performing contacts query.", t);
                if (c != null) {
                    c.close();
                    return false;
                }
                return false;
            } catch (Throwable th) {
                if (c != null) {
                    c.close();
                }
                throw th;
            }
        }
    }

    private static final int clamp(int x, int low, int high) {
        return x < low ? low : x > high ? high : x;
    }

    private static int priorityBumpMap(int incomingScore) {
        int theScore = clamp(incomingScore, (-2) * 10, 2 * 10);
        if (theScore != incomingScore) {
            return incomingScore;
        }
        return theScore <= (-10) ? (int) (theScore + (1.5d * (theScore + (2 * 10)))) : (int) (theScore + (0.5d * ((2 * 10) - theScore)));
    }

    @Override // com.android.internal.notification.NotificationScorer
    public void initialize(Context context) {
        this.mContext = context;
        this.mEnabled = 1 == Settings.Global.getInt(this.mContext.getContentResolver(), SETTING_ENABLE_SCORER, 0);
    }

    @Override // com.android.internal.notification.NotificationScorer
    public int getScore(Notification notification, int score) {
        if (notification == null || !this.mEnabled) {
            return score;
        }
        boolean hasStarredPriority = hasStarredContact(notification.extras);
        if (hasStarredPriority) {
            score = priorityBumpMap(score);
        }
        return score;
    }
}