package com.android.internal.telephony;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.telephony.Rlog;
import android.text.TextUtils;
import com.android.internal.telephony.IIccPhoneBook;
import com.android.internal.telephony.uicc.AdnRecord;
import gov.nist.core.Separators;
import java.util.List;

/* loaded from: IccProvider.class */
public class IccProvider extends ContentProvider {
    private static final String TAG = "IccProvider";
    private static final boolean DBG = false;
    private static final int ADN = 1;
    private static final int FDN = 2;
    private static final int SDN = 3;
    private static final String STR_TAG = "tag";
    private static final String STR_NUMBER = "number";
    private static final String STR_PIN2 = "pin2";
    private static final String STR_EMAILS = "emails";
    private static final String[] ADDRESS_BOOK_COLUMN_NAMES = {"name", "number", STR_EMAILS, "_id"};
    private static final UriMatcher URL_MATCHER = new UriMatcher(-1);

    static {
        URL_MATCHER.addURI("icc", "adn", 1);
        URL_MATCHER.addURI("icc", "fdn", 2);
        URL_MATCHER.addURI("icc", "sdn", 3);
    }

    @Override // android.content.ContentProvider
    public boolean onCreate() {
        return true;
    }

    @Override // android.content.ContentProvider
    public Cursor query(Uri url, String[] projection, String selection, String[] selectionArgs, String sort) {
        switch (URL_MATCHER.match(url)) {
            case 1:
                return loadFromEf(28474);
            case 2:
                return loadFromEf(28475);
            case 3:
                return loadFromEf(28489);
            default:
                throw new IllegalArgumentException("Unknown URL " + url);
        }
    }

    @Override // android.content.ContentProvider
    public String getType(Uri url) {
        switch (URL_MATCHER.match(url)) {
            case 1:
            case 2:
            case 3:
                return "vnd.android.cursor.dir/sim-contact";
            default:
                throw new IllegalArgumentException("Unknown URL " + url);
        }
    }

    @Override // android.content.ContentProvider
    public Uri insert(Uri url, ContentValues initialValues) {
        int efType;
        String pin2 = null;
        int match = URL_MATCHER.match(url);
        switch (match) {
            case 1:
                efType = 28474;
                break;
            case 2:
                efType = 28475;
                pin2 = initialValues.getAsString(STR_PIN2);
                break;
            default:
                throw new UnsupportedOperationException("Cannot insert into URL: " + url);
        }
        String tag = initialValues.getAsString("tag");
        String number = initialValues.getAsString("number");
        boolean success = addIccRecordToEf(efType, tag, number, null, pin2);
        if (!success) {
            return null;
        }
        StringBuilder buf = new StringBuilder("content://icc/");
        switch (match) {
            case 1:
                buf.append("adn/");
                break;
            case 2:
                buf.append("fdn/");
                break;
        }
        buf.append(0);
        Uri resultUri = Uri.parse(buf.toString());
        return resultUri;
    }

    private String normalizeValue(String inVal) {
        int len = inVal.length();
        String retVal = inVal;
        if (inVal.charAt(0) == '\'' && inVal.charAt(len - 1) == '\'') {
            retVal = inVal.substring(1, len - 1);
        }
        return retVal;
    }

    @Override // android.content.ContentProvider
    public int delete(Uri url, String where, String[] whereArgs) {
        int efType;
        int match = URL_MATCHER.match(url);
        switch (match) {
            case 1:
                efType = 28474;
                break;
            case 2:
                efType = 28475;
                break;
            default:
                throw new UnsupportedOperationException("Cannot insert into URL: " + url);
        }
        String tag = null;
        String number = null;
        String[] emails = null;
        String pin2 = null;
        String[] tokens = where.split("AND");
        int n = tokens.length;
        while (true) {
            n--;
            if (n >= 0) {
                String param = tokens[n];
                String[] pair = param.split(Separators.EQUALS, 2);
                String key = pair[0].trim();
                String val = pair[1].trim();
                if ("tag".equals(key)) {
                    tag = normalizeValue(val);
                } else if ("number".equals(key)) {
                    number = normalizeValue(val);
                } else if (STR_EMAILS.equals(key)) {
                    emails = null;
                } else if (STR_PIN2.equals(key)) {
                    pin2 = normalizeValue(val);
                }
            } else if (TextUtils.isEmpty(number)) {
                return 0;
            } else {
                if (efType == 28475 && TextUtils.isEmpty(pin2)) {
                    return 0;
                }
                boolean success = deleteIccRecordFromEf(efType, tag, number, emails, pin2);
                if (!success) {
                    return 0;
                }
                return 1;
            }
        }
    }

    @Override // android.content.ContentProvider
    public int update(Uri url, ContentValues values, String where, String[] whereArgs) {
        int efType;
        String pin2 = null;
        int match = URL_MATCHER.match(url);
        switch (match) {
            case 1:
                efType = 28474;
                break;
            case 2:
                efType = 28475;
                pin2 = values.getAsString(STR_PIN2);
                break;
            default:
                throw new UnsupportedOperationException("Cannot insert into URL: " + url);
        }
        String tag = values.getAsString("tag");
        String number = values.getAsString("number");
        String newTag = values.getAsString("newTag");
        String newNumber = values.getAsString("newNumber");
        boolean success = updateIccRecordInEf(efType, tag, number, newTag, newNumber, pin2);
        if (!success) {
            return 0;
        }
        return 1;
    }

    private MatrixCursor loadFromEf(int efType) {
        List<AdnRecord> adnRecords = null;
        try {
            IIccPhoneBook iccIpb = IIccPhoneBook.Stub.asInterface(ServiceManager.getService("simphonebook"));
            if (iccIpb != null) {
                adnRecords = iccIpb.getAdnRecordsInEf(efType);
            }
        } catch (RemoteException e) {
        } catch (SecurityException e2) {
        }
        if (adnRecords != null) {
            int N = adnRecords.size();
            MatrixCursor cursor = new MatrixCursor(ADDRESS_BOOK_COLUMN_NAMES, N);
            for (int i = 0; i < N; i++) {
                loadRecord(adnRecords.get(i), cursor, i);
            }
            return cursor;
        }
        Rlog.w(TAG, "Cannot load ADN records");
        return new MatrixCursor(ADDRESS_BOOK_COLUMN_NAMES);
    }

    private boolean addIccRecordToEf(int efType, String name, String number, String[] emails, String pin2) {
        boolean success = false;
        try {
            IIccPhoneBook iccIpb = IIccPhoneBook.Stub.asInterface(ServiceManager.getService("simphonebook"));
            if (iccIpb != null) {
                success = iccIpb.updateAdnRecordsInEfBySearch(efType, "", "", name, number, pin2);
            }
        } catch (RemoteException e) {
        } catch (SecurityException e2) {
        }
        return success;
    }

    private boolean updateIccRecordInEf(int efType, String oldName, String oldNumber, String newName, String newNumber, String pin2) {
        boolean success = false;
        try {
            IIccPhoneBook iccIpb = IIccPhoneBook.Stub.asInterface(ServiceManager.getService("simphonebook"));
            if (iccIpb != null) {
                success = iccIpb.updateAdnRecordsInEfBySearch(efType, oldName, oldNumber, newName, newNumber, pin2);
            }
        } catch (RemoteException e) {
        } catch (SecurityException e2) {
        }
        return success;
    }

    private boolean deleteIccRecordFromEf(int efType, String name, String number, String[] emails, String pin2) {
        boolean success = false;
        try {
            IIccPhoneBook iccIpb = IIccPhoneBook.Stub.asInterface(ServiceManager.getService("simphonebook"));
            if (iccIpb != null) {
                success = iccIpb.updateAdnRecordsInEfBySearch(efType, name, number, "", "", pin2);
            }
        } catch (RemoteException e) {
        } catch (SecurityException e2) {
        }
        return success;
    }

    private void loadRecord(AdnRecord record, MatrixCursor cursor, int id) {
        if (!record.isEmpty()) {
            Object[] contact = new Object[4];
            String alphaTag = record.getAlphaTag();
            String number = record.getNumber();
            contact[0] = alphaTag;
            contact[1] = number;
            String[] emails = record.getEmails();
            if (emails != null) {
                StringBuilder emailString = new StringBuilder();
                for (String email : emails) {
                    emailString.append(email);
                    emailString.append(Separators.COMMA);
                }
                contact[2] = emailString.toString();
            }
            contact[3] = Integer.valueOf(id);
            cursor.addRow(contact);
        }
    }

    private void log(String msg) {
        Rlog.d(TAG, "[IccProvider] " + msg);
    }
}