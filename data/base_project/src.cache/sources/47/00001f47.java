package com.android.server.pm;

/* loaded from: PackageVerificationResponse.class */
public class PackageVerificationResponse {
    public final int code;
    public final int callerUid;

    public PackageVerificationResponse(int code, int callerUid) {
        this.code = code;
        this.callerUid = callerUid;
    }
}