package android.appwidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/* loaded from: AppWidgetProvider.class */
public class AppWidgetProvider extends BroadcastReceiver {
    @Override // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        int[] appWidgetIds;
        String action = intent.getAction();
        if (AppWidgetManager.ACTION_APPWIDGET_UPDATE.equals(action)) {
            Bundle extras = intent.getExtras();
            if (extras != null && (appWidgetIds = extras.getIntArray(AppWidgetManager.EXTRA_APPWIDGET_IDS)) != null && appWidgetIds.length > 0) {
                onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_DELETED.equals(action)) {
            Bundle extras2 = intent.getExtras();
            if (extras2 != null && extras2.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
                int appWidgetId = extras2.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                onDeleted(context, new int[]{appWidgetId});
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_OPTIONS_CHANGED.equals(action)) {
            Bundle extras3 = intent.getExtras();
            if (extras3 != null && extras3.containsKey(AppWidgetManager.EXTRA_APPWIDGET_ID) && extras3.containsKey(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS)) {
                int appWidgetId2 = extras3.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID);
                Bundle widgetExtras = extras3.getBundle(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS);
                onAppWidgetOptionsChanged(context, AppWidgetManager.getInstance(context), appWidgetId2, widgetExtras);
            }
        } else if (AppWidgetManager.ACTION_APPWIDGET_ENABLED.equals(action)) {
            onEnabled(context);
        } else if (AppWidgetManager.ACTION_APPWIDGET_DISABLED.equals(action)) {
            onDisabled(context);
        }
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
    }

    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
    }

    public void onDeleted(Context context, int[] appWidgetIds) {
    }

    public void onEnabled(Context context) {
    }

    public void onDisabled(Context context) {
    }
}