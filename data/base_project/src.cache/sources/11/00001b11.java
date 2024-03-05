package com.android.internal.util;

import android.os.Message;

/* loaded from: IState.class */
public interface IState {
    public static final boolean HANDLED = true;
    public static final boolean NOT_HANDLED = false;

    void enter();

    void exit();

    boolean processMessage(Message message);

    String getName();
}