package android.provider;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.IContentProvider;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.SQLException;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.text.TextUtils;
import android.util.AndroidException;
import android.util.Log;
import com.android.internal.widget.ILockSettings;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;

/* loaded from: Settings.class */
public final class Settings {
    public static final String ACTION_SETTINGS = "android.settings.SETTINGS";
    public static final String ACTION_APN_SETTINGS = "android.settings.APN_SETTINGS";
    public static final String ACTION_LOCATION_SOURCE_SETTINGS = "android.settings.LOCATION_SOURCE_SETTINGS";
    public static final String ACTION_WIRELESS_SETTINGS = "android.settings.WIRELESS_SETTINGS";
    public static final String ACTION_AIRPLANE_MODE_SETTINGS = "android.settings.AIRPLANE_MODE_SETTINGS";
    public static final String ACTION_ACCESSIBILITY_SETTINGS = "android.settings.ACCESSIBILITY_SETTINGS";
    public static final String ACTION_SECURITY_SETTINGS = "android.settings.SECURITY_SETTINGS";
    public static final String ACTION_TRUSTED_CREDENTIALS_USER = "com.android.settings.TRUSTED_CREDENTIALS_USER";
    public static final String ACTION_MONITORING_CERT_INFO = "com.android.settings.MONITORING_CERT_INFO";
    public static final String ACTION_PRIVACY_SETTINGS = "android.settings.PRIVACY_SETTINGS";
    public static final String ACTION_WIFI_SETTINGS = "android.settings.WIFI_SETTINGS";
    public static final String ACTION_WIFI_IP_SETTINGS = "android.settings.WIFI_IP_SETTINGS";
    public static final String ACTION_BLUETOOTH_SETTINGS = "android.settings.BLUETOOTH_SETTINGS";
    public static final String ACTION_WIFI_DISPLAY_SETTINGS = "android.settings.WIFI_DISPLAY_SETTINGS";
    public static final String ACTION_DATE_SETTINGS = "android.settings.DATE_SETTINGS";
    public static final String ACTION_SOUND_SETTINGS = "android.settings.SOUND_SETTINGS";
    public static final String ACTION_DISPLAY_SETTINGS = "android.settings.DISPLAY_SETTINGS";
    public static final String ACTION_LOCALE_SETTINGS = "android.settings.LOCALE_SETTINGS";
    public static final String ACTION_INPUT_METHOD_SETTINGS = "android.settings.INPUT_METHOD_SETTINGS";
    public static final String ACTION_INPUT_METHOD_SUBTYPE_SETTINGS = "android.settings.INPUT_METHOD_SUBTYPE_SETTINGS";
    public static final String ACTION_SHOW_INPUT_METHOD_PICKER = "android.settings.SHOW_INPUT_METHOD_PICKER";
    public static final String ACTION_USER_DICTIONARY_SETTINGS = "android.settings.USER_DICTIONARY_SETTINGS";
    public static final String ACTION_USER_DICTIONARY_INSERT = "com.android.settings.USER_DICTIONARY_INSERT";
    public static final String ACTION_APPLICATION_SETTINGS = "android.settings.APPLICATION_SETTINGS";
    public static final String ACTION_APPLICATION_DEVELOPMENT_SETTINGS = "android.settings.APPLICATION_DEVELOPMENT_SETTINGS";
    public static final String ACTION_QUICK_LAUNCH_SETTINGS = "android.settings.QUICK_LAUNCH_SETTINGS";
    public static final String ACTION_MANAGE_APPLICATIONS_SETTINGS = "android.settings.MANAGE_APPLICATIONS_SETTINGS";
    public static final String ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS = "android.settings.MANAGE_ALL_APPLICATIONS_SETTINGS";
    public static final String ACTION_APPLICATION_DETAILS_SETTINGS = "android.settings.APPLICATION_DETAILS_SETTINGS";
    public static final String ACTION_APP_OPS_SETTINGS = "android.settings.APP_OPS_SETTINGS";
    public static final String ACTION_SYSTEM_UPDATE_SETTINGS = "android.settings.SYSTEM_UPDATE_SETTINGS";
    public static final String ACTION_SYNC_SETTINGS = "android.settings.SYNC_SETTINGS";
    public static final String ACTION_ADD_ACCOUNT = "android.settings.ADD_ACCOUNT_SETTINGS";
    public static final String ACTION_NETWORK_OPERATOR_SETTINGS = "android.settings.NETWORK_OPERATOR_SETTINGS";
    public static final String ACTION_DATA_ROAMING_SETTINGS = "android.settings.DATA_ROAMING_SETTINGS";
    public static final String ACTION_INTERNAL_STORAGE_SETTINGS = "android.settings.INTERNAL_STORAGE_SETTINGS";
    public static final String ACTION_MEMORY_CARD_SETTINGS = "android.settings.MEMORY_CARD_SETTINGS";
    public static final String ACTION_SEARCH_SETTINGS = "android.search.action.SEARCH_SETTINGS";
    public static final String ACTION_DEVICE_INFO_SETTINGS = "android.settings.DEVICE_INFO_SETTINGS";
    public static final String ACTION_NFC_SETTINGS = "android.settings.NFC_SETTINGS";
    public static final String ACTION_NFCSHARING_SETTINGS = "android.settings.NFCSHARING_SETTINGS";
    public static final String ACTION_NFC_PAYMENT_SETTINGS = "android.settings.NFC_PAYMENT_SETTINGS";
    public static final String ACTION_DREAM_SETTINGS = "android.settings.DREAM_SETTINGS";
    public static final String ACTION_NOTIFICATION_LISTENER_SETTINGS = "android.settings.NOTIFICATION_LISTENER_SETTINGS";
    public static final String ACTION_CAPTIONING_SETTINGS = "android.settings.CAPTIONING_SETTINGS";
    public static final String ACTION_PRINT_SETTINGS = "android.settings.ACTION_PRINT_SETTINGS";
    public static final String CALL_METHOD_GET_SYSTEM = "GET_system";
    public static final String CALL_METHOD_GET_SECURE = "GET_secure";
    public static final String CALL_METHOD_GET_GLOBAL = "GET_global";
    public static final String CALL_METHOD_USER_KEY = "_user";
    public static final String CALL_METHOD_PUT_SYSTEM = "PUT_system";
    public static final String CALL_METHOD_PUT_SECURE = "PUT_secure";
    public static final String CALL_METHOD_PUT_GLOBAL = "PUT_global";
    public static final String EXTRA_AUTHORITIES = "authorities";
    public static final String EXTRA_ACCOUNT_TYPES = "account_types";
    public static final String EXTRA_INPUT_METHOD_ID = "input_method_id";
    private static final String JID_RESOURCE_PREFIX = "android";
    public static final String AUTHORITY = "settings";
    private static final String TAG = "Settings";
    private static final boolean LOCAL_LOGV = false;
    private static final Object mLocationSettingsLock = new Object();

    /* loaded from: Settings$SettingNotFoundException.class */
    public static class SettingNotFoundException extends AndroidException {
        public SettingNotFoundException(String msg) {
            super(msg);
        }
    }

    /* loaded from: Settings$NameValueTable.class */
    public static class NameValueTable implements BaseColumns {
        public static final String NAME = "name";
        public static final String VALUE = "value";

        protected static boolean putString(ContentResolver resolver, Uri uri, String name, String value) {
            try {
                ContentValues values = new ContentValues();
                values.put("name", name);
                values.put("value", value);
                resolver.insert(uri, values);
                return true;
            } catch (SQLException e) {
                Log.w(Settings.TAG, "Can't set key " + name + " in " + uri, e);
                return false;
            }
        }

        public static Uri getUriFor(Uri uri, String name) {
            return Uri.withAppendedPath(uri, name);
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Settings$NameValueCache.class */
    public static class NameValueCache {
        private final String mVersionSystemProperty;
        private final Uri mUri;
        private static final String[] SELECT_VALUE = {"value"};
        private static final String NAME_EQ_PLACEHOLDER = "name=?";
        private final HashMap<String, String> mValues = new HashMap<>();
        private long mValuesVersion = 0;
        private IContentProvider mContentProvider = null;
        private final String mCallGetCommand;
        private final String mCallSetCommand;

        public NameValueCache(String versionSystemProperty, Uri uri, String getCommand, String setCommand) {
            this.mVersionSystemProperty = versionSystemProperty;
            this.mUri = uri;
            this.mCallGetCommand = getCommand;
            this.mCallSetCommand = setCommand;
        }

        private IContentProvider lazyGetProvider(ContentResolver cr) {
            IContentProvider cp;
            synchronized (this) {
                cp = this.mContentProvider;
                if (cp == null) {
                    IContentProvider acquireProvider = cr.acquireProvider(this.mUri.getAuthority());
                    this.mContentProvider = acquireProvider;
                    cp = acquireProvider;
                }
            }
            return cp;
        }

        public boolean putStringForUser(ContentResolver cr, String name, String value, int userHandle) {
            try {
                Bundle arg = new Bundle();
                arg.putString("value", value);
                arg.putInt(Settings.CALL_METHOD_USER_KEY, userHandle);
                IContentProvider cp = lazyGetProvider(cr);
                cp.call(cr.getPackageName(), this.mCallSetCommand, name, arg);
                return true;
            } catch (RemoteException e) {
                Log.w(Settings.TAG, "Can't set key " + name + " in " + this.mUri, e);
                return false;
            }
        }

        public String getStringForUser(ContentResolver cr, String name, int userHandle) {
            boolean isSelf = userHandle == UserHandle.myUserId();
            if (isSelf) {
                long newValuesVersion = SystemProperties.getLong(this.mVersionSystemProperty, 0L);
                synchronized (this) {
                    if (this.mValuesVersion != newValuesVersion) {
                        this.mValues.clear();
                        this.mValuesVersion = newValuesVersion;
                    }
                    if (this.mValues.containsKey(name)) {
                        return this.mValues.get(name);
                    }
                }
            }
            IContentProvider cp = lazyGetProvider(cr);
            if (this.mCallGetCommand != null) {
                Bundle args = null;
                if (!isSelf) {
                    try {
                        args = new Bundle();
                        args.putInt(Settings.CALL_METHOD_USER_KEY, userHandle);
                    } catch (RemoteException e) {
                    }
                }
                Bundle b = cp.call(cr.getPackageName(), this.mCallGetCommand, name, args);
                if (b != null) {
                    String value = b.getPairValue();
                    if (isSelf) {
                        synchronized (this) {
                            this.mValues.put(name, value);
                        }
                    }
                    return value;
                }
            }
            Cursor c = null;
            try {
                try {
                    Cursor c2 = cp.query(cr.getPackageName(), this.mUri, SELECT_VALUE, NAME_EQ_PLACEHOLDER, new String[]{name}, null, null);
                    if (c2 == null) {
                        Log.w(Settings.TAG, "Can't get key " + name + " from " + this.mUri);
                        if (c2 != null) {
                            c2.close();
                        }
                        return null;
                    }
                    String value2 = c2.moveToNext() ? c2.getString(0) : null;
                    synchronized (this) {
                        this.mValues.put(name, value2);
                    }
                    if (c2 != null) {
                        c2.close();
                    }
                    return value2;
                } catch (RemoteException e2) {
                    Log.w(Settings.TAG, "Can't get key " + name + " from " + this.mUri, e2);
                    if (0 != 0) {
                        c.close();
                    }
                    return null;
                }
            } catch (Throwable th) {
                if (0 != 0) {
                    c.close();
                }
                throw th;
            }
        }
    }

    /* loaded from: Settings$System.class */
    public static final class System extends NameValueTable {
        private static final HashSet<String> MOVED_TO_GLOBAL;
        private static final HashSet<String> MOVED_TO_SECURE_THEN_GLOBAL;
        @Deprecated
        public static final String STAY_ON_WHILE_PLUGGED_IN = "stay_on_while_plugged_in";
        public static final String END_BUTTON_BEHAVIOR = "end_button_behavior";
        public static final int END_BUTTON_BEHAVIOR_HOME = 1;
        public static final int END_BUTTON_BEHAVIOR_SLEEP = 2;
        public static final int END_BUTTON_BEHAVIOR_DEFAULT = 2;
        public static final String ADVANCED_SETTINGS = "advanced_settings";
        public static final int ADVANCED_SETTINGS_DEFAULT = 0;
        @Deprecated
        public static final String AIRPLANE_MODE_ON = "airplane_mode_on";
        @Deprecated
        public static final String RADIO_BLUETOOTH = "bluetooth";
        @Deprecated
        public static final String RADIO_WIFI = "wifi";
        @Deprecated
        public static final String RADIO_WIMAX = "wimax";
        @Deprecated
        public static final String RADIO_CELL = "cell";
        @Deprecated
        public static final String RADIO_NFC = "nfc";
        @Deprecated
        public static final String AIRPLANE_MODE_RADIOS = "airplane_mode_radios";
        @Deprecated
        public static final String AIRPLANE_MODE_TOGGLEABLE_RADIOS = "airplane_mode_toggleable_radios";
        @Deprecated
        public static final String WIFI_SLEEP_POLICY = "wifi_sleep_policy";
        @Deprecated
        public static final int WIFI_SLEEP_POLICY_DEFAULT = 0;
        @Deprecated
        public static final int WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED = 1;
        @Deprecated
        public static final int WIFI_SLEEP_POLICY_NEVER = 2;
        @Deprecated
        public static final String MODE_RINGER = "mode_ringer";
        @Deprecated
        public static final String WIFI_USE_STATIC_IP = "wifi_use_static_ip";
        @Deprecated
        public static final String WIFI_STATIC_IP = "wifi_static_ip";
        @Deprecated
        public static final String WIFI_STATIC_GATEWAY = "wifi_static_gateway";
        @Deprecated
        public static final String WIFI_STATIC_NETMASK = "wifi_static_netmask";
        @Deprecated
        public static final String WIFI_STATIC_DNS1 = "wifi_static_dns1";
        @Deprecated
        public static final String WIFI_STATIC_DNS2 = "wifi_static_dns2";
        public static final String BLUETOOTH_DISCOVERABILITY = "bluetooth_discoverability";
        public static final String BLUETOOTH_DISCOVERABILITY_TIMEOUT = "bluetooth_discoverability_timeout";
        @Deprecated
        public static final String LOCK_PATTERN_ENABLED = "lock_pattern_autolock";
        @Deprecated
        public static final String LOCK_PATTERN_VISIBLE = "lock_pattern_visible_pattern";
        @Deprecated
        public static final String LOCK_PATTERN_TACTILE_FEEDBACK_ENABLED = "lock_pattern_tactile_feedback_enabled";
        public static final String NEXT_ALARM_FORMATTED = "next_alarm_formatted";
        public static final String FONT_SCALE = "font_scale";
        @Deprecated
        public static final String DEBUG_APP = "debug_app";
        @Deprecated
        public static final String WAIT_FOR_DEBUGGER = "wait_for_debugger";
        @Deprecated
        public static final String DIM_SCREEN = "dim_screen";
        public static final String SCREEN_OFF_TIMEOUT = "screen_off_timeout";
        public static final String SCREEN_BRIGHTNESS = "screen_brightness";
        public static final String SCREEN_BRIGHTNESS_MODE = "screen_brightness_mode";
        public static final String SCREEN_AUTO_BRIGHTNESS_ADJ = "screen_auto_brightness_adj";
        public static final int SCREEN_BRIGHTNESS_MODE_MANUAL = 0;
        public static final int SCREEN_BRIGHTNESS_MODE_AUTOMATIC = 1;
        @Deprecated
        public static final String SHOW_PROCESSES = "show_processes";
        @Deprecated
        public static final String ALWAYS_FINISH_ACTIVITIES = "always_finish_activities";
        public static final String MODE_RINGER_STREAMS_AFFECTED = "mode_ringer_streams_affected";
        public static final String MUTE_STREAMS_AFFECTED = "mute_streams_affected";
        public static final String VIBRATE_ON = "vibrate_on";
        public static final String VIBRATE_INPUT_DEVICES = "vibrate_input_devices";
        public static final String VOLUME_RING = "volume_ring";
        public static final String VOLUME_SYSTEM = "volume_system";
        public static final String VOLUME_VOICE = "volume_voice";
        public static final String VOLUME_MUSIC = "volume_music";
        public static final String VOLUME_ALARM = "volume_alarm";
        public static final String VOLUME_NOTIFICATION = "volume_notification";
        public static final String VOLUME_BLUETOOTH_SCO = "volume_bluetooth_sco";
        public static final String VOLUME_MASTER = "volume_master";
        public static final String VOLUME_MASTER_MUTE = "volume_master_mute";
        @Deprecated
        public static final String NOTIFICATIONS_USE_RING_VOLUME = "notifications_use_ring_volume";
        public static final String VIBRATE_IN_SILENT = "vibrate_in_silent";
        public static final String[] VOLUME_SETTINGS;
        public static final String APPEND_FOR_LAST_AUDIBLE = "_last_audible";
        public static final String RINGTONE = "ringtone";
        public static final Uri DEFAULT_RINGTONE_URI;
        public static final String NOTIFICATION_SOUND = "notification_sound";
        public static final Uri DEFAULT_NOTIFICATION_URI;
        public static final String ALARM_ALERT = "alarm_alert";
        public static final Uri DEFAULT_ALARM_ALERT_URI;
        public static final String MEDIA_BUTTON_RECEIVER = "media_button_receiver";
        public static final String TEXT_AUTO_REPLACE = "auto_replace";
        public static final String TEXT_AUTO_CAPS = "auto_caps";
        public static final String TEXT_AUTO_PUNCTUATE = "auto_punctuate";
        public static final String TEXT_SHOW_PASSWORD = "show_password";
        public static final String SHOW_GTALK_SERVICE_STATUS = "SHOW_GTALK_SERVICE_STATUS";
        @Deprecated
        public static final String WALLPAPER_ACTIVITY = "wallpaper_activity";
        @Deprecated
        public static final String AUTO_TIME = "auto_time";
        @Deprecated
        public static final String AUTO_TIME_ZONE = "auto_time_zone";
        public static final String TIME_12_24 = "time_12_24";
        public static final String DATE_FORMAT = "date_format";
        public static final String SETUP_WIZARD_HAS_RUN = "setup_wizard_has_run";
        @Deprecated
        public static final String WINDOW_ANIMATION_SCALE = "window_animation_scale";
        @Deprecated
        public static final String TRANSITION_ANIMATION_SCALE = "transition_animation_scale";
        @Deprecated
        public static final String ANIMATOR_DURATION_SCALE = "animator_duration_scale";
        public static final String ACCELEROMETER_ROTATION = "accelerometer_rotation";
        public static final String USER_ROTATION = "user_rotation";
        public static final String HIDE_ROTATION_LOCK_TOGGLE_FOR_ACCESSIBILITY = "hide_rotation_lock_toggle_for_accessibility";
        public static final String VIBRATE_WHEN_RINGING = "vibrate_when_ringing";
        public static final String DTMF_TONE_WHEN_DIALING = "dtmf_tone";
        public static final String DTMF_TONE_TYPE_WHEN_DIALING = "dtmf_tone_type";
        public static final String HEARING_AID = "hearing_aid";
        public static final String TTY_MODE = "tty_mode";
        public static final String SOUND_EFFECTS_ENABLED = "sound_effects_enabled";
        public static final String HAPTIC_FEEDBACK_ENABLED = "haptic_feedback_enabled";
        @Deprecated
        public static final String SHOW_WEB_SUGGESTIONS = "show_web_suggestions";
        public static final String NOTIFICATION_LIGHT_PULSE = "notification_light_pulse";
        public static final String POINTER_LOCATION = "pointer_location";
        public static final String SHOW_TOUCHES = "show_touches";
        public static final String WINDOW_ORIENTATION_LISTENER_LOG = "window_orientation_listener_log";
        @Deprecated
        public static final String POWER_SOUNDS_ENABLED = "power_sounds_enabled";
        @Deprecated
        public static final String DOCK_SOUNDS_ENABLED = "dock_sounds_enabled";
        public static final String LOCKSCREEN_SOUNDS_ENABLED = "lockscreen_sounds_enabled";
        public static final String LOCKSCREEN_DISABLED = "lockscreen.disabled";
        @Deprecated
        public static final String LOW_BATTERY_SOUND = "low_battery_sound";
        @Deprecated
        public static final String DESK_DOCK_SOUND = "desk_dock_sound";
        @Deprecated
        public static final String DESK_UNDOCK_SOUND = "desk_undock_sound";
        @Deprecated
        public static final String CAR_DOCK_SOUND = "car_dock_sound";
        @Deprecated
        public static final String CAR_UNDOCK_SOUND = "car_undock_sound";
        @Deprecated
        public static final String LOCK_SOUND = "lock_sound";
        @Deprecated
        public static final String UNLOCK_SOUND = "unlock_sound";
        public static final String SIP_RECEIVE_CALLS = "sip_receive_calls";
        public static final String SIP_CALL_OPTIONS = "sip_call_options";
        public static final String SIP_ALWAYS = "SIP_ALWAYS";
        public static final String SIP_ADDRESS_ONLY = "SIP_ADDRESS_ONLY";
        public static final String SIP_ASK_ME_EACH_TIME = "SIP_ASK_ME_EACH_TIME";
        public static final String POINTER_SPEED = "pointer_speed";
        public static final String EGG_MODE = "egg_mode";
        public static final String[] SETTINGS_TO_BACKUP;
        @Deprecated
        public static final String ADB_ENABLED = "adb_enabled";
        @Deprecated
        public static final String ANDROID_ID = "android_id";
        @Deprecated
        public static final String BLUETOOTH_ON = "bluetooth_on";
        @Deprecated
        public static final String DATA_ROAMING = "data_roaming";
        @Deprecated
        public static final String DEVICE_PROVISIONED = "device_provisioned";
        @Deprecated
        public static final String HTTP_PROXY = "http_proxy";
        @Deprecated
        public static final String INSTALL_NON_MARKET_APPS = "install_non_market_apps";
        @Deprecated
        public static final String LOCATION_PROVIDERS_ALLOWED = "location_providers_allowed";
        @Deprecated
        public static final String LOGGING_ID = "logging_id";
        @Deprecated
        public static final String NETWORK_PREFERENCE = "network_preference";
        @Deprecated
        public static final String PARENTAL_CONTROL_ENABLED = "parental_control_enabled";
        @Deprecated
        public static final String PARENTAL_CONTROL_LAST_UPDATE = "parental_control_last_update";
        @Deprecated
        public static final String PARENTAL_CONTROL_REDIRECT_URL = "parental_control_redirect_url";
        @Deprecated
        public static final String SETTINGS_CLASSNAME = "settings_classname";
        @Deprecated
        public static final String USB_MASS_STORAGE_ENABLED = "usb_mass_storage_enabled";
        @Deprecated
        public static final String USE_GOOGLE_MAIL = "use_google_mail";
        @Deprecated
        public static final String WIFI_MAX_DHCP_RETRY_COUNT = "wifi_max_dhcp_retry_count";
        @Deprecated
        public static final String WIFI_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wifi_mobile_data_transition_wakelock_timeout_ms";
        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wifi_networks_available_notification_on";
        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_REPEAT_DELAY = "wifi_networks_available_repeat_delay";
        @Deprecated
        public static final String WIFI_NUM_OPEN_NETWORKS_KEPT = "wifi_num_open_networks_kept";
        @Deprecated
        public static final String WIFI_ON = "wifi_on";
        @Deprecated
        public static final String WIFI_WATCHDOG_ACCEPTABLE_PACKET_LOSS_PERCENTAGE = "wifi_watchdog_acceptable_packet_loss_percentage";
        @Deprecated
        public static final String WIFI_WATCHDOG_AP_COUNT = "wifi_watchdog_ap_count";
        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_DELAY_MS = "wifi_watchdog_background_check_delay_ms";
        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_ENABLED = "wifi_watchdog_background_check_enabled";
        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_TIMEOUT_MS = "wifi_watchdog_background_check_timeout_ms";
        @Deprecated
        public static final String WIFI_WATCHDOG_INITIAL_IGNORED_PING_COUNT = "wifi_watchdog_initial_ignored_ping_count";
        @Deprecated
        public static final String WIFI_WATCHDOG_MAX_AP_CHECKS = "wifi_watchdog_max_ap_checks";
        @Deprecated
        public static final String WIFI_WATCHDOG_ON = "wifi_watchdog_on";
        @Deprecated
        public static final String WIFI_WATCHDOG_PING_COUNT = "wifi_watchdog_ping_count";
        @Deprecated
        public static final String WIFI_WATCHDOG_PING_DELAY_MS = "wifi_watchdog_ping_delay_ms";
        @Deprecated
        public static final String WIFI_WATCHDOG_PING_TIMEOUT_MS = "wifi_watchdog_ping_timeout_ms";
        public static final Uri CONTENT_URI = Uri.parse("content://settings/system");
        public static final String SYS_PROP_SETTING_VERSION = "sys.settings_system_version";
        private static final NameValueCache sNameValueCache = new NameValueCache(SYS_PROP_SETTING_VERSION, CONTENT_URI, Settings.CALL_METHOD_GET_SYSTEM, Settings.CALL_METHOD_PUT_SYSTEM);
        private static final HashSet<String> MOVED_TO_SECURE = new HashSet<>(30);

        static {
            MOVED_TO_SECURE.add("android_id");
            MOVED_TO_SECURE.add("http_proxy");
            MOVED_TO_SECURE.add("location_providers_allowed");
            MOVED_TO_SECURE.add(Secure.LOCK_BIOMETRIC_WEAK_FLAGS);
            MOVED_TO_SECURE.add("lock_pattern_autolock");
            MOVED_TO_SECURE.add("lock_pattern_visible_pattern");
            MOVED_TO_SECURE.add("lock_pattern_tactile_feedback_enabled");
            MOVED_TO_SECURE.add("logging_id");
            MOVED_TO_SECURE.add("parental_control_enabled");
            MOVED_TO_SECURE.add("parental_control_last_update");
            MOVED_TO_SECURE.add("parental_control_redirect_url");
            MOVED_TO_SECURE.add("settings_classname");
            MOVED_TO_SECURE.add("use_google_mail");
            MOVED_TO_SECURE.add("wifi_networks_available_notification_on");
            MOVED_TO_SECURE.add("wifi_networks_available_repeat_delay");
            MOVED_TO_SECURE.add("wifi_num_open_networks_kept");
            MOVED_TO_SECURE.add("wifi_on");
            MOVED_TO_SECURE.add("wifi_watchdog_acceptable_packet_loss_percentage");
            MOVED_TO_SECURE.add("wifi_watchdog_ap_count");
            MOVED_TO_SECURE.add("wifi_watchdog_background_check_delay_ms");
            MOVED_TO_SECURE.add("wifi_watchdog_background_check_enabled");
            MOVED_TO_SECURE.add("wifi_watchdog_background_check_timeout_ms");
            MOVED_TO_SECURE.add("wifi_watchdog_initial_ignored_ping_count");
            MOVED_TO_SECURE.add("wifi_watchdog_max_ap_checks");
            MOVED_TO_SECURE.add("wifi_watchdog_on");
            MOVED_TO_SECURE.add("wifi_watchdog_ping_count");
            MOVED_TO_SECURE.add("wifi_watchdog_ping_delay_ms");
            MOVED_TO_SECURE.add("wifi_watchdog_ping_timeout_ms");
            MOVED_TO_GLOBAL = new HashSet<>();
            MOVED_TO_SECURE_THEN_GLOBAL = new HashSet<>();
            MOVED_TO_SECURE_THEN_GLOBAL.add("adb_enabled");
            MOVED_TO_SECURE_THEN_GLOBAL.add("bluetooth_on");
            MOVED_TO_SECURE_THEN_GLOBAL.add("data_roaming");
            MOVED_TO_SECURE_THEN_GLOBAL.add("device_provisioned");
            MOVED_TO_SECURE_THEN_GLOBAL.add("install_non_market_apps");
            MOVED_TO_SECURE_THEN_GLOBAL.add("usb_mass_storage_enabled");
            MOVED_TO_SECURE_THEN_GLOBAL.add("http_proxy");
            MOVED_TO_GLOBAL.add("airplane_mode_on");
            MOVED_TO_GLOBAL.add("airplane_mode_radios");
            MOVED_TO_GLOBAL.add("airplane_mode_toggleable_radios");
            MOVED_TO_GLOBAL.add("auto_time");
            MOVED_TO_GLOBAL.add("auto_time_zone");
            MOVED_TO_GLOBAL.add("car_dock_sound");
            MOVED_TO_GLOBAL.add("car_undock_sound");
            MOVED_TO_GLOBAL.add("desk_dock_sound");
            MOVED_TO_GLOBAL.add("desk_undock_sound");
            MOVED_TO_GLOBAL.add("dock_sounds_enabled");
            MOVED_TO_GLOBAL.add("lock_sound");
            MOVED_TO_GLOBAL.add("unlock_sound");
            MOVED_TO_GLOBAL.add("low_battery_sound");
            MOVED_TO_GLOBAL.add("power_sounds_enabled");
            MOVED_TO_GLOBAL.add("stay_on_while_plugged_in");
            MOVED_TO_GLOBAL.add("wifi_sleep_policy");
            MOVED_TO_GLOBAL.add("mode_ringer");
            MOVED_TO_GLOBAL.add("window_animation_scale");
            MOVED_TO_GLOBAL.add("transition_animation_scale");
            MOVED_TO_GLOBAL.add("animator_duration_scale");
            MOVED_TO_GLOBAL.add(Global.FANCY_IME_ANIMATIONS);
            MOVED_TO_GLOBAL.add(Global.COMPATIBILITY_MODE);
            MOVED_TO_GLOBAL.add(Global.EMERGENCY_TONE);
            MOVED_TO_GLOBAL.add(Global.CALL_AUTO_RETRY);
            MOVED_TO_GLOBAL.add("debug_app");
            MOVED_TO_GLOBAL.add("wait_for_debugger");
            MOVED_TO_GLOBAL.add("show_processes");
            MOVED_TO_GLOBAL.add("always_finish_activities");
            MOVED_TO_GLOBAL.add(Global.TZINFO_UPDATE_CONTENT_URL);
            MOVED_TO_GLOBAL.add(Global.TZINFO_UPDATE_METADATA_URL);
            MOVED_TO_GLOBAL.add(Global.SELINUX_UPDATE_CONTENT_URL);
            MOVED_TO_GLOBAL.add(Global.SELINUX_UPDATE_METADATA_URL);
            MOVED_TO_GLOBAL.add(Global.SMS_SHORT_CODES_UPDATE_CONTENT_URL);
            MOVED_TO_GLOBAL.add(Global.SMS_SHORT_CODES_UPDATE_METADATA_URL);
            MOVED_TO_GLOBAL.add(Global.CERT_PIN_UPDATE_CONTENT_URL);
            MOVED_TO_GLOBAL.add(Global.CERT_PIN_UPDATE_METADATA_URL);
            VOLUME_SETTINGS = new String[]{VOLUME_VOICE, VOLUME_SYSTEM, VOLUME_RING, VOLUME_MUSIC, VOLUME_ALARM, VOLUME_NOTIFICATION, VOLUME_BLUETOOTH_SCO};
            DEFAULT_RINGTONE_URI = getUriFor(RINGTONE);
            DEFAULT_NOTIFICATION_URI = getUriFor(NOTIFICATION_SOUND);
            DEFAULT_ALARM_ALERT_URI = getUriFor(ALARM_ALERT);
            SETTINGS_TO_BACKUP = new String[]{"stay_on_while_plugged_in", WIFI_USE_STATIC_IP, WIFI_STATIC_IP, WIFI_STATIC_GATEWAY, WIFI_STATIC_NETMASK, WIFI_STATIC_DNS1, WIFI_STATIC_DNS2, BLUETOOTH_DISCOVERABILITY, BLUETOOTH_DISCOVERABILITY_TIMEOUT, DIM_SCREEN, SCREEN_OFF_TIMEOUT, SCREEN_BRIGHTNESS, SCREEN_BRIGHTNESS_MODE, SCREEN_AUTO_BRIGHTNESS_ADJ, VIBRATE_INPUT_DEVICES, MODE_RINGER_STREAMS_AFFECTED, VOLUME_VOICE, VOLUME_SYSTEM, VOLUME_RING, VOLUME_MUSIC, VOLUME_ALARM, VOLUME_NOTIFICATION, VOLUME_BLUETOOTH_SCO, "volume_voice_last_audible", "volume_system_last_audible", "volume_ring_last_audible", "volume_music_last_audible", "volume_alarm_last_audible", "volume_notification_last_audible", "volume_bluetooth_sco_last_audible", TEXT_AUTO_REPLACE, TEXT_AUTO_CAPS, TEXT_AUTO_PUNCTUATE, TEXT_SHOW_PASSWORD, "auto_time", "auto_time_zone", TIME_12_24, DATE_FORMAT, DTMF_TONE_WHEN_DIALING, DTMF_TONE_TYPE_WHEN_DIALING, HEARING_AID, TTY_MODE, SOUND_EFFECTS_ENABLED, HAPTIC_FEEDBACK_ENABLED, "power_sounds_enabled", "dock_sounds_enabled", LOCKSCREEN_SOUNDS_ENABLED, SHOW_WEB_SUGGESTIONS, NOTIFICATION_LIGHT_PULSE, SIP_CALL_OPTIONS, SIP_RECEIVE_CALLS, POINTER_SPEED, VIBRATE_WHEN_RINGING, RINGTONE, NOTIFICATION_SOUND};
        }

        public static void getMovedKeys(HashSet<String> outKeySet) {
            outKeySet.addAll(MOVED_TO_GLOBAL);
            outKeySet.addAll(MOVED_TO_SECURE_THEN_GLOBAL);
        }

        public static void getNonLegacyMovedKeys(HashSet<String> outKeySet) {
            outKeySet.addAll(MOVED_TO_GLOBAL);
        }

        public static String getString(ContentResolver resolver, String name) {
            return getStringForUser(resolver, name, UserHandle.myUserId());
        }

        public static String getStringForUser(ContentResolver resolver, String name, int userHandle) {
            if (MOVED_TO_SECURE.contains(name)) {
                Log.w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.System to android.provider.Settings.Secure, returning read-only value.");
                return Secure.getStringForUser(resolver, name, userHandle);
            } else if (MOVED_TO_GLOBAL.contains(name) || MOVED_TO_SECURE_THEN_GLOBAL.contains(name)) {
                Log.w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.System to android.provider.Settings.Global, returning read-only value.");
                return Global.getStringForUser(resolver, name, userHandle);
            } else {
                return sNameValueCache.getStringForUser(resolver, name, userHandle);
            }
        }

        public static boolean putString(ContentResolver resolver, String name, String value) {
            return putStringForUser(resolver, name, value, UserHandle.myUserId());
        }

        public static boolean putStringForUser(ContentResolver resolver, String name, String value, int userHandle) {
            if (MOVED_TO_SECURE.contains(name)) {
                Log.w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.System to android.provider.Settings.Secure, value is unchanged.");
                return false;
            } else if (MOVED_TO_GLOBAL.contains(name) || MOVED_TO_SECURE_THEN_GLOBAL.contains(name)) {
                Log.w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.System to android.provider.Settings.Global, value is unchanged.");
                return false;
            } else {
                return sNameValueCache.putStringForUser(resolver, name, value, userHandle);
            }
        }

        public static Uri getUriFor(String name) {
            if (MOVED_TO_SECURE.contains(name)) {
                Log.w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.System to android.provider.Settings.Secure, returning Secure URI.");
                return Secure.getUriFor(Secure.CONTENT_URI, name);
            } else if (MOVED_TO_GLOBAL.contains(name) || MOVED_TO_SECURE_THEN_GLOBAL.contains(name)) {
                Log.w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.System to android.provider.Settings.Global, returning read-only global URI.");
                return Global.getUriFor(Global.CONTENT_URI, name);
            } else {
                return getUriFor(CONTENT_URI, name);
            }
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            return getIntForUser(cr, name, def, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int def, int userHandle) {
            String v = getStringForUser(cr, name, userHandle);
            if (v != null) {
                try {
                    return Integer.parseInt(v);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
            return def;
        }

        public static int getInt(ContentResolver cr, String name) throws SettingNotFoundException {
            return getIntForUser(cr, name, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int userHandle) throws SettingNotFoundException {
            String v = getStringForUser(cr, name, userHandle);
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                throw new SettingNotFoundException(name);
            }
        }

        public static boolean putInt(ContentResolver cr, String name, int value) {
            return putIntForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putIntForUser(ContentResolver cr, String name, int value, int userHandle) {
            return putStringForUser(cr, name, Integer.toString(value), userHandle);
        }

        public static long getLong(ContentResolver cr, String name, long def) {
            return getLongForUser(cr, name, def, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, long def, int userHandle) {
            long value;
            long parseLong;
            String valString = getStringForUser(cr, name, userHandle);
            if (valString != null) {
                try {
                    parseLong = Long.parseLong(valString);
                } catch (NumberFormatException e) {
                    value = def;
                }
            } else {
                parseLong = def;
            }
            value = parseLong;
            return value;
        }

        public static long getLong(ContentResolver cr, String name) throws SettingNotFoundException {
            return getLongForUser(cr, name, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, int userHandle) throws SettingNotFoundException {
            String valString = getStringForUser(cr, name, userHandle);
            try {
                return Long.parseLong(valString);
            } catch (NumberFormatException e) {
                throw new SettingNotFoundException(name);
            }
        }

        public static boolean putLong(ContentResolver cr, String name, long value) {
            return putLongForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putLongForUser(ContentResolver cr, String name, long value, int userHandle) {
            return putStringForUser(cr, name, Long.toString(value), userHandle);
        }

        public static float getFloat(ContentResolver cr, String name, float def) {
            return getFloatForUser(cr, name, def, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, float def, int userHandle) {
            String v = getStringForUser(cr, name, userHandle);
            if (v != null) {
                try {
                    return Float.parseFloat(v);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
            return def;
        }

        public static float getFloat(ContentResolver cr, String name) throws SettingNotFoundException {
            return getFloatForUser(cr, name, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, int userHandle) throws SettingNotFoundException {
            String v = getStringForUser(cr, name, userHandle);
            if (v == null) {
                throw new SettingNotFoundException(name);
            }
            try {
                return Float.parseFloat(v);
            } catch (NumberFormatException e) {
                throw new SettingNotFoundException(name);
            }
        }

        public static boolean putFloat(ContentResolver cr, String name, float value) {
            return putFloatForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putFloatForUser(ContentResolver cr, String name, float value, int userHandle) {
            return putStringForUser(cr, name, Float.toString(value), userHandle);
        }

        public static void getConfiguration(ContentResolver cr, Configuration outConfig) {
            getConfigurationForUser(cr, outConfig, UserHandle.myUserId());
        }

        public static void getConfigurationForUser(ContentResolver cr, Configuration outConfig, int userHandle) {
            outConfig.fontScale = getFloatForUser(cr, FONT_SCALE, outConfig.fontScale, userHandle);
            if (outConfig.fontScale < 0.0f) {
                outConfig.fontScale = 1.0f;
            }
        }

        public static void clearConfiguration(Configuration inoutConfig) {
            inoutConfig.fontScale = 0.0f;
        }

        public static boolean putConfiguration(ContentResolver cr, Configuration config) {
            return putConfigurationForUser(cr, config, UserHandle.myUserId());
        }

        public static boolean putConfigurationForUser(ContentResolver cr, Configuration config, int userHandle) {
            return putFloatForUser(cr, FONT_SCALE, config.fontScale, userHandle);
        }

        public static boolean hasInterestingConfigurationChanges(int changes) {
            return (changes & 1073741824) != 0;
        }

        @Deprecated
        public static boolean getShowGTalkServiceStatus(ContentResolver cr) {
            return getShowGTalkServiceStatusForUser(cr, UserHandle.myUserId());
        }

        public static boolean getShowGTalkServiceStatusForUser(ContentResolver cr, int userHandle) {
            return getIntForUser(cr, SHOW_GTALK_SERVICE_STATUS, 0, userHandle) != 0;
        }

        @Deprecated
        public static void setShowGTalkServiceStatus(ContentResolver cr, boolean flag) {
            setShowGTalkServiceStatusForUser(cr, flag, UserHandle.myUserId());
        }

        @Deprecated
        public static void setShowGTalkServiceStatusForUser(ContentResolver cr, boolean flag, int userHandle) {
            putIntForUser(cr, SHOW_GTALK_SERVICE_STATUS, flag ? 1 : 0, userHandle);
        }
    }

    /* loaded from: Settings$Secure.class */
    public static final class Secure extends NameValueTable {
        private static boolean sIsSystemProcess;
        private static final HashSet<String> MOVED_TO_GLOBAL;
        @Deprecated
        public static final String DEVELOPMENT_SETTINGS_ENABLED = "development_settings_enabled";
        @Deprecated
        public static final String BUGREPORT_IN_POWER_MENU = "bugreport_in_power_menu";
        @Deprecated
        public static final String ADB_ENABLED = "adb_enabled";
        public static final String ALLOW_MOCK_LOCATION = "mock_location";
        public static final String ANDROID_ID = "android_id";
        @Deprecated
        public static final String BLUETOOTH_ON = "bluetooth_on";
        @Deprecated
        public static final String DATA_ROAMING = "data_roaming";
        public static final String DEFAULT_INPUT_METHOD = "default_input_method";
        public static final String SELECTED_INPUT_METHOD_SUBTYPE = "selected_input_method_subtype";
        public static final String INPUT_METHODS_SUBTYPE_HISTORY = "input_methods_subtype_history";
        public static final String INPUT_METHOD_SELECTOR_VISIBILITY = "input_method_selector_visibility";
        public static final String BLUETOOTH_HCI_LOG = "bluetooth_hci_log";
        @Deprecated
        public static final String DEVICE_PROVISIONED = "device_provisioned";
        public static final String USER_SETUP_COMPLETE = "user_setup_complete";
        public static final String ENABLED_INPUT_METHODS = "enabled_input_methods";
        public static final String DISABLED_SYSTEM_INPUT_METHODS = "disabled_system_input_methods";
        @Deprecated
        public static final String HTTP_PROXY = "http_proxy";
        @Deprecated
        public static final String INSTALL_NON_MARKET_APPS = "install_non_market_apps";
        @Deprecated
        public static final String LOCATION_PROVIDERS_ALLOWED = "location_providers_allowed";
        public static final String LOCATION_MODE = "location_mode";
        public static final int LOCATION_MODE_OFF = 0;
        public static final int LOCATION_MODE_SENSORS_ONLY = 1;
        public static final int LOCATION_MODE_BATTERY_SAVING = 2;
        public static final int LOCATION_MODE_HIGH_ACCURACY = 3;
        public static final String LOCK_BIOMETRIC_WEAK_FLAGS = "lock_biometric_weak_flags";
        public static final String LOCK_PATTERN_ENABLED = "lock_pattern_autolock";
        public static final String LOCK_PATTERN_VISIBLE = "lock_pattern_visible_pattern";
        @Deprecated
        public static final String LOCK_PATTERN_TACTILE_FEEDBACK_ENABLED = "lock_pattern_tactile_feedback_enabled";
        public static final String LOCK_SCREEN_LOCK_AFTER_TIMEOUT = "lock_screen_lock_after_timeout";
        public static final String LOCK_SCREEN_OWNER_INFO = "lock_screen_owner_info";
        public static final String LOCK_SCREEN_APPWIDGET_IDS = "lock_screen_appwidget_ids";
        public static final String LOCK_SCREEN_FALLBACK_APPWIDGET_ID = "lock_screen_fallback_appwidget_id";
        public static final String LOCK_SCREEN_STICKY_APPWIDGET = "lock_screen_sticky_appwidget";
        public static final String LOCK_SCREEN_OWNER_INFO_ENABLED = "lock_screen_owner_info_enabled";
        @Deprecated
        public static final String LOGGING_ID = "logging_id";
        @Deprecated
        public static final String NETWORK_PREFERENCE = "network_preference";
        public static final String PARENTAL_CONTROL_ENABLED = "parental_control_enabled";
        public static final String PARENTAL_CONTROL_LAST_UPDATE = "parental_control_last_update";
        public static final String PARENTAL_CONTROL_REDIRECT_URL = "parental_control_redirect_url";
        public static final String SETTINGS_CLASSNAME = "settings_classname";
        @Deprecated
        public static final String USB_MASS_STORAGE_ENABLED = "usb_mass_storage_enabled";
        @Deprecated
        public static final String USE_GOOGLE_MAIL = "use_google_mail";
        public static final String ACCESSIBILITY_ENABLED = "accessibility_enabled";
        public static final String TOUCH_EXPLORATION_ENABLED = "touch_exploration_enabled";
        public static final String ENABLED_ACCESSIBILITY_SERVICES = "enabled_accessibility_services";
        public static final String TOUCH_EXPLORATION_GRANTED_ACCESSIBILITY_SERVICES = "touch_exploration_granted_accessibility_services";
        public static final String ACCESSIBILITY_SPEAK_PASSWORD = "speak_password";
        public static final String ACCESSIBILITY_SCRIPT_INJECTION = "accessibility_script_injection";
        public static final String ACCESSIBILITY_SCREEN_READER_URL = "accessibility_script_injection_url";
        public static final String ACCESSIBILITY_WEB_CONTENT_KEY_BINDINGS = "accessibility_web_content_key_bindings";
        public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED = "accessibility_display_magnification_enabled";
        public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_SCALE = "accessibility_display_magnification_scale";
        public static final String ACCESSIBILITY_DISPLAY_MAGNIFICATION_AUTO_UPDATE = "accessibility_display_magnification_auto_update";
        public static final String ACCESSIBILITY_CAPTIONING_ENABLED = "accessibility_captioning_enabled";
        public static final String ACCESSIBILITY_CAPTIONING_LOCALE = "accessibility_captioning_locale";
        public static final String ACCESSIBILITY_CAPTIONING_PRESET = "accessibility_captioning_preset";
        public static final String ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR = "accessibility_captioning_background_color";
        public static final String ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR = "accessibility_captioning_foreground_color";
        public static final String ACCESSIBILITY_CAPTIONING_EDGE_TYPE = "accessibility_captioning_edge_type";
        public static final String ACCESSIBILITY_CAPTIONING_EDGE_COLOR = "accessibility_captioning_edge_color";
        public static final String ACCESSIBILITY_CAPTIONING_TYPEFACE = "accessibility_captioning_typeface";
        public static final String ACCESSIBILITY_CAPTIONING_FONT_SCALE = "accessibility_captioning_font_scale";
        public static final String LONG_PRESS_TIMEOUT = "long_press_timeout";
        public static final String ENABLED_PRINT_SERVICES = "enabled_print_services";
        public static final String ENABLED_ON_FIRST_BOOT_SYSTEM_PRINT_SERVICES = "enabled_on_first_boot_system_print_services";
        @Deprecated
        public static final String TTS_USE_DEFAULTS = "tts_use_defaults";
        public static final String TTS_DEFAULT_RATE = "tts_default_rate";
        public static final String TTS_DEFAULT_PITCH = "tts_default_pitch";
        public static final String TTS_DEFAULT_SYNTH = "tts_default_synth";
        @Deprecated
        public static final String TTS_DEFAULT_LANG = "tts_default_lang";
        @Deprecated
        public static final String TTS_DEFAULT_COUNTRY = "tts_default_country";
        @Deprecated
        public static final String TTS_DEFAULT_VARIANT = "tts_default_variant";
        public static final String TTS_DEFAULT_LOCALE = "tts_default_locale";
        public static final String TTS_ENABLED_PLUGINS = "tts_enabled_plugins";
        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wifi_networks_available_notification_on";
        @Deprecated
        public static final String WIFI_NETWORKS_AVAILABLE_REPEAT_DELAY = "wifi_networks_available_repeat_delay";
        @Deprecated
        public static final String WIFI_NUM_OPEN_NETWORKS_KEPT = "wifi_num_open_networks_kept";
        @Deprecated
        public static final String WIFI_ON = "wifi_on";
        @Deprecated
        public static final String WIFI_WATCHDOG_ACCEPTABLE_PACKET_LOSS_PERCENTAGE = "wifi_watchdog_acceptable_packet_loss_percentage";
        @Deprecated
        public static final String WIFI_WATCHDOG_AP_COUNT = "wifi_watchdog_ap_count";
        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_DELAY_MS = "wifi_watchdog_background_check_delay_ms";
        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_ENABLED = "wifi_watchdog_background_check_enabled";
        @Deprecated
        public static final String WIFI_WATCHDOG_BACKGROUND_CHECK_TIMEOUT_MS = "wifi_watchdog_background_check_timeout_ms";
        @Deprecated
        public static final String WIFI_WATCHDOG_INITIAL_IGNORED_PING_COUNT = "wifi_watchdog_initial_ignored_ping_count";
        @Deprecated
        public static final String WIFI_WATCHDOG_MAX_AP_CHECKS = "wifi_watchdog_max_ap_checks";
        @Deprecated
        public static final String WIFI_WATCHDOG_ON = "wifi_watchdog_on";
        @Deprecated
        public static final String WIFI_WATCHDOG_WATCH_LIST = "wifi_watchdog_watch_list";
        @Deprecated
        public static final String WIFI_WATCHDOG_PING_COUNT = "wifi_watchdog_ping_count";
        @Deprecated
        public static final String WIFI_WATCHDOG_PING_DELAY_MS = "wifi_watchdog_ping_delay_ms";
        @Deprecated
        public static final String WIFI_WATCHDOG_PING_TIMEOUT_MS = "wifi_watchdog_ping_timeout_ms";
        @Deprecated
        public static final String WIFI_MAX_DHCP_RETRY_COUNT = "wifi_max_dhcp_retry_count";
        @Deprecated
        public static final String WIFI_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wifi_mobile_data_transition_wakelock_timeout_ms";
        @Deprecated
        public static final String BACKGROUND_DATA = "background_data";
        public static final String ALLOWED_GEOLOCATION_ORIGINS = "allowed_geolocation_origins";
        public static final String PREFERRED_TTY_MODE = "preferred_tty_mode";
        public static final String ENHANCED_VOICE_PRIVACY_ENABLED = "enhanced_voice_privacy_enabled";
        public static final String TTY_MODE_ENABLED = "tty_mode_enabled";
        public static final String BACKUP_ENABLED = "backup_enabled";
        public static final String BACKUP_AUTO_RESTORE = "backup_auto_restore";
        public static final String BACKUP_PROVISIONED = "backup_provisioned";
        public static final String BACKUP_TRANSPORT = "backup_transport";
        public static final String LAST_SETUP_SHOWN = "last_setup_shown";
        @Deprecated
        public static final String WIFI_IDLE_MS = "wifi_idle_ms";
        public static final String SEARCH_GLOBAL_SEARCH_ACTIVITY = "search_global_search_activity";
        public static final String SEARCH_NUM_PROMOTED_SOURCES = "search_num_promoted_sources";
        public static final String SEARCH_MAX_RESULTS_TO_DISPLAY = "search_max_results_to_display";
        public static final String SEARCH_MAX_RESULTS_PER_SOURCE = "search_max_results_per_source";
        public static final String SEARCH_WEB_RESULTS_OVERRIDE_LIMIT = "search_web_results_override_limit";
        public static final String SEARCH_PROMOTED_SOURCE_DEADLINE_MILLIS = "search_promoted_source_deadline_millis";
        public static final String SEARCH_SOURCE_TIMEOUT_MILLIS = "search_source_timeout_millis";
        public static final String SEARCH_PREFILL_MILLIS = "search_prefill_millis";
        public static final String SEARCH_MAX_STAT_AGE_MILLIS = "search_max_stat_age_millis";
        public static final String SEARCH_MAX_SOURCE_EVENT_AGE_MILLIS = "search_max_source_event_age_millis";
        public static final String SEARCH_MIN_IMPRESSIONS_FOR_SOURCE_RANKING = "search_min_impressions_for_source_ranking";
        public static final String SEARCH_MIN_CLICKS_FOR_SOURCE_RANKING = "search_min_clicks_for_source_ranking";
        public static final String SEARCH_MAX_SHORTCUTS_RETURNED = "search_max_shortcuts_returned";
        public static final String SEARCH_QUERY_THREAD_CORE_POOL_SIZE = "search_query_thread_core_pool_size";
        public static final String SEARCH_QUERY_THREAD_MAX_POOL_SIZE = "search_query_thread_max_pool_size";
        public static final String SEARCH_SHORTCUT_REFRESH_CORE_POOL_SIZE = "search_shortcut_refresh_core_pool_size";
        public static final String SEARCH_SHORTCUT_REFRESH_MAX_POOL_SIZE = "search_shortcut_refresh_max_pool_size";
        public static final String SEARCH_THREAD_KEEPALIVE_SECONDS = "search_thread_keepalive_seconds";
        public static final String SEARCH_PER_SOURCE_CONCURRENT_QUERY_LIMIT = "search_per_source_concurrent_query_limit";
        public static final String MOUNT_PLAY_NOTIFICATION_SND = "mount_play_not_snd";
        public static final String MOUNT_UMS_AUTOSTART = "mount_ums_autostart";
        public static final String MOUNT_UMS_PROMPT = "mount_ums_prompt";
        public static final String MOUNT_UMS_NOTIFY_ENABLED = "mount_ums_notify_enabled";
        public static final String ANR_SHOW_BACKGROUND = "anr_show_background";
        public static final String VOICE_RECOGNITION_SERVICE = "voice_recognition_service";
        public static final String PACKAGE_VERIFIER_USER_CONSENT = "package_verifier_user_consent";
        public static final String SELECTED_SPELL_CHECKER = "selected_spell_checker";
        public static final String SELECTED_SPELL_CHECKER_SUBTYPE = "selected_spell_checker_subtype";
        public static final String SPELL_CHECKER_ENABLED = "spell_checker_enabled";
        public static final String INCALL_POWER_BUTTON_BEHAVIOR = "incall_power_button_behavior";
        public static final int INCALL_POWER_BUTTON_BEHAVIOR_SCREEN_OFF = 1;
        public static final int INCALL_POWER_BUTTON_BEHAVIOR_HANGUP = 2;
        public static final int INCALL_POWER_BUTTON_BEHAVIOR_DEFAULT = 1;
        public static final String UI_NIGHT_MODE = "ui_night_mode";
        public static final String SCREENSAVER_ENABLED = "screensaver_enabled";
        public static final String SCREENSAVER_COMPONENTS = "screensaver_components";
        public static final String SCREENSAVER_ACTIVATE_ON_DOCK = "screensaver_activate_on_dock";
        public static final String SCREENSAVER_ACTIVATE_ON_SLEEP = "screensaver_activate_on_sleep";
        public static final String SCREENSAVER_DEFAULT_COMPONENT = "screensaver_default_component";
        public static final String NFC_PAYMENT_DEFAULT_COMPONENT = "nfc_payment_default_component";
        public static final String SMS_DEFAULT_APPLICATION = "sms_default_application";
        public static final String ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners";
        public static final String BAR_SERVICE_COMPONENT = "bar_service_component";
        public static final String IMMERSIVE_MODE_CONFIRMATIONS = "immersive_mode_confirmations";
        public static final String PRINT_SERVICE_SEARCH_URI = "print_service_search_uri";
        public static final String PAYMENT_SERVICE_SEARCH_URI = "payment_service_search_uri";
        public static final String[] SETTINGS_TO_BACKUP;
        public static final Uri CONTENT_URI = Uri.parse("content://settings/secure");
        public static final String SYS_PROP_SETTING_VERSION = "sys.settings_secure_version";
        private static final NameValueCache sNameValueCache = new NameValueCache(SYS_PROP_SETTING_VERSION, CONTENT_URI, Settings.CALL_METHOD_GET_SECURE, Settings.CALL_METHOD_PUT_SECURE);
        private static ILockSettings sLockSettings = null;
        private static final HashSet<String> MOVED_TO_LOCK_SETTINGS = new HashSet<>(3);

        static {
            MOVED_TO_LOCK_SETTINGS.add("lock_pattern_autolock");
            MOVED_TO_LOCK_SETTINGS.add("lock_pattern_visible_pattern");
            MOVED_TO_LOCK_SETTINGS.add("lock_pattern_tactile_feedback_enabled");
            MOVED_TO_GLOBAL = new HashSet<>();
            MOVED_TO_GLOBAL.add("adb_enabled");
            MOVED_TO_GLOBAL.add(Global.ASSISTED_GPS_ENABLED);
            MOVED_TO_GLOBAL.add("bluetooth_on");
            MOVED_TO_GLOBAL.add("bugreport_in_power_menu");
            MOVED_TO_GLOBAL.add(Global.CDMA_CELL_BROADCAST_SMS);
            MOVED_TO_GLOBAL.add(Global.CDMA_ROAMING_MODE);
            MOVED_TO_GLOBAL.add(Global.CDMA_SUBSCRIPTION_MODE);
            MOVED_TO_GLOBAL.add(Global.DATA_ACTIVITY_TIMEOUT_MOBILE);
            MOVED_TO_GLOBAL.add(Global.DATA_ACTIVITY_TIMEOUT_WIFI);
            MOVED_TO_GLOBAL.add("data_roaming");
            MOVED_TO_GLOBAL.add("development_settings_enabled");
            MOVED_TO_GLOBAL.add("device_provisioned");
            MOVED_TO_GLOBAL.add(Global.DISPLAY_DENSITY_FORCED);
            MOVED_TO_GLOBAL.add(Global.DISPLAY_SIZE_FORCED);
            MOVED_TO_GLOBAL.add(Global.DOWNLOAD_MAX_BYTES_OVER_MOBILE);
            MOVED_TO_GLOBAL.add(Global.DOWNLOAD_RECOMMENDED_MAX_BYTES_OVER_MOBILE);
            MOVED_TO_GLOBAL.add("install_non_market_apps");
            MOVED_TO_GLOBAL.add(Global.MOBILE_DATA);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_DEV_BUCKET_DURATION);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_DEV_DELETE_AGE);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_DEV_PERSIST_BYTES);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_DEV_ROTATE_AGE);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_ENABLED);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_GLOBAL_ALERT_BYTES);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_POLL_INTERVAL);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_REPORT_XT_OVER_DEV);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_SAMPLE_ENABLED);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_TIME_CACHE_MAX_AGE);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_UID_BUCKET_DURATION);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_UID_DELETE_AGE);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_UID_PERSIST_BYTES);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_UID_ROTATE_AGE);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_UID_TAG_BUCKET_DURATION);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_UID_TAG_DELETE_AGE);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_UID_TAG_PERSIST_BYTES);
            MOVED_TO_GLOBAL.add(Global.NETSTATS_UID_TAG_ROTATE_AGE);
            MOVED_TO_GLOBAL.add("network_preference");
            MOVED_TO_GLOBAL.add(Global.NITZ_UPDATE_DIFF);
            MOVED_TO_GLOBAL.add(Global.NITZ_UPDATE_SPACING);
            MOVED_TO_GLOBAL.add(Global.NTP_SERVER);
            MOVED_TO_GLOBAL.add(Global.NTP_TIMEOUT);
            MOVED_TO_GLOBAL.add(Global.PDP_WATCHDOG_ERROR_POLL_COUNT);
            MOVED_TO_GLOBAL.add(Global.PDP_WATCHDOG_LONG_POLL_INTERVAL_MS);
            MOVED_TO_GLOBAL.add(Global.PDP_WATCHDOG_MAX_PDP_RESET_FAIL_COUNT);
            MOVED_TO_GLOBAL.add(Global.PDP_WATCHDOG_POLL_INTERVAL_MS);
            MOVED_TO_GLOBAL.add(Global.PDP_WATCHDOG_TRIGGER_PACKET_COUNT);
            MOVED_TO_GLOBAL.add(Global.SAMPLING_PROFILER_MS);
            MOVED_TO_GLOBAL.add(Global.SETUP_PREPAID_DATA_SERVICE_URL);
            MOVED_TO_GLOBAL.add(Global.SETUP_PREPAID_DETECTION_REDIR_HOST);
            MOVED_TO_GLOBAL.add(Global.SETUP_PREPAID_DETECTION_TARGET_URL);
            MOVED_TO_GLOBAL.add(Global.TETHER_DUN_APN);
            MOVED_TO_GLOBAL.add(Global.TETHER_DUN_REQUIRED);
            MOVED_TO_GLOBAL.add(Global.TETHER_SUPPORTED);
            MOVED_TO_GLOBAL.add("usb_mass_storage_enabled");
            MOVED_TO_GLOBAL.add("use_google_mail");
            MOVED_TO_GLOBAL.add(Global.WEB_AUTOFILL_QUERY_URL);
            MOVED_TO_GLOBAL.add(Global.WIFI_COUNTRY_CODE);
            MOVED_TO_GLOBAL.add(Global.WIFI_FRAMEWORK_SCAN_INTERVAL_MS);
            MOVED_TO_GLOBAL.add(Global.WIFI_FREQUENCY_BAND);
            MOVED_TO_GLOBAL.add("wifi_idle_ms");
            MOVED_TO_GLOBAL.add("wifi_max_dhcp_retry_count");
            MOVED_TO_GLOBAL.add("wifi_mobile_data_transition_wakelock_timeout_ms");
            MOVED_TO_GLOBAL.add("wifi_networks_available_notification_on");
            MOVED_TO_GLOBAL.add("wifi_networks_available_repeat_delay");
            MOVED_TO_GLOBAL.add("wifi_num_open_networks_kept");
            MOVED_TO_GLOBAL.add("wifi_on");
            MOVED_TO_GLOBAL.add(Global.WIFI_P2P_DEVICE_NAME);
            MOVED_TO_GLOBAL.add(Global.WIFI_SAVED_STATE);
            MOVED_TO_GLOBAL.add(Global.WIFI_SUPPLICANT_SCAN_INTERVAL_MS);
            MOVED_TO_GLOBAL.add(Global.WIFI_SUSPEND_OPTIMIZATIONS_ENABLED);
            MOVED_TO_GLOBAL.add("wifi_watchdog_on");
            MOVED_TO_GLOBAL.add(Global.WIFI_WATCHDOG_POOR_NETWORK_TEST_ENABLED);
            MOVED_TO_GLOBAL.add(Global.WIMAX_NETWORKS_AVAILABLE_NOTIFICATION_ON);
            MOVED_TO_GLOBAL.add(Global.PACKAGE_VERIFIER_ENABLE);
            MOVED_TO_GLOBAL.add(Global.PACKAGE_VERIFIER_TIMEOUT);
            MOVED_TO_GLOBAL.add(Global.PACKAGE_VERIFIER_DEFAULT_RESPONSE);
            MOVED_TO_GLOBAL.add(Global.DATA_STALL_ALARM_NON_AGGRESSIVE_DELAY_IN_MS);
            MOVED_TO_GLOBAL.add(Global.DATA_STALL_ALARM_AGGRESSIVE_DELAY_IN_MS);
            MOVED_TO_GLOBAL.add(Global.GPRS_REGISTER_CHECK_PERIOD_MS);
            MOVED_TO_GLOBAL.add(Global.WTF_IS_FATAL);
            MOVED_TO_GLOBAL.add(Global.BATTERY_DISCHARGE_DURATION_THRESHOLD);
            MOVED_TO_GLOBAL.add(Global.BATTERY_DISCHARGE_THRESHOLD);
            MOVED_TO_GLOBAL.add(Global.SEND_ACTION_APP_ERROR);
            MOVED_TO_GLOBAL.add(Global.DROPBOX_AGE_SECONDS);
            MOVED_TO_GLOBAL.add(Global.DROPBOX_MAX_FILES);
            MOVED_TO_GLOBAL.add(Global.DROPBOX_QUOTA_KB);
            MOVED_TO_GLOBAL.add(Global.DROPBOX_QUOTA_PERCENT);
            MOVED_TO_GLOBAL.add(Global.DROPBOX_RESERVE_PERCENT);
            MOVED_TO_GLOBAL.add(Global.DROPBOX_TAG_PREFIX);
            MOVED_TO_GLOBAL.add(Global.ERROR_LOGCAT_PREFIX);
            MOVED_TO_GLOBAL.add(Global.SYS_FREE_STORAGE_LOG_INTERVAL);
            MOVED_TO_GLOBAL.add(Global.DISK_FREE_CHANGE_REPORTING_THRESHOLD);
            MOVED_TO_GLOBAL.add(Global.SYS_STORAGE_THRESHOLD_PERCENTAGE);
            MOVED_TO_GLOBAL.add(Global.SYS_STORAGE_THRESHOLD_MAX_BYTES);
            MOVED_TO_GLOBAL.add(Global.SYS_STORAGE_FULL_THRESHOLD_BYTES);
            MOVED_TO_GLOBAL.add(Global.SYNC_MAX_RETRY_DELAY_IN_SECONDS);
            MOVED_TO_GLOBAL.add(Global.CONNECTIVITY_CHANGE_DELAY);
            MOVED_TO_GLOBAL.add(Global.CAPTIVE_PORTAL_DETECTION_ENABLED);
            MOVED_TO_GLOBAL.add(Global.CAPTIVE_PORTAL_SERVER);
            MOVED_TO_GLOBAL.add(Global.NSD_ON);
            MOVED_TO_GLOBAL.add(Global.SET_INSTALL_LOCATION);
            MOVED_TO_GLOBAL.add(Global.DEFAULT_INSTALL_LOCATION);
            MOVED_TO_GLOBAL.add(Global.INET_CONDITION_DEBOUNCE_UP_DELAY);
            MOVED_TO_GLOBAL.add(Global.INET_CONDITION_DEBOUNCE_DOWN_DELAY);
            MOVED_TO_GLOBAL.add(Global.READ_EXTERNAL_STORAGE_ENFORCED_DEFAULT);
            MOVED_TO_GLOBAL.add("http_proxy");
            MOVED_TO_GLOBAL.add(Global.GLOBAL_HTTP_PROXY_HOST);
            MOVED_TO_GLOBAL.add(Global.GLOBAL_HTTP_PROXY_PORT);
            MOVED_TO_GLOBAL.add(Global.GLOBAL_HTTP_PROXY_EXCLUSION_LIST);
            MOVED_TO_GLOBAL.add(Global.SET_GLOBAL_HTTP_PROXY);
            MOVED_TO_GLOBAL.add(Global.DEFAULT_DNS_SERVER);
            MOVED_TO_GLOBAL.add(Global.PREFERRED_NETWORK_MODE);
            SETTINGS_TO_BACKUP = new String[]{"bugreport_in_power_menu", ALLOW_MOCK_LOCATION, "parental_control_enabled", "parental_control_redirect_url", "usb_mass_storage_enabled", ACCESSIBILITY_DISPLAY_MAGNIFICATION_ENABLED, ACCESSIBILITY_DISPLAY_MAGNIFICATION_SCALE, ACCESSIBILITY_DISPLAY_MAGNIFICATION_AUTO_UPDATE, ACCESSIBILITY_SCRIPT_INJECTION, BACKUP_AUTO_RESTORE, ENABLED_ACCESSIBILITY_SERVICES, TOUCH_EXPLORATION_GRANTED_ACCESSIBILITY_SERVICES, TOUCH_EXPLORATION_ENABLED, ACCESSIBILITY_ENABLED, ACCESSIBILITY_SPEAK_PASSWORD, ACCESSIBILITY_CAPTIONING_ENABLED, ACCESSIBILITY_CAPTIONING_LOCALE, ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR, ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR, ACCESSIBILITY_CAPTIONING_EDGE_TYPE, ACCESSIBILITY_CAPTIONING_EDGE_COLOR, ACCESSIBILITY_CAPTIONING_TYPEFACE, ACCESSIBILITY_CAPTIONING_FONT_SCALE, TTS_USE_DEFAULTS, TTS_DEFAULT_RATE, TTS_DEFAULT_PITCH, TTS_DEFAULT_SYNTH, TTS_DEFAULT_LANG, TTS_DEFAULT_COUNTRY, TTS_ENABLED_PLUGINS, TTS_DEFAULT_LOCALE, "wifi_networks_available_notification_on", "wifi_networks_available_repeat_delay", "wifi_num_open_networks_kept", MOUNT_PLAY_NOTIFICATION_SND, MOUNT_UMS_AUTOSTART, MOUNT_UMS_PROMPT, MOUNT_UMS_NOTIFY_ENABLED, UI_NIGHT_MODE};
        }

        public static void getMovedKeys(HashSet<String> outKeySet) {
            outKeySet.addAll(MOVED_TO_GLOBAL);
        }

        public static String getString(ContentResolver resolver, String name) {
            return getStringForUser(resolver, name, UserHandle.myUserId());
        }

        public static String getStringForUser(ContentResolver resolver, String name, int userHandle) {
            if (MOVED_TO_GLOBAL.contains(name)) {
                Log.w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.Secure to android.provider.Settings.Global.");
                return Global.getStringForUser(resolver, name, userHandle);
            }
            if (MOVED_TO_LOCK_SETTINGS.contains(name)) {
                synchronized (Secure.class) {
                    if (sLockSettings == null) {
                        sLockSettings = ILockSettings.Stub.asInterface(ServiceManager.getService("lock_settings"));
                        sIsSystemProcess = Process.myUid() == 1000;
                    }
                }
                if (sLockSettings != null && !sIsSystemProcess) {
                    try {
                        return sLockSettings.getString(name, "0", userHandle);
                    } catch (RemoteException e) {
                    }
                }
            }
            return sNameValueCache.getStringForUser(resolver, name, userHandle);
        }

        public static boolean putString(ContentResolver resolver, String name, String value) {
            return putStringForUser(resolver, name, value, UserHandle.myUserId());
        }

        public static boolean putStringForUser(ContentResolver resolver, String name, String value, int userHandle) {
            if (MOVED_TO_GLOBAL.contains(name)) {
                Log.w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.System to android.provider.Settings.Global");
                return Global.putStringForUser(resolver, name, value, userHandle);
            }
            return sNameValueCache.putStringForUser(resolver, name, value, userHandle);
        }

        public static Uri getUriFor(String name) {
            if (MOVED_TO_GLOBAL.contains(name)) {
                Log.w(Settings.TAG, "Setting " + name + " has moved from android.provider.Settings.Secure to android.provider.Settings.Global, returning global URI.");
                return Global.getUriFor(Global.CONTENT_URI, name);
            }
            return getUriFor(CONTENT_URI, name);
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            return getIntForUser(cr, name, def, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int def, int userHandle) {
            if (LOCATION_MODE.equals(name)) {
                return getLocationModeForUser(cr, userHandle);
            }
            String v = getStringForUser(cr, name, userHandle);
            if (v != null) {
                try {
                    return Integer.parseInt(v);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
            return def;
        }

        public static int getInt(ContentResolver cr, String name) throws SettingNotFoundException {
            return getIntForUser(cr, name, UserHandle.myUserId());
        }

        public static int getIntForUser(ContentResolver cr, String name, int userHandle) throws SettingNotFoundException {
            if (LOCATION_MODE.equals(name)) {
                return getLocationModeForUser(cr, userHandle);
            }
            String v = getStringForUser(cr, name, userHandle);
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                throw new SettingNotFoundException(name);
            }
        }

        public static boolean putInt(ContentResolver cr, String name, int value) {
            return putIntForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putIntForUser(ContentResolver cr, String name, int value, int userHandle) {
            if (LOCATION_MODE.equals(name)) {
                return setLocationModeForUser(cr, value, userHandle);
            }
            return putStringForUser(cr, name, Integer.toString(value), userHandle);
        }

        public static long getLong(ContentResolver cr, String name, long def) {
            return getLongForUser(cr, name, def, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, long def, int userHandle) {
            long value;
            long parseLong;
            String valString = getStringForUser(cr, name, userHandle);
            if (valString != null) {
                try {
                    parseLong = Long.parseLong(valString);
                } catch (NumberFormatException e) {
                    value = def;
                }
            } else {
                parseLong = def;
            }
            value = parseLong;
            return value;
        }

        public static long getLong(ContentResolver cr, String name) throws SettingNotFoundException {
            return getLongForUser(cr, name, UserHandle.myUserId());
        }

        public static long getLongForUser(ContentResolver cr, String name, int userHandle) throws SettingNotFoundException {
            String valString = getStringForUser(cr, name, userHandle);
            try {
                return Long.parseLong(valString);
            } catch (NumberFormatException e) {
                throw new SettingNotFoundException(name);
            }
        }

        public static boolean putLong(ContentResolver cr, String name, long value) {
            return putLongForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putLongForUser(ContentResolver cr, String name, long value, int userHandle) {
            return putStringForUser(cr, name, Long.toString(value), userHandle);
        }

        public static float getFloat(ContentResolver cr, String name, float def) {
            return getFloatForUser(cr, name, def, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, float def, int userHandle) {
            String v = getStringForUser(cr, name, userHandle);
            if (v != null) {
                try {
                    return Float.parseFloat(v);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
            return def;
        }

        public static float getFloat(ContentResolver cr, String name) throws SettingNotFoundException {
            return getFloatForUser(cr, name, UserHandle.myUserId());
        }

        public static float getFloatForUser(ContentResolver cr, String name, int userHandle) throws SettingNotFoundException {
            String v = getStringForUser(cr, name, userHandle);
            if (v == null) {
                throw new SettingNotFoundException(name);
            }
            try {
                return Float.parseFloat(v);
            } catch (NumberFormatException e) {
                throw new SettingNotFoundException(name);
            }
        }

        public static boolean putFloat(ContentResolver cr, String name, float value) {
            return putFloatForUser(cr, name, value, UserHandle.myUserId());
        }

        public static boolean putFloatForUser(ContentResolver cr, String name, float value, int userHandle) {
            return putStringForUser(cr, name, Float.toString(value), userHandle);
        }

        @Deprecated
        public static final boolean isLocationProviderEnabled(ContentResolver cr, String provider) {
            return isLocationProviderEnabledForUser(cr, provider, UserHandle.myUserId());
        }

        @Deprecated
        public static final boolean isLocationProviderEnabledForUser(ContentResolver cr, String provider, int userId) {
            String allowedProviders = getStringForUser(cr, "location_providers_allowed", userId);
            return TextUtils.delimitedStringContains(allowedProviders, ',', provider);
        }

        @Deprecated
        public static final void setLocationProviderEnabled(ContentResolver cr, String provider, boolean enabled) {
            setLocationProviderEnabledForUser(cr, provider, enabled, UserHandle.myUserId());
        }

        @Deprecated
        public static final boolean setLocationProviderEnabledForUser(ContentResolver cr, String provider, boolean enabled, int userId) {
            String provider2;
            boolean putStringForUser;
            synchronized (Settings.mLocationSettingsLock) {
                if (enabled) {
                    provider2 = "+" + provider;
                } else {
                    provider2 = "-" + provider;
                }
                putStringForUser = putStringForUser(cr, "location_providers_allowed", provider2, userId);
            }
            return putStringForUser;
        }

        private static final boolean setLocationModeForUser(ContentResolver cr, int mode, int userId) {
            boolean z;
            synchronized (Settings.mLocationSettingsLock) {
                boolean gps = false;
                boolean network = false;
                switch (mode) {
                    case 0:
                        break;
                    case 1:
                        gps = true;
                        break;
                    case 2:
                        network = true;
                        break;
                    case 3:
                        gps = true;
                        network = true;
                        break;
                    default:
                        throw new IllegalArgumentException("Invalid location mode: " + mode);
                }
                boolean gpsSuccess = setLocationProviderEnabledForUser(cr, LocationManager.GPS_PROVIDER, gps, userId);
                boolean nlpSuccess = setLocationProviderEnabledForUser(cr, LocationManager.NETWORK_PROVIDER, network, userId);
                z = gpsSuccess && nlpSuccess;
            }
            return z;
        }

        private static final int getLocationModeForUser(ContentResolver cr, int userId) {
            synchronized (Settings.mLocationSettingsLock) {
                boolean gpsEnabled = isLocationProviderEnabledForUser(cr, LocationManager.GPS_PROVIDER, userId);
                boolean networkEnabled = isLocationProviderEnabledForUser(cr, LocationManager.NETWORK_PROVIDER, userId);
                if (gpsEnabled && networkEnabled) {
                    return 3;
                }
                if (gpsEnabled) {
                    return 1;
                }
                if (networkEnabled) {
                    return 2;
                }
                return 0;
            }
        }
    }

    /* loaded from: Settings$Global.class */
    public static final class Global extends NameValueTable {
        public static final String AIRPLANE_MODE_ON = "airplane_mode_on";
        public static final String RADIO_BLUETOOTH = "bluetooth";
        public static final String RADIO_WIFI = "wifi";
        public static final String RADIO_WIMAX = "wimax";
        public static final String RADIO_CELL = "cell";
        public static final String RADIO_NFC = "nfc";
        public static final String AIRPLANE_MODE_RADIOS = "airplane_mode_radios";
        public static final String AIRPLANE_MODE_TOGGLEABLE_RADIOS = "airplane_mode_toggleable_radios";
        public static final String WIFI_SLEEP_POLICY = "wifi_sleep_policy";
        public static final int WIFI_SLEEP_POLICY_DEFAULT = 0;
        public static final int WIFI_SLEEP_POLICY_NEVER_WHILE_PLUGGED = 1;
        public static final int WIFI_SLEEP_POLICY_NEVER = 2;
        public static final String AUTO_TIME = "auto_time";
        public static final String AUTO_TIME_ZONE = "auto_time_zone";
        public static final String CAR_DOCK_SOUND = "car_dock_sound";
        public static final String CAR_UNDOCK_SOUND = "car_undock_sound";
        public static final String DESK_DOCK_SOUND = "desk_dock_sound";
        public static final String DESK_UNDOCK_SOUND = "desk_undock_sound";
        public static final String DOCK_SOUNDS_ENABLED = "dock_sounds_enabled";
        public static final String LOCK_SOUND = "lock_sound";
        public static final String UNLOCK_SOUND = "unlock_sound";
        public static final String LOW_BATTERY_SOUND = "low_battery_sound";
        public static final String POWER_SOUNDS_ENABLED = "power_sounds_enabled";
        public static final String WIRELESS_CHARGING_STARTED_SOUND = "wireless_charging_started_sound";
        public static final String STAY_ON_WHILE_PLUGGED_IN = "stay_on_while_plugged_in";
        public static final String BUGREPORT_IN_POWER_MENU = "bugreport_in_power_menu";
        public static final String ADB_ENABLED = "adb_enabled";
        public static final String ASSISTED_GPS_ENABLED = "assisted_gps_enabled";
        public static final String BLUETOOTH_ON = "bluetooth_on";
        public static final String CDMA_CELL_BROADCAST_SMS = "cdma_cell_broadcast_sms";
        public static final String CDMA_ROAMING_MODE = "roaming_settings";
        public static final String CDMA_SUBSCRIPTION_MODE = "subscription_mode";
        public static final String DATA_ACTIVITY_TIMEOUT_MOBILE = "data_activity_timeout_mobile";
        public static final String DATA_ACTIVITY_TIMEOUT_WIFI = "data_activity_timeout_wifi";
        public static final String DATA_ROAMING = "data_roaming";
        public static final String MDC_INITIAL_MAX_RETRY = "mdc_initial_max_retry";
        public static final String DEVELOPMENT_SETTINGS_ENABLED = "development_settings_enabled";
        public static final String DEVICE_PROVISIONED = "device_provisioned";
        public static final String DISPLAY_DENSITY_FORCED = "display_density_forced";
        public static final String DISPLAY_SIZE_FORCED = "display_size_forced";
        public static final String DOWNLOAD_MAX_BYTES_OVER_MOBILE = "download_manager_max_bytes_over_mobile";
        public static final String DOWNLOAD_RECOMMENDED_MAX_BYTES_OVER_MOBILE = "download_manager_recommended_max_bytes_over_mobile";
        public static final String INSTALL_NON_MARKET_APPS = "install_non_market_apps";
        public static final String MOBILE_DATA = "mobile_data";
        public static final String NETSTATS_ENABLED = "netstats_enabled";
        public static final String NETSTATS_POLL_INTERVAL = "netstats_poll_interval";
        public static final String NETSTATS_TIME_CACHE_MAX_AGE = "netstats_time_cache_max_age";
        public static final String NETSTATS_GLOBAL_ALERT_BYTES = "netstats_global_alert_bytes";
        public static final String NETSTATS_SAMPLE_ENABLED = "netstats_sample_enabled";
        public static final String NETSTATS_REPORT_XT_OVER_DEV = "netstats_report_xt_over_dev";
        public static final String NETSTATS_DEV_BUCKET_DURATION = "netstats_dev_bucket_duration";
        public static final String NETSTATS_DEV_PERSIST_BYTES = "netstats_dev_persist_bytes";
        public static final String NETSTATS_DEV_ROTATE_AGE = "netstats_dev_rotate_age";
        public static final String NETSTATS_DEV_DELETE_AGE = "netstats_dev_delete_age";
        public static final String NETSTATS_UID_BUCKET_DURATION = "netstats_uid_bucket_duration";
        public static final String NETSTATS_UID_PERSIST_BYTES = "netstats_uid_persist_bytes";
        public static final String NETSTATS_UID_ROTATE_AGE = "netstats_uid_rotate_age";
        public static final String NETSTATS_UID_DELETE_AGE = "netstats_uid_delete_age";
        public static final String NETSTATS_UID_TAG_BUCKET_DURATION = "netstats_uid_tag_bucket_duration";
        public static final String NETSTATS_UID_TAG_PERSIST_BYTES = "netstats_uid_tag_persist_bytes";
        public static final String NETSTATS_UID_TAG_ROTATE_AGE = "netstats_uid_tag_rotate_age";
        public static final String NETSTATS_UID_TAG_DELETE_AGE = "netstats_uid_tag_delete_age";
        public static final String NETWORK_PREFERENCE = "network_preference";
        public static final String NITZ_UPDATE_DIFF = "nitz_update_diff";
        public static final String NITZ_UPDATE_SPACING = "nitz_update_spacing";
        public static final String NTP_SERVER = "ntp_server";
        public static final String NTP_TIMEOUT = "ntp_timeout";
        public static final String PACKAGE_VERIFIER_ENABLE = "package_verifier_enable";
        public static final String PACKAGE_VERIFIER_TIMEOUT = "verifier_timeout";
        public static final String PACKAGE_VERIFIER_DEFAULT_RESPONSE = "verifier_default_response";
        public static final String PACKAGE_VERIFIER_SETTING_VISIBLE = "verifier_setting_visible";
        public static final String PACKAGE_VERIFIER_INCLUDE_ADB = "verifier_verify_adb_installs";
        public static final String PDP_WATCHDOG_POLL_INTERVAL_MS = "pdp_watchdog_poll_interval_ms";
        public static final String PDP_WATCHDOG_LONG_POLL_INTERVAL_MS = "pdp_watchdog_long_poll_interval_ms";
        public static final String PDP_WATCHDOG_ERROR_POLL_INTERVAL_MS = "pdp_watchdog_error_poll_interval_ms";
        public static final String PDP_WATCHDOG_TRIGGER_PACKET_COUNT = "pdp_watchdog_trigger_packet_count";
        public static final String PDP_WATCHDOG_ERROR_POLL_COUNT = "pdp_watchdog_error_poll_count";
        public static final String PDP_WATCHDOG_MAX_PDP_RESET_FAIL_COUNT = "pdp_watchdog_max_pdp_reset_fail_count";
        public static final String SAMPLING_PROFILER_MS = "sampling_profiler_ms";
        public static final String SETUP_PREPAID_DATA_SERVICE_URL = "setup_prepaid_data_service_url";
        public static final String SETUP_PREPAID_DETECTION_TARGET_URL = "setup_prepaid_detection_target_url";
        public static final String SETUP_PREPAID_DETECTION_REDIR_HOST = "setup_prepaid_detection_redir_host";
        public static final String SMS_OUTGOING_CHECK_INTERVAL_MS = "sms_outgoing_check_interval_ms";
        public static final String SMS_OUTGOING_CHECK_MAX_COUNT = "sms_outgoing_check_max_count";
        public static final String SMS_SHORT_CODE_CONFIRMATION = "sms_short_code_confirmation";
        public static final String SMS_SHORT_CODE_RULE = "sms_short_code_rule";
        public static final String TETHER_SUPPORTED = "tether_supported";
        public static final String TETHER_DUN_REQUIRED = "tether_dun_required";
        public static final String TETHER_DUN_APN = "tether_dun_apn";
        public static final String USB_MASS_STORAGE_ENABLED = "usb_mass_storage_enabled";
        public static final String USE_GOOGLE_MAIL = "use_google_mail";
        public static final String WEB_AUTOFILL_QUERY_URL = "web_autofill_query_url";
        public static final String WIFI_DISPLAY_ON = "wifi_display_on";
        public static final String WIFI_DISPLAY_CERTIFICATION_ON = "wifi_display_certification_on";
        public static final String WIFI_DISPLAY_WPS_CONFIG = "wifi_display_wps_config";
        public static final String WIFI_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wifi_networks_available_notification_on";
        public static final String WIMAX_NETWORKS_AVAILABLE_NOTIFICATION_ON = "wimax_networks_available_notification_on";
        public static final String WIFI_NETWORKS_AVAILABLE_REPEAT_DELAY = "wifi_networks_available_repeat_delay";
        public static final String WIFI_COUNTRY_CODE = "wifi_country_code";
        public static final String WIFI_FRAMEWORK_SCAN_INTERVAL_MS = "wifi_framework_scan_interval_ms";
        public static final String WIFI_IDLE_MS = "wifi_idle_ms";
        public static final String WIFI_NUM_OPEN_NETWORKS_KEPT = "wifi_num_open_networks_kept";
        public static final String WIFI_ON = "wifi_on";
        public static final String WIFI_SCAN_ALWAYS_AVAILABLE = "wifi_scan_always_enabled";
        public static final String WIFI_SAVED_STATE = "wifi_saved_state";
        public static final String WIFI_SUPPLICANT_SCAN_INTERVAL_MS = "wifi_supplicant_scan_interval_ms";
        public static final String WIFI_SCAN_INTERVAL_WHEN_P2P_CONNECTED_MS = "wifi_scan_interval_p2p_connected_ms";
        public static final String WIFI_WATCHDOG_ON = "wifi_watchdog_on";
        public static final String WIFI_SUSPEND_OPTIMIZATIONS_ENABLED = "wifi_suspend_optimizations_enabled";
        public static final String WIFI_MAX_DHCP_RETRY_COUNT = "wifi_max_dhcp_retry_count";
        public static final String WIFI_MOBILE_DATA_TRANSITION_WAKELOCK_TIMEOUT_MS = "wifi_mobile_data_transition_wakelock_timeout_ms";
        public static final String WIFI_FREQUENCY_BAND = "wifi_frequency_band";
        public static final String WIFI_P2P_DEVICE_NAME = "wifi_p2p_device_name";
        public static final String WIFI_REENABLE_DELAY_MS = "wifi_reenable_delay";
        public static final String DATA_STALL_ALARM_NON_AGGRESSIVE_DELAY_IN_MS = "data_stall_alarm_non_aggressive_delay_in_ms";
        public static final String DATA_STALL_ALARM_AGGRESSIVE_DELAY_IN_MS = "data_stall_alarm_aggressive_delay_in_ms";
        public static final String PROVISIONING_APN_ALARM_DELAY_IN_MS = "provisioning_apn_alarm_delay_in_ms";
        public static final String GPRS_REGISTER_CHECK_PERIOD_MS = "gprs_register_check_period_ms";
        public static final String WTF_IS_FATAL = "wtf_is_fatal";
        public static final String MODE_RINGER = "mode_ringer";
        public static final String OVERLAY_DISPLAY_DEVICES = "overlay_display_devices";
        public static final String BATTERY_DISCHARGE_DURATION_THRESHOLD = "battery_discharge_duration_threshold";
        public static final String BATTERY_DISCHARGE_THRESHOLD = "battery_discharge_threshold";
        public static final String SEND_ACTION_APP_ERROR = "send_action_app_error";
        public static final String DROPBOX_AGE_SECONDS = "dropbox_age_seconds";
        public static final String DROPBOX_MAX_FILES = "dropbox_max_files";
        public static final String DROPBOX_QUOTA_KB = "dropbox_quota_kb";
        public static final String DROPBOX_QUOTA_PERCENT = "dropbox_quota_percent";
        public static final String DROPBOX_RESERVE_PERCENT = "dropbox_reserve_percent";
        public static final String DROPBOX_TAG_PREFIX = "dropbox:";
        public static final String ERROR_LOGCAT_PREFIX = "logcat_for_";
        public static final String SYS_FREE_STORAGE_LOG_INTERVAL = "sys_free_storage_log_interval";
        public static final String DISK_FREE_CHANGE_REPORTING_THRESHOLD = "disk_free_change_reporting_threshold";
        public static final String SYS_STORAGE_THRESHOLD_PERCENTAGE = "sys_storage_threshold_percentage";
        public static final String SYS_STORAGE_THRESHOLD_MAX_BYTES = "sys_storage_threshold_max_bytes";
        public static final String SYS_STORAGE_FULL_THRESHOLD_BYTES = "sys_storage_full_threshold_bytes";
        public static final String SYNC_MAX_RETRY_DELAY_IN_SECONDS = "sync_max_retry_delay_in_seconds";
        public static final String CONNECTIVITY_CHANGE_DELAY = "connectivity_change_delay";
        public static final String CONNECTIVITY_SAMPLING_INTERVAL_IN_SECONDS = "connectivity_sampling_interval_in_seconds";
        public static final String PAC_CHANGE_DELAY = "pac_change_delay";
        public static final String CAPTIVE_PORTAL_DETECTION_ENABLED = "captive_portal_detection_enabled";
        public static final String CAPTIVE_PORTAL_SERVER = "captive_portal_server";
        public static final String NSD_ON = "nsd_on";
        public static final String SET_INSTALL_LOCATION = "set_install_location";
        public static final String DEFAULT_INSTALL_LOCATION = "default_install_location";
        public static final String INET_CONDITION_DEBOUNCE_UP_DELAY = "inet_condition_debounce_up_delay";
        public static final String INET_CONDITION_DEBOUNCE_DOWN_DELAY = "inet_condition_debounce_down_delay";
        public static final String READ_EXTERNAL_STORAGE_ENFORCED_DEFAULT = "read_external_storage_enforced_default";
        public static final String HTTP_PROXY = "http_proxy";
        public static final String GLOBAL_HTTP_PROXY_HOST = "global_http_proxy_host";
        public static final String GLOBAL_HTTP_PROXY_PORT = "global_http_proxy_port";
        public static final String GLOBAL_HTTP_PROXY_EXCLUSION_LIST = "global_http_proxy_exclusion_list";
        public static final String GLOBAL_HTTP_PROXY_PAC = "global_proxy_pac_url";
        public static final String SET_GLOBAL_HTTP_PROXY = "set_global_http_proxy";
        public static final String DEFAULT_DNS_SERVER = "default_dns_server";
        public static final String BLUETOOTH_HEADSET_PRIORITY_PREFIX = "bluetooth_headset_priority_";
        public static final String BLUETOOTH_A2DP_SINK_PRIORITY_PREFIX = "bluetooth_a2dp_sink_priority_";
        public static final String BLUETOOTH_INPUT_DEVICE_PRIORITY_PREFIX = "bluetooth_input_device_priority_";
        public static final String BLUETOOTH_MAP_PRIORITY_PREFIX = "bluetooth_map_priority_";
        public static final String WINDOW_ANIMATION_SCALE = "window_animation_scale";
        public static final String TRANSITION_ANIMATION_SCALE = "transition_animation_scale";
        public static final String ANIMATOR_DURATION_SCALE = "animator_duration_scale";
        public static final String FANCY_IME_ANIMATIONS = "fancy_ime_animations";
        public static final String COMPATIBILITY_MODE = "compatibility_mode";
        public static final String PREFERRED_NETWORK_MODE = "preferred_network_mode";
        public static final String DEBUG_APP = "debug_app";
        public static final String WAIT_FOR_DEBUGGER = "wait_for_debugger";
        public static final String SHOW_PROCESSES = "show_processes";
        public static final String ALWAYS_FINISH_ACTIVITIES = "always_finish_activities";
        public static final String AUDIO_SAFE_VOLUME_STATE = "audio_safe_volume_state";
        public static final String TZINFO_UPDATE_CONTENT_URL = "tzinfo_content_url";
        public static final String TZINFO_UPDATE_METADATA_URL = "tzinfo_metadata_url";
        public static final String SELINUX_UPDATE_CONTENT_URL = "selinux_content_url";
        public static final String SELINUX_UPDATE_METADATA_URL = "selinux_metadata_url";
        public static final String SMS_SHORT_CODES_UPDATE_CONTENT_URL = "sms_short_codes_content_url";
        public static final String SMS_SHORT_CODES_UPDATE_METADATA_URL = "sms_short_codes_metadata_url";
        public static final String CERT_PIN_UPDATE_CONTENT_URL = "cert_pin_content_url";
        public static final String CERT_PIN_UPDATE_METADATA_URL = "cert_pin_metadata_url";
        public static final String INTENT_FIREWALL_UPDATE_CONTENT_URL = "intent_firewall_content_url";
        public static final String INTENT_FIREWALL_UPDATE_METADATA_URL = "intent_firewall_metadata_url";
        public static final String SELINUX_STATUS = "selinux_status";
        public static final String DEVELOPMENT_FORCE_RTL = "debug.force_rtl";
        public static final String LOW_BATTERY_SOUND_TIMEOUT = "low_battery_sound_timeout";
        public static final Uri CONTENT_URI = Uri.parse("content://settings/global");
        public static final String ENABLE_ACCESSIBILITY_GLOBAL_GESTURE_ENABLED = "enable_accessibility_global_gesture_enabled";
        public static final String WIFI_WATCHDOG_POOR_NETWORK_TEST_ENABLED = "wifi_watchdog_poor_network_test_enabled";
        public static final String EMERGENCY_TONE = "emergency_tone";
        public static final String CALL_AUTO_RETRY = "call_auto_retry";
        public static final String DOCK_AUDIO_MEDIA_ENABLED = "dock_audio_media_enabled";
        public static final String[] SETTINGS_TO_BACKUP = {"bugreport_in_power_menu", "stay_on_while_plugged_in", "auto_time", "auto_time_zone", "power_sounds_enabled", "dock_sounds_enabled", "usb_mass_storage_enabled", ENABLE_ACCESSIBILITY_GLOBAL_GESTURE_ENABLED, "wifi_networks_available_notification_on", "wifi_networks_available_repeat_delay", WIFI_WATCHDOG_POOR_NETWORK_TEST_ENABLED, "wifi_num_open_networks_kept", EMERGENCY_TONE, CALL_AUTO_RETRY, DOCK_AUDIO_MEDIA_ENABLED};
        public static final String SYS_PROP_SETTING_VERSION = "sys.settings_global_version";
        private static NameValueCache sNameValueCache = new NameValueCache(SYS_PROP_SETTING_VERSION, CONTENT_URI, Settings.CALL_METHOD_GET_GLOBAL, Settings.CALL_METHOD_PUT_GLOBAL);

        public static final String getBluetoothHeadsetPriorityKey(String address) {
            return BLUETOOTH_HEADSET_PRIORITY_PREFIX + address.toUpperCase(Locale.ROOT);
        }

        public static final String getBluetoothA2dpSinkPriorityKey(String address) {
            return BLUETOOTH_A2DP_SINK_PRIORITY_PREFIX + address.toUpperCase(Locale.ROOT);
        }

        public static final String getBluetoothInputDevicePriorityKey(String address) {
            return BLUETOOTH_INPUT_DEVICE_PRIORITY_PREFIX + address.toUpperCase(Locale.ROOT);
        }

        public static final String getBluetoothMapPriorityKey(String address) {
            return BLUETOOTH_MAP_PRIORITY_PREFIX + address.toUpperCase(Locale.ROOT);
        }

        public static String getString(ContentResolver resolver, String name) {
            return getStringForUser(resolver, name, UserHandle.myUserId());
        }

        public static String getStringForUser(ContentResolver resolver, String name, int userHandle) {
            return sNameValueCache.getStringForUser(resolver, name, userHandle);
        }

        public static boolean putString(ContentResolver resolver, String name, String value) {
            return putStringForUser(resolver, name, value, UserHandle.myUserId());
        }

        public static boolean putStringForUser(ContentResolver resolver, String name, String value, int userHandle) {
            return sNameValueCache.putStringForUser(resolver, name, value, userHandle);
        }

        public static Uri getUriFor(String name) {
            return getUriFor(CONTENT_URI, name);
        }

        public static int getInt(ContentResolver cr, String name, int def) {
            String v = getString(cr, name);
            if (v != null) {
                try {
                    return Integer.parseInt(v);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
            return def;
        }

        public static int getInt(ContentResolver cr, String name) throws SettingNotFoundException {
            String v = getString(cr, name);
            try {
                return Integer.parseInt(v);
            } catch (NumberFormatException e) {
                throw new SettingNotFoundException(name);
            }
        }

        public static boolean putInt(ContentResolver cr, String name, int value) {
            return putString(cr, name, Integer.toString(value));
        }

        public static long getLong(ContentResolver cr, String name, long def) {
            long value;
            long parseLong;
            String valString = getString(cr, name);
            if (valString != null) {
                try {
                    parseLong = Long.parseLong(valString);
                } catch (NumberFormatException e) {
                    value = def;
                }
            } else {
                parseLong = def;
            }
            value = parseLong;
            return value;
        }

        public static long getLong(ContentResolver cr, String name) throws SettingNotFoundException {
            String valString = getString(cr, name);
            try {
                return Long.parseLong(valString);
            } catch (NumberFormatException e) {
                throw new SettingNotFoundException(name);
            }
        }

        public static boolean putLong(ContentResolver cr, String name, long value) {
            return putString(cr, name, Long.toString(value));
        }

        public static float getFloat(ContentResolver cr, String name, float def) {
            String v = getString(cr, name);
            if (v != null) {
                try {
                    return Float.parseFloat(v);
                } catch (NumberFormatException e) {
                    return def;
                }
            }
            return def;
        }

        public static float getFloat(ContentResolver cr, String name) throws SettingNotFoundException {
            String v = getString(cr, name);
            if (v == null) {
                throw new SettingNotFoundException(name);
            }
            try {
                return Float.parseFloat(v);
            } catch (NumberFormatException e) {
                throw new SettingNotFoundException(name);
            }
        }

        public static boolean putFloat(ContentResolver cr, String name, float value) {
            return putString(cr, name, Float.toString(value));
        }
    }

    /* loaded from: Settings$Bookmarks.class */
    public static final class Bookmarks implements BaseColumns {
        private static final String TAG = "Bookmarks";
        public static final String ID = "_id";
        public static final String TITLE = "title";
        public static final String FOLDER = "folder";
        public static final String INTENT = "intent";
        public static final String SHORTCUT = "shortcut";
        public static final String ORDERING = "ordering";
        private static final String sShortcutSelection = "shortcut=?";
        public static final Uri CONTENT_URI = Uri.parse("content://settings/bookmarks");
        private static final String[] sIntentProjection = {"intent"};
        private static final String[] sShortcutProjection = {"_id", "shortcut"};

        public static Intent getIntentForShortcut(ContentResolver cr, char shortcut) {
            Intent intent = null;
            Cursor c = cr.query(CONTENT_URI, sIntentProjection, sShortcutSelection, new String[]{String.valueOf((int) shortcut)}, ORDERING);
            while (intent == null) {
                try {
                    if (!c.moveToNext()) {
                        break;
                    }
                    try {
                        String intentURI = c.getString(c.getColumnIndexOrThrow("intent"));
                        intent = Intent.parseUri(intentURI, 0);
                    } catch (IllegalArgumentException e) {
                        Log.w(TAG, "Intent column not found", e);
                    } catch (URISyntaxException e2) {
                    }
                } finally {
                    if (c != null) {
                        c.close();
                    }
                }
            }
            return intent;
        }

        public static Uri add(ContentResolver cr, Intent intent, String title, String folder, char shortcut, int ordering) {
            if (shortcut != 0) {
                cr.delete(CONTENT_URI, sShortcutSelection, new String[]{String.valueOf((int) shortcut)});
            }
            ContentValues values = new ContentValues();
            if (title != null) {
                values.put("title", title);
            }
            if (folder != null) {
                values.put("folder", folder);
            }
            values.put("intent", intent.toUri(0));
            if (shortcut != 0) {
                values.put("shortcut", Integer.valueOf(shortcut));
            }
            values.put(ORDERING, Integer.valueOf(ordering));
            return cr.insert(CONTENT_URI, values);
        }

        public static CharSequence getLabelForFolder(Resources r, String folder) {
            return folder;
        }

        public static CharSequence getTitle(Context context, Cursor cursor) {
            int titleColumn = cursor.getColumnIndex("title");
            int intentColumn = cursor.getColumnIndex("intent");
            if (titleColumn == -1 || intentColumn == -1) {
                throw new IllegalArgumentException("The cursor must contain the TITLE and INTENT columns.");
            }
            String title = cursor.getString(titleColumn);
            if (!TextUtils.isEmpty(title)) {
                return title;
            }
            String intentUri = cursor.getString(intentColumn);
            if (TextUtils.isEmpty(intentUri)) {
                return "";
            }
            try {
                Intent intent = Intent.parseUri(intentUri, 0);
                PackageManager packageManager = context.getPackageManager();
                ResolveInfo info = packageManager.resolveActivity(intent, 0);
                return info != null ? info.loadLabel(packageManager) : "";
            } catch (URISyntaxException e) {
                return "";
            }
        }
    }

    public static String getGTalkDeviceId(long androidId) {
        return "android-" + Long.toHexString(androidId);
    }
}