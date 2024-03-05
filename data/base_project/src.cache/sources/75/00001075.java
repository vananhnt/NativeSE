package android.support.v4.text;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import java.util.Locale;

/* loaded from: TextUtilsCompat.class */
public class TextUtilsCompat {
    public static final Locale ROOT = new Locale("", "");
    private static String ARAB_SCRIPT_SUBTAG = "Arab";
    private static String HEBR_SCRIPT_SUBTAG = "Hebr";

    private static int getLayoutDirectionFromFirstChar(Locale locale) {
        switch (Character.getDirectionality(locale.getDisplayName(locale).charAt(0))) {
            case 1:
            case 2:
                return 1;
            default:
                return 0;
        }
    }

    public static int getLayoutDirectionFromLocale(@Nullable Locale locale) {
        if (locale == null || locale.equals(ROOT)) {
            return 0;
        }
        String script = ICUCompat.getScript(ICUCompat.addLikelySubtags(locale.toString()));
        return script == null ? getLayoutDirectionFromFirstChar(locale) : (script.equalsIgnoreCase(ARAB_SCRIPT_SUBTAG) || script.equalsIgnoreCase(HEBR_SCRIPT_SUBTAG)) ? 1 : 0;
    }

    @NonNull
    public static String htmlEncode(@NonNull String str) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char charAt = str.charAt(i);
            if (charAt == '\"') {
                sb.append("&quot;");
            } else if (charAt == '<') {
                sb.append("&lt;");
            } else if (charAt != '>') {
                switch (charAt) {
                    case '&':
                        sb.append("&amp;");
                        continue;
                    case '\'':
                        sb.append("&#39;");
                        continue;
                    default:
                        sb.append(charAt);
                        continue;
                }
            } else {
                sb.append("&gt;");
            }
        }
        return sb.toString();
    }
}