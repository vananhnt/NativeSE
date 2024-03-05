package android.renderscript;

import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/* loaded from: Font.class */
public class Font extends BaseObj {
    private static final String[] sSansNames = {"sans-serif", "arial", "helvetica", "tahoma", "verdana"};
    private static final String[] sSerifNames = {"serif", "times", "times new roman", "palatino", "georgia", "baskerville", "goudy", "fantasy", "cursive", "ITC Stone Serif"};
    private static final String[] sMonoNames = {"monospace", "courier", "courier new", "monaco"};
    private static Map<String, FontFamily> sFontFamilyMap;

    /* loaded from: Font$Style.class */
    public enum Style {
        NORMAL,
        BOLD,
        ITALIC,
        BOLD_ITALIC
    }

    static {
        initFontFamilyMap();
    }

    /* JADX INFO: Access modifiers changed from: private */
    /* loaded from: Font$FontFamily.class */
    public static class FontFamily {
        String[] mNames;
        String mNormalFileName;
        String mBoldFileName;
        String mItalicFileName;
        String mBoldItalicFileName;

        private FontFamily() {
        }
    }

    private static void addFamilyToMap(FontFamily family) {
        for (int i = 0; i < family.mNames.length; i++) {
            sFontFamilyMap.put(family.mNames[i], family);
        }
    }

    private static void initFontFamilyMap() {
        sFontFamilyMap = new HashMap();
        FontFamily sansFamily = new FontFamily();
        sansFamily.mNames = sSansNames;
        sansFamily.mNormalFileName = "Roboto-Regular.ttf";
        sansFamily.mBoldFileName = "Roboto-Bold.ttf";
        sansFamily.mItalicFileName = "Roboto-Italic.ttf";
        sansFamily.mBoldItalicFileName = "Roboto-BoldItalic.ttf";
        addFamilyToMap(sansFamily);
        FontFamily serifFamily = new FontFamily();
        serifFamily.mNames = sSerifNames;
        serifFamily.mNormalFileName = "DroidSerif-Regular.ttf";
        serifFamily.mBoldFileName = "DroidSerif-Bold.ttf";
        serifFamily.mItalicFileName = "DroidSerif-Italic.ttf";
        serifFamily.mBoldItalicFileName = "DroidSerif-BoldItalic.ttf";
        addFamilyToMap(serifFamily);
        FontFamily monoFamily = new FontFamily();
        monoFamily.mNames = sMonoNames;
        monoFamily.mNormalFileName = "DroidSansMono.ttf";
        monoFamily.mBoldFileName = "DroidSansMono.ttf";
        monoFamily.mItalicFileName = "DroidSansMono.ttf";
        monoFamily.mBoldItalicFileName = "DroidSansMono.ttf";
        addFamilyToMap(monoFamily);
    }

    static String getFontFileName(String familyName, Style style) {
        FontFamily family = sFontFamilyMap.get(familyName);
        if (family != null) {
            switch (style) {
                case NORMAL:
                    return family.mNormalFileName;
                case BOLD:
                    return family.mBoldFileName;
                case ITALIC:
                    return family.mItalicFileName;
                case BOLD_ITALIC:
                    return family.mBoldItalicFileName;
                default:
                    return "DroidSans.ttf";
            }
        }
        return "DroidSans.ttf";
    }

    Font(int id, RenderScript rs) {
        super(id, rs);
    }

    public static Font createFromFile(RenderScript rs, Resources res, String path, float pointSize) {
        rs.validate();
        int dpi = res.getDisplayMetrics().densityDpi;
        int fontId = rs.nFontCreateFromFile(path, pointSize, dpi);
        if (fontId == 0) {
            throw new RSRuntimeException("Unable to create font from file " + path);
        }
        Font rsFont = new Font(fontId, rs);
        return rsFont;
    }

    public static Font createFromFile(RenderScript rs, Resources res, File path, float pointSize) {
        return createFromFile(rs, res, path.getAbsolutePath(), pointSize);
    }

    public static Font createFromAsset(RenderScript rs, Resources res, String path, float pointSize) {
        rs.validate();
        AssetManager mgr = res.getAssets();
        int dpi = res.getDisplayMetrics().densityDpi;
        int fontId = rs.nFontCreateFromAsset(mgr, path, pointSize, dpi);
        if (fontId == 0) {
            throw new RSRuntimeException("Unable to create font from asset " + path);
        }
        Font rsFont = new Font(fontId, rs);
        return rsFont;
    }

    public static Font createFromResource(RenderScript rs, Resources res, int id, float pointSize) {
        String name = "R." + Integer.toString(id);
        rs.validate();
        try {
            InputStream is = res.openRawResource(id);
            int dpi = res.getDisplayMetrics().densityDpi;
            if (is instanceof AssetManager.AssetInputStream) {
                int asset = ((AssetManager.AssetInputStream) is).getAssetInt();
                int fontId = rs.nFontCreateFromAssetStream(name, pointSize, dpi, asset);
                if (fontId == 0) {
                    throw new RSRuntimeException("Unable to create font from resource " + id);
                }
                Font rsFont = new Font(fontId, rs);
                return rsFont;
            }
            throw new RSRuntimeException("Unsupported asset stream created");
        } catch (Exception e) {
            throw new RSRuntimeException("Unable to open resource " + id);
        }
    }

    public static Font create(RenderScript rs, Resources res, String familyName, Style fontStyle, float pointSize) {
        String fileName = getFontFileName(familyName, fontStyle);
        String fontPath = Environment.getRootDirectory().getAbsolutePath();
        return createFromFile(rs, res, fontPath + "/fonts/" + fileName, pointSize);
    }
}