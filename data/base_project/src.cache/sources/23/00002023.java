package com.android.server.wm;

import android.content.res.CompatibilityInfo;

/* loaded from: StartingData.class */
final class StartingData {
    final String pkg;
    final int theme;
    final CompatibilityInfo compatInfo;
    final CharSequence nonLocalizedLabel;
    final int labelRes;
    final int icon;
    final int logo;
    final int windowFlags;

    /* JADX INFO: Access modifiers changed from: package-private */
    public StartingData(String _pkg, int _theme, CompatibilityInfo _compatInfo, CharSequence _nonLocalizedLabel, int _labelRes, int _icon, int _logo, int _windowFlags) {
        this.pkg = _pkg;
        this.theme = _theme;
        this.compatInfo = _compatInfo;
        this.nonLocalizedLabel = _nonLocalizedLabel;
        this.labelRes = _labelRes;
        this.icon = _icon;
        this.logo = _logo;
        this.windowFlags = _windowFlags;
    }
}