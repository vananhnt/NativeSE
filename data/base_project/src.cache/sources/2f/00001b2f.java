package com.android.internal.view;

import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.RemoteException;
import android.view.DragEvent;
import android.view.IWindow;
import android.view.IWindowSession;

/* loaded from: BaseIWindow.class */
public class BaseIWindow extends IWindow.Stub {
    private IWindowSession mSession;
    public int mSeq;

    public void setSession(IWindowSession session) {
        this.mSession = session;
    }

    public void resized(Rect frame, Rect overscanInsets, Rect contentInsets, Rect visibleInsets, boolean reportDraw, Configuration newConfig) {
        if (reportDraw) {
            try {
                this.mSession.finishDrawing(this);
            } catch (RemoteException e) {
            }
        }
    }

    public void moved(int newX, int newY) {
    }

    public void dispatchAppVisibility(boolean visible) {
    }

    public void dispatchGetNewSurface() {
    }

    @Override // android.view.IWindow
    public void dispatchScreenState(boolean on) {
    }

    public void windowFocusChanged(boolean hasFocus, boolean touchEnabled) {
    }

    public void executeCommand(String command, String parameters, ParcelFileDescriptor out) {
    }

    @Override // android.view.IWindow
    public void closeSystemDialogs(String reason) {
    }

    public void dispatchWallpaperOffsets(float x, float y, float xStep, float yStep, boolean sync) {
        if (sync) {
            try {
                this.mSession.wallpaperOffsetsComplete(asBinder());
            } catch (RemoteException e) {
            }
        }
    }

    @Override // android.view.IWindow
    public void dispatchDragEvent(DragEvent event) {
    }

    @Override // android.view.IWindow
    public void dispatchSystemUiVisibilityChanged(int seq, int globalUi, int localValue, int localChanges) {
        this.mSeq = seq;
    }

    public void dispatchWallpaperCommand(String action, int x, int y, int z, Bundle extras, boolean sync) {
        if (sync) {
            try {
                this.mSession.wallpaperCommandComplete(asBinder(), null);
            } catch (RemoteException e) {
            }
        }
    }

    @Override // android.view.IWindow
    public void doneAnimating() {
    }
}