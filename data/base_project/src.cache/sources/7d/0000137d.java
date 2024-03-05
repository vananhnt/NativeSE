package android.text;

import android.app.backup.FullBackup;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.provider.CalendarContract;
import android.provider.Telephony;
import android.text.Html;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.ParagraphStyle;
import android.text.style.QuoteSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.SubscriptSpan;
import android.text.style.SuperscriptSpan;
import android.text.style.TextAppearanceSpan;
import android.text.style.TypefaceSpan;
import android.text.style.URLSpan;
import android.text.style.UnderlineSpan;
import com.android.internal.R;
import gov.nist.core.Separators;
import java.io.IOException;
import java.io.StringReader;
import org.ccil.cowan.tagsoup.Parser;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/* compiled from: Html.java */
/* loaded from: HtmlToSpannedConverter.class */
class HtmlToSpannedConverter implements ContentHandler {
    private static final float[] HEADER_SIZES = {1.5f, 1.4f, 1.3f, 1.2f, 1.1f, 1.0f};
    private String mSource;
    private XMLReader mReader;
    private SpannableStringBuilder mSpannableStringBuilder = new SpannableStringBuilder();
    private Html.ImageGetter mImageGetter;
    private Html.TagHandler mTagHandler;

    public HtmlToSpannedConverter(String source, Html.ImageGetter imageGetter, Html.TagHandler tagHandler, Parser parser) {
        this.mSource = source;
        this.mImageGetter = imageGetter;
        this.mTagHandler = tagHandler;
        this.mReader = parser;
    }

    public Spanned convert() {
        this.mReader.setContentHandler(this);
        try {
            this.mReader.parse(new InputSource(new StringReader(this.mSource)));
            Object[] obj = this.mSpannableStringBuilder.getSpans(0, this.mSpannableStringBuilder.length(), ParagraphStyle.class);
            for (int i = 0; i < obj.length; i++) {
                int start = this.mSpannableStringBuilder.getSpanStart(obj[i]);
                int end = this.mSpannableStringBuilder.getSpanEnd(obj[i]);
                if (end - 2 >= 0 && this.mSpannableStringBuilder.charAt(end - 1) == '\n' && this.mSpannableStringBuilder.charAt(end - 2) == '\n') {
                    end--;
                }
                if (end == start) {
                    this.mSpannableStringBuilder.removeSpan(obj[i]);
                } else {
                    this.mSpannableStringBuilder.setSpan(obj[i], start, end, 51);
                }
            }
            return this.mSpannableStringBuilder;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e2) {
            throw new RuntimeException(e2);
        }
    }

    private void handleStartTag(String tag, Attributes attributes) {
        if (!tag.equalsIgnoreCase("br")) {
            if (tag.equalsIgnoreCase("p")) {
                handleP(this.mSpannableStringBuilder);
            } else if (tag.equalsIgnoreCase("div")) {
                handleP(this.mSpannableStringBuilder);
            } else if (tag.equalsIgnoreCase("strong")) {
                start(this.mSpannableStringBuilder, new Bold());
            } else if (tag.equalsIgnoreCase("b")) {
                start(this.mSpannableStringBuilder, new Bold());
            } else if (tag.equalsIgnoreCase("em")) {
                start(this.mSpannableStringBuilder, new Italic());
            } else if (tag.equalsIgnoreCase("cite")) {
                start(this.mSpannableStringBuilder, new Italic());
            } else if (tag.equalsIgnoreCase("dfn")) {
                start(this.mSpannableStringBuilder, new Italic());
            } else if (tag.equalsIgnoreCase("i")) {
                start(this.mSpannableStringBuilder, new Italic());
            } else if (tag.equalsIgnoreCase("big")) {
                start(this.mSpannableStringBuilder, new Big());
            } else if (tag.equalsIgnoreCase("small")) {
                start(this.mSpannableStringBuilder, new Small());
            } else if (tag.equalsIgnoreCase("font")) {
                startFont(this.mSpannableStringBuilder, attributes);
            } else if (tag.equalsIgnoreCase("blockquote")) {
                handleP(this.mSpannableStringBuilder);
                start(this.mSpannableStringBuilder, new Blockquote());
            } else if (tag.equalsIgnoreCase("tt")) {
                start(this.mSpannableStringBuilder, new Monospace());
            } else if (tag.equalsIgnoreCase(FullBackup.APK_TREE_TOKEN)) {
                startA(this.mSpannableStringBuilder, attributes);
            } else if (tag.equalsIgnoreCase("u")) {
                start(this.mSpannableStringBuilder, new Underline());
            } else if (tag.equalsIgnoreCase("sup")) {
                start(this.mSpannableStringBuilder, new Super());
            } else if (tag.equalsIgnoreCase(Telephony.BaseMmsColumns.SUBJECT)) {
                start(this.mSpannableStringBuilder, new Sub());
            } else if (tag.length() == 2 && Character.toLowerCase(tag.charAt(0)) == 'h' && tag.charAt(1) >= '1' && tag.charAt(1) <= '6') {
                handleP(this.mSpannableStringBuilder);
                start(this.mSpannableStringBuilder, new Header(tag.charAt(1) - '1'));
            } else if (tag.equalsIgnoreCase("img")) {
                startImg(this.mSpannableStringBuilder, attributes, this.mImageGetter);
            } else if (this.mTagHandler != null) {
                this.mTagHandler.handleTag(true, tag, this.mSpannableStringBuilder, this.mReader);
            }
        }
    }

    private void handleEndTag(String tag) {
        if (tag.equalsIgnoreCase("br")) {
            handleBr(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("p")) {
            handleP(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("div")) {
            handleP(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("strong")) {
            end(this.mSpannableStringBuilder, Bold.class, new StyleSpan(1));
        } else if (tag.equalsIgnoreCase("b")) {
            end(this.mSpannableStringBuilder, Bold.class, new StyleSpan(1));
        } else if (tag.equalsIgnoreCase("em")) {
            end(this.mSpannableStringBuilder, Italic.class, new StyleSpan(2));
        } else if (tag.equalsIgnoreCase("cite")) {
            end(this.mSpannableStringBuilder, Italic.class, new StyleSpan(2));
        } else if (tag.equalsIgnoreCase("dfn")) {
            end(this.mSpannableStringBuilder, Italic.class, new StyleSpan(2));
        } else if (tag.equalsIgnoreCase("i")) {
            end(this.mSpannableStringBuilder, Italic.class, new StyleSpan(2));
        } else if (tag.equalsIgnoreCase("big")) {
            end(this.mSpannableStringBuilder, Big.class, new RelativeSizeSpan(1.25f));
        } else if (tag.equalsIgnoreCase("small")) {
            end(this.mSpannableStringBuilder, Small.class, new RelativeSizeSpan(0.8f));
        } else if (tag.equalsIgnoreCase("font")) {
            endFont(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("blockquote")) {
            handleP(this.mSpannableStringBuilder);
            end(this.mSpannableStringBuilder, Blockquote.class, new QuoteSpan());
        } else if (tag.equalsIgnoreCase("tt")) {
            end(this.mSpannableStringBuilder, Monospace.class, new TypefaceSpan("monospace"));
        } else if (tag.equalsIgnoreCase(FullBackup.APK_TREE_TOKEN)) {
            endA(this.mSpannableStringBuilder);
        } else if (tag.equalsIgnoreCase("u")) {
            end(this.mSpannableStringBuilder, Underline.class, new UnderlineSpan());
        } else if (tag.equalsIgnoreCase("sup")) {
            end(this.mSpannableStringBuilder, Super.class, new SuperscriptSpan());
        } else if (tag.equalsIgnoreCase(Telephony.BaseMmsColumns.SUBJECT)) {
            end(this.mSpannableStringBuilder, Sub.class, new SubscriptSpan());
        } else if (tag.length() == 2 && Character.toLowerCase(tag.charAt(0)) == 'h' && tag.charAt(1) >= '1' && tag.charAt(1) <= '6') {
            handleP(this.mSpannableStringBuilder);
            endHeader(this.mSpannableStringBuilder);
        } else if (this.mTagHandler != null) {
            this.mTagHandler.handleTag(false, tag, this.mSpannableStringBuilder, this.mReader);
        }
    }

    private static void handleP(SpannableStringBuilder text) {
        int len = text.length();
        if (len >= 1 && text.charAt(len - 1) == '\n') {
            if (len >= 2 && text.charAt(len - 2) == '\n') {
                return;
            }
            text.append(Separators.RETURN);
        } else if (len != 0) {
            text.append("\n\n");
        }
    }

    private static void handleBr(SpannableStringBuilder text) {
        text.append(Separators.RETURN);
    }

    private static Object getLast(Spanned text, Class kind) {
        Object[] objs = text.getSpans(0, text.length(), kind);
        if (objs.length == 0) {
            return null;
        }
        return objs[objs.length - 1];
    }

    private static void start(SpannableStringBuilder text, Object mark) {
        int len = text.length();
        text.setSpan(mark, len, len, 17);
    }

    private static void end(SpannableStringBuilder text, Class kind, Object repl) {
        int len = text.length();
        Object obj = getLast(text, kind);
        int where = text.getSpanStart(obj);
        text.removeSpan(obj);
        if (where != len) {
            text.setSpan(repl, where, len, 33);
        }
    }

    private static void startImg(SpannableStringBuilder text, Attributes attributes, Html.ImageGetter img) {
        String src = attributes.getValue("", "src");
        Drawable d = null;
        if (img != null) {
            d = img.getDrawable(src);
        }
        if (d == null) {
            d = Resources.getSystem().getDrawable(R.drawable.unknown_image);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
        }
        int len = text.length();
        text.append("￼");
        text.setSpan(new ImageSpan(d, src), len, text.length(), 33);
    }

    private static void startFont(SpannableStringBuilder text, Attributes attributes) {
        String color = attributes.getValue("", CalendarContract.ColorsColumns.COLOR);
        String face = attributes.getValue("", "face");
        int len = text.length();
        text.setSpan(new Font(color, face), len, len, 17);
    }

    private static void endFont(SpannableStringBuilder text) {
        int len = text.length();
        Object obj = getLast(text, Font.class);
        int where = text.getSpanStart(obj);
        text.removeSpan(obj);
        if (where != len) {
            Font f = (Font) obj;
            if (!TextUtils.isEmpty(f.mColor)) {
                if (f.mColor.startsWith(Separators.AT)) {
                    Resources res = Resources.getSystem();
                    String name = f.mColor.substring(1);
                    int colorRes = res.getIdentifier(name, CalendarContract.ColorsColumns.COLOR, "android");
                    if (colorRes != 0) {
                        ColorStateList colors = res.getColorStateList(colorRes);
                        text.setSpan(new TextAppearanceSpan(null, 0, 0, colors, null), where, len, 33);
                    }
                } else {
                    int c = Color.getHtmlColor(f.mColor);
                    if (c != -1) {
                        text.setSpan(new ForegroundColorSpan(c | (-16777216)), where, len, 33);
                    }
                }
            }
            if (f.mFace != null) {
                text.setSpan(new TypefaceSpan(f.mFace), where, len, 33);
            }
        }
    }

    private static void startA(SpannableStringBuilder text, Attributes attributes) {
        String href = attributes.getValue("", "href");
        int len = text.length();
        text.setSpan(new Href(href), len, len, 17);
    }

    private static void endA(SpannableStringBuilder text) {
        int len = text.length();
        Object obj = getLast(text, Href.class);
        int where = text.getSpanStart(obj);
        text.removeSpan(obj);
        if (where != len) {
            Href h = (Href) obj;
            if (h.mHref != null) {
                text.setSpan(new URLSpan(h.mHref), where, len, 33);
            }
        }
    }

    private static void endHeader(SpannableStringBuilder text) {
        int len = text.length();
        Object obj = getLast(text, Header.class);
        int where = text.getSpanStart(obj);
        text.removeSpan(obj);
        while (len > where && text.charAt(len - 1) == '\n') {
            len--;
        }
        if (where != len) {
            Header h = (Header) obj;
            text.setSpan(new RelativeSizeSpan(HEADER_SIZES[h.mLevel]), where, len, 33);
            text.setSpan(new StyleSpan(1), where, len, 33);
        }
    }

    @Override // org.xml.sax.ContentHandler
    public void setDocumentLocator(Locator locator) {
    }

    @Override // org.xml.sax.ContentHandler
    public void startDocument() throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void endDocument() throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void endPrefixMapping(String prefix) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        handleStartTag(localName, attributes);
    }

    @Override // org.xml.sax.ContentHandler
    public void endElement(String uri, String localName, String qName) throws SAXException {
        handleEndTag(localName);
    }

    @Override // org.xml.sax.ContentHandler
    public void characters(char[] ch, int start, int length) throws SAXException {
        char pred;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            char c = ch[i + start];
            if (c == ' ' || c == '\n') {
                int len = sb.length();
                if (len == 0) {
                    int len2 = this.mSpannableStringBuilder.length();
                    if (len2 == 0) {
                        pred = '\n';
                    } else {
                        pred = this.mSpannableStringBuilder.charAt(len2 - 1);
                    }
                } else {
                    pred = sb.charAt(len - 1);
                }
                if (pred != ' ' && pred != '\n') {
                    sb.append(' ');
                }
            } else {
                sb.append(c);
            }
        }
        this.mSpannableStringBuilder.append((CharSequence) sb);
    }

    @Override // org.xml.sax.ContentHandler
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void processingInstruction(String target, String data) throws SAXException {
    }

    @Override // org.xml.sax.ContentHandler
    public void skippedEntity(String name) throws SAXException {
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: HtmlToSpannedConverter$Bold.class */
    public static class Bold {
        private Bold() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: HtmlToSpannedConverter$Italic.class */
    public static class Italic {
        private Italic() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: HtmlToSpannedConverter$Underline.class */
    public static class Underline {
        private Underline() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: HtmlToSpannedConverter$Big.class */
    public static class Big {
        private Big() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: HtmlToSpannedConverter$Small.class */
    public static class Small {
        private Small() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: HtmlToSpannedConverter$Monospace.class */
    public static class Monospace {
        private Monospace() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: HtmlToSpannedConverter$Blockquote.class */
    public static class Blockquote {
        private Blockquote() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: HtmlToSpannedConverter$Super.class */
    public static class Super {
        private Super() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: HtmlToSpannedConverter$Sub.class */
    public static class Sub {
        private Sub() {
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: HtmlToSpannedConverter$Font.class */
    public static class Font {
        public String mColor;
        public String mFace;

        public Font(String color, String face) {
            this.mColor = color;
            this.mFace = face;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: HtmlToSpannedConverter$Href.class */
    public static class Href {
        public String mHref;

        public Href(String href) {
            this.mHref = href;
        }
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* compiled from: Html.java */
    /* loaded from: HtmlToSpannedConverter$Header.class */
    public static class Header {
        private int mLevel;

        public Header(int level) {
            this.mLevel = level;
        }
    }
}