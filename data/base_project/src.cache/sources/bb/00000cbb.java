package android.provider;

import android.accounts.Account;
import android.app.Activity;
import android.app.backup.FullBackup;
import android.content.ContentProviderClient;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.CursorEntityIterator;
import android.content.Entity;
import android.content.EntityIterator;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Rect;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.os.RemoteException;
import android.provider.Contacts;
import android.provider.SyncStateContract;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/* loaded from: ContactsContract.class */
public final class ContactsContract {
    public static final String AUTHORITY = "com.android.contacts";
    public static final Uri AUTHORITY_URI = Uri.parse("content://com.android.contacts");
    public static final String CALLER_IS_SYNCADAPTER = "caller_is_syncadapter";
    public static final String DIRECTORY_PARAM_KEY = "directory";
    public static final String LIMIT_PARAM_KEY = "limit";
    public static final String PRIMARY_ACCOUNT_NAME = "name_for_primary_account";
    public static final String PRIMARY_ACCOUNT_TYPE = "type_for_primary_account";
    public static final String STREQUENT_PHONE_ONLY = "strequent_phone_only";
    public static final String DEFERRED_SNIPPETING = "deferred_snippeting";
    public static final String DEFERRED_SNIPPETING_QUERY = "deferred_snippeting_query";
    public static final String REMOVE_DUPLICATE_ENTRIES = "remove_duplicate_entries";

    /* loaded from: ContactsContract$Authorization.class */
    public static final class Authorization {
        public static final String AUTHORIZATION_METHOD = "authorize";
        public static final String KEY_URI_TO_AUTHORIZE = "uri_to_authorize";
        public static final String KEY_AUTHORIZED_URI = "authorized_uri";
    }

    /* loaded from: ContactsContract$BaseSyncColumns.class */
    protected interface BaseSyncColumns {
        public static final String SYNC1 = "sync1";
        public static final String SYNC2 = "sync2";
        public static final String SYNC3 = "sync3";
        public static final String SYNC4 = "sync4";
    }

    /* loaded from: ContactsContract$ContactCounts.class */
    public static final class ContactCounts {
        public static final String ADDRESS_BOOK_INDEX_EXTRAS = "address_book_index_extras";
        public static final String EXTRA_ADDRESS_BOOK_INDEX_TITLES = "address_book_index_titles";
        public static final String EXTRA_ADDRESS_BOOK_INDEX_COUNTS = "address_book_index_counts";
    }

    /* loaded from: ContactsContract$ContactNameColumns.class */
    protected interface ContactNameColumns {
        public static final String DISPLAY_NAME_SOURCE = "display_name_source";
        public static final String DISPLAY_NAME_PRIMARY = "display_name";
        public static final String DISPLAY_NAME_ALTERNATIVE = "display_name_alt";
        public static final String PHONETIC_NAME_STYLE = "phonetic_name_style";
        public static final String PHONETIC_NAME = "phonetic_name";
        public static final String SORT_KEY_PRIMARY = "sort_key";
        public static final String SORT_KEY_ALTERNATIVE = "sort_key_alt";
    }

    /* loaded from: ContactsContract$ContactOptionsColumns.class */
    protected interface ContactOptionsColumns {
        public static final String TIMES_CONTACTED = "times_contacted";
        public static final String LAST_TIME_CONTACTED = "last_time_contacted";
        public static final String STARRED = "starred";
        public static final String PINNED = "pinned";
        public static final String CUSTOM_RINGTONE = "custom_ringtone";
        public static final String SEND_TO_VOICEMAIL = "send_to_voicemail";
    }

    /* loaded from: ContactsContract$ContactStatusColumns.class */
    protected interface ContactStatusColumns {
        public static final String CONTACT_PRESENCE = "contact_presence";
        public static final String CONTACT_CHAT_CAPABILITY = "contact_chat_capability";
        public static final String CONTACT_STATUS = "contact_status";
        public static final String CONTACT_STATUS_TIMESTAMP = "contact_status_ts";
        public static final String CONTACT_STATUS_RES_PACKAGE = "contact_status_res_package";
        public static final String CONTACT_STATUS_LABEL = "contact_status_label";
        public static final String CONTACT_STATUS_ICON = "contact_status_icon";
    }

    /* loaded from: ContactsContract$ContactsColumns.class */
    protected interface ContactsColumns {
        public static final String DISPLAY_NAME = "display_name";
        public static final String NAME_RAW_CONTACT_ID = "name_raw_contact_id";
        public static final String PHOTO_ID = "photo_id";
        public static final String PHOTO_FILE_ID = "photo_file_id";
        public static final String PHOTO_URI = "photo_uri";
        public static final String PHOTO_THUMBNAIL_URI = "photo_thumb_uri";
        public static final String IN_VISIBLE_GROUP = "in_visible_group";
        public static final String IS_USER_PROFILE = "is_user_profile";
        public static final String HAS_PHONE_NUMBER = "has_phone_number";
        public static final String LOOKUP_KEY = "lookup";
        public static final String CONTACT_LAST_UPDATED_TIMESTAMP = "contact_last_updated_timestamp";
    }

    /* loaded from: ContactsContract$DataColumns.class */
    protected interface DataColumns {
        public static final String RES_PACKAGE = "res_package";
        public static final String MIMETYPE = "mimetype";
        public static final String RAW_CONTACT_ID = "raw_contact_id";
        public static final String IS_PRIMARY = "is_primary";
        public static final String IS_SUPER_PRIMARY = "is_super_primary";
        public static final String IS_READ_ONLY = "is_read_only";
        public static final String DATA_VERSION = "data_version";
        public static final String DATA1 = "data1";
        public static final String DATA2 = "data2";
        public static final String DATA3 = "data3";
        public static final String DATA4 = "data4";
        public static final String DATA5 = "data5";
        public static final String DATA6 = "data6";
        public static final String DATA7 = "data7";
        public static final String DATA8 = "data8";
        public static final String DATA9 = "data9";
        public static final String DATA10 = "data10";
        public static final String DATA11 = "data11";
        public static final String DATA12 = "data12";
        public static final String DATA13 = "data13";
        public static final String DATA14 = "data14";
        public static final String DATA15 = "data15";
        public static final String SYNC1 = "data_sync1";
        public static final String SYNC2 = "data_sync2";
        public static final String SYNC3 = "data_sync3";
        public static final String SYNC4 = "data_sync4";
    }

    /* loaded from: ContactsContract$DataColumnsWithJoins.class */
    protected interface DataColumnsWithJoins extends BaseColumns, DataColumns, StatusColumns, RawContactsColumns, ContactsColumns, ContactNameColumns, ContactOptionsColumns, ContactStatusColumns, DataUsageStatColumns {
    }

    /* loaded from: ContactsContract$DataUsageFeedback.class */
    public static final class DataUsageFeedback {
        public static final Uri FEEDBACK_URI = Uri.withAppendedPath(Data.CONTENT_URI, "usagefeedback");
        public static final Uri DELETE_USAGE_URI = Uri.withAppendedPath(Contacts.CONTENT_URI, "delete_usage");
        public static final String USAGE_TYPE = "type";
        public static final String USAGE_TYPE_CALL = "call";
        public static final String USAGE_TYPE_LONG_TEXT = "long_text";
        public static final String USAGE_TYPE_SHORT_TEXT = "short_text";
    }

    /* loaded from: ContactsContract$DataUsageStatColumns.class */
    protected interface DataUsageStatColumns {
        public static final String LAST_TIME_USED = "last_time_used";
        public static final String TIMES_USED = "times_used";
    }

    /* loaded from: ContactsContract$DeletedContactsColumns.class */
    protected interface DeletedContactsColumns {
        public static final String CONTACT_ID = "contact_id";
        public static final String CONTACT_DELETED_TIMESTAMP = "contact_deleted_timestamp";
    }

    /* loaded from: ContactsContract$DisplayNameSources.class */
    public interface DisplayNameSources {
        public static final int UNDEFINED = 0;
        public static final int EMAIL = 10;
        public static final int PHONE = 20;
        public static final int ORGANIZATION = 30;
        public static final int NICKNAME = 35;
        public static final int STRUCTURED_NAME = 40;
    }

    /* loaded from: ContactsContract$FullNameStyle.class */
    public interface FullNameStyle {
        public static final int UNDEFINED = 0;
        public static final int WESTERN = 1;
        public static final int CJK = 2;
        public static final int CHINESE = 3;
        public static final int JAPANESE = 4;
        public static final int KOREAN = 5;
    }

    /* loaded from: ContactsContract$GroupsColumns.class */
    protected interface GroupsColumns {
        public static final String DATA_SET = "data_set";
        public static final String ACCOUNT_TYPE_AND_DATA_SET = "account_type_and_data_set";
        public static final String TITLE = "title";
        public static final String RES_PACKAGE = "res_package";
        public static final String TITLE_RES = "title_res";
        public static final String NOTES = "notes";
        public static final String SYSTEM_ID = "system_id";
        public static final String SUMMARY_COUNT = "summ_count";
        public static final String PARAM_RETURN_GROUP_COUNT_PER_ACCOUNT = "return_group_count_per_account";
        public static final String SUMMARY_GROUP_COUNT_PER_ACCOUNT = "group_count_per_account";
        public static final String SUMMARY_WITH_PHONES = "summ_phones";
        public static final String GROUP_VISIBLE = "group_visible";
        public static final String DELETED = "deleted";
        public static final String SHOULD_SYNC = "should_sync";
        public static final String AUTO_ADD = "auto_add";
        public static final String FAVORITES = "favorites";
        public static final String GROUP_IS_READ_ONLY = "group_is_read_only";
    }

    /* loaded from: ContactsContract$Intents.class */
    public static final class Intents {
        public static final String SEARCH_SUGGESTION_CLICKED = "android.provider.Contacts.SEARCH_SUGGESTION_CLICKED";
        public static final String SEARCH_SUGGESTION_DIAL_NUMBER_CLICKED = "android.provider.Contacts.SEARCH_SUGGESTION_DIAL_NUMBER_CLICKED";
        public static final String SEARCH_SUGGESTION_CREATE_CONTACT_CLICKED = "android.provider.Contacts.SEARCH_SUGGESTION_CREATE_CONTACT_CLICKED";
        public static final String CONTACTS_DATABASE_CREATED = "android.provider.Contacts.DATABASE_CREATED";
        public static final String ATTACH_IMAGE = "com.android.contacts.action.ATTACH_IMAGE";
        public static final String INVITE_CONTACT = "com.android.contacts.action.INVITE_CONTACT";
        public static final String SHOW_OR_CREATE_CONTACT = "com.android.contacts.action.SHOW_OR_CREATE_CONTACT";
        public static final String ACTION_GET_MULTIPLE_PHONES = "com.android.contacts.action.GET_MULTIPLE_PHONES";
        public static final String ACTION_PROFILE_CHANGED = "android.provider.Contacts.PROFILE_CHANGED";
        public static final String EXTRA_FORCE_CREATE = "com.android.contacts.action.FORCE_CREATE";
        public static final String EXTRA_CREATE_DESCRIPTION = "com.android.contacts.action.CREATE_DESCRIPTION";
        public static final String EXTRA_PHONE_URIS = "com.android.contacts.extra.PHONE_URIS";
        @Deprecated
        public static final String EXTRA_TARGET_RECT = "target_rect";
        @Deprecated
        public static final String EXTRA_MODE = "mode";
        @Deprecated
        public static final int MODE_SMALL = 1;
        @Deprecated
        public static final int MODE_MEDIUM = 2;
        @Deprecated
        public static final int MODE_LARGE = 3;
        @Deprecated
        public static final String EXTRA_EXCLUDE_MIMES = "exclude_mimes";

        /* loaded from: ContactsContract$Intents$Insert.class */
        public static final class Insert {
            public static final String ACTION = "android.intent.action.INSERT";
            public static final String FULL_MODE = "full_mode";
            public static final String NAME = "name";
            public static final String PHONETIC_NAME = "phonetic_name";
            public static final String COMPANY = "company";
            public static final String JOB_TITLE = "job_title";
            public static final String NOTES = "notes";
            public static final String PHONE = "phone";
            public static final String PHONE_TYPE = "phone_type";
            public static final String PHONE_ISPRIMARY = "phone_isprimary";
            public static final String SECONDARY_PHONE = "secondary_phone";
            public static final String SECONDARY_PHONE_TYPE = "secondary_phone_type";
            public static final String TERTIARY_PHONE = "tertiary_phone";
            public static final String TERTIARY_PHONE_TYPE = "tertiary_phone_type";
            public static final String EMAIL = "email";
            public static final String EMAIL_TYPE = "email_type";
            public static final String EMAIL_ISPRIMARY = "email_isprimary";
            public static final String SECONDARY_EMAIL = "secondary_email";
            public static final String SECONDARY_EMAIL_TYPE = "secondary_email_type";
            public static final String TERTIARY_EMAIL = "tertiary_email";
            public static final String TERTIARY_EMAIL_TYPE = "tertiary_email_type";
            public static final String POSTAL = "postal";
            public static final String POSTAL_TYPE = "postal_type";
            public static final String POSTAL_ISPRIMARY = "postal_isprimary";
            public static final String IM_HANDLE = "im_handle";
            public static final String IM_PROTOCOL = "im_protocol";
            public static final String IM_ISPRIMARY = "im_isprimary";
            public static final String DATA = "data";
            public static final String ACCOUNT = "com.android.contacts.extra.ACCOUNT";
            public static final String DATA_SET = "com.android.contacts.extra.DATA_SET";
        }

        /* loaded from: ContactsContract$Intents$UI.class */
        public static final class UI {
            public static final String LIST_DEFAULT = "com.android.contacts.action.LIST_DEFAULT";
            public static final String LIST_GROUP_ACTION = "com.android.contacts.action.LIST_GROUP";
            public static final String GROUP_NAME_EXTRA_KEY = "com.android.contacts.extra.GROUP";
            public static final String LIST_ALL_CONTACTS_ACTION = "com.android.contacts.action.LIST_ALL_CONTACTS";
            public static final String LIST_CONTACTS_WITH_PHONES_ACTION = "com.android.contacts.action.LIST_CONTACTS_WITH_PHONES";
            public static final String LIST_STARRED_ACTION = "com.android.contacts.action.LIST_STARRED";
            public static final String LIST_FREQUENT_ACTION = "com.android.contacts.action.LIST_FREQUENT";
            public static final String LIST_STREQUENT_ACTION = "com.android.contacts.action.LIST_STREQUENT";
            public static final String TITLE_EXTRA_KEY = "com.android.contacts.extra.TITLE_EXTRA";
            public static final String FILTER_CONTACTS_ACTION = "com.android.contacts.action.FILTER_CONTACTS";
            public static final String FILTER_TEXT_EXTRA_KEY = "com.android.contacts.extra.FILTER_TEXT";
        }
    }

    /* loaded from: ContactsContract$PhoneLookupColumns.class */
    protected interface PhoneLookupColumns {
        public static final String NUMBER = "number";
        public static final String TYPE = "type";
        public static final String LABEL = "label";
        public static final String NORMALIZED_NUMBER = "normalized_number";
    }

    /* loaded from: ContactsContract$PhoneticNameStyle.class */
    public interface PhoneticNameStyle {
        public static final int UNDEFINED = 0;
        public static final int PINYIN = 3;
        public static final int JAPANESE = 4;
        public static final int KOREAN = 5;
    }

    /* loaded from: ContactsContract$PhotoFilesColumns.class */
    protected interface PhotoFilesColumns {
        public static final String HEIGHT = "height";
        public static final String WIDTH = "width";
        public static final String FILESIZE = "filesize";
    }

    /* loaded from: ContactsContract$PinnedPositions.class */
    public static final class PinnedPositions {
        public static final Uri UPDATE_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "pinned_position_update");
        public static final int UNPINNED = Integer.MAX_VALUE;
        public static final int DEMOTED = -1;
        public static final String UNDEMOTE = "undemote";
        public static final String STAR_WHEN_PINNING = "star_when_pinning";
    }

    /* loaded from: ContactsContract$Preferences.class */
    public static final class Preferences {
        public static final String SORT_ORDER = "android.contacts.SORT_ORDER";
        public static final int SORT_ORDER_PRIMARY = 1;
        public static final int SORT_ORDER_ALTERNATIVE = 2;
        public static final String DISPLAY_ORDER = "android.contacts.DISPLAY_ORDER";
        public static final int DISPLAY_ORDER_PRIMARY = 1;
        public static final int DISPLAY_ORDER_ALTERNATIVE = 2;
    }

    /* loaded from: ContactsContract$PresenceColumns.class */
    protected interface PresenceColumns {
        public static final String DATA_ID = "presence_data_id";
        public static final String PROTOCOL = "protocol";
        public static final String CUSTOM_PROTOCOL = "custom_protocol";
        public static final String IM_HANDLE = "im_handle";
        public static final String IM_ACCOUNT = "im_account";
    }

    /* loaded from: ContactsContract$RawContactsColumns.class */
    protected interface RawContactsColumns {
        public static final String CONTACT_ID = "contact_id";
        public static final String DATA_SET = "data_set";
        public static final String ACCOUNT_TYPE_AND_DATA_SET = "account_type_and_data_set";
        public static final String AGGREGATION_MODE = "aggregation_mode";
        public static final String DELETED = "deleted";
        public static final String NAME_VERIFIED = "name_verified";
        public static final String RAW_CONTACT_IS_READ_ONLY = "raw_contact_is_read_only";
        public static final String RAW_CONTACT_IS_USER_PROFILE = "raw_contact_is_user_profile";
    }

    /* loaded from: ContactsContract$SearchSnippetColumns.class */
    public static class SearchSnippetColumns {
        public static final String SNIPPET = "snippet";
        public static final String SNIPPET_ARGS_PARAM_KEY = "snippet_args";
        public static final String DEFERRED_SNIPPETING_KEY = "deferred_snippeting";
    }

    /* loaded from: ContactsContract$SettingsColumns.class */
    protected interface SettingsColumns {
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String DATA_SET = "data_set";
        public static final String SHOULD_SYNC = "should_sync";
        public static final String UNGROUPED_VISIBLE = "ungrouped_visible";
        public static final String ANY_UNSYNCED = "any_unsynced";
        public static final String UNGROUPED_COUNT = "summ_count";
        public static final String UNGROUPED_WITH_PHONES = "summ_phones";
    }

    /* loaded from: ContactsContract$StatusColumns.class */
    protected interface StatusColumns {
        public static final String PRESENCE = "mode";
        @Deprecated
        public static final String PRESENCE_STATUS = "mode";
        public static final int OFFLINE = 0;
        public static final int INVISIBLE = 1;
        public static final int AWAY = 2;
        public static final int IDLE = 3;
        public static final int DO_NOT_DISTURB = 4;
        public static final int AVAILABLE = 5;
        public static final String STATUS = "status";
        @Deprecated
        public static final String PRESENCE_CUSTOM_STATUS = "status";
        public static final String STATUS_TIMESTAMP = "status_ts";
        public static final String STATUS_RES_PACKAGE = "status_res_package";
        public static final String STATUS_LABEL = "status_label";
        public static final String STATUS_ICON = "status_icon";
        public static final String CHAT_CAPABILITY = "chat_capability";
        public static final int CAPABILITY_HAS_VOICE = 1;
        public static final int CAPABILITY_HAS_VIDEO = 2;
        public static final int CAPABILITY_HAS_CAMERA = 4;
    }

    /* loaded from: ContactsContract$StreamItemPhotosColumns.class */
    protected interface StreamItemPhotosColumns {
        public static final String STREAM_ITEM_ID = "stream_item_id";
        public static final String SORT_INDEX = "sort_index";
        public static final String PHOTO_FILE_ID = "photo_file_id";
        public static final String PHOTO_URI = "photo_uri";
        public static final String SYNC1 = "stream_item_photo_sync1";
        public static final String SYNC2 = "stream_item_photo_sync2";
        public static final String SYNC3 = "stream_item_photo_sync3";
        public static final String SYNC4 = "stream_item_photo_sync4";
    }

    /* loaded from: ContactsContract$StreamItemsColumns.class */
    protected interface StreamItemsColumns {
        public static final String CONTACT_ID = "contact_id";
        public static final String CONTACT_LOOKUP_KEY = "contact_lookup";
        public static final String RAW_CONTACT_ID = "raw_contact_id";
        public static final String RES_PACKAGE = "res_package";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String ACCOUNT_NAME = "account_name";
        public static final String DATA_SET = "data_set";
        public static final String RAW_CONTACT_SOURCE_ID = "raw_contact_source_id";
        public static final String RES_ICON = "icon";
        public static final String RES_LABEL = "label";
        public static final String TEXT = "text";
        public static final String TIMESTAMP = "timestamp";
        public static final String COMMENTS = "comments";
        public static final String SYNC1 = "stream_item_sync1";
        public static final String SYNC2 = "stream_item_sync2";
        public static final String SYNC3 = "stream_item_sync3";
        public static final String SYNC4 = "stream_item_sync4";
    }

    /* loaded from: ContactsContract$SyncColumns.class */
    protected interface SyncColumns extends BaseSyncColumns {
        public static final String ACCOUNT_NAME = "account_name";
        public static final String ACCOUNT_TYPE = "account_type";
        public static final String SOURCE_ID = "sourceid";
        public static final String VERSION = "version";
        public static final String DIRTY = "dirty";
    }

    @Deprecated
    /* loaded from: ContactsContract$SyncStateColumns.class */
    public interface SyncStateColumns extends SyncStateContract.Columns {
    }

    /* loaded from: ContactsContract$Directory.class */
    public static final class Directory implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "directories");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/contact_directories";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact_directory";
        public static final long DEFAULT = 0;
        public static final long LOCAL_INVISIBLE = 1;
        public static final String PACKAGE_NAME = "packageName";
        public static final String TYPE_RESOURCE_ID = "typeResourceId";
        public static final String DISPLAY_NAME = "displayName";
        public static final String DIRECTORY_AUTHORITY = "authority";
        public static final String ACCOUNT_TYPE = "accountType";
        public static final String ACCOUNT_NAME = "accountName";
        public static final String EXPORT_SUPPORT = "exportSupport";
        public static final int EXPORT_SUPPORT_NONE = 0;
        public static final int EXPORT_SUPPORT_SAME_ACCOUNT_ONLY = 1;
        public static final int EXPORT_SUPPORT_ANY_ACCOUNT = 2;
        public static final String SHORTCUT_SUPPORT = "shortcutSupport";
        public static final int SHORTCUT_SUPPORT_NONE = 0;
        public static final int SHORTCUT_SUPPORT_DATA_ITEMS_ONLY = 1;
        public static final int SHORTCUT_SUPPORT_FULL = 2;
        public static final String PHOTO_SUPPORT = "photoSupport";
        public static final int PHOTO_SUPPORT_NONE = 0;
        public static final int PHOTO_SUPPORT_THUMBNAIL_ONLY = 1;
        public static final int PHOTO_SUPPORT_FULL_SIZE_ONLY = 2;
        public static final int PHOTO_SUPPORT_FULL = 3;

        private Directory() {
        }

        public static void notifyDirectoryChange(ContentResolver resolver) {
            ContentValues contentValues = new ContentValues();
            resolver.update(CONTENT_URI, contentValues, null, null);
        }
    }

    /* loaded from: ContactsContract$SyncState.class */
    public static final class SyncState implements SyncStateContract.Columns {
        public static final String CONTENT_DIRECTORY = "syncstate";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "syncstate");

        private SyncState() {
        }

        public static byte[] get(ContentProviderClient provider, Account account) throws RemoteException {
            return SyncStateContract.Helpers.get(provider, CONTENT_URI, account);
        }

        public static Pair<Uri, byte[]> getWithUri(ContentProviderClient provider, Account account) throws RemoteException {
            return SyncStateContract.Helpers.getWithUri(provider, CONTENT_URI, account);
        }

        public static void set(ContentProviderClient provider, Account account, byte[] data) throws RemoteException {
            SyncStateContract.Helpers.set(provider, CONTENT_URI, account, data);
        }

        public static ContentProviderOperation newSetOperation(Account account, byte[] data) {
            return SyncStateContract.Helpers.newSetOperation(CONTENT_URI, account, data);
        }
    }

    /* loaded from: ContactsContract$ProfileSyncState.class */
    public static final class ProfileSyncState implements SyncStateContract.Columns {
        public static final String CONTENT_DIRECTORY = "syncstate";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(Profile.CONTENT_URI, "syncstate");

        private ProfileSyncState() {
        }

        public static byte[] get(ContentProviderClient provider, Account account) throws RemoteException {
            return SyncStateContract.Helpers.get(provider, CONTENT_URI, account);
        }

        public static Pair<Uri, byte[]> getWithUri(ContentProviderClient provider, Account account) throws RemoteException {
            return SyncStateContract.Helpers.getWithUri(provider, CONTENT_URI, account);
        }

        public static void set(ContentProviderClient provider, Account account, byte[] data) throws RemoteException {
            SyncStateContract.Helpers.set(provider, CONTENT_URI, account, data);
        }

        public static ContentProviderOperation newSetOperation(Account account, byte[] data) {
            return SyncStateContract.Helpers.newSetOperation(CONTENT_URI, account, data);
        }
    }

    /* loaded from: ContactsContract$Contacts.class */
    public static class Contacts implements BaseColumns, ContactsColumns, ContactOptionsColumns, ContactNameColumns, ContactStatusColumns {
        public static final String QUERY_PARAMETER_VCARD_NO_PHOTO = "nophoto";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/contact";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact";
        public static final String CONTENT_VCARD_TYPE = "text/x-vcard";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, android.provider.Contacts.AUTHORITY);
        public static final Uri CONTENT_LOOKUP_URI = Uri.withAppendedPath(CONTENT_URI, ContactsColumns.LOOKUP_KEY);
        public static final Uri CONTENT_VCARD_URI = Uri.withAppendedPath(CONTENT_URI, "as_vcard");
        public static final Uri CONTENT_MULTI_VCARD_URI = Uri.withAppendedPath(CONTENT_URI, "as_multi_vcard");
        public static final Uri CONTENT_FILTER_URI = Uri.withAppendedPath(CONTENT_URI, "filter");
        public static final Uri CONTENT_STREQUENT_URI = Uri.withAppendedPath(CONTENT_URI, "strequent");
        public static final Uri CONTENT_FREQUENT_URI = Uri.withAppendedPath(CONTENT_URI, "frequent");
        public static final Uri CONTENT_STREQUENT_FILTER_URI = Uri.withAppendedPath(CONTENT_STREQUENT_URI, "filter");
        public static final Uri CONTENT_GROUP_URI = Uri.withAppendedPath(CONTENT_URI, WifiConfiguration.GroupCipher.varName);

        private Contacts() {
        }

        public static Uri getLookupUri(ContentResolver resolver, Uri contactUri) {
            Cursor c = resolver.query(contactUri, new String[]{ContactsColumns.LOOKUP_KEY, "_id"}, null, null, null);
            if (c == null) {
                return null;
            }
            try {
                if (c.moveToFirst()) {
                    String lookupKey = c.getString(0);
                    long contactId = c.getLong(1);
                    Uri lookupUri = getLookupUri(contactId, lookupKey);
                    c.close();
                    return lookupUri;
                }
                c.close();
                return null;
            } catch (Throwable th) {
                c.close();
                throw th;
            }
        }

        public static Uri getLookupUri(long contactId, String lookupKey) {
            return ContentUris.withAppendedId(Uri.withAppendedPath(CONTENT_LOOKUP_URI, lookupKey), contactId);
        }

        public static Uri lookupContact(ContentResolver resolver, Uri lookupUri) {
            Cursor c;
            if (lookupUri == null || (c = resolver.query(lookupUri, new String[]{"_id"}, null, null, null)) == null) {
                return null;
            }
            try {
                if (c.moveToFirst()) {
                    long contactId = c.getLong(0);
                    Uri withAppendedId = ContentUris.withAppendedId(CONTENT_URI, contactId);
                    c.close();
                    return withAppendedId;
                }
                c.close();
                return null;
            } catch (Throwable th) {
                c.close();
                throw th;
            }
        }

        @Deprecated
        public static void markAsContacted(ContentResolver resolver, long contactId) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, contactId);
            ContentValues values = new ContentValues();
            values.put("last_time_contacted", Long.valueOf(System.currentTimeMillis()));
            resolver.update(uri, values, null, null);
        }

        /* loaded from: ContactsContract$Contacts$Data.class */
        public static final class Data implements BaseColumns, DataColumns {
            public static final String CONTENT_DIRECTORY = "data";

            private Data() {
            }
        }

        /* loaded from: ContactsContract$Contacts$Entity.class */
        public static final class Entity implements BaseColumns, ContactsColumns, ContactNameColumns, RawContactsColumns, BaseSyncColumns, SyncColumns, DataColumns, StatusColumns, ContactOptionsColumns, ContactStatusColumns {
            public static final String CONTENT_DIRECTORY = "entities";
            public static final String RAW_CONTACT_ID = "raw_contact_id";
            public static final String DATA_ID = "data_id";

            private Entity() {
            }
        }

        /* loaded from: ContactsContract$Contacts$StreamItems.class */
        public static final class StreamItems implements StreamItemsColumns {
            public static final String CONTENT_DIRECTORY = "stream_items";

            private StreamItems() {
            }
        }

        /* loaded from: ContactsContract$Contacts$AggregationSuggestions.class */
        public static final class AggregationSuggestions implements BaseColumns, ContactsColumns, ContactOptionsColumns, ContactStatusColumns {
            public static final String CONTENT_DIRECTORY = "suggestions";
            public static final String PARAMETER_MATCH_NAME = "name";
            public static final String PARAMETER_MATCH_EMAIL = "email";
            public static final String PARAMETER_MATCH_PHONE = "phone";
            public static final String PARAMETER_MATCH_NICKNAME = "nickname";

            private AggregationSuggestions() {
            }

            /* loaded from: ContactsContract$Contacts$AggregationSuggestions$Builder.class */
            public static final class Builder {
                private long mContactId;
                private ArrayList<String> mKinds = new ArrayList<>();
                private ArrayList<String> mValues = new ArrayList<>();
                private int mLimit;

                public Builder setContactId(long contactId) {
                    this.mContactId = contactId;
                    return this;
                }

                public Builder addParameter(String kind, String value) {
                    if (!TextUtils.isEmpty(value)) {
                        this.mKinds.add(kind);
                        this.mValues.add(value);
                    }
                    return this;
                }

                public Builder setLimit(int limit) {
                    this.mLimit = limit;
                    return this;
                }

                public Uri build() {
                    Uri.Builder builder = Contacts.CONTENT_URI.buildUpon();
                    builder.appendEncodedPath(String.valueOf(this.mContactId));
                    builder.appendPath(AggregationSuggestions.CONTENT_DIRECTORY);
                    if (this.mLimit != 0) {
                        builder.appendQueryParameter("limit", String.valueOf(this.mLimit));
                    }
                    int count = this.mKinds.size();
                    for (int i = 0; i < count; i++) {
                        builder.appendQueryParameter("query", this.mKinds.get(i) + Separators.COLON + this.mValues.get(i));
                    }
                    return builder.build();
                }
            }

            public static final Builder builder() {
                return new Builder();
            }
        }

        /* loaded from: ContactsContract$Contacts$Photo.class */
        public static final class Photo implements BaseColumns, DataColumnsWithJoins {
            public static final String CONTENT_DIRECTORY = "photo";
            public static final String DISPLAY_PHOTO = "display_photo";
            public static final String PHOTO_FILE_ID = "data14";
            public static final String PHOTO = "data15";

            private Photo() {
            }
        }

        public static InputStream openContactPhotoInputStream(ContentResolver cr, Uri contactUri, boolean preferHighres) {
            if (preferHighres) {
                Uri displayPhotoUri = Uri.withAppendedPath(contactUri, "display_photo");
                try {
                    AssetFileDescriptor fd = cr.openAssetFileDescriptor(displayPhotoUri, FullBackup.ROOT_TREE_TOKEN);
                    return fd.createInputStream();
                } catch (IOException e) {
                }
            }
            Uri photoUri = Uri.withAppendedPath(contactUri, "photo");
            if (photoUri == null) {
                return null;
            }
            Cursor cursor = cr.query(photoUri, new String[]{"data15"}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToNext()) {
                        byte[] data = cursor.getBlob(0);
                        if (data == null) {
                            if (cursor != null) {
                                cursor.close();
                            }
                            return null;
                        }
                        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
                        if (cursor != null) {
                            cursor.close();
                        }
                        return byteArrayInputStream;
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            return null;
        }

        public static InputStream openContactPhotoInputStream(ContentResolver cr, Uri contactUri) {
            return openContactPhotoInputStream(cr, contactUri, false);
        }
    }

    /* loaded from: ContactsContract$Profile.class */
    public static final class Profile implements BaseColumns, ContactsColumns, ContactOptionsColumns, ContactNameColumns, ContactStatusColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "profile");
        public static final Uri CONTENT_VCARD_URI = Uri.withAppendedPath(CONTENT_URI, "as_vcard");
        public static final Uri CONTENT_RAW_CONTACTS_URI = Uri.withAppendedPath(CONTENT_URI, "raw_contacts");
        public static final long MIN_ID = 9223372034707292160L;

        private Profile() {
        }
    }

    public static boolean isProfileId(long id) {
        return id >= Profile.MIN_ID;
    }

    /* loaded from: ContactsContract$DeletedContacts.class */
    public static final class DeletedContacts implements DeletedContactsColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "deleted_contacts");
        private static final int DAYS_KEPT = 30;
        public static final long DAYS_KEPT_MILLISECONDS = 2592000000L;

        private DeletedContacts() {
        }
    }

    /* loaded from: ContactsContract$RawContacts.class */
    public static final class RawContacts implements BaseColumns, RawContactsColumns, ContactOptionsColumns, ContactNameColumns, SyncColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "raw_contacts");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/raw_contact";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/raw_contact";
        public static final int AGGREGATION_MODE_DEFAULT = 0;
        @Deprecated
        public static final int AGGREGATION_MODE_IMMEDIATE = 1;
        public static final int AGGREGATION_MODE_SUSPENDED = 2;
        public static final int AGGREGATION_MODE_DISABLED = 3;

        private RawContacts() {
        }

        public static Uri getContactLookupUri(ContentResolver resolver, Uri rawContactUri) {
            Uri dataUri = Uri.withAppendedPath(rawContactUri, "data");
            Cursor cursor = resolver.query(dataUri, new String[]{"contact_id", ContactsColumns.LOOKUP_KEY}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        long contactId = cursor.getLong(0);
                        String lookupKey = cursor.getString(1);
                        Uri lookupUri = Contacts.getLookupUri(contactId, lookupKey);
                        if (cursor != null) {
                            cursor.close();
                        }
                        return lookupUri;
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            return null;
        }

        /* loaded from: ContactsContract$RawContacts$Data.class */
        public static final class Data implements BaseColumns, DataColumns {
            public static final String CONTENT_DIRECTORY = "data";

            private Data() {
            }
        }

        /* loaded from: ContactsContract$RawContacts$Entity.class */
        public static final class Entity implements BaseColumns, DataColumns {
            public static final String CONTENT_DIRECTORY = "entity";
            public static final String DATA_ID = "data_id";

            private Entity() {
            }
        }

        /* loaded from: ContactsContract$RawContacts$StreamItems.class */
        public static final class StreamItems implements BaseColumns, StreamItemsColumns {
            public static final String CONTENT_DIRECTORY = "stream_items";

            private StreamItems() {
            }
        }

        /* loaded from: ContactsContract$RawContacts$DisplayPhoto.class */
        public static final class DisplayPhoto {
            public static final String CONTENT_DIRECTORY = "display_photo";

            private DisplayPhoto() {
            }
        }

        public static EntityIterator newEntityIterator(Cursor cursor) {
            return new EntityIteratorImpl(cursor);
        }

        /* loaded from: ContactsContract$RawContacts$EntityIteratorImpl.class */
        private static class EntityIteratorImpl extends CursorEntityIterator {
            private static final String[] DATA_KEYS = {"data1", "data2", "data3", "data4", "data5", "data6", "data7", "data8", "data9", "data10", "data11", DataColumns.DATA12, DataColumns.DATA13, "data14", "data15", DataColumns.SYNC1, DataColumns.SYNC2, DataColumns.SYNC3, DataColumns.SYNC4};

            public EntityIteratorImpl(Cursor cursor) {
                super(cursor);
            }

            @Override // android.content.CursorEntityIterator
            public android.content.Entity getEntityAndIncrementCursor(Cursor cursor) throws RemoteException {
                int columnRawContactId = cursor.getColumnIndexOrThrow("_id");
                long rawContactId = cursor.getLong(columnRawContactId);
                ContentValues cv = new ContentValues();
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "account_name");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "account_type");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "data_set");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv, "_id");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv, "dirty");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv, "version");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "sourceid");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "sync1");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "sync2");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "sync3");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv, "sync4");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv, "deleted");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv, "contact_id");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv, "starred");
                DatabaseUtils.cursorIntToContentValuesIfPresent(cursor, cv, RawContactsColumns.NAME_VERIFIED);
                android.content.Entity contact = new android.content.Entity(cv);
                while (rawContactId == cursor.getLong(columnRawContactId)) {
                    ContentValues cv2 = new ContentValues();
                    cv2.put("_id", Long.valueOf(cursor.getLong(cursor.getColumnIndexOrThrow("data_id"))));
                    DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv2, "res_package");
                    DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv2, "mimetype");
                    DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv2, DataColumns.IS_PRIMARY);
                    DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv2, DataColumns.IS_SUPER_PRIMARY);
                    DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, cv2, DataColumns.DATA_VERSION);
                    DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv2, CommonDataKinds.GroupMembership.GROUP_SOURCE_ID);
                    DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, cv2, DataColumns.DATA_VERSION);
                    String[] arr$ = DATA_KEYS;
                    for (String key : arr$) {
                        int columnIndex = cursor.getColumnIndexOrThrow(key);
                        switch (cursor.getType(columnIndex)) {
                            case 0:
                                break;
                            case 1:
                            case 2:
                            case 3:
                                cv2.put(key, cursor.getString(columnIndex));
                                break;
                            case 4:
                                cv2.put(key, cursor.getBlob(columnIndex));
                                break;
                            default:
                                throw new IllegalStateException("Invalid or unhandled data type");
                        }
                    }
                    contact.addSubValue(Data.CONTENT_URI, cv2);
                    if (!cursor.moveToNext()) {
                        return contact;
                    }
                }
                return contact;
            }
        }
    }

    /* loaded from: ContactsContract$StreamItems.class */
    public static final class StreamItems implements BaseColumns, StreamItemsColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "stream_items");
        public static final Uri CONTENT_PHOTO_URI = Uri.withAppendedPath(CONTENT_URI, "photo");
        public static final Uri CONTENT_LIMIT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "stream_items_limit");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/stream_item";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/stream_item";
        public static final String MAX_ITEMS = "max_items";

        private StreamItems() {
        }

        /* loaded from: ContactsContract$StreamItems$StreamItemPhotos.class */
        public static final class StreamItemPhotos implements BaseColumns, StreamItemPhotosColumns {
            public static final String CONTENT_DIRECTORY = "photo";
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/stream_item_photo";
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/stream_item_photo";

            private StreamItemPhotos() {
            }
        }
    }

    /* loaded from: ContactsContract$StreamItemPhotos.class */
    public static final class StreamItemPhotos implements BaseColumns, StreamItemPhotosColumns {
        public static final String PHOTO = "photo";

        private StreamItemPhotos() {
        }
    }

    /* loaded from: ContactsContract$PhotoFiles.class */
    public static final class PhotoFiles implements BaseColumns, PhotoFilesColumns {
        private PhotoFiles() {
        }
    }

    /* loaded from: ContactsContract$Data.class */
    public static final class Data implements DataColumnsWithJoins {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "data");
        public static final String VISIBLE_CONTACTS_ONLY = "visible_contacts_only";
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/data";

        private Data() {
        }

        public static Uri getContactLookupUri(ContentResolver resolver, Uri dataUri) {
            Cursor cursor = resolver.query(dataUri, new String[]{"contact_id", ContactsColumns.LOOKUP_KEY}, null, null, null);
            if (cursor != null) {
                try {
                    if (cursor.moveToFirst()) {
                        long contactId = cursor.getLong(0);
                        String lookupKey = cursor.getString(1);
                        Uri lookupUri = Contacts.getLookupUri(contactId, lookupKey);
                        if (cursor != null) {
                            cursor.close();
                        }
                        return lookupUri;
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }
            }
            return null;
        }
    }

    /* loaded from: ContactsContract$RawContactsEntity.class */
    public static final class RawContactsEntity implements BaseColumns, DataColumns, RawContactsColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "raw_contact_entities");
        public static final Uri PROFILE_CONTENT_URI = Uri.withAppendedPath(Profile.CONTENT_URI, "raw_contact_entities");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/raw_contact_entity";
        public static final String FOR_EXPORT_ONLY = "for_export_only";
        public static final String DATA_ID = "data_id";

        private RawContactsEntity() {
        }
    }

    /* loaded from: ContactsContract$PhoneLookup.class */
    public static final class PhoneLookup implements BaseColumns, PhoneLookupColumns, ContactsColumns, ContactOptionsColumns {
        public static final Uri CONTENT_FILTER_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "phone_lookup");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/phone_lookup";
        public static final String QUERY_PARAMETER_SIP_ADDRESS = "sip";

        private PhoneLookup() {
        }
    }

    /* loaded from: ContactsContract$StatusUpdates.class */
    public static class StatusUpdates implements StatusColumns, PresenceColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "status_updates");
        public static final Uri PROFILE_CONTENT_URI = Uri.withAppendedPath(Profile.CONTENT_URI, "status_updates");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/status-update";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/status-update";

        private StatusUpdates() {
        }

        public static final int getPresenceIconResourceId(int status) {
            switch (status) {
                case 0:
                default:
                    return 17301610;
                case 1:
                    return 17301609;
                case 2:
                case 3:
                    return 17301607;
                case 4:
                    return 17301608;
                case 5:
                    return 17301611;
            }
        }

        public static final int getPresencePrecedence(int status) {
            return status;
        }
    }

    @Deprecated
    /* loaded from: ContactsContract$Presence.class */
    public static final class Presence extends StatusUpdates {
        public Presence() {
            super();
        }
    }

    /* loaded from: ContactsContract$CommonDataKinds.class */
    public static final class CommonDataKinds {
        public static final String PACKAGE_COMMON = "common";

        /* loaded from: ContactsContract$CommonDataKinds$BaseTypes.class */
        public interface BaseTypes {
            public static final int TYPE_CUSTOM = 0;
        }

        /* loaded from: ContactsContract$CommonDataKinds$Callable.class */
        public static final class Callable implements DataColumnsWithJoins, CommonColumns {
            public static final Uri CONTENT_URI = Uri.withAppendedPath(Data.CONTENT_URI, "callables");
            public static final Uri CONTENT_FILTER_URI = Uri.withAppendedPath(CONTENT_URI, "filter");
        }

        /* loaded from: ContactsContract$CommonDataKinds$CommonColumns.class */
        protected interface CommonColumns extends BaseTypes {
            public static final String DATA = "data1";
            public static final String TYPE = "data2";
            public static final String LABEL = "data3";
        }

        /* loaded from: ContactsContract$CommonDataKinds$Contactables.class */
        public static final class Contactables implements DataColumnsWithJoins, CommonColumns {
            public static final Uri CONTENT_URI = Uri.withAppendedPath(Data.CONTENT_URI, "contactables");
            public static final Uri CONTENT_FILTER_URI = Uri.withAppendedPath(CONTENT_URI, "filter");
            public static final String VISIBLE_CONTACTS_ONLY = "visible_contacts_only";
        }

        private CommonDataKinds() {
        }

        /* loaded from: ContactsContract$CommonDataKinds$StructuredName.class */
        public static final class StructuredName implements DataColumnsWithJoins {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/name";
            public static final String DISPLAY_NAME = "data1";
            public static final String GIVEN_NAME = "data2";
            public static final String FAMILY_NAME = "data3";
            public static final String PREFIX = "data4";
            public static final String MIDDLE_NAME = "data5";
            public static final String SUFFIX = "data6";
            public static final String PHONETIC_GIVEN_NAME = "data7";
            public static final String PHONETIC_MIDDLE_NAME = "data8";
            public static final String PHONETIC_FAMILY_NAME = "data9";
            public static final String FULL_NAME_STYLE = "data10";
            public static final String PHONETIC_NAME_STYLE = "data11";

            private StructuredName() {
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$Nickname.class */
        public static final class Nickname implements DataColumnsWithJoins, CommonColumns {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/nickname";
            public static final int TYPE_DEFAULT = 1;
            public static final int TYPE_OTHER_NAME = 2;
            public static final int TYPE_MAIDEN_NAME = 3;
            @Deprecated
            public static final int TYPE_MAINDEN_NAME = 3;
            public static final int TYPE_SHORT_NAME = 4;
            public static final int TYPE_INITIALS = 5;
            public static final String NAME = "data1";

            private Nickname() {
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$Phone.class */
        public static final class Phone implements DataColumnsWithJoins, CommonColumns {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/phone_v2";
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/phone_v2";
            public static final Uri CONTENT_URI = Uri.withAppendedPath(Data.CONTENT_URI, Contacts.People.Phones.CONTENT_DIRECTORY);
            public static final Uri CONTENT_FILTER_URI = Uri.withAppendedPath(CONTENT_URI, "filter");
            public static final String SEARCH_DISPLAY_NAME_KEY = "search_display_name";
            public static final String SEARCH_PHONE_NUMBER_KEY = "search_phone_number";
            public static final int TYPE_HOME = 1;
            public static final int TYPE_MOBILE = 2;
            public static final int TYPE_WORK = 3;
            public static final int TYPE_FAX_WORK = 4;
            public static final int TYPE_FAX_HOME = 5;
            public static final int TYPE_PAGER = 6;
            public static final int TYPE_OTHER = 7;
            public static final int TYPE_CALLBACK = 8;
            public static final int TYPE_CAR = 9;
            public static final int TYPE_COMPANY_MAIN = 10;
            public static final int TYPE_ISDN = 11;
            public static final int TYPE_MAIN = 12;
            public static final int TYPE_OTHER_FAX = 13;
            public static final int TYPE_RADIO = 14;
            public static final int TYPE_TELEX = 15;
            public static final int TYPE_TTY_TDD = 16;
            public static final int TYPE_WORK_MOBILE = 17;
            public static final int TYPE_WORK_PAGER = 18;
            public static final int TYPE_ASSISTANT = 19;
            public static final int TYPE_MMS = 20;
            public static final String NUMBER = "data1";
            public static final String NORMALIZED_NUMBER = "data4";

            private Phone() {
            }

            @Deprecated
            public static final CharSequence getDisplayLabel(Context context, int type, CharSequence label, CharSequence[] labelArray) {
                return getTypeLabel(context.getResources(), type, label);
            }

            @Deprecated
            public static final CharSequence getDisplayLabel(Context context, int type, CharSequence label) {
                return getTypeLabel(context.getResources(), type, label);
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return R.string.phoneTypeHome;
                    case 2:
                        return R.string.phoneTypeMobile;
                    case 3:
                        return R.string.phoneTypeWork;
                    case 4:
                        return R.string.phoneTypeFaxWork;
                    case 5:
                        return R.string.phoneTypeFaxHome;
                    case 6:
                        return R.string.phoneTypePager;
                    case 7:
                        return R.string.phoneTypeOther;
                    case 8:
                        return R.string.phoneTypeCallback;
                    case 9:
                        return R.string.phoneTypeCar;
                    case 10:
                        return R.string.phoneTypeCompanyMain;
                    case 11:
                        return R.string.phoneTypeIsdn;
                    case 12:
                        return R.string.phoneTypeMain;
                    case 13:
                        return R.string.phoneTypeOtherFax;
                    case 14:
                        return R.string.phoneTypeRadio;
                    case 15:
                        return R.string.phoneTypeTelex;
                    case 16:
                        return R.string.phoneTypeTtyTdd;
                    case 17:
                        return R.string.phoneTypeWorkMobile;
                    case 18:
                        return R.string.phoneTypeWorkPager;
                    case 19:
                        return R.string.phoneTypeAssistant;
                    case 20:
                        return R.string.phoneTypeMms;
                    default:
                        return R.string.phoneTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if ((type == 0 || type == 19) && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$Email.class */
        public static final class Email implements DataColumnsWithJoins, CommonColumns {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/email_v2";
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/email_v2";
            public static final Uri CONTENT_URI = Uri.withAppendedPath(Data.CONTENT_URI, "emails");
            public static final Uri CONTENT_LOOKUP_URI = Uri.withAppendedPath(CONTENT_URI, ContactsColumns.LOOKUP_KEY);
            public static final Uri CONTENT_FILTER_URI = Uri.withAppendedPath(CONTENT_URI, "filter");
            public static final String ADDRESS = "data1";
            public static final int TYPE_HOME = 1;
            public static final int TYPE_WORK = 2;
            public static final int TYPE_OTHER = 3;
            public static final int TYPE_MOBILE = 4;
            public static final String DISPLAY_NAME = "data4";

            private Email() {
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return R.string.emailTypeHome;
                    case 2:
                        return R.string.emailTypeWork;
                    case 3:
                        return R.string.emailTypeOther;
                    case 4:
                        return R.string.emailTypeMobile;
                    default:
                        return R.string.emailTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if (type == 0 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$StructuredPostal.class */
        public static final class StructuredPostal implements DataColumnsWithJoins, CommonColumns {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/postal-address_v2";
            public static final String CONTENT_TYPE = "vnd.android.cursor.dir/postal-address_v2";
            public static final Uri CONTENT_URI = Uri.withAppendedPath(Data.CONTENT_URI, "postals");
            public static final int TYPE_HOME = 1;
            public static final int TYPE_WORK = 2;
            public static final int TYPE_OTHER = 3;
            public static final String FORMATTED_ADDRESS = "data1";
            public static final String STREET = "data4";
            public static final String POBOX = "data5";
            public static final String NEIGHBORHOOD = "data6";
            public static final String CITY = "data7";
            public static final String REGION = "data8";
            public static final String POSTCODE = "data9";
            public static final String COUNTRY = "data10";

            private StructuredPostal() {
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return R.string.postalTypeHome;
                    case 2:
                        return R.string.postalTypeWork;
                    case 3:
                        return R.string.postalTypeOther;
                    default:
                        return R.string.postalTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if (type == 0 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$Im.class */
        public static final class Im implements DataColumnsWithJoins, CommonColumns {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/im";
            public static final int TYPE_HOME = 1;
            public static final int TYPE_WORK = 2;
            public static final int TYPE_OTHER = 3;
            public static final String PROTOCOL = "data5";
            public static final String CUSTOM_PROTOCOL = "data6";
            public static final int PROTOCOL_CUSTOM = -1;
            public static final int PROTOCOL_AIM = 0;
            public static final int PROTOCOL_MSN = 1;
            public static final int PROTOCOL_YAHOO = 2;
            public static final int PROTOCOL_SKYPE = 3;
            public static final int PROTOCOL_QQ = 4;
            public static final int PROTOCOL_GOOGLE_TALK = 5;
            public static final int PROTOCOL_ICQ = 6;
            public static final int PROTOCOL_JABBER = 7;
            public static final int PROTOCOL_NETMEETING = 8;

            private Im() {
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return R.string.imTypeHome;
                    case 2:
                        return R.string.imTypeWork;
                    case 3:
                        return R.string.imTypeOther;
                    default:
                        return R.string.imTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if (type == 0 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }

            public static final int getProtocolLabelResource(int type) {
                switch (type) {
                    case 0:
                        return R.string.imProtocolAim;
                    case 1:
                        return R.string.imProtocolMsn;
                    case 2:
                        return R.string.imProtocolYahoo;
                    case 3:
                        return R.string.imProtocolSkype;
                    case 4:
                        return R.string.imProtocolQq;
                    case 5:
                        return R.string.imProtocolGoogleTalk;
                    case 6:
                        return R.string.imProtocolIcq;
                    case 7:
                        return R.string.imProtocolJabber;
                    case 8:
                        return R.string.imProtocolNetMeeting;
                    default:
                        return R.string.imProtocolCustom;
                }
            }

            public static final CharSequence getProtocolLabel(Resources res, int type, CharSequence label) {
                if (type == -1 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getProtocolLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$Organization.class */
        public static final class Organization implements DataColumnsWithJoins, CommonColumns {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/organization";
            public static final int TYPE_WORK = 1;
            public static final int TYPE_OTHER = 2;
            public static final String COMPANY = "data1";
            public static final String TITLE = "data4";
            public static final String DEPARTMENT = "data5";
            public static final String JOB_DESCRIPTION = "data6";
            public static final String SYMBOL = "data7";
            public static final String PHONETIC_NAME = "data8";
            public static final String OFFICE_LOCATION = "data9";
            public static final String PHONETIC_NAME_STYLE = "data10";

            private Organization() {
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return R.string.orgTypeWork;
                    case 2:
                        return R.string.orgTypeOther;
                    default:
                        return R.string.orgTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if (type == 0 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$Relation.class */
        public static final class Relation implements DataColumnsWithJoins, CommonColumns {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/relation";
            public static final int TYPE_ASSISTANT = 1;
            public static final int TYPE_BROTHER = 2;
            public static final int TYPE_CHILD = 3;
            public static final int TYPE_DOMESTIC_PARTNER = 4;
            public static final int TYPE_FATHER = 5;
            public static final int TYPE_FRIEND = 6;
            public static final int TYPE_MANAGER = 7;
            public static final int TYPE_MOTHER = 8;
            public static final int TYPE_PARENT = 9;
            public static final int TYPE_PARTNER = 10;
            public static final int TYPE_REFERRED_BY = 11;
            public static final int TYPE_RELATIVE = 12;
            public static final int TYPE_SISTER = 13;
            public static final int TYPE_SPOUSE = 14;
            public static final String NAME = "data1";

            private Relation() {
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return R.string.relationTypeAssistant;
                    case 2:
                        return R.string.relationTypeBrother;
                    case 3:
                        return R.string.relationTypeChild;
                    case 4:
                        return R.string.relationTypeDomesticPartner;
                    case 5:
                        return R.string.relationTypeFather;
                    case 6:
                        return R.string.relationTypeFriend;
                    case 7:
                        return R.string.relationTypeManager;
                    case 8:
                        return R.string.relationTypeMother;
                    case 9:
                        return R.string.relationTypeParent;
                    case 10:
                        return R.string.relationTypePartner;
                    case 11:
                        return R.string.relationTypeReferredBy;
                    case 12:
                        return R.string.relationTypeRelative;
                    case 13:
                        return R.string.relationTypeSister;
                    case 14:
                        return R.string.relationTypeSpouse;
                    default:
                        return R.string.orgTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if (type == 0 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$Event.class */
        public static final class Event implements DataColumnsWithJoins, CommonColumns {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/contact_event";
            public static final int TYPE_ANNIVERSARY = 1;
            public static final int TYPE_OTHER = 2;
            public static final int TYPE_BIRTHDAY = 3;
            public static final String START_DATE = "data1";

            private Event() {
            }

            public static int getTypeResource(Integer type) {
                if (type == null) {
                    return R.string.eventTypeOther;
                }
                switch (type.intValue()) {
                    case 1:
                        return R.string.eventTypeAnniversary;
                    case 2:
                        return R.string.eventTypeOther;
                    case 3:
                        return R.string.eventTypeBirthday;
                    default:
                        return R.string.eventTypeCustom;
                }
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$Photo.class */
        public static final class Photo implements DataColumnsWithJoins {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/photo";
            public static final String PHOTO_FILE_ID = "data14";
            public static final String PHOTO = "data15";

            private Photo() {
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$Note.class */
        public static final class Note implements DataColumnsWithJoins {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/note";
            public static final String NOTE = "data1";

            private Note() {
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$GroupMembership.class */
        public static final class GroupMembership implements DataColumnsWithJoins {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/group_membership";
            public static final String GROUP_ROW_ID = "data1";
            public static final String GROUP_SOURCE_ID = "group_sourceid";

            private GroupMembership() {
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$Website.class */
        public static final class Website implements DataColumnsWithJoins, CommonColumns {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/website";
            public static final int TYPE_HOMEPAGE = 1;
            public static final int TYPE_BLOG = 2;
            public static final int TYPE_PROFILE = 3;
            public static final int TYPE_HOME = 4;
            public static final int TYPE_WORK = 5;
            public static final int TYPE_FTP = 6;
            public static final int TYPE_OTHER = 7;
            public static final String URL = "data1";

            private Website() {
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$SipAddress.class */
        public static final class SipAddress implements DataColumnsWithJoins, CommonColumns {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/sip_address";
            public static final int TYPE_HOME = 1;
            public static final int TYPE_WORK = 2;
            public static final int TYPE_OTHER = 3;
            public static final String SIP_ADDRESS = "data1";

            private SipAddress() {
            }

            public static final int getTypeLabelResource(int type) {
                switch (type) {
                    case 1:
                        return R.string.sipAddressTypeHome;
                    case 2:
                        return R.string.sipAddressTypeWork;
                    case 3:
                        return R.string.sipAddressTypeOther;
                    default:
                        return R.string.sipAddressTypeCustom;
                }
            }

            public static final CharSequence getTypeLabel(Resources res, int type, CharSequence label) {
                if (type == 0 && !TextUtils.isEmpty(label)) {
                    return label;
                }
                int labelRes = getTypeLabelResource(type);
                return res.getText(labelRes);
            }
        }

        /* loaded from: ContactsContract$CommonDataKinds$Identity.class */
        public static final class Identity implements DataColumnsWithJoins {
            public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/identity";
            public static final String IDENTITY = "data1";
            public static final String NAMESPACE = "data2";

            private Identity() {
            }
        }
    }

    /* loaded from: ContactsContract$Groups.class */
    public static final class Groups implements BaseColumns, GroupsColumns, SyncColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "groups");
        public static final Uri CONTENT_SUMMARY_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "groups_summary");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/group";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/group";

        private Groups() {
        }

        public static EntityIterator newEntityIterator(Cursor cursor) {
            return new EntityIteratorImpl(cursor);
        }

        /* loaded from: ContactsContract$Groups$EntityIteratorImpl.class */
        private static class EntityIteratorImpl extends CursorEntityIterator {
            public EntityIteratorImpl(Cursor cursor) {
                super(cursor);
            }

            @Override // android.content.CursorEntityIterator
            public Entity getEntityAndIncrementCursor(Cursor cursor) throws RemoteException {
                ContentValues values = new ContentValues();
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, values, "_id");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "account_name");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "account_type");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, values, "dirty");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, values, "version");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "sourceid");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "res_package");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "title");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, GroupsColumns.TITLE_RES);
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, values, GroupsColumns.GROUP_VISIBLE);
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "sync1");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "sync2");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "sync3");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "sync4");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "system_id");
                DatabaseUtils.cursorLongToContentValuesIfPresent(cursor, values, "deleted");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "notes");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, "should_sync");
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, GroupsColumns.FAVORITES);
                DatabaseUtils.cursorStringToContentValuesIfPresent(cursor, values, GroupsColumns.AUTO_ADD);
                cursor.moveToNext();
                return new Entity(values);
            }
        }
    }

    /* loaded from: ContactsContract$AggregationExceptions.class */
    public static final class AggregationExceptions implements BaseColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "aggregation_exceptions");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/aggregation_exception";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/aggregation_exception";
        public static final String TYPE = "type";
        public static final int TYPE_AUTOMATIC = 0;
        public static final int TYPE_KEEP_TOGETHER = 1;
        public static final int TYPE_KEEP_SEPARATE = 2;
        public static final String RAW_CONTACT_ID1 = "raw_contact_id1";
        public static final String RAW_CONTACT_ID2 = "raw_contact_id2";

        private AggregationExceptions() {
        }
    }

    /* loaded from: ContactsContract$Settings.class */
    public static final class Settings implements SettingsColumns {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "settings");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/setting";
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/setting";

        private Settings() {
        }
    }

    /* loaded from: ContactsContract$ProviderStatus.class */
    public static final class ProviderStatus {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "provider_status");
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/provider_status";
        public static final String STATUS = "status";
        public static final int STATUS_NORMAL = 0;
        public static final int STATUS_UPGRADING = 1;
        public static final int STATUS_UPGRADE_OUT_OF_MEMORY = 2;
        public static final int STATUS_CHANGING_LOCALE = 3;
        public static final int STATUS_NO_ACCOUNTS_NO_CONTACTS = 4;
        public static final String DATA1 = "data1";

        private ProviderStatus() {
        }
    }

    /* loaded from: ContactsContract$QuickContact.class */
    public static final class QuickContact {
        public static final String ACTION_QUICK_CONTACT = "com.android.contacts.action.QUICK_CONTACT";
        @Deprecated
        public static final String EXTRA_TARGET_RECT = "target_rect";
        public static final String EXTRA_MODE = "mode";
        public static final String EXTRA_EXCLUDE_MIMES = "exclude_mimes";
        public static final int MODE_SMALL = 1;
        public static final int MODE_MEDIUM = 2;
        public static final int MODE_LARGE = 3;

        public static Intent composeQuickContactsIntent(Context context, View target, Uri lookupUri, int mode, String[] excludeMimes) {
            float appScale = context.getResources().getCompatibilityInfo().applicationScale;
            int[] pos = new int[2];
            target.getLocationOnScreen(pos);
            Rect rect = new Rect();
            rect.left = (int) ((pos[0] * appScale) + 0.5f);
            rect.top = (int) ((pos[1] * appScale) + 0.5f);
            rect.right = (int) (((pos[0] + target.getWidth()) * appScale) + 0.5f);
            rect.bottom = (int) (((pos[1] + target.getHeight()) * appScale) + 0.5f);
            return composeQuickContactsIntent(context, rect, lookupUri, mode, excludeMimes);
        }

        public static Intent composeQuickContactsIntent(Context context, Rect target, Uri lookupUri, int mode, String[] excludeMimes) {
            Context actualContext;
            Context context2 = context;
            while (true) {
                actualContext = context2;
                if (!(actualContext instanceof ContextWrapper) || (actualContext instanceof Activity)) {
                    break;
                }
                context2 = ((ContextWrapper) actualContext).getBaseContext();
            }
            int intentFlags = actualContext instanceof Activity ? 524288 : 268468224;
            Intent intent = new Intent(ACTION_QUICK_CONTACT).addFlags(intentFlags);
            intent.setData(lookupUri);
            intent.setSourceBounds(target);
            intent.putExtra("mode", mode);
            intent.putExtra("exclude_mimes", excludeMimes);
            return intent;
        }

        public static void showQuickContact(Context context, View target, Uri lookupUri, int mode, String[] excludeMimes) {
            Intent intent = composeQuickContactsIntent(context, target, lookupUri, mode, excludeMimes);
            context.startActivity(intent);
        }

        public static void showQuickContact(Context context, Rect target, Uri lookupUri, int mode, String[] excludeMimes) {
            Intent intent = composeQuickContactsIntent(context, target, lookupUri, mode, excludeMimes);
            context.startActivity(intent);
        }
    }

    /* loaded from: ContactsContract$DisplayPhoto.class */
    public static final class DisplayPhoto {
        public static final Uri CONTENT_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "display_photo");
        public static final Uri CONTENT_MAX_DIMENSIONS_URI = Uri.withAppendedPath(ContactsContract.AUTHORITY_URI, "photo_dimensions");
        public static final String DISPLAY_MAX_DIM = "display_max_dim";
        public static final String THUMBNAIL_MAX_DIM = "thumbnail_max_dim";

        private DisplayPhoto() {
        }
    }
}