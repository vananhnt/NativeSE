package android.text.format;

import android.content.Context;
import android.net.NetworkUtils;
import com.android.internal.R;

/* loaded from: Formatter.class */
public final class Formatter {
    public static String formatFileSize(Context context, long number) {
        return formatFileSize(context, number, false);
    }

    public static String formatShortFileSize(Context context, long number) {
        return formatFileSize(context, number, true);
    }

    private static String formatFileSize(Context context, long number, boolean shorter) {
        String value;
        if (context == null) {
            return "";
        }
        float result = (float) number;
        int suffix = 17039432;
        if (result > 900.0f) {
            suffix = 17039433;
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = 17039434;
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = 17039435;
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = 17039436;
            result /= 1024.0f;
        }
        if (result > 900.0f) {
            suffix = 17039437;
            result /= 1024.0f;
        }
        if (result < 1.0f) {
            value = String.format("%.2f", Float.valueOf(result));
        } else if (result < 10.0f) {
            if (shorter) {
                value = String.format("%.1f", Float.valueOf(result));
            } else {
                value = String.format("%.2f", Float.valueOf(result));
            }
        } else if (result < 100.0f) {
            if (shorter) {
                value = String.format("%.0f", Float.valueOf(result));
            } else {
                value = String.format("%.2f", Float.valueOf(result));
            }
        } else {
            value = String.format("%.0f", Float.valueOf(result));
        }
        return context.getResources().getString(R.string.fileSizeSuffix, value, context.getString(suffix));
    }

    @Deprecated
    public static String formatIpAddress(int ipv4Address) {
        return NetworkUtils.intToInetAddress(ipv4Address).getHostAddress();
    }
}