package android.text;

import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.AlignmentSpan;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.ParagraphStyle;
import android.text.style.QuoteSpan;
import android.text.style.StrikethroughSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import com.android.internal.util.ArrayUtils;
import gov.nist.core.Separators;
import org.ccil.cowan.tagsoup.HTMLSchema;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

/* loaded from: Html.class */
public class Html {

    /* loaded from: Html$ImageGetter.class */
    public interface ImageGetter {
        Drawable getDrawable(String str);
    }

    /* loaded from: Html$TagHandler.class */
    public interface TagHandler {
        void handleTag(boolean z, String str, Editable editable, XMLReader xMLReader);
    }

    private Html() {
    }

    public static Spanned fromHtml(String source) {
        return fromHtml(source, null, null);
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Html$HtmlParser.class */
    public static class HtmlParser {
        private static final HTMLSchema schema = new HTMLSchema();

        private HtmlParser() {
        }
    }

    public static Spanned fromHtml(String source, ImageGetter imageGetter, TagHandler tagHandler) {
        Parser parser = new Parser();
        try {
            parser.setProperty(Parser.schemaProperty, HtmlParser.schema);
            HtmlToSpannedConverter converter = new HtmlToSpannedConverter(source, imageGetter, tagHandler, parser);
            return converter.convert();
        } catch (SAXNotRecognizedException e) {
            throw new RuntimeException(e);
        } catch (SAXNotSupportedException e2) {
            throw new RuntimeException(e2);
        }
    }

    public static String toHtml(Spanned text) {
        StringBuilder out = new StringBuilder();
        withinHtml(out, text);
        return out.toString();
    }

    public static String escapeHtml(CharSequence text) {
        StringBuilder out = new StringBuilder();
        withinStyle(out, text, 0, text.length());
        return out.toString();
    }

    private static void withinHtml(StringBuilder out, Spanned text) {
        int len = text.length();
        int i = 0;
        while (true) {
            int i2 = i;
            if (i2 < text.length()) {
                int next = text.nextSpanTransition(i2, len, ParagraphStyle.class);
                ParagraphStyle[] style = (ParagraphStyle[]) text.getSpans(i2, next, ParagraphStyle.class);
                String elements = Separators.SP;
                boolean needDiv = false;
                for (int j = 0; j < style.length; j++) {
                    if (style[j] instanceof AlignmentSpan) {
                        Layout.Alignment align = ((AlignmentSpan) style[j]).getAlignment();
                        needDiv = true;
                        if (align == Layout.Alignment.ALIGN_CENTER) {
                            elements = "align=\"center\" " + elements;
                        } else if (align == Layout.Alignment.ALIGN_OPPOSITE) {
                            elements = "align=\"right\" " + elements;
                        } else {
                            elements = "align=\"left\" " + elements;
                        }
                    }
                }
                if (needDiv) {
                    out.append("<div ").append(elements).append(Separators.GREATER_THAN);
                }
                withinDiv(out, text, i2, next);
                if (needDiv) {
                    out.append("</div>");
                }
                i = next;
            } else {
                return;
            }
        }
    }

    private static void withinDiv(StringBuilder out, Spanned text, int start, int end) {
        int i = start;
        while (true) {
            int i2 = i;
            if (i2 < end) {
                int next = text.nextSpanTransition(i2, end, QuoteSpan.class);
                QuoteSpan[] quotes = (QuoteSpan[]) text.getSpans(i2, next, QuoteSpan.class);
                for (QuoteSpan quoteSpan : quotes) {
                    out.append("<blockquote>");
                }
                withinBlockquote(out, text, i2, next);
                for (QuoteSpan quoteSpan2 : quotes) {
                    out.append("</blockquote>\n");
                }
                i = next;
            } else {
                return;
            }
        }
    }

    private static String getOpenParaTagWithDirection(Spanned text, int start, int end) {
        int len = end - start;
        byte[] levels = new byte[ArrayUtils.idealByteArraySize(len)];
        char[] buffer = TextUtils.obtain(len);
        TextUtils.getChars(text, start, end, buffer, 0);
        int paraDir = AndroidBidi.bidi(2, buffer, levels, len, false);
        switch (paraDir) {
            case -1:
                return "<p dir=\"rtl\">";
            case 1:
            default:
                return "<p dir=\"ltr\">";
        }
    }

    private static void withinBlockquote(StringBuilder out, Spanned text, int start, int end) {
        out.append(getOpenParaTagWithDirection(text, start, end));
        int i = start;
        while (true) {
            int i2 = i;
            if (i2 < end) {
                int next = TextUtils.indexOf((CharSequence) text, '\n', i2, end);
                if (next < 0) {
                    next = end;
                }
                int nl = 0;
                while (next < end && text.charAt(next) == '\n') {
                    nl++;
                    next++;
                }
                withinParagraph(out, text, i2, next - nl, nl, next == end);
                i = next;
            } else {
                out.append("</p>\n");
                return;
            }
        }
    }

    private static void withinParagraph(StringBuilder out, Spanned text, int start, int end, int nl, boolean last) {
        String color;
        int i = start;
        while (true) {
            int i2 = i;
            if (i2 >= end) {
                break;
            }
            int next = text.nextSpanTransition(i2, end, CharacterStyle.class);
            CharacterStyle[] style = (CharacterStyle[]) text.getSpans(i2, next, CharacterStyle.class);
            for (int j = 0; j < style.length; j++) {
                if (style[j] instanceof StyleSpan) {
                    int s = ((StyleSpan) style[j]).getStyle();
                    if ((s & 1) != 0) {
                        out.append("<b>");
                    }
                    if ((s & 2) != 0) {
                        out.append("<i>");
                    }
                }
                if ((style[j] instanceof TypefaceSpan) && ((TypefaceSpan) style[j]).getFamily().equals("monospace")) {
                    out.append("<tt>");
                }
                if (style[j] instanceof SuperscriptSpan) {
                    out.append("<sup>");
                }
                if (style[j] instanceof SubscriptSpan) {
                    out.append("<sub>");
                }
                if (style[j] instanceof UnderlineSpan) {
                    out.append("<u>");
                }
                if (style[j] instanceof StrikethroughSpan) {
                    out.append("<strike>");
                }
                if (style[j] instanceof URLSpan) {
                    out.append("<a href=\"");
                    out.append(((URLSpan) style[j]).getURL());
                    out.append("\">");
                }
                if (style[j] instanceof ImageSpan) {
                    out.append("<img src=\"");
                    out.append(((ImageSpan) style[j]).getSource());
                    out.append("\">");
                    i2 = next;
                }
                if (style[j] instanceof AbsoluteSizeSpan) {
                    out.append("<font size =\"");
                    out.append(((AbsoluteSizeSpan) style[j]).getSize() / 6);
                    out.append("\">");
                }
                if (style[j] instanceof ForegroundColorSpan) {
                    out.append("<font color =\"#");
                    String hexString = Integer.toHexString(((ForegroundColorSpan) style[j]).getForegroundColor() + 16777216);
                    while (true) {
                        color = hexString;
                        if (color.length() >= 6) {
                            break;
                        }
                        hexString = "0" + color;
                    }
                    out.append(color);
                    out.append("\">");
                }
            }
            withinStyle(out, text, i2, next);
            for (int j2 = style.length - 1; j2 >= 0; j2--) {
                if (style[j2] instanceof ForegroundColorSpan) {
                    out.append("</font>");
                }
                if (style[j2] instanceof AbsoluteSizeSpan) {
                    out.append("</font>");
                }
                if (style[j2] instanceof URLSpan) {
                    out.append("</a>");
                }
                if (style[j2] instanceof StrikethroughSpan) {
                    out.append("</strike>");
                }
                if (style[j2] instanceof UnderlineSpan) {
                    out.append("</u>");
                }
                if (style[j2] instanceof SubscriptSpan) {
                    out.append("</sub>");
                }
                if (style[j2] instanceof SuperscriptSpan) {
                    out.append("</sup>");
                }
                if ((style[j2] instanceof TypefaceSpan) && ((TypefaceSpan) style[j2]).getFamily().equals("monospace")) {
                    out.append("</tt>");
                }
                if (style[j2] instanceof StyleSpan) {
                    int s2 = ((StyleSpan) style[j2]).getStyle();
                    if ((s2 & 1) != 0) {
                        out.append("</b>");
                    }
                    if ((s2 & 2) != 0) {
                        out.append("</i>");
                    }
                }
            }
            i = next;
        }
        String p = last ? "" : "</p>\n" + getOpenParaTagWithDirection(text, start, end);
        if (nl == 1) {
            out.append("<br>\n");
        } else if (nl == 2) {
            out.append(p);
        } else {
            for (int i3 = 2; i3 < nl; i3++) {
                out.append("<br>");
            }
            out.append(p);
        }
    }

    private static void withinStyle(StringBuilder out, CharSequence text, int start, int end) {
        int i = start;
        while (i < end) {
            char c = text.charAt(i);
            if (c == '<') {
                out.append("&lt;");
            } else if (c == '>') {
                out.append("&gt;");
            } else if (c == '&') {
                out.append("&amp;");
            } else if (c > '~' || c < ' ') {
                out.append("&#").append((int) c).append(Separators.SEMICOLON);
            } else if (c == ' ') {
                while (i + 1 < end && text.charAt(i + 1) == ' ') {
                    out.append("&nbsp;");
                    i++;
                }
                out.append(' ');
            } else {
                out.append(c);
            }
            i++;
        }
    }
}