package android.widget;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/* loaded from: ResourceCursorAdapter.class */
public abstract class ResourceCursorAdapter extends CursorAdapter {
    private int mLayout;
    private int mDropDownLayout;
    private LayoutInflater mInflater;

    @Deprecated
    public ResourceCursorAdapter(Context context, int layout, Cursor c) {
        super(context, c);
        this.mDropDownLayout = layout;
        this.mLayout = layout;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ResourceCursorAdapter(Context context, int layout, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
        this.mDropDownLayout = layout;
        this.mLayout = layout;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public ResourceCursorAdapter(Context context, int layout, Cursor c, int flags) {
        super(context, c, flags);
        this.mDropDownLayout = layout;
        this.mLayout = layout;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override // android.widget.CursorAdapter
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return this.mInflater.inflate(this.mLayout, parent, false);
    }

    @Override // android.widget.CursorAdapter
    public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
        return this.mInflater.inflate(this.mDropDownLayout, parent, false);
    }

    public void setViewResource(int layout) {
        this.mLayout = layout;
    }

    public void setDropDownViewResource(int dropDownLayout) {
        this.mDropDownLayout = dropDownLayout;
    }
}