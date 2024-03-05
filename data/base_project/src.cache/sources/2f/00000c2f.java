package android.print;

import android.bluetooth.BluetoothHealth;
import android.bluetooth.BluetoothInputDevice;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.hardware.Camera;
import android.media.videoeditor.MediaArtistNativeHelper;
import android.net.ConnectivityManager;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import com.android.internal.R;
import dalvik.bytecode.Opcodes;
import java.util.Map;

/* loaded from: PrintAttributes.class */
public final class PrintAttributes implements Parcelable {
    public static final int COLOR_MODE_MONOCHROME = 1;
    public static final int COLOR_MODE_COLOR = 2;
    private static final int VALID_COLOR_MODES = 3;
    private MediaSize mMediaSize;
    private Resolution mResolution;
    private Margins mMinMargins;
    private int mColorMode;
    public static final Parcelable.Creator<PrintAttributes> CREATOR = new Parcelable.Creator<PrintAttributes>() { // from class: android.print.PrintAttributes.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintAttributes createFromParcel(Parcel parcel) {
            return new PrintAttributes(parcel);
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public PrintAttributes[] newArray(int size) {
            return new PrintAttributes[size];
        }
    };

    PrintAttributes() {
    }

    private PrintAttributes(Parcel parcel) {
        this.mMediaSize = parcel.readInt() == 1 ? MediaSize.createFromParcel(parcel) : null;
        this.mResolution = parcel.readInt() == 1 ? Resolution.createFromParcel(parcel) : null;
        this.mMinMargins = parcel.readInt() == 1 ? Margins.createFromParcel(parcel) : null;
        this.mColorMode = parcel.readInt();
    }

    public MediaSize getMediaSize() {
        return this.mMediaSize;
    }

    public void setMediaSize(MediaSize mediaSize) {
        this.mMediaSize = mediaSize;
    }

    public Resolution getResolution() {
        return this.mResolution;
    }

    public void setResolution(Resolution resolution) {
        this.mResolution = resolution;
    }

    public Margins getMinMargins() {
        return this.mMinMargins;
    }

    public void setMinMargins(Margins margins) {
        this.mMinMargins = margins;
    }

    public int getColorMode() {
        return this.mColorMode;
    }

    public void setColorMode(int colorMode) {
        enforceValidColorMode(colorMode);
        this.mColorMode = colorMode;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        if (this.mMediaSize != null) {
            parcel.writeInt(1);
            this.mMediaSize.writeToParcel(parcel);
        } else {
            parcel.writeInt(0);
        }
        if (this.mResolution != null) {
            parcel.writeInt(1);
            this.mResolution.writeToParcel(parcel);
        } else {
            parcel.writeInt(0);
        }
        if (this.mMinMargins != null) {
            parcel.writeInt(1);
            this.mMinMargins.writeToParcel(parcel);
        } else {
            parcel.writeInt(0);
        }
        parcel.writeInt(this.mColorMode);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int hashCode() {
        int result = (31 * 1) + this.mColorMode;
        return (31 * ((31 * ((31 * result) + (this.mMinMargins == null ? 0 : this.mMinMargins.hashCode()))) + (this.mMediaSize == null ? 0 : this.mMediaSize.hashCode()))) + (this.mResolution == null ? 0 : this.mResolution.hashCode());
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        PrintAttributes other = (PrintAttributes) obj;
        if (this.mColorMode != other.mColorMode) {
            return false;
        }
        if (this.mMinMargins == null) {
            if (other.mMinMargins != null) {
                return false;
            }
        } else if (!this.mMinMargins.equals(other.mMinMargins)) {
            return false;
        }
        if (this.mMediaSize == null) {
            if (other.mMediaSize != null) {
                return false;
            }
        } else if (!this.mMediaSize.equals(other.mMediaSize)) {
            return false;
        }
        if (this.mResolution == null) {
            if (other.mResolution != null) {
                return false;
            }
            return true;
        } else if (!this.mResolution.equals(other.mResolution)) {
            return false;
        } else {
            return true;
        }
    }

    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("PrintAttributes{");
        builder.append("mediaSize: ").append(this.mMediaSize);
        if (this.mMediaSize != null) {
            builder.append(", orientation: ").append(this.mMediaSize.isPortrait() ? Camera.Parameters.SCENE_MODE_PORTRAIT : Camera.Parameters.SCENE_MODE_LANDSCAPE);
        } else {
            builder.append(", orientation: ").append("null");
        }
        builder.append(", resolution: ").append(this.mResolution);
        builder.append(", minMargins: ").append(this.mMinMargins);
        builder.append(", colorMode: ").append(colorModeToString(this.mColorMode));
        builder.append("}");
        return builder.toString();
    }

    public void clear() {
        this.mMediaSize = null;
        this.mResolution = null;
        this.mMinMargins = null;
        this.mColorMode = 0;
    }

    public void copyFrom(PrintAttributes other) {
        this.mMediaSize = other.mMediaSize;
        this.mResolution = other.mResolution;
        this.mMinMargins = other.mMinMargins;
        this.mColorMode = other.mColorMode;
    }

    /* loaded from: PrintAttributes$MediaSize.class */
    public static final class MediaSize {
        private static final String LOG_TAG = "MediaSize";
        private static final Map<String, MediaSize> sIdToMediaSizeMap = new ArrayMap();
        public static final MediaSize UNKNOWN_PORTRAIT = new MediaSize("UNKNOWN_PORTRAIT", "android", R.string.mediasize_unknown_portrait, 1, Integer.MAX_VALUE);
        public static final MediaSize UNKNOWN_LANDSCAPE = new MediaSize("UNKNOWN_LANDSCAPE", "android", R.string.mediasize_unknown_landscape, Integer.MAX_VALUE, 1);
        public static final MediaSize ISO_A0 = new MediaSize("ISO_A0", "android", R.string.mediasize_iso_a0, 33110, 46810);
        public static final MediaSize ISO_A1 = new MediaSize("ISO_A1", "android", R.string.mediasize_iso_a1, 23390, 33110);
        public static final MediaSize ISO_A2 = new MediaSize("ISO_A2", "android", R.string.mediasize_iso_a2, 16540, 23390);
        public static final MediaSize ISO_A3 = new MediaSize("ISO_A3", "android", R.string.mediasize_iso_a3, 11690, 16540);
        public static final MediaSize ISO_A4 = new MediaSize("ISO_A4", "android", R.string.mediasize_iso_a4, 8270, 11690);
        public static final MediaSize ISO_A5 = new MediaSize("ISO_A5", "android", R.string.mediasize_iso_a5, 5830, 8270);
        public static final MediaSize ISO_A6 = new MediaSize("ISO_A6", "android", R.string.mediasize_iso_a6, 4130, 5830);
        public static final MediaSize ISO_A7 = new MediaSize("ISO_A7", "android", R.string.mediasize_iso_a7, 2910, 4130);
        public static final MediaSize ISO_A8 = new MediaSize("ISO_A8", "android", R.string.mediasize_iso_a8, 2050, 2910);
        public static final MediaSize ISO_A9 = new MediaSize("ISO_A9", "android", R.string.mediasize_iso_a9, 1460, 2050);
        public static final MediaSize ISO_A10 = new MediaSize("ISO_A10", "android", R.string.mediasize_iso_a10, 1020, 1460);
        public static final MediaSize ISO_B0 = new MediaSize("ISO_B0", "android", R.string.mediasize_iso_b0, 39370, 55670);
        public static final MediaSize ISO_B1 = new MediaSize("ISO_B1", "android", R.string.mediasize_iso_b1, 27830, 39370);
        public static final MediaSize ISO_B2 = new MediaSize("ISO_B2", "android", R.string.mediasize_iso_b2, 19690, 27830);
        public static final MediaSize ISO_B3 = new MediaSize("ISO_B3", "android", R.string.mediasize_iso_b3, 13900, 19690);
        public static final MediaSize ISO_B4 = new MediaSize("ISO_B4", "android", R.string.mediasize_iso_b4, 9840, 13900);
        public static final MediaSize ISO_B5 = new MediaSize("ISO_B5", "android", R.string.mediasize_iso_b5, 6930, 9840);
        public static final MediaSize ISO_B6 = new MediaSize("ISO_B6", "android", R.string.mediasize_iso_b6, 4920, 6930);
        public static final MediaSize ISO_B7 = new MediaSize("ISO_B7", "android", R.string.mediasize_iso_b7, 3460, 4920);
        public static final MediaSize ISO_B8 = new MediaSize("ISO_B8", "android", R.string.mediasize_iso_b8, 2440, 3460);
        public static final MediaSize ISO_B9 = new MediaSize("ISO_B9", "android", R.string.mediasize_iso_b9, 1730, 2440);
        public static final MediaSize ISO_B10 = new MediaSize("ISO_B10", "android", R.string.mediasize_iso_b10, 1220, 1730);
        public static final MediaSize ISO_C0 = new MediaSize("ISO_C0", "android", R.string.mediasize_iso_c0, 36100, 51060);
        public static final MediaSize ISO_C1 = new MediaSize("ISO_C1", "android", R.string.mediasize_iso_c1, 25510, 36100);
        public static final MediaSize ISO_C2 = new MediaSize("ISO_C2", "android", R.string.mediasize_iso_c2, 18030, 25510);
        public static final MediaSize ISO_C3 = new MediaSize("ISO_C3", "android", R.string.mediasize_iso_c3, 12760, 18030);
        public static final MediaSize ISO_C4 = new MediaSize("ISO_C4", "android", R.string.mediasize_iso_c4, 9020, 12760);
        public static final MediaSize ISO_C5 = new MediaSize("ISO_C5", "android", R.string.mediasize_iso_c5, 6380, 9020);
        public static final MediaSize ISO_C6 = new MediaSize("ISO_C6", "android", R.string.mediasize_iso_c6, 4490, 6380);
        public static final MediaSize ISO_C7 = new MediaSize("ISO_C7", "android", R.string.mediasize_iso_c7, 3190, 4490);
        public static final MediaSize ISO_C8 = new MediaSize("ISO_C8", "android", R.string.mediasize_iso_c8, 2240, 3190);
        public static final MediaSize ISO_C9 = new MediaSize("ISO_C9", "android", R.string.mediasize_iso_c9, 1570, 2240);
        public static final MediaSize ISO_C10 = new MediaSize("ISO_C10", "android", R.string.mediasize_iso_c10, 1100, 1570);
        public static final MediaSize NA_LETTER = new MediaSize("NA_LETTER", "android", R.string.mediasize_na_letter, 8500, 11000);
        public static final MediaSize NA_GOVT_LETTER = new MediaSize("NA_GOVT_LETTER", "android", R.string.mediasize_na_gvrnmt_letter, MediaArtistNativeHelper.AudioSamplingFrequency.FREQ_8000, 10500);
        public static final MediaSize NA_LEGAL = new MediaSize("NA_LEGAL", "android", R.string.mediasize_na_legal, 8500, 14000);
        public static final MediaSize NA_JUNIOR_LEGAL = new MediaSize("NA_JUNIOR_LEGAL", "android", R.string.mediasize_na_junior_legal, MediaArtistNativeHelper.AudioSamplingFrequency.FREQ_8000, BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED);
        public static final MediaSize NA_LEDGER = new MediaSize("NA_LEDGER", "android", R.string.mediasize_na_ledger, 17000, 11000);
        public static final MediaSize NA_TABLOID = new MediaSize("NA_TABLOID", "android", R.string.mediasize_na_tabloid, 11000, 17000);
        public static final MediaSize NA_INDEX_3X5 = new MediaSize("NA_INDEX_3X5", "android", R.string.mediasize_na_index_3x5, ConnectivityManager.CONNECTIVITY_CHANGE_DELAY_DEFAULT, BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED);
        public static final MediaSize NA_INDEX_4X6 = new MediaSize("NA_INDEX_4X6", "android", R.string.mediasize_na_index_4x6, 4000, BluetoothHealth.HEALTH_OPERATION_SUCCESS);
        public static final MediaSize NA_INDEX_5X8 = new MediaSize("NA_INDEX_5X8", "android", R.string.mediasize_na_index_5x8, BluetoothInputDevice.INPUT_DISCONNECT_FAILED_NOT_CONNECTED, MediaArtistNativeHelper.AudioSamplingFrequency.FREQ_8000);
        public static final MediaSize NA_MONARCH = new MediaSize("NA_MONARCH", "android", R.string.mediasize_na_monarch, 7250, 10500);
        public static final MediaSize NA_QUARTO = new MediaSize("NA_QUARTO", "android", R.string.mediasize_na_quarto, MediaArtistNativeHelper.AudioSamplingFrequency.FREQ_8000, 10000);
        public static final MediaSize NA_FOOLSCAP = new MediaSize("NA_FOOLSCAP", "android", R.string.mediasize_na_foolscap, MediaArtistNativeHelper.AudioSamplingFrequency.FREQ_8000, 13000);
        public static final MediaSize ROC_8K = new MediaSize("ROC_8K", "android", R.string.mediasize_chinese_roc_8k, 10629, 15354);
        public static final MediaSize ROC_16K = new MediaSize("ROC_16K", "android", R.string.mediasize_chinese_roc_16k, 7677, 10629);
        public static final MediaSize PRC_1 = new MediaSize("PRC_1", "android", R.string.mediasize_chinese_prc_1, 4015, 6496);
        public static final MediaSize PRC_2 = new MediaSize("PRC_2", "android", R.string.mediasize_chinese_prc_2, 4015, 6929);
        public static final MediaSize PRC_3 = new MediaSize("PRC_3", "android", R.string.mediasize_chinese_prc_3, 4921, 6929);
        public static final MediaSize PRC_4 = new MediaSize("PRC_4", "android", R.string.mediasize_chinese_prc_4, 4330, 8189);
        public static final MediaSize PRC_5 = new MediaSize("PRC_5", "android", R.string.mediasize_chinese_prc_5, 4330, 8661);
        public static final MediaSize PRC_6 = new MediaSize("PRC_6", "android", R.string.mediasize_chinese_prc_6, 4724, 12599);
        public static final MediaSize PRC_7 = new MediaSize("PRC_7", "android", R.string.mediasize_chinese_prc_7, 6299, 9055);
        public static final MediaSize PRC_8 = new MediaSize("PRC_8", "android", R.string.mediasize_chinese_prc_8, 4724, 12165);
        public static final MediaSize PRC_9 = new MediaSize("PRC_9", "android", R.string.mediasize_chinese_prc_9, 9016, 12756);
        public static final MediaSize PRC_10 = new MediaSize("PRC_10", "android", R.string.mediasize_chinese_prc_10, 12756, 18032);
        public static final MediaSize PRC_16K = new MediaSize("PRC_16K", "android", R.string.mediasize_chinese_prc_16k, 5749, 8465);
        public static final MediaSize OM_PA_KAI = new MediaSize("OM_PA_KAI", "android", R.string.mediasize_chinese_om_pa_kai, 10512, 15315);
        public static final MediaSize OM_DAI_PA_KAI = new MediaSize("OM_DAI_PA_KAI", "android", R.string.mediasize_chinese_om_dai_pa_kai, 10827, 15551);
        public static final MediaSize OM_JUURO_KU_KAI = new MediaSize("OM_JUURO_KU_KAI", "android", R.string.mediasize_chinese_om_jurro_ku_kai, 7796, 10827);
        public static final MediaSize JIS_B10 = new MediaSize("JIS_B10", "android", R.string.mediasize_japanese_jis_b10, 1259, 1772);
        public static final MediaSize JIS_B9 = new MediaSize("JIS_B9", "android", R.string.mediasize_japanese_jis_b9, 1772, 2520);
        public static final MediaSize JIS_B8 = new MediaSize("JIS_B8", "android", R.string.mediasize_japanese_jis_b8, 2520, Opcodes.OP_IPUT_JUMBO);
        public static final MediaSize JIS_B7 = new MediaSize("JIS_B7", "android", R.string.mediasize_japanese_jis_b7, Opcodes.OP_IPUT_JUMBO, 5049);
        public static final MediaSize JIS_B6 = new MediaSize("JIS_B6", "android", R.string.mediasize_japanese_jis_b6, 5049, 7165);
        public static final MediaSize JIS_B5 = new MediaSize("JIS_B5", "android", R.string.mediasize_japanese_jis_b5, 7165, 10118);
        public static final MediaSize JIS_B4 = new MediaSize("JIS_B4", "android", R.string.mediasize_japanese_jis_b4, 10118, 14331);
        public static final MediaSize JIS_B3 = new MediaSize("JIS_B3", "android", R.string.mediasize_japanese_jis_b3, 14331, 20276);
        public static final MediaSize JIS_B2 = new MediaSize("JIS_B2", "android", R.string.mediasize_japanese_jis_b2, 20276, 28661);
        public static final MediaSize JIS_B1 = new MediaSize("JIS_B1", "android", R.string.mediasize_japanese_jis_b1, 28661, 40551);
        public static final MediaSize JIS_B0 = new MediaSize("JIS_B0", "android", R.string.mediasize_japanese_jis_b0, 40551, 57323);
        public static final MediaSize JIS_EXEC = new MediaSize("JIS_EXEC", "android", R.string.mediasize_japanese_jis_exec, 8504, 12992);
        public static final MediaSize JPN_CHOU4 = new MediaSize("JPN_CHOU4", "android", R.string.mediasize_japanese_chou4, 3543, 8071);
        public static final MediaSize JPN_CHOU3 = new MediaSize("JPN_CHOU3", "android", R.string.mediasize_japanese_chou3, 4724, 9252);
        public static final MediaSize JPN_CHOU2 = new MediaSize("JPN_CHOU2", "android", R.string.mediasize_japanese_chou2, 4374, 5748);
        public static final MediaSize JPN_HAGAKI = new MediaSize("JPN_HAGAKI", "android", R.string.mediasize_japanese_hagaki, 3937, 5827);
        public static final MediaSize JPN_OUFUKU = new MediaSize("JPN_OUFUKU", "android", R.string.mediasize_japanese_oufuku, 5827, 7874);
        public static final MediaSize JPN_KAHU = new MediaSize("JPN_KAHU", "android", R.string.mediasize_japanese_kahu, 9449, 12681);
        public static final MediaSize JPN_KAKU2 = new MediaSize("JPN_KAKU2", "android", R.string.mediasize_japanese_kaku2, 9449, 13071);
        public static final MediaSize JPN_YOU4 = new MediaSize("JPN_YOU4", "android", R.string.mediasize_japanese_you4, 4134, 9252);
        private final String mId;
        public final String mLabel;
        public final String mPackageName;
        public final int mLabelResId;
        private final int mWidthMils;
        private final int mHeightMils;

        public MediaSize(String id, String packageName, int labelResId, int widthMils, int heightMils) {
            if (TextUtils.isEmpty(id)) {
                throw new IllegalArgumentException("id cannot be empty.");
            }
            if (TextUtils.isEmpty(packageName)) {
                throw new IllegalArgumentException("packageName cannot be empty.");
            }
            if (labelResId <= 0) {
                throw new IllegalArgumentException("labelResId must be greater than zero.");
            }
            if (widthMils <= 0) {
                throw new IllegalArgumentException("widthMils cannot be less than or equal to zero.");
            }
            if (heightMils <= 0) {
                throw new IllegalArgumentException("heightMils cannot be less than or euqual to zero.");
            }
            this.mPackageName = packageName;
            this.mId = id;
            this.mLabelResId = labelResId;
            this.mWidthMils = widthMils;
            this.mHeightMils = heightMils;
            this.mLabel = null;
            sIdToMediaSizeMap.put(this.mId, this);
        }

        public MediaSize(String id, String label, int widthMils, int heightMils) {
            if (TextUtils.isEmpty(id)) {
                throw new IllegalArgumentException("id cannot be empty.");
            }
            if (TextUtils.isEmpty(label)) {
                throw new IllegalArgumentException("label cannot be empty.");
            }
            if (widthMils <= 0) {
                throw new IllegalArgumentException("widthMils cannot be less than or equal to zero.");
            }
            if (heightMils <= 0) {
                throw new IllegalArgumentException("heightMils cannot be less than or euqual to zero.");
            }
            this.mId = id;
            this.mLabel = label;
            this.mWidthMils = widthMils;
            this.mHeightMils = heightMils;
            this.mLabelResId = 0;
            this.mPackageName = null;
        }

        public MediaSize(String id, String label, String packageName, int widthMils, int heightMils, int labelResId) {
            this.mPackageName = packageName;
            this.mId = id;
            this.mLabelResId = labelResId;
            this.mWidthMils = widthMils;
            this.mHeightMils = heightMils;
            this.mLabel = label;
        }

        public String getId() {
            return this.mId;
        }

        public String getLabel(PackageManager packageManager) {
            if (!TextUtils.isEmpty(this.mPackageName) && this.mLabelResId > 0) {
                try {
                    return packageManager.getResourcesForApplication(this.mPackageName).getString(this.mLabelResId);
                } catch (PackageManager.NameNotFoundException e) {
                    Log.w(LOG_TAG, "Could not load resouce" + this.mLabelResId + " from package " + this.mPackageName);
                } catch (Resources.NotFoundException e2) {
                    Log.w(LOG_TAG, "Could not load resouce" + this.mLabelResId + " from package " + this.mPackageName);
                }
            }
            return this.mLabel;
        }

        public int getWidthMils() {
            return this.mWidthMils;
        }

        public int getHeightMils() {
            return this.mHeightMils;
        }

        public boolean isPortrait() {
            return this.mHeightMils >= this.mWidthMils;
        }

        public MediaSize asPortrait() {
            return new MediaSize(this.mId, this.mLabel, this.mPackageName, Math.min(this.mWidthMils, this.mHeightMils), Math.max(this.mWidthMils, this.mHeightMils), this.mLabelResId);
        }

        public MediaSize asLandscape() {
            return new MediaSize(this.mId, this.mLabel, this.mPackageName, Math.max(this.mWidthMils, this.mHeightMils), Math.min(this.mWidthMils, this.mHeightMils), this.mLabelResId);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void writeToParcel(Parcel parcel) {
            parcel.writeString(this.mId);
            parcel.writeString(this.mLabel);
            parcel.writeString(this.mPackageName);
            parcel.writeInt(this.mWidthMils);
            parcel.writeInt(this.mHeightMils);
            parcel.writeInt(this.mLabelResId);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static MediaSize createFromParcel(Parcel parcel) {
            return new MediaSize(parcel.readString(), parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt(), parcel.readInt());
        }

        public int hashCode() {
            int result = (31 * 1) + this.mWidthMils;
            return (31 * result) + this.mHeightMils;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            MediaSize other = (MediaSize) obj;
            if (this.mWidthMils != other.mWidthMils || this.mHeightMils != other.mHeightMils) {
                return false;
            }
            return true;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("MediaSize{");
            builder.append("id: ").append(this.mId);
            builder.append(", label: ").append(this.mLabel);
            builder.append(", packageName: ").append(this.mPackageName);
            builder.append(", heightMils: ").append(this.mHeightMils);
            builder.append(", widthMils: ").append(this.mWidthMils);
            builder.append(", labelResId: ").append(this.mLabelResId);
            builder.append("}");
            return builder.toString();
        }

        public static MediaSize getStandardMediaSizeById(String id) {
            return sIdToMediaSizeMap.get(id);
        }
    }

    /* loaded from: PrintAttributes$Resolution.class */
    public static final class Resolution {
        private final String mId;
        private final String mLabel;
        private final int mHorizontalDpi;
        private final int mVerticalDpi;

        public Resolution(String id, String label, int horizontalDpi, int verticalDpi) {
            if (TextUtils.isEmpty(id)) {
                throw new IllegalArgumentException("id cannot be empty.");
            }
            if (TextUtils.isEmpty(label)) {
                throw new IllegalArgumentException("label cannot be empty.");
            }
            if (horizontalDpi <= 0) {
                throw new IllegalArgumentException("horizontalDpi cannot be less than or equal to zero.");
            }
            if (verticalDpi <= 0) {
                throw new IllegalArgumentException("verticalDpi cannot be less than or equal to zero.");
            }
            this.mId = id;
            this.mLabel = label;
            this.mHorizontalDpi = horizontalDpi;
            this.mVerticalDpi = verticalDpi;
        }

        public String getId() {
            return this.mId;
        }

        public String getLabel() {
            return this.mLabel;
        }

        public int getHorizontalDpi() {
            return this.mHorizontalDpi;
        }

        public int getVerticalDpi() {
            return this.mVerticalDpi;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void writeToParcel(Parcel parcel) {
            parcel.writeString(this.mId);
            parcel.writeString(this.mLabel);
            parcel.writeInt(this.mHorizontalDpi);
            parcel.writeInt(this.mVerticalDpi);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static Resolution createFromParcel(Parcel parcel) {
            return new Resolution(parcel.readString(), parcel.readString(), parcel.readInt(), parcel.readInt());
        }

        public int hashCode() {
            int result = (31 * 1) + this.mHorizontalDpi;
            return (31 * result) + this.mVerticalDpi;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Resolution other = (Resolution) obj;
            if (this.mHorizontalDpi != other.mHorizontalDpi || this.mVerticalDpi != other.mVerticalDpi) {
                return false;
            }
            return true;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Resolution{");
            builder.append("id: ").append(this.mId);
            builder.append(", label: ").append(this.mLabel);
            builder.append(", horizontalDpi: ").append(this.mHorizontalDpi);
            builder.append(", verticalDpi: ").append(this.mVerticalDpi);
            builder.append("}");
            return builder.toString();
        }
    }

    /* loaded from: PrintAttributes$Margins.class */
    public static final class Margins {
        public static final Margins NO_MARGINS = new Margins(0, 0, 0, 0);
        private final int mLeftMils;
        private final int mTopMils;
        private final int mRightMils;
        private final int mBottomMils;

        public Margins(int leftMils, int topMils, int rightMils, int bottomMils) {
            this.mTopMils = topMils;
            this.mLeftMils = leftMils;
            this.mRightMils = rightMils;
            this.mBottomMils = bottomMils;
        }

        public int getLeftMils() {
            return this.mLeftMils;
        }

        public int getTopMils() {
            return this.mTopMils;
        }

        public int getRightMils() {
            return this.mRightMils;
        }

        public int getBottomMils() {
            return this.mBottomMils;
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public void writeToParcel(Parcel parcel) {
            parcel.writeInt(this.mLeftMils);
            parcel.writeInt(this.mTopMils);
            parcel.writeInt(this.mRightMils);
            parcel.writeInt(this.mBottomMils);
        }

        /* JADX INFO: Access modifiers changed from: package-private */
        public static Margins createFromParcel(Parcel parcel) {
            return new Margins(parcel.readInt(), parcel.readInt(), parcel.readInt(), parcel.readInt());
        }

        public int hashCode() {
            int result = (31 * 1) + this.mBottomMils;
            return (31 * ((31 * ((31 * result) + this.mLeftMils)) + this.mRightMils)) + this.mTopMils;
        }

        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null || getClass() != obj.getClass()) {
                return false;
            }
            Margins other = (Margins) obj;
            if (this.mBottomMils != other.mBottomMils || this.mLeftMils != other.mLeftMils || this.mRightMils != other.mRightMils || this.mTopMils != other.mTopMils) {
                return false;
            }
            return true;
        }

        public String toString() {
            StringBuilder builder = new StringBuilder();
            builder.append("Margins{");
            builder.append("leftMils: ").append(this.mLeftMils);
            builder.append(", topMils: ").append(this.mTopMils);
            builder.append(", rightMils: ").append(this.mRightMils);
            builder.append(", bottomMils: ").append(this.mBottomMils);
            builder.append("}");
            return builder.toString();
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static String colorModeToString(int colorMode) {
        switch (colorMode) {
            case 1:
                return "COLOR_MODE_MONOCHROME";
            case 2:
                return "COLOR_MODE_COLOR";
            default:
                return "COLOR_MODE_UNKNOWN";
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public static void enforceValidColorMode(int colorMode) {
        if ((colorMode & 3) == 0 && Integer.bitCount(colorMode) == 1) {
            throw new IllegalArgumentException("invalid color mode: " + colorMode);
        }
    }

    /* loaded from: PrintAttributes$Builder.class */
    public static final class Builder {
        private final PrintAttributes mAttributes = new PrintAttributes();

        public Builder setMediaSize(MediaSize mediaSize) {
            this.mAttributes.setMediaSize(mediaSize);
            return this;
        }

        public Builder setResolution(Resolution resolution) {
            this.mAttributes.setResolution(resolution);
            return this;
        }

        public Builder setMinMargins(Margins margins) {
            this.mAttributes.setMinMargins(margins);
            return this;
        }

        public Builder setColorMode(int colorMode) {
            if (Integer.bitCount(colorMode) > 1) {
                throw new IllegalArgumentException("can specify at most one colorMode bit.");
            }
            this.mAttributes.setColorMode(colorMode);
            return this;
        }

        public PrintAttributes build() {
            return this.mAttributes;
        }
    }
}