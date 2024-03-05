package android.speech.tts;

import android.os.Bundle;

/* loaded from: SynthesisRequest.class */
public final class SynthesisRequest {
    private final String mText;
    private final Bundle mParams;
    private String mLanguage;
    private String mCountry;
    private String mVariant;
    private int mSpeechRate;
    private int mPitch;
    private int mCallerUid;

    public SynthesisRequest(String text, Bundle params) {
        this.mText = text;
        this.mParams = new Bundle(params);
    }

    public String getText() {
        return this.mText;
    }

    public String getLanguage() {
        return this.mLanguage;
    }

    public String getCountry() {
        return this.mCountry;
    }

    public String getVariant() {
        return this.mVariant;
    }

    public int getSpeechRate() {
        return this.mSpeechRate;
    }

    public int getPitch() {
        return this.mPitch;
    }

    public Bundle getParams() {
        return this.mParams;
    }

    public int getCallerUid() {
        return this.mCallerUid;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setLanguage(String language, String country, String variant) {
        this.mLanguage = language;
        this.mCountry = country;
        this.mVariant = variant;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setSpeechRate(int speechRate) {
        this.mSpeechRate = speechRate;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setPitch(int pitch) {
        this.mPitch = pitch;
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void setCallerUid(int uid) {
        this.mCallerUid = uid;
    }
}