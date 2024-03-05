package com.android.webview.chromium;

import android.content.Context;
import android.os.Build;
import com.android.org.chromium.android_webview.AwResource;
import com.android.org.chromium.content.R;
import gov.nist.core.Separators;
import java.lang.reflect.Field;

/* loaded from: ResourceProvider.class */
public class ResourceProvider {
    private static boolean sInitialized;

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void registerResources(Context context) {
        if (sInitialized) {
            return;
        }
        AwResource.setResources(context.getResources());
        R.attr.select_dialog_multichoice = com.android.internal.R.attr.webviewchromium_select_dialog_multichoice;
        R.attr.select_dialog_singlechoice = com.android.internal.R.attr.webviewchromium_select_dialog_singlechoice;
        com.android.org.chromium.ui.R.color.color_picker_border_color = com.android.internal.R.color.webviewchromium_color_picker_border_color;
        R.dimen.link_preview_overlay_radius = com.android.internal.R.dimen.webviewchromium_link_preview_overlay_radius;
        R.drawable.ondemand_overlay = com.android.internal.R.drawable.webviewchromium_ondemand_overlay;
        com.android.org.chromium.ui.R.drawable.color_picker_advanced_select_handle = com.android.internal.R.drawable.webviewchromium_color_picker_advanced_select_handle;
        R.id.position_in_year = com.android.internal.R.id.webviewchromium_position_in_year;
        R.id.year = com.android.internal.R.id.webviewchromium_year;
        R.id.pickers = com.android.internal.R.id.webviewchromium_pickers;
        R.id.date_picker = com.android.internal.R.id.webviewchromium_date_picker;
        R.id.select_action_menu_select_all = com.android.internal.R.id.webviewchromium_select_action_menu_select_all;
        R.id.select_action_menu_cut = com.android.internal.R.id.webviewchromium_select_action_menu_cut;
        R.id.select_action_menu_copy = com.android.internal.R.id.webviewchromium_select_action_menu_copy;
        R.id.select_action_menu_paste = com.android.internal.R.id.webviewchromium_select_action_menu_paste;
        R.id.select_action_menu_share = com.android.internal.R.id.webviewchromium_select_action_menu_share;
        R.id.select_action_menu_web_search = com.android.internal.R.id.webviewchromium_select_action_menu_web_search;
        R.id.time_picker = com.android.internal.R.id.webviewchromium_time_picker;
        com.android.org.chromium.ui.R.id.selected_color_view = com.android.internal.R.id.webviewchromium_color_picker_selected_color_view;
        com.android.org.chromium.ui.R.id.title = com.android.internal.R.id.webviewchromium_color_picker_title;
        com.android.org.chromium.ui.R.id.more_colors_button = com.android.internal.R.id.webviewchromium_color_picker_more_colors_button;
        com.android.org.chromium.ui.R.id.color_picker_advanced = com.android.internal.R.id.webviewchromium_color_picker_advanced;
        com.android.org.chromium.ui.R.id.color_picker_simple = com.android.internal.R.id.webviewchromium_color_picker_simple;
        com.android.org.chromium.ui.R.id.more_colors_button_border = com.android.internal.R.id.webviewchromium_color_picker_more_colors_button_border;
        com.android.org.chromium.ui.R.id.color_picker_simple_border = com.android.internal.R.id.webviewchromium_color_picker_simple_border;
        com.android.org.chromium.ui.R.id.gradient = com.android.internal.R.id.webviewchromium_color_picker_gradient;
        com.android.org.chromium.ui.R.id.text = com.android.internal.R.id.webviewchromium_color_picker_text;
        com.android.org.chromium.ui.R.id.seek_bar = com.android.internal.R.id.webviewchromium_color_picker_seek_bar;
        com.android.org.chromium.ui.R.id.autofill_label = com.android.internal.R.id.webviewchromium_autofill_label;
        com.android.org.chromium.ui.R.id.autofill_popup_window = com.android.internal.R.id.webviewchromium_autofill_popup_window;
        com.android.org.chromium.ui.R.id.autofill_sublabel = com.android.internal.R.id.webviewchromium_autofill_sublabel;
        R.layout.date_time_picker_dialog = com.android.internal.R.layout.webviewchromium_date_time_picker_dialog;
        R.layout.two_field_date_picker = com.android.internal.R.layout.webviewchromium_two_field_date_picker;
        com.android.org.chromium.ui.R.layout.color_picker_dialog_title = com.android.internal.R.layout.webviewchromium_color_picker_dialog_title;
        com.android.org.chromium.ui.R.layout.color_picker_dialog_content = com.android.internal.R.layout.webviewchromium_color_picker_dialog_content;
        com.android.org.chromium.ui.R.layout.color_picker_advanced_component = com.android.internal.R.layout.webviewchromium_color_picker_advanced_component;
        com.android.org.chromium.ui.R.layout.autofill_text = com.android.internal.R.layout.webviewchromium_autofill_text;
        R.menu.select_action_menu = com.android.internal.R.menu.webviewchromium_select_action_menu;
        R.string.accessibility_content_view = com.android.internal.R.string.webviewchromium_accessibility_content_view;
        R.string.accessibility_date_picker_month = com.android.internal.R.string.webviewchromium_accessibility_date_picker_month;
        R.string.accessibility_date_picker_week = com.android.internal.R.string.webviewchromium_accessibility_date_picker_week;
        R.string.accessibility_date_picker_year = com.android.internal.R.string.webviewchromium_accessibility_date_picker_year;
        R.string.accessibility_datetime_picker_date = com.android.internal.R.string.webviewchromium_accessibility_datetime_picker_date;
        R.string.accessibility_datetime_picker_time = com.android.internal.R.string.webviewchromium_accessibility_datetime_picker_time;
        R.string.actionbar_share = com.android.internal.R.string.share;
        R.string.actionbar_web_search = com.android.internal.R.string.websearch;
        R.string.date_picker_dialog_clear = com.android.internal.R.string.webviewchromium_date_picker_dialog_clear;
        R.string.date_picker_dialog_set = com.android.internal.R.string.webviewchromium_date_picker_dialog_set;
        R.string.date_picker_dialog_title = com.android.internal.R.string.webviewchromium_date_picker_dialog_title;
        R.string.date_time_picker_dialog_title = com.android.internal.R.string.webviewchromium_date_time_picker_dialog_title;
        R.string.media_player_error_button = com.android.internal.R.string.webviewchromium_media_player_error_button;
        R.string.media_player_error_text_invalid_progressive_playback = com.android.internal.R.string.webviewchromium_media_player_error_text_invalid_progressive_playback;
        R.string.media_player_error_text_unknown = com.android.internal.R.string.webviewchromium_media_player_error_text_unknown;
        R.string.media_player_error_title = com.android.internal.R.string.webviewchromium_media_player_error_title;
        R.string.media_player_loading_video = com.android.internal.R.string.webviewchromium_media_player_loading_video;
        R.string.month_picker_dialog_title = com.android.internal.R.string.webviewchromium_month_picker_dialog_title;
        R.string.week_picker_dialog_title = com.android.internal.R.string.webviewchromium_week_picker_dialog_title;
        com.android.org.chromium.ui.R.string.low_memory_error = com.android.internal.R.string.webviewchromium_low_memory_error;
        com.android.org.chromium.ui.R.string.opening_file_error = com.android.internal.R.string.webviewchromium_opening_file_error;
        com.android.org.chromium.ui.R.string.color_picker_button_more = com.android.internal.R.string.webviewchromium_color_picker_button_more;
        com.android.org.chromium.ui.R.string.color_picker_hue = com.android.internal.R.string.webviewchromium_color_picker_hue;
        com.android.org.chromium.ui.R.string.color_picker_saturation = com.android.internal.R.string.webviewchromium_color_picker_saturation;
        com.android.org.chromium.ui.R.string.color_picker_value = com.android.internal.R.string.webviewchromium_color_picker_value;
        com.android.org.chromium.ui.R.string.color_picker_button_set = com.android.internal.R.string.webviewchromium_color_picker_button_set;
        com.android.org.chromium.ui.R.string.color_picker_button_cancel = com.android.internal.R.string.webviewchromium_color_picker_button_cancel;
        com.android.org.chromium.ui.R.string.color_picker_dialog_title = com.android.internal.R.string.webviewchromium_color_picker_dialog_title;
        R.style.SelectPopupDialog = com.android.internal.R.style.webviewchromium_SelectPopupDialog;
        com.android.org.chromium.ui.R.style.AutofillPopupWindow = com.android.internal.R.style.webviewchromium_AutofillPopupWindow;
        if (Build.IS_DEBUGGABLE) {
            verifyFields(R.class);
            verifyFields(com.android.org.chromium.ui.R.class);
        }
        AwResource.RAW_LOAD_ERROR = com.android.internal.R.raw.loaderror;
        AwResource.RAW_NO_DOMAIN = com.android.internal.R.raw.nodomain;
        AwResource.STRING_DEFAULT_TEXT_ENCODING = com.android.internal.R.string.default_text_encoding;
        sInitialized = true;
    }

    private static void verifyFields(Class<?> R) {
        Class<?>[] arr$ = R.getDeclaredClasses();
        for (Class<?> c : arr$) {
            verifyFields(c);
        }
        Field[] arr$2 = R.getDeclaredFields();
        for (Field f : arr$2) {
            if (f.getInt(null) == 0) {
                throw new RuntimeException("Missing resource mapping for " + R.getName() + Separators.DOT + f.getName());
                break;
            }
        }
    }
}