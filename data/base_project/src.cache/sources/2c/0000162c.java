package android.view.accessibility;

import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

/* loaded from: CaptioningManager.class */
public class CaptioningManager {
    private static final int DEFAULT_ENABLED = 0;
    private static final int DEFAULT_PRESET = 0;
    private static final float DEFAULT_FONT_SCALE = 1.0f;
    private final ContentResolver mContentResolver;
    private final ArrayList<CaptioningChangeListener> mListeners = new ArrayList<>();
    private final Handler mHandler = new Handler();
    private final ContentObserver mContentObserver = new ContentObserver(this.mHandler) { // from class: android.view.accessibility.CaptioningManager.1
        @Override // android.database.ContentObserver
        public void onChange(boolean selfChange, Uri uri) {
            String uriPath = uri.getPath();
            String name = uriPath.substring(uriPath.lastIndexOf(47) + 1);
            if (Settings.Secure.ACCESSIBILITY_CAPTIONING_ENABLED.equals(name)) {
                CaptioningManager.this.notifyEnabledChanged();
            } else if (Settings.Secure.ACCESSIBILITY_CAPTIONING_LOCALE.equals(name)) {
                CaptioningManager.this.notifyLocaleChanged();
            } else if (Settings.Secure.ACCESSIBILITY_CAPTIONING_FONT_SCALE.equals(name)) {
                CaptioningManager.this.notifyFontScaleChanged();
            } else {
                CaptioningManager.this.mHandler.removeCallbacks(CaptioningManager.this.mStyleChangedRunnable);
                CaptioningManager.this.mHandler.post(CaptioningManager.this.mStyleChangedRunnable);
            }
        }
    };
    private final Runnable mStyleChangedRunnable = new Runnable() { // from class: android.view.accessibility.CaptioningManager.2
        @Override // java.lang.Runnable
        public void run() {
            CaptioningManager.this.notifyUserStyleChanged();
        }
    };

    public CaptioningManager(Context context) {
        this.mContentResolver = context.getContentResolver();
    }

    public final boolean isEnabled() {
        return Settings.Secure.getInt(this.mContentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_ENABLED, 0) == 1;
    }

    public final String getRawLocale() {
        return Settings.Secure.getString(this.mContentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_LOCALE);
    }

    public final Locale getLocale() {
        String rawLocale = getRawLocale();
        if (!TextUtils.isEmpty(rawLocale)) {
            String[] splitLocale = rawLocale.split("_");
            switch (splitLocale.length) {
                case 1:
                    return new Locale(splitLocale[0]);
                case 2:
                    return new Locale(splitLocale[0], splitLocale[1]);
                case 3:
                    return new Locale(splitLocale[0], splitLocale[1], splitLocale[2]);
                default:
                    return null;
            }
        }
        return null;
    }

    public final float getFontScale() {
        return Settings.Secure.getFloat(this.mContentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_FONT_SCALE, 1.0f);
    }

    public int getRawUserStyle() {
        return Settings.Secure.getInt(this.mContentResolver, Settings.Secure.ACCESSIBILITY_CAPTIONING_PRESET, 0);
    }

    public CaptionStyle getUserStyle() {
        int preset = getRawUserStyle();
        if (preset == -1) {
            return CaptionStyle.getCustomStyle(this.mContentResolver);
        }
        return CaptionStyle.PRESETS[preset];
    }

    public void addCaptioningChangeListener(CaptioningChangeListener listener) {
        synchronized (this.mListeners) {
            if (this.mListeners.isEmpty()) {
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_ENABLED);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_EDGE_TYPE);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_EDGE_COLOR);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_TYPEFACE);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_FONT_SCALE);
                registerObserver(Settings.Secure.ACCESSIBILITY_CAPTIONING_LOCALE);
            }
            this.mListeners.add(listener);
        }
    }

    private void registerObserver(String key) {
        this.mContentResolver.registerContentObserver(Settings.Secure.getUriFor(key), false, this.mContentObserver);
    }

    public void removeCaptioningChangeListener(CaptioningChangeListener listener) {
        synchronized (this.mListeners) {
            this.mListeners.remove(listener);
            if (this.mListeners.isEmpty()) {
                this.mContentResolver.unregisterContentObserver(this.mContentObserver);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyEnabledChanged() {
        boolean enabled = isEnabled();
        synchronized (this.mListeners) {
            Iterator i$ = this.mListeners.iterator();
            while (i$.hasNext()) {
                CaptioningChangeListener listener = i$.next();
                listener.onEnabledChanged(enabled);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyUserStyleChanged() {
        CaptionStyle userStyle = getUserStyle();
        synchronized (this.mListeners) {
            Iterator i$ = this.mListeners.iterator();
            while (i$.hasNext()) {
                CaptioningChangeListener listener = i$.next();
                listener.onUserStyleChanged(userStyle);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyLocaleChanged() {
        Locale locale = getLocale();
        synchronized (this.mListeners) {
            Iterator i$ = this.mListeners.iterator();
            while (i$.hasNext()) {
                CaptioningChangeListener listener = i$.next();
                listener.onLocaleChanged(locale);
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void notifyFontScaleChanged() {
        float fontScale = getFontScale();
        synchronized (this.mListeners) {
            Iterator i$ = this.mListeners.iterator();
            while (i$.hasNext()) {
                CaptioningChangeListener listener = i$.next();
                listener.onFontScaleChanged(fontScale);
            }
        }
    }

    /* loaded from: CaptioningManager$CaptionStyle.class */
    public static final class CaptionStyle {
        public static final int PRESET_CUSTOM = -1;
        public static final int EDGE_TYPE_NONE = 0;
        public static final int EDGE_TYPE_OUTLINE = 1;
        public static final int EDGE_TYPE_DROP_SHADOW = 2;
        public final int foregroundColor;
        public final int backgroundColor;
        public final int edgeType;
        public final int edgeColor;
        public final String mRawTypeface;
        private Typeface mParsedTypeface;
        private static final CaptionStyle WHITE_ON_BLACK = new CaptionStyle(-1, -16777216, 0, -16777216, null);
        private static final CaptionStyle BLACK_ON_WHITE = new CaptionStyle(-16777216, -1, 0, -16777216, null);
        private static final CaptionStyle YELLOW_ON_BLACK = new CaptionStyle(-256, -16777216, 0, -16777216, null);
        private static final CaptionStyle YELLOW_ON_BLUE = new CaptionStyle(-256, Color.BLUE, 0, -16777216, null);
        public static final CaptionStyle[] PRESETS = {WHITE_ON_BLACK, BLACK_ON_WHITE, YELLOW_ON_BLACK, YELLOW_ON_BLUE};
        private static final CaptionStyle DEFAULT_CUSTOM = WHITE_ON_BLACK;

        private CaptionStyle(int foregroundColor, int backgroundColor, int edgeType, int edgeColor, String rawTypeface) {
            this.foregroundColor = foregroundColor;
            this.backgroundColor = backgroundColor;
            this.edgeType = edgeType;
            this.edgeColor = edgeColor;
            this.mRawTypeface = rawTypeface;
        }

        public Typeface getTypeface() {
            if (this.mParsedTypeface == null && !TextUtils.isEmpty(this.mRawTypeface)) {
                this.mParsedTypeface = Typeface.create(this.mRawTypeface, 0);
            }
            return this.mParsedTypeface;
        }

        public static CaptionStyle getCustomStyle(ContentResolver cr) {
            CaptionStyle defStyle = DEFAULT_CUSTOM;
            int foregroundColor = Settings.Secure.getInt(cr, Settings.Secure.ACCESSIBILITY_CAPTIONING_FOREGROUND_COLOR, defStyle.foregroundColor);
            int backgroundColor = Settings.Secure.getInt(cr, Settings.Secure.ACCESSIBILITY_CAPTIONING_BACKGROUND_COLOR, defStyle.backgroundColor);
            int edgeType = Settings.Secure.getInt(cr, Settings.Secure.ACCESSIBILITY_CAPTIONING_EDGE_TYPE, defStyle.edgeType);
            int edgeColor = Settings.Secure.getInt(cr, Settings.Secure.ACCESSIBILITY_CAPTIONING_EDGE_COLOR, defStyle.edgeColor);
            String rawTypeface = Settings.Secure.getString(cr, Settings.Secure.ACCESSIBILITY_CAPTIONING_TYPEFACE);
            if (rawTypeface == null) {
                rawTypeface = defStyle.mRawTypeface;
            }
            return new CaptionStyle(foregroundColor, backgroundColor, edgeType, edgeColor, rawTypeface);
        }
    }

    /* loaded from: CaptioningManager$CaptioningChangeListener.class */
    public static abstract class CaptioningChangeListener {
        public void onEnabledChanged(boolean enabled) {
        }

        public void onUserStyleChanged(CaptionStyle userStyle) {
        }

        public void onLocaleChanged(Locale locale) {
        }

        public void onFontScaleChanged(float fontScale) {
        }
    }
}