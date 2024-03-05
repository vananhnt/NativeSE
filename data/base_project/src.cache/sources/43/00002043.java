package com.android.webview.chromium;

import android.util.Log;
import android.webkit.WebSettings;
import com.android.org.chromium.android_webview.AwSettings;

/* loaded from: ContentSettingsAdapter.class */
public class ContentSettingsAdapter extends WebSettings {
    private static final String TAG = ContentSettingsAdapter.class.getSimpleName();
    private AwSettings mAwSettings;

    public ContentSettingsAdapter(AwSettings awSettings) {
        this.mAwSettings = awSettings;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public AwSettings getAwSettings() {
        return this.mAwSettings;
    }

    @Override // android.webkit.WebSettings
    @Deprecated
    public void setNavDump(boolean enabled) {
    }

    @Override // android.webkit.WebSettings
    @Deprecated
    public boolean getNavDump() {
        return false;
    }

    @Override // android.webkit.WebSettings
    public void setSupportZoom(boolean support) {
        this.mAwSettings.setSupportZoom(support);
    }

    @Override // android.webkit.WebSettings
    public boolean supportZoom() {
        return this.mAwSettings.supportZoom();
    }

    @Override // android.webkit.WebSettings
    public void setBuiltInZoomControls(boolean enabled) {
        this.mAwSettings.setBuiltInZoomControls(enabled);
    }

    @Override // android.webkit.WebSettings
    public boolean getBuiltInZoomControls() {
        return this.mAwSettings.getBuiltInZoomControls();
    }

    @Override // android.webkit.WebSettings
    public void setDisplayZoomControls(boolean enabled) {
        this.mAwSettings.setDisplayZoomControls(enabled);
    }

    @Override // android.webkit.WebSettings
    public boolean getDisplayZoomControls() {
        return this.mAwSettings.getDisplayZoomControls();
    }

    @Override // android.webkit.WebSettings
    public void setAllowFileAccess(boolean allow) {
        this.mAwSettings.setAllowFileAccess(allow);
    }

    @Override // android.webkit.WebSettings
    public boolean getAllowFileAccess() {
        return this.mAwSettings.getAllowFileAccess();
    }

    @Override // android.webkit.WebSettings
    public void setAllowContentAccess(boolean allow) {
        this.mAwSettings.setAllowContentAccess(allow);
    }

    @Override // android.webkit.WebSettings
    public boolean getAllowContentAccess() {
        return this.mAwSettings.getAllowContentAccess();
    }

    @Override // android.webkit.WebSettings
    public void setLoadWithOverviewMode(boolean overview) {
        this.mAwSettings.setLoadWithOverviewMode(overview);
    }

    @Override // android.webkit.WebSettings
    public boolean getLoadWithOverviewMode() {
        return this.mAwSettings.getLoadWithOverviewMode();
    }

    @Override // android.webkit.WebSettings
    public void setEnableSmoothTransition(boolean enable) {
    }

    @Override // android.webkit.WebSettings
    public boolean enableSmoothTransition() {
        return false;
    }

    @Override // android.webkit.WebSettings
    public void setUseWebViewBackgroundForOverscrollBackground(boolean view) {
    }

    @Override // android.webkit.WebSettings
    public boolean getUseWebViewBackgroundForOverscrollBackground() {
        return false;
    }

    @Override // android.webkit.WebSettings
    public void setSaveFormData(boolean save) {
        this.mAwSettings.setSaveFormData(save);
    }

    @Override // android.webkit.WebSettings
    public boolean getSaveFormData() {
        return this.mAwSettings.getSaveFormData();
    }

    @Override // android.webkit.WebSettings
    public void setSavePassword(boolean save) {
    }

    @Override // android.webkit.WebSettings
    public boolean getSavePassword() {
        return false;
    }

    @Override // android.webkit.WebSettings
    public synchronized void setTextZoom(int textZoom) {
        this.mAwSettings.setTextZoom(textZoom);
    }

    @Override // android.webkit.WebSettings
    public synchronized int getTextZoom() {
        return this.mAwSettings.getTextZoom();
    }

    @Override // android.webkit.WebSettings
    public void setDefaultZoom(WebSettings.ZoomDensity zoom) {
        if (zoom != WebSettings.ZoomDensity.MEDIUM) {
            Log.w(TAG, "setDefaultZoom not supported, zoom=" + zoom);
        }
    }

    @Override // android.webkit.WebSettings
    public WebSettings.ZoomDensity getDefaultZoom() {
        return WebSettings.ZoomDensity.MEDIUM;
    }

    @Override // android.webkit.WebSettings
    public void setLightTouchEnabled(boolean enabled) {
    }

    @Override // android.webkit.WebSettings
    public boolean getLightTouchEnabled() {
        return false;
    }

    @Override // android.webkit.WebSettings
    public synchronized void setUserAgent(int ua) {
        if (ua == 0) {
            setUserAgentString(null);
        } else {
            Log.w(TAG, "setUserAgent not supported, ua=" + ua);
        }
    }

    @Override // android.webkit.WebSettings
    public synchronized int getUserAgent() {
        return AwSettings.getDefaultUserAgent().equals(getUserAgentString()) ? 0 : -1;
    }

    @Override // android.webkit.WebSettings
    public synchronized void setUseWideViewPort(boolean use) {
        this.mAwSettings.setUseWideViewPort(use);
    }

    @Override // android.webkit.WebSettings
    public synchronized boolean getUseWideViewPort() {
        return this.mAwSettings.getUseWideViewPort();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setSupportMultipleWindows(boolean support) {
        this.mAwSettings.setSupportMultipleWindows(support);
    }

    @Override // android.webkit.WebSettings
    public synchronized boolean supportMultipleWindows() {
        return this.mAwSettings.supportMultipleWindows();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setLayoutAlgorithm(WebSettings.LayoutAlgorithm l) {
        AwSettings.LayoutAlgorithm[] chromiumValues = {AwSettings.LayoutAlgorithm.NORMAL, AwSettings.LayoutAlgorithm.SINGLE_COLUMN, AwSettings.LayoutAlgorithm.NARROW_COLUMNS, AwSettings.LayoutAlgorithm.TEXT_AUTOSIZING};
        this.mAwSettings.setLayoutAlgorithm(chromiumValues[l.ordinal()]);
    }

    @Override // android.webkit.WebSettings
    public synchronized WebSettings.LayoutAlgorithm getLayoutAlgorithm() {
        WebSettings.LayoutAlgorithm[] webViewValues = {WebSettings.LayoutAlgorithm.NORMAL, WebSettings.LayoutAlgorithm.SINGLE_COLUMN, WebSettings.LayoutAlgorithm.NARROW_COLUMNS, WebSettings.LayoutAlgorithm.TEXT_AUTOSIZING};
        return webViewValues[this.mAwSettings.getLayoutAlgorithm().ordinal()];
    }

    @Override // android.webkit.WebSettings
    public synchronized void setStandardFontFamily(String font) {
        this.mAwSettings.setStandardFontFamily(font);
    }

    @Override // android.webkit.WebSettings
    public synchronized String getStandardFontFamily() {
        return this.mAwSettings.getStandardFontFamily();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setFixedFontFamily(String font) {
        this.mAwSettings.setFixedFontFamily(font);
    }

    @Override // android.webkit.WebSettings
    public synchronized String getFixedFontFamily() {
        return this.mAwSettings.getFixedFontFamily();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setSansSerifFontFamily(String font) {
        this.mAwSettings.setSansSerifFontFamily(font);
    }

    @Override // android.webkit.WebSettings
    public synchronized String getSansSerifFontFamily() {
        return this.mAwSettings.getSansSerifFontFamily();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setSerifFontFamily(String font) {
        this.mAwSettings.setSerifFontFamily(font);
    }

    @Override // android.webkit.WebSettings
    public synchronized String getSerifFontFamily() {
        return this.mAwSettings.getSerifFontFamily();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setCursiveFontFamily(String font) {
        this.mAwSettings.setCursiveFontFamily(font);
    }

    @Override // android.webkit.WebSettings
    public synchronized String getCursiveFontFamily() {
        return this.mAwSettings.getCursiveFontFamily();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setFantasyFontFamily(String font) {
        this.mAwSettings.setFantasyFontFamily(font);
    }

    @Override // android.webkit.WebSettings
    public synchronized String getFantasyFontFamily() {
        return this.mAwSettings.getFantasyFontFamily();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setMinimumFontSize(int size) {
        this.mAwSettings.setMinimumFontSize(size);
    }

    @Override // android.webkit.WebSettings
    public synchronized int getMinimumFontSize() {
        return this.mAwSettings.getMinimumFontSize();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setMinimumLogicalFontSize(int size) {
        this.mAwSettings.setMinimumLogicalFontSize(size);
    }

    @Override // android.webkit.WebSettings
    public synchronized int getMinimumLogicalFontSize() {
        return this.mAwSettings.getMinimumLogicalFontSize();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setDefaultFontSize(int size) {
        this.mAwSettings.setDefaultFontSize(size);
    }

    @Override // android.webkit.WebSettings
    public synchronized int getDefaultFontSize() {
        return this.mAwSettings.getDefaultFontSize();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setDefaultFixedFontSize(int size) {
        this.mAwSettings.setDefaultFixedFontSize(size);
    }

    @Override // android.webkit.WebSettings
    public synchronized int getDefaultFixedFontSize() {
        return this.mAwSettings.getDefaultFixedFontSize();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setLoadsImagesAutomatically(boolean flag) {
        this.mAwSettings.setLoadsImagesAutomatically(flag);
    }

    @Override // android.webkit.WebSettings
    public synchronized boolean getLoadsImagesAutomatically() {
        return this.mAwSettings.getLoadsImagesAutomatically();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setBlockNetworkImage(boolean flag) {
        this.mAwSettings.setImagesEnabled(!flag);
    }

    @Override // android.webkit.WebSettings
    public synchronized boolean getBlockNetworkImage() {
        return !this.mAwSettings.getImagesEnabled();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setBlockNetworkLoads(boolean flag) {
        this.mAwSettings.setBlockNetworkLoads(flag);
    }

    @Override // android.webkit.WebSettings
    public synchronized boolean getBlockNetworkLoads() {
        return this.mAwSettings.getBlockNetworkLoads();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setJavaScriptEnabled(boolean flag) {
        this.mAwSettings.setJavaScriptEnabled(flag);
    }

    @Override // android.webkit.WebSettings
    public void setAllowUniversalAccessFromFileURLs(boolean flag) {
        this.mAwSettings.setAllowUniversalAccessFromFileURLs(flag);
    }

    @Override // android.webkit.WebSettings
    public void setAllowFileAccessFromFileURLs(boolean flag) {
        this.mAwSettings.setAllowFileAccessFromFileURLs(flag);
    }

    @Override // android.webkit.WebSettings
    public synchronized void setPluginsEnabled(boolean flag) {
        this.mAwSettings.setPluginsEnabled(flag);
    }

    @Override // android.webkit.WebSettings
    public synchronized void setPluginState(WebSettings.PluginState state) {
        this.mAwSettings.setPluginState(state);
    }

    @Override // android.webkit.WebSettings
    public synchronized void setDatabasePath(String databasePath) {
    }

    @Override // android.webkit.WebSettings
    public synchronized void setGeolocationDatabasePath(String databasePath) {
    }

    @Override // android.webkit.WebSettings
    public synchronized void setAppCacheEnabled(boolean flag) {
        this.mAwSettings.setAppCacheEnabled(flag);
    }

    @Override // android.webkit.WebSettings
    public synchronized void setAppCachePath(String appCachePath) {
        this.mAwSettings.setAppCachePath(appCachePath);
    }

    @Override // android.webkit.WebSettings
    public synchronized void setAppCacheMaxSize(long appCacheMaxSize) {
    }

    @Override // android.webkit.WebSettings
    public synchronized void setDatabaseEnabled(boolean flag) {
        this.mAwSettings.setDatabaseEnabled(flag);
    }

    @Override // android.webkit.WebSettings
    public synchronized void setDomStorageEnabled(boolean flag) {
        this.mAwSettings.setDomStorageEnabled(flag);
    }

    @Override // android.webkit.WebSettings
    public synchronized boolean getDomStorageEnabled() {
        return this.mAwSettings.getDomStorageEnabled();
    }

    @Override // android.webkit.WebSettings
    public synchronized String getDatabasePath() {
        return "";
    }

    @Override // android.webkit.WebSettings
    public synchronized boolean getDatabaseEnabled() {
        return this.mAwSettings.getDatabaseEnabled();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setGeolocationEnabled(boolean flag) {
        this.mAwSettings.setGeolocationEnabled(flag);
    }

    @Override // android.webkit.WebSettings
    public synchronized boolean getJavaScriptEnabled() {
        return this.mAwSettings.getJavaScriptEnabled();
    }

    @Override // android.webkit.WebSettings
    public boolean getAllowUniversalAccessFromFileURLs() {
        return this.mAwSettings.getAllowUniversalAccessFromFileURLs();
    }

    @Override // android.webkit.WebSettings
    public boolean getAllowFileAccessFromFileURLs() {
        return this.mAwSettings.getAllowFileAccessFromFileURLs();
    }

    @Override // android.webkit.WebSettings
    public synchronized boolean getPluginsEnabled() {
        return this.mAwSettings.getPluginsEnabled();
    }

    @Override // android.webkit.WebSettings
    public synchronized WebSettings.PluginState getPluginState() {
        return this.mAwSettings.getPluginState();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setJavaScriptCanOpenWindowsAutomatically(boolean flag) {
        this.mAwSettings.setJavaScriptCanOpenWindowsAutomatically(flag);
    }

    @Override // android.webkit.WebSettings
    public synchronized boolean getJavaScriptCanOpenWindowsAutomatically() {
        return this.mAwSettings.getJavaScriptCanOpenWindowsAutomatically();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setDefaultTextEncodingName(String encoding) {
        this.mAwSettings.setDefaultTextEncodingName(encoding);
    }

    @Override // android.webkit.WebSettings
    public synchronized String getDefaultTextEncodingName() {
        return this.mAwSettings.getDefaultTextEncodingName();
    }

    @Override // android.webkit.WebSettings
    public synchronized void setUserAgentString(String ua) {
        this.mAwSettings.setUserAgentString(ua);
    }

    @Override // android.webkit.WebSettings
    public synchronized String getUserAgentString() {
        return this.mAwSettings.getUserAgentString();
    }

    @Override // android.webkit.WebSettings
    public void setNeedInitialFocus(boolean flag) {
        this.mAwSettings.setShouldFocusFirstNode(flag);
    }

    @Override // android.webkit.WebSettings
    public synchronized void setRenderPriority(WebSettings.RenderPriority priority) {
    }

    @Override // android.webkit.WebSettings
    public void setCacheMode(int mode) {
        this.mAwSettings.setCacheMode(mode);
    }

    @Override // android.webkit.WebSettings
    public int getCacheMode() {
        return this.mAwSettings.getCacheMode();
    }

    @Override // android.webkit.WebSettings
    public void setMediaPlaybackRequiresUserGesture(boolean require) {
        this.mAwSettings.setMediaPlaybackRequiresUserGesture(require);
    }

    @Override // android.webkit.WebSettings
    public boolean getMediaPlaybackRequiresUserGesture() {
        return this.mAwSettings.getMediaPlaybackRequiresUserGesture();
    }
}