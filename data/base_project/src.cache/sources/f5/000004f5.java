package android.filterpacks.videoproc;

import android.filterfw.core.Filter;
import android.filterfw.core.FilterContext;
import android.filterfw.core.Frame;
import android.filterfw.core.FrameFormat;
import android.filterfw.core.GLFrame;
import android.filterfw.core.GenerateFieldPort;
import android.filterfw.core.GenerateFinalPort;
import android.filterfw.core.MutableFrameFormat;
import android.filterfw.core.ShaderProgram;
import android.filterfw.format.ImageFormat;
import android.opengl.GLES20;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.provider.MediaStore;
import android.util.Log;
import java.nio.ByteBuffer;
import java.util.Arrays;

/* loaded from: BackDropperFilter.class */
public class BackDropperFilter extends Filter {
    private final int BACKGROUND_STRETCH = 0;
    private final int BACKGROUND_FIT = 1;
    private final int BACKGROUND_FILL_CROP = 2;
    @GenerateFieldPort(name = "backgroundFitMode", hasDefault = true)
    private int mBackgroundFitMode;
    @GenerateFieldPort(name = "learningDuration", hasDefault = true)
    private int mLearningDuration;
    @GenerateFieldPort(name = "learningVerifyDuration", hasDefault = true)
    private int mLearningVerifyDuration;
    @GenerateFieldPort(name = "acceptStddev", hasDefault = true)
    private float mAcceptStddev;
    @GenerateFieldPort(name = "hierLrgScale", hasDefault = true)
    private float mHierarchyLrgScale;
    @GenerateFieldPort(name = "hierMidScale", hasDefault = true)
    private float mHierarchyMidScale;
    @GenerateFieldPort(name = "hierSmlScale", hasDefault = true)
    private float mHierarchySmlScale;
    @GenerateFieldPort(name = "maskWidthExp", hasDefault = true)
    private int mMaskWidthExp;
    @GenerateFieldPort(name = "maskHeightExp", hasDefault = true)
    private int mMaskHeightExp;
    @GenerateFieldPort(name = "hierLrgExp", hasDefault = true)
    private int mHierarchyLrgExp;
    @GenerateFieldPort(name = "hierMidExp", hasDefault = true)
    private int mHierarchyMidExp;
    @GenerateFieldPort(name = "hierSmlExp", hasDefault = true)
    private int mHierarchySmlExp;
    @GenerateFieldPort(name = "lumScale", hasDefault = true)
    private float mLumScale;
    @GenerateFieldPort(name = "chromaScale", hasDefault = true)
    private float mChromaScale;
    @GenerateFieldPort(name = "maskBg", hasDefault = true)
    private float mMaskBg;
    @GenerateFieldPort(name = "maskFg", hasDefault = true)
    private float mMaskFg;
    @GenerateFieldPort(name = "exposureChange", hasDefault = true)
    private float mExposureChange;
    @GenerateFieldPort(name = "whitebalanceredChange", hasDefault = true)
    private float mWhiteBalanceRedChange;
    @GenerateFieldPort(name = "whitebalanceblueChange", hasDefault = true)
    private float mWhiteBalanceBlueChange;
    @GenerateFieldPort(name = "autowbToggle", hasDefault = true)
    private int mAutoWBToggle;
    @GenerateFieldPort(name = "learningAdaptRate", hasDefault = true)
    private float mAdaptRateLearning;
    @GenerateFieldPort(name = "adaptRateBg", hasDefault = true)
    private float mAdaptRateBg;
    @GenerateFieldPort(name = "adaptRateFg", hasDefault = true)
    private float mAdaptRateFg;
    @GenerateFieldPort(name = "maskVerifyRate", hasDefault = true)
    private float mVerifyRate;
    @GenerateFieldPort(name = "learningDoneListener", hasDefault = true)
    private LearningDoneListener mLearningDoneListener;
    @GenerateFieldPort(name = "useTheForce", hasDefault = true)
    private boolean mUseTheForce;
    @GenerateFinalPort(name = "provideDebugOutputs", hasDefault = true)
    private boolean mProvideDebugOutputs;
    @GenerateFieldPort(name = "mirrorBg", hasDefault = true)
    private boolean mMirrorBg;
    @GenerateFieldPort(name = MediaStore.Images.ImageColumns.ORIENTATION, hasDefault = true)
    private int mOrientation;
    private static final int DEFAULT_LEARNING_DURATION = 40;
    private static final int DEFAULT_LEARNING_VERIFY_DURATION = 10;
    private static final float DEFAULT_ACCEPT_STDDEV = 0.85f;
    private static final float DEFAULT_HIER_LRG_SCALE = 0.7f;
    private static final float DEFAULT_HIER_MID_SCALE = 0.6f;
    private static final float DEFAULT_HIER_SML_SCALE = 0.5f;
    private static final int DEFAULT_MASK_WIDTH_EXPONENT = 8;
    private static final int DEFAULT_MASK_HEIGHT_EXPONENT = 8;
    private static final int DEFAULT_HIER_LRG_EXPONENT = 3;
    private static final int DEFAULT_HIER_MID_EXPONENT = 2;
    private static final int DEFAULT_HIER_SML_EXPONENT = 0;
    private static final float DEFAULT_Y_SCALE_FACTOR = 0.4f;
    private static final float DEFAULT_UV_SCALE_FACTOR = 1.35f;
    private static final float DEFAULT_MASK_BLEND_BG = 0.65f;
    private static final float DEFAULT_MASK_BLEND_FG = 0.95f;
    private static final float DEFAULT_EXPOSURE_CHANGE = 1.0f;
    private static final float DEFAULT_WHITE_BALANCE_RED_CHANGE = 0.0f;
    private static final float DEFAULT_WHITE_BALANCE_BLUE_CHANGE = 0.0f;
    private static final int DEFAULT_WHITE_BALANCE_TOGGLE = 0;
    private static final float DEFAULT_LEARNING_ADAPT_RATE = 0.2f;
    private static final float DEFAULT_ADAPT_RATE_BG = 0.0f;
    private static final float DEFAULT_ADAPT_RATE_FG = 0.0f;
    private static final float DEFAULT_MASK_VERIFY_RATE = 0.25f;
    private static final int DEFAULT_LEARNING_DONE_THRESHOLD = 20;
    private static final String MASK_SMOOTH_EXPONENT = "2.0";
    private static final String DISTANCE_STORAGE_SCALE = "0.6";
    private static final String VARIANCE_STORAGE_SCALE = "5.0";
    private static final String DEFAULT_AUTO_WB_SCALE = "0.25";
    private static final String MIN_VARIANCE = "3.0";
    private static final String RGB_TO_YUV_MATRIX = "0.299, -0.168736,  0.5,      0.000, 0.587, -0.331264, -0.418688, 0.000, 0.114,  0.5,      -0.081312, 0.000, 0.000,  0.5,       0.5,      1.000 ";
    private FrameFormat mOutputFormat;
    private MutableFrameFormat mMemoryFormat;
    private MutableFrameFormat mMaskFormat;
    private MutableFrameFormat mAverageFormat;
    private final boolean mLogVerbose;
    private static final String TAG = "BackDropperFilter";
    private static final String mBgDistanceShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 variance = inv_var_scale * texture2D(tex_sampler_2, v_texcoord);\n\n  float dist_y = gauss_dist_y(fg.r, mean.r, variance.r);\n  float dist_uv = gauss_dist_uv(fg.gb, mean.gb, variance.gb);\n  gl_FragColor = vec4(0.5*fg.rg, dist_scale*dist_y, dist_scale*dist_uv);\n}\n";
    private static final String mBgMaskShader = "uniform sampler2D tex_sampler_0;\nuniform float accept_variance;\nuniform vec2 yuv_weights;\nuniform float scale_lrg;\nuniform float scale_mid;\nuniform float scale_sml;\nuniform float exp_lrg;\nuniform float exp_mid;\nuniform float exp_sml;\nvarying vec2 v_texcoord;\nbool is_fg(vec2 dist_yc, float accept_variance) {\n  return ( dot(yuv_weights, dist_yc) >= accept_variance );\n}\nvoid main() {\n  vec4 dist_lrg_sc = texture2D(tex_sampler_0, v_texcoord, exp_lrg);\n  vec4 dist_mid_sc = texture2D(tex_sampler_0, v_texcoord, exp_mid);\n  vec4 dist_sml_sc = texture2D(tex_sampler_0, v_texcoord, exp_sml);\n  vec2 dist_lrg = inv_dist_scale * dist_lrg_sc.ba;\n  vec2 dist_mid = inv_dist_scale * dist_mid_sc.ba;\n  vec2 dist_sml = inv_dist_scale * dist_sml_sc.ba;\n  vec2 norm_dist = 0.75 * dist_sml / accept_variance;\n  bool is_fg_lrg = is_fg(dist_lrg, accept_variance * scale_lrg);\n  bool is_fg_mid = is_fg_lrg || is_fg(dist_mid, accept_variance * scale_mid);\n  float is_fg_sml =\n      float(is_fg_mid || is_fg(dist_sml, accept_variance * scale_sml));\n  float alpha = 0.5 * is_fg_sml + 0.3 * float(is_fg_mid) + 0.2 * float(is_fg_lrg);\n  gl_FragColor = vec4(alpha, norm_dist, is_fg_sml);\n}\n";
    private static final String mAutomaticWhiteBalance = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float pyramid_depth;\nuniform bool autowb_toggle;\nvarying vec2 v_texcoord;\nvoid main() {\n   vec4 mean_video = texture2D(tex_sampler_0, v_texcoord, pyramid_depth);\n   vec4 mean_bg = texture2D(tex_sampler_1, v_texcoord, pyramid_depth);\n   float green_normalizer = mean_video.g / mean_bg.g;\n   vec4 adjusted_value = vec4(mean_bg.r / mean_video.r * green_normalizer, 1., \n                         mean_bg.b / mean_video.b * green_normalizer, 1.) * auto_wb_scale; \n   gl_FragColor = autowb_toggle ? adjusted_value : vec4(auto_wb_scale);\n}\n";
    private static final String mBgSubtractShader = "uniform mat3 bg_fit_transform;\nuniform float mask_blend_bg;\nuniform float mask_blend_fg;\nuniform float exposure_change;\nuniform float whitebalancered_change;\nuniform float whitebalanceblue_change;\nuniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform sampler2D tex_sampler_3;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec2 bg_texcoord = (bg_fit_transform * vec3(v_texcoord, 1.)).xy;\n  vec4 bg_rgb = texture2D(tex_sampler_1, bg_texcoord);\n  vec4 wb_auto_scale = texture2D(tex_sampler_3, v_texcoord) * exposure_change / auto_wb_scale;\n  vec4 wb_manual_scale = vec4(1. + whitebalancered_change, 1., 1. + whitebalanceblue_change, 1.);\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord);\n  vec4 fg_adjusted = fg_rgb * wb_manual_scale * wb_auto_scale;\n  vec4 mask = texture2D(tex_sampler_2, v_texcoord, \n                      2.0);\n  float alpha = smoothstep(mask_blend_bg, mask_blend_fg, mask.a);\n  gl_FragColor = mix(bg_rgb, fg_adjusted, alpha);\n";
    private static final String mBgSubtractForceShader = "  vec4 ghost_rgb = (fg_adjusted * 0.7 + vec4(0.3,0.3,0.4,0.))*0.65 + \n                   0.35*bg_rgb;\n  float glow_start = 0.75 * mask_blend_bg; \n  float glow_max   = mask_blend_bg; \n  gl_FragColor = mask.a < glow_start ? bg_rgb : \n                 mask.a < glow_max ? mix(bg_rgb, vec4(0.9,0.9,1.0,1.0), \n                                     (mask.a - glow_start) / (glow_max - glow_start) ) : \n                 mask.a < mask_blend_fg ? mix(vec4(0.9,0.9,1.0,1.0), ghost_rgb, \n                                    (mask.a - glow_max) / (mask_blend_fg - glow_max) ) : \n                 ghost_rgb;\n}\n";
    private static final String mUpdateBgModelMeanShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_2, v_texcoord, \n                      2.0);\n\n  float alpha = local_adapt_rate(mask.a);\n  vec4 new_mean = mix(mean, fg, alpha);\n  gl_FragColor = new_mean;\n}\n";
    private static final String mUpdateBgModelVarianceShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform sampler2D tex_sampler_2;\nuniform sampler2D tex_sampler_3;\nuniform float subsample_level;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 fg_rgb = texture2D(tex_sampler_0, v_texcoord, subsample_level);\n  vec4 fg = coeff_yuv * vec4(fg_rgb.rgb, 1.);\n  vec4 mean = texture2D(tex_sampler_1, v_texcoord);\n  vec4 variance = inv_var_scale * texture2D(tex_sampler_2, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_3, v_texcoord, \n                      2.0);\n\n  float alpha = local_adapt_rate(mask.a);\n  vec4 cur_variance = (fg-mean)*(fg-mean);\n  vec4 new_variance = mix(variance, cur_variance, alpha);\n  new_variance = max(new_variance, vec4(min_variance));\n  gl_FragColor = var_scale * new_variance;\n}\n";
    private static final String mMaskVerifyShader = "uniform sampler2D tex_sampler_0;\nuniform sampler2D tex_sampler_1;\nuniform float verify_rate;\nvarying vec2 v_texcoord;\nvoid main() {\n  vec4 lastmask = texture2D(tex_sampler_0, v_texcoord);\n  vec4 mask = texture2D(tex_sampler_1, v_texcoord);\n  float newmask = mix(lastmask.a, mask.a, verify_rate);\n  gl_FragColor = vec4(0., 0., 0., newmask);\n}\n";
    private ShaderProgram mBgDistProgram;
    private ShaderProgram mBgMaskProgram;
    private ShaderProgram mBgSubtractProgram;
    private ShaderProgram mBgUpdateMeanProgram;
    private ShaderProgram mBgUpdateVarianceProgram;
    private ShaderProgram mCopyOutProgram;
    private ShaderProgram mAutomaticWhiteBalanceProgram;
    private ShaderProgram mMaskVerifyProgram;
    private ShaderProgram copyShaderProgram;
    private boolean mPingPong;
    private GLFrame[] mBgMean;
    private GLFrame[] mBgVariance;
    private GLFrame[] mMaskVerify;
    private GLFrame mDistance;
    private GLFrame mAutoWB;
    private GLFrame mMask;
    private GLFrame mVideoInput;
    private GLFrame mBgInput;
    private GLFrame mMaskAverage;
    private boolean isOpen;
    private int mFrameCount;
    private boolean mStartLearning;
    private boolean mBackgroundFitModeChanged;
    private float mRelativeAspect;
    private int mPyramidDepth;
    private int mSubsampleLevel;
    private long startTime;
    private static final float[] DEFAULT_BG_FIT_TRANSFORM = {1.0f, 0.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f, 0.0f, 1.0f};
    private static final String[] mInputNames = {"video", "background"};
    private static final String[] mOutputNames = {"video"};
    private static final String[] mDebugOutputNames = {"debug1", "debug2"};
    private static String mSharedUtilShader = "precision mediump float;\nuniform float fg_adapt_rate;\nuniform float bg_adapt_rate;\nconst mat4 coeff_yuv = mat4(0.299, -0.168736,  0.5,      0.000, 0.587, -0.331264, -0.418688, 0.000, 0.114,  0.5,      -0.081312, 0.000, 0.000,  0.5,       0.5,      1.000 );\nconst float dist_scale = 0.6;\nconst float inv_dist_scale = 1. / dist_scale;\nconst float var_scale=5.0;\nconst float inv_var_scale = 1. / var_scale;\nconst float min_variance = inv_var_scale *3.0/ 256.;\nconst float auto_wb_scale = 0.25;\n\nfloat gauss_dist_y(float y, float mean, float variance) {\n  float dist = (y - mean) * (y - mean) / variance;\n  return dist;\n}\nfloat gauss_dist_uv(vec2 uv, vec2 mean, vec2 variance) {\n  vec2 dist = (uv - mean) * (uv - mean) / variance;\n  return dist.r + dist.g;\n}\nfloat local_adapt_rate(float alpha) {\n  return mix(bg_adapt_rate, fg_adapt_rate, alpha);\n}\n\n";

    /* loaded from: BackDropperFilter$LearningDoneListener.class */
    public interface LearningDoneListener {
        void onLearningDone(BackDropperFilter backDropperFilter);
    }

    public BackDropperFilter(String name) {
        super(name);
        this.BACKGROUND_STRETCH = 0;
        this.BACKGROUND_FIT = 1;
        this.BACKGROUND_FILL_CROP = 2;
        this.mBackgroundFitMode = 2;
        this.mLearningDuration = 40;
        this.mLearningVerifyDuration = 10;
        this.mAcceptStddev = DEFAULT_ACCEPT_STDDEV;
        this.mHierarchyLrgScale = DEFAULT_HIER_LRG_SCALE;
        this.mHierarchyMidScale = 0.6f;
        this.mHierarchySmlScale = DEFAULT_HIER_SML_SCALE;
        this.mMaskWidthExp = 8;
        this.mMaskHeightExp = 8;
        this.mHierarchyLrgExp = 3;
        this.mHierarchyMidExp = 2;
        this.mHierarchySmlExp = 0;
        this.mLumScale = 0.4f;
        this.mChromaScale = DEFAULT_UV_SCALE_FACTOR;
        this.mMaskBg = DEFAULT_MASK_BLEND_BG;
        this.mMaskFg = DEFAULT_MASK_BLEND_FG;
        this.mExposureChange = 1.0f;
        this.mWhiteBalanceRedChange = 0.0f;
        this.mWhiteBalanceBlueChange = 0.0f;
        this.mAutoWBToggle = 0;
        this.mAdaptRateLearning = 0.2f;
        this.mAdaptRateBg = 0.0f;
        this.mAdaptRateFg = 0.0f;
        this.mVerifyRate = 0.25f;
        this.mLearningDoneListener = null;
        this.mUseTheForce = false;
        this.mProvideDebugOutputs = false;
        this.mMirrorBg = false;
        this.mOrientation = 0;
        this.startTime = -1L;
        this.mLogVerbose = Log.isLoggable(TAG, 2);
        String adjStr = SystemProperties.get("ro.media.effect.bgdropper.adj");
        if (adjStr.length() > 0) {
            try {
                this.mAcceptStddev += Float.parseFloat(adjStr);
                if (this.mLogVerbose) {
                    Log.v(TAG, "Adjusting accept threshold by " + adjStr + ", now " + this.mAcceptStddev);
                }
            } catch (NumberFormatException e) {
                Log.e(TAG, "Badly formatted property ro.media.effect.bgdropper.adj: " + adjStr);
            }
        }
    }

    @Override // android.filterfw.core.Filter
    public void setupPorts() {
        FrameFormat imageFormat = ImageFormat.create(3, 0);
        String[] arr$ = mInputNames;
        for (String inputName : arr$) {
            addMaskedInputPort(inputName, imageFormat);
        }
        String[] arr$2 = mOutputNames;
        for (String outputName : arr$2) {
            addOutputBasedOnInput(outputName, "video");
        }
        if (this.mProvideDebugOutputs) {
            String[] arr$3 = mDebugOutputNames;
            for (String outputName2 : arr$3) {
                addOutputBasedOnInput(outputName2, "video");
            }
        }
    }

    @Override // android.filterfw.core.Filter
    public FrameFormat getOutputFormat(String portName, FrameFormat inputFormat) {
        MutableFrameFormat format = inputFormat.mutableCopy();
        if (!Arrays.asList(mOutputNames).contains(portName)) {
            format.setDimensions(0, 0);
        }
        return format;
    }

    private boolean createMemoryFormat(FrameFormat inputFormat) {
        if (this.mMemoryFormat != null) {
            return false;
        }
        if (inputFormat.getWidth() == 0 || inputFormat.getHeight() == 0) {
            throw new RuntimeException("Attempting to process input frame with unknown size");
        }
        this.mMaskFormat = inputFormat.mutableCopy();
        int maskWidth = (int) Math.pow(2.0d, this.mMaskWidthExp);
        int maskHeight = (int) Math.pow(2.0d, this.mMaskHeightExp);
        this.mMaskFormat.setDimensions(maskWidth, maskHeight);
        this.mPyramidDepth = Math.max(this.mMaskWidthExp, this.mMaskHeightExp);
        this.mMemoryFormat = this.mMaskFormat.mutableCopy();
        int widthExp = Math.max(this.mMaskWidthExp, pyramidLevel(inputFormat.getWidth()));
        int heightExp = Math.max(this.mMaskHeightExp, pyramidLevel(inputFormat.getHeight()));
        this.mPyramidDepth = Math.max(widthExp, heightExp);
        int memWidth = Math.max(maskWidth, (int) Math.pow(2.0d, widthExp));
        int memHeight = Math.max(maskHeight, (int) Math.pow(2.0d, heightExp));
        this.mMemoryFormat.setDimensions(memWidth, memHeight);
        this.mSubsampleLevel = this.mPyramidDepth - Math.max(this.mMaskWidthExp, this.mMaskHeightExp);
        if (this.mLogVerbose) {
            Log.v(TAG, "Mask frames size " + maskWidth + " x " + maskHeight);
            Log.v(TAG, "Pyramid levels " + widthExp + " x " + heightExp);
            Log.v(TAG, "Memory frames size " + memWidth + " x " + memHeight);
        }
        this.mAverageFormat = inputFormat.mutableCopy();
        this.mAverageFormat.setDimensions(1, 1);
        return true;
    }

    @Override // android.filterfw.core.Filter
    public void prepare(FilterContext context) {
        if (this.mLogVerbose) {
            Log.v(TAG, "Preparing BackDropperFilter!");
        }
        this.mBgMean = new GLFrame[2];
        this.mBgVariance = new GLFrame[2];
        this.mMaskVerify = new GLFrame[2];
        this.copyShaderProgram = ShaderProgram.createIdentity(context);
    }

    private void allocateFrames(FrameFormat inputFormat, FilterContext context) {
        if (!createMemoryFormat(inputFormat)) {
            return;
        }
        if (this.mLogVerbose) {
            Log.v(TAG, "Allocating BackDropperFilter frames");
        }
        int numBytes = this.mMaskFormat.getSize();
        byte[] initialBgMean = new byte[numBytes];
        byte[] initialBgVariance = new byte[numBytes];
        byte[] initialMaskVerify = new byte[numBytes];
        for (int i = 0; i < numBytes; i++) {
            initialBgMean[i] = Byte.MIN_VALUE;
            initialBgVariance[i] = 10;
            initialMaskVerify[i] = 0;
        }
        for (int i2 = 0; i2 < 2; i2++) {
            this.mBgMean[i2] = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
            this.mBgMean[i2].setData(initialBgMean, 0, numBytes);
            this.mBgVariance[i2] = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
            this.mBgVariance[i2].setData(initialBgVariance, 0, numBytes);
            this.mMaskVerify[i2] = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
            this.mMaskVerify[i2].setData(initialMaskVerify, 0, numBytes);
        }
        if (this.mLogVerbose) {
            Log.v(TAG, "Done allocating texture for Mean and Variance objects!");
        }
        this.mDistance = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
        this.mMask = (GLFrame) context.getFrameManager().newFrame(this.mMaskFormat);
        this.mAutoWB = (GLFrame) context.getFrameManager().newFrame(this.mAverageFormat);
        this.mVideoInput = (GLFrame) context.getFrameManager().newFrame(this.mMemoryFormat);
        this.mBgInput = (GLFrame) context.getFrameManager().newFrame(this.mMemoryFormat);
        this.mMaskAverage = (GLFrame) context.getFrameManager().newFrame(this.mAverageFormat);
        this.mBgDistProgram = new ShaderProgram(context, mSharedUtilShader + mBgDistanceShader);
        this.mBgDistProgram.setHostValue("subsample_level", Float.valueOf(this.mSubsampleLevel));
        this.mBgMaskProgram = new ShaderProgram(context, mSharedUtilShader + mBgMaskShader);
        this.mBgMaskProgram.setHostValue("accept_variance", Float.valueOf(this.mAcceptStddev * this.mAcceptStddev));
        float[] yuvWeights = {this.mLumScale, this.mChromaScale};
        this.mBgMaskProgram.setHostValue("yuv_weights", yuvWeights);
        this.mBgMaskProgram.setHostValue("scale_lrg", Float.valueOf(this.mHierarchyLrgScale));
        this.mBgMaskProgram.setHostValue("scale_mid", Float.valueOf(this.mHierarchyMidScale));
        this.mBgMaskProgram.setHostValue("scale_sml", Float.valueOf(this.mHierarchySmlScale));
        this.mBgMaskProgram.setHostValue("exp_lrg", Float.valueOf(this.mSubsampleLevel + this.mHierarchyLrgExp));
        this.mBgMaskProgram.setHostValue("exp_mid", Float.valueOf(this.mSubsampleLevel + this.mHierarchyMidExp));
        this.mBgMaskProgram.setHostValue("exp_sml", Float.valueOf(this.mSubsampleLevel + this.mHierarchySmlExp));
        if (this.mUseTheForce) {
            this.mBgSubtractProgram = new ShaderProgram(context, mSharedUtilShader + mBgSubtractShader + mBgSubtractForceShader);
        } else {
            this.mBgSubtractProgram = new ShaderProgram(context, mSharedUtilShader + mBgSubtractShader + "}\n");
        }
        this.mBgSubtractProgram.setHostValue("bg_fit_transform", DEFAULT_BG_FIT_TRANSFORM);
        this.mBgSubtractProgram.setHostValue("mask_blend_bg", Float.valueOf(this.mMaskBg));
        this.mBgSubtractProgram.setHostValue("mask_blend_fg", Float.valueOf(this.mMaskFg));
        this.mBgSubtractProgram.setHostValue("exposure_change", Float.valueOf(this.mExposureChange));
        this.mBgSubtractProgram.setHostValue("whitebalanceblue_change", Float.valueOf(this.mWhiteBalanceBlueChange));
        this.mBgSubtractProgram.setHostValue("whitebalancered_change", Float.valueOf(this.mWhiteBalanceRedChange));
        this.mBgUpdateMeanProgram = new ShaderProgram(context, mSharedUtilShader + mUpdateBgModelMeanShader);
        this.mBgUpdateMeanProgram.setHostValue("subsample_level", Float.valueOf(this.mSubsampleLevel));
        this.mBgUpdateVarianceProgram = new ShaderProgram(context, mSharedUtilShader + mUpdateBgModelVarianceShader);
        this.mBgUpdateVarianceProgram.setHostValue("subsample_level", Float.valueOf(this.mSubsampleLevel));
        this.mCopyOutProgram = ShaderProgram.createIdentity(context);
        this.mAutomaticWhiteBalanceProgram = new ShaderProgram(context, mSharedUtilShader + mAutomaticWhiteBalance);
        this.mAutomaticWhiteBalanceProgram.setHostValue("pyramid_depth", Float.valueOf(this.mPyramidDepth));
        this.mAutomaticWhiteBalanceProgram.setHostValue("autowb_toggle", Integer.valueOf(this.mAutoWBToggle));
        this.mMaskVerifyProgram = new ShaderProgram(context, mSharedUtilShader + mMaskVerifyShader);
        this.mMaskVerifyProgram.setHostValue("verify_rate", Float.valueOf(this.mVerifyRate));
        if (this.mLogVerbose) {
            Log.v(TAG, "Shader width set to " + this.mMemoryFormat.getWidth());
        }
        this.mRelativeAspect = 1.0f;
        this.mFrameCount = 0;
        this.mStartLearning = true;
    }

    @Override // android.filterfw.core.Filter
    public void process(FilterContext context) {
        Frame video = pullInput("video");
        Frame background = pullInput("background");
        allocateFrames(video.getFormat(), context);
        if (this.mStartLearning) {
            if (this.mLogVerbose) {
                Log.v(TAG, "Starting learning");
            }
            this.mBgUpdateMeanProgram.setHostValue("bg_adapt_rate", Float.valueOf(this.mAdaptRateLearning));
            this.mBgUpdateMeanProgram.setHostValue("fg_adapt_rate", Float.valueOf(this.mAdaptRateLearning));
            this.mBgUpdateVarianceProgram.setHostValue("bg_adapt_rate", Float.valueOf(this.mAdaptRateLearning));
            this.mBgUpdateVarianceProgram.setHostValue("fg_adapt_rate", Float.valueOf(this.mAdaptRateLearning));
            this.mFrameCount = 0;
        }
        int inputIndex = this.mPingPong ? (char) 0 : (char) 1;
        int outputIndex = this.mPingPong ? (char) 1 : (char) 0;
        this.mPingPong = !this.mPingPong;
        updateBgScaling(video, background, this.mBackgroundFitModeChanged);
        this.mBackgroundFitModeChanged = false;
        this.copyShaderProgram.process(video, this.mVideoInput);
        this.copyShaderProgram.process(background, this.mBgInput);
        this.mVideoInput.generateMipMap();
        this.mVideoInput.setTextureParameter(10241, 9985);
        this.mBgInput.generateMipMap();
        this.mBgInput.setTextureParameter(10241, 9985);
        if (this.mStartLearning) {
            this.copyShaderProgram.process(this.mVideoInput, this.mBgMean[inputIndex]);
            this.mStartLearning = false;
        }
        Frame[] distInputs = {this.mVideoInput, this.mBgMean[inputIndex], this.mBgVariance[inputIndex]};
        this.mBgDistProgram.process(distInputs, this.mDistance);
        this.mDistance.generateMipMap();
        this.mDistance.setTextureParameter(10241, 9985);
        this.mBgMaskProgram.process(this.mDistance, this.mMask);
        this.mMask.generateMipMap();
        this.mMask.setTextureParameter(10241, 9985);
        Frame[] autoWBInputs = {this.mVideoInput, this.mBgInput};
        this.mAutomaticWhiteBalanceProgram.process(autoWBInputs, this.mAutoWB);
        if (this.mFrameCount <= this.mLearningDuration) {
            pushOutput("video", video);
            if (this.mFrameCount == this.mLearningDuration - this.mLearningVerifyDuration) {
                this.copyShaderProgram.process(this.mMask, this.mMaskVerify[outputIndex]);
                this.mBgUpdateMeanProgram.setHostValue("bg_adapt_rate", Float.valueOf(this.mAdaptRateBg));
                this.mBgUpdateMeanProgram.setHostValue("fg_adapt_rate", Float.valueOf(this.mAdaptRateFg));
                this.mBgUpdateVarianceProgram.setHostValue("bg_adapt_rate", Float.valueOf(this.mAdaptRateBg));
                this.mBgUpdateVarianceProgram.setHostValue("fg_adapt_rate", Float.valueOf(this.mAdaptRateFg));
            } else if (this.mFrameCount > this.mLearningDuration - this.mLearningVerifyDuration) {
                Frame[] maskVerifyInputs = {this.mMaskVerify[inputIndex], this.mMask};
                this.mMaskVerifyProgram.process(maskVerifyInputs, this.mMaskVerify[outputIndex]);
                this.mMaskVerify[outputIndex].generateMipMap();
                this.mMaskVerify[outputIndex].setTextureParameter(10241, 9985);
            }
            if (this.mFrameCount == this.mLearningDuration) {
                this.copyShaderProgram.process(this.mMaskVerify[outputIndex], this.mMaskAverage);
                ByteBuffer mMaskAverageByteBuffer = this.mMaskAverage.getData();
                byte[] mask_average = mMaskAverageByteBuffer.array();
                int bi = mask_average[3] & 255;
                if (this.mLogVerbose) {
                    Log.v(TAG, String.format("Mask_average is %d, threshold is %d", Integer.valueOf(bi), 20));
                }
                if (bi >= 20) {
                    this.mStartLearning = true;
                } else {
                    if (this.mLogVerbose) {
                        Log.v(TAG, "Learning done");
                    }
                    if (this.mLearningDoneListener != null) {
                        this.mLearningDoneListener.onLearningDone(this);
                    }
                }
            }
        } else {
            Frame output = context.getFrameManager().newFrame(video.getFormat());
            Frame[] subtractInputs = {video, background, this.mMask, this.mAutoWB};
            this.mBgSubtractProgram.process(subtractInputs, output);
            pushOutput("video", output);
            output.release();
        }
        if (this.mFrameCount < this.mLearningDuration - this.mLearningVerifyDuration || this.mAdaptRateBg > 0.0d || this.mAdaptRateFg > 0.0d) {
            Frame[] meanUpdateInputs = {this.mVideoInput, this.mBgMean[inputIndex], this.mMask};
            this.mBgUpdateMeanProgram.process(meanUpdateInputs, this.mBgMean[outputIndex]);
            this.mBgMean[outputIndex].generateMipMap();
            this.mBgMean[outputIndex].setTextureParameter(10241, 9985);
            Frame[] varianceUpdateInputs = {this.mVideoInput, this.mBgMean[inputIndex], this.mBgVariance[inputIndex], this.mMask};
            this.mBgUpdateVarianceProgram.process(varianceUpdateInputs, this.mBgVariance[outputIndex]);
            this.mBgVariance[outputIndex].generateMipMap();
            this.mBgVariance[outputIndex].setTextureParameter(10241, 9985);
        }
        if (this.mProvideDebugOutputs) {
            Frame dbg1 = context.getFrameManager().newFrame(video.getFormat());
            this.mCopyOutProgram.process(video, dbg1);
            pushOutput("debug1", dbg1);
            dbg1.release();
            Frame dbg2 = context.getFrameManager().newFrame(this.mMemoryFormat);
            this.mCopyOutProgram.process(this.mMask, dbg2);
            pushOutput("debug2", dbg2);
            dbg2.release();
        }
        this.mFrameCount++;
        if (this.mLogVerbose && this.mFrameCount % 30 == 0) {
            if (this.startTime == -1) {
                context.getGLEnvironment().activate();
                GLES20.glFinish();
                this.startTime = SystemClock.elapsedRealtime();
                return;
            }
            context.getGLEnvironment().activate();
            GLES20.glFinish();
            long endTime = SystemClock.elapsedRealtime();
            Log.v(TAG, "Avg. frame duration: " + String.format("%.2f", Double.valueOf((endTime - this.startTime) / 30.0d)) + " ms. Avg. fps: " + String.format("%.2f", Double.valueOf(1000.0d / ((endTime - this.startTime) / 30.0d))));
            this.startTime = endTime;
        }
    }

    @Override // android.filterfw.core.Filter
    public void close(FilterContext context) {
        if (this.mMemoryFormat == null) {
            return;
        }
        if (this.mLogVerbose) {
            Log.v(TAG, "Filter Closing!");
        }
        for (int i = 0; i < 2; i++) {
            this.mBgMean[i].release();
            this.mBgVariance[i].release();
            this.mMaskVerify[i].release();
        }
        this.mDistance.release();
        this.mMask.release();
        this.mAutoWB.release();
        this.mVideoInput.release();
        this.mBgInput.release();
        this.mMaskAverage.release();
        this.mMemoryFormat = null;
    }

    public synchronized void relearn() {
        this.mStartLearning = true;
    }

    @Override // android.filterfw.core.Filter
    public void fieldPortValueUpdated(String name, FilterContext context) {
        if (name.equals("backgroundFitMode")) {
            this.mBackgroundFitModeChanged = true;
        } else if (name.equals("acceptStddev")) {
            this.mBgMaskProgram.setHostValue("accept_variance", Float.valueOf(this.mAcceptStddev * this.mAcceptStddev));
        } else if (name.equals("hierLrgScale")) {
            this.mBgMaskProgram.setHostValue("scale_lrg", Float.valueOf(this.mHierarchyLrgScale));
        } else if (name.equals("hierMidScale")) {
            this.mBgMaskProgram.setHostValue("scale_mid", Float.valueOf(this.mHierarchyMidScale));
        } else if (name.equals("hierSmlScale")) {
            this.mBgMaskProgram.setHostValue("scale_sml", Float.valueOf(this.mHierarchySmlScale));
        } else if (name.equals("hierLrgExp")) {
            this.mBgMaskProgram.setHostValue("exp_lrg", Float.valueOf(this.mSubsampleLevel + this.mHierarchyLrgExp));
        } else if (name.equals("hierMidExp")) {
            this.mBgMaskProgram.setHostValue("exp_mid", Float.valueOf(this.mSubsampleLevel + this.mHierarchyMidExp));
        } else if (name.equals("hierSmlExp")) {
            this.mBgMaskProgram.setHostValue("exp_sml", Float.valueOf(this.mSubsampleLevel + this.mHierarchySmlExp));
        } else if (name.equals("lumScale") || name.equals("chromaScale")) {
            float[] yuvWeights = {this.mLumScale, this.mChromaScale};
            this.mBgMaskProgram.setHostValue("yuv_weights", yuvWeights);
        } else if (name.equals("maskBg")) {
            this.mBgSubtractProgram.setHostValue("mask_blend_bg", Float.valueOf(this.mMaskBg));
        } else if (name.equals("maskFg")) {
            this.mBgSubtractProgram.setHostValue("mask_blend_fg", Float.valueOf(this.mMaskFg));
        } else if (name.equals("exposureChange")) {
            this.mBgSubtractProgram.setHostValue("exposure_change", Float.valueOf(this.mExposureChange));
        } else if (name.equals("whitebalanceredChange")) {
            this.mBgSubtractProgram.setHostValue("whitebalancered_change", Float.valueOf(this.mWhiteBalanceRedChange));
        } else if (name.equals("whitebalanceblueChange")) {
            this.mBgSubtractProgram.setHostValue("whitebalanceblue_change", Float.valueOf(this.mWhiteBalanceBlueChange));
        } else if (name.equals("autowbToggle")) {
            this.mAutomaticWhiteBalanceProgram.setHostValue("autowb_toggle", Integer.valueOf(this.mAutoWBToggle));
        }
    }

    private void updateBgScaling(Frame video, Frame background, boolean fitModeChanged) {
        float foregroundAspect = video.getFormat().getWidth() / video.getFormat().getHeight();
        float backgroundAspect = background.getFormat().getWidth() / background.getFormat().getHeight();
        float currentRelativeAspect = foregroundAspect / backgroundAspect;
        if (currentRelativeAspect != this.mRelativeAspect || fitModeChanged) {
            this.mRelativeAspect = currentRelativeAspect;
            float xMin = 0.0f;
            float xWidth = 1.0f;
            float yMin = 0.0f;
            float yWidth = 1.0f;
            switch (this.mBackgroundFitMode) {
                case 1:
                    if (this.mRelativeAspect > 1.0f) {
                        xMin = DEFAULT_HIER_SML_SCALE - (DEFAULT_HIER_SML_SCALE * this.mRelativeAspect);
                        xWidth = 1.0f * this.mRelativeAspect;
                        break;
                    } else {
                        yMin = DEFAULT_HIER_SML_SCALE - (DEFAULT_HIER_SML_SCALE / this.mRelativeAspect);
                        yWidth = 1.0f / this.mRelativeAspect;
                        break;
                    }
                case 2:
                    if (this.mRelativeAspect > 1.0f) {
                        yMin = DEFAULT_HIER_SML_SCALE - (DEFAULT_HIER_SML_SCALE / this.mRelativeAspect);
                        yWidth = 1.0f / this.mRelativeAspect;
                        break;
                    } else {
                        xMin = DEFAULT_HIER_SML_SCALE - (DEFAULT_HIER_SML_SCALE * this.mRelativeAspect);
                        xWidth = this.mRelativeAspect;
                        break;
                    }
            }
            if (this.mMirrorBg) {
                if (this.mLogVerbose) {
                    Log.v(TAG, "Mirroring the background!");
                }
                if (this.mOrientation == 0 || this.mOrientation == 180) {
                    xWidth = -xWidth;
                    xMin = 1.0f - xMin;
                } else {
                    yWidth = -yWidth;
                    yMin = 1.0f - yMin;
                }
            }
            if (this.mLogVerbose) {
                Log.v(TAG, "bgTransform: xMin, yMin, xWidth, yWidth : " + xMin + ", " + yMin + ", " + xWidth + ", " + yWidth + ", mRelAspRatio = " + this.mRelativeAspect);
            }
            float[] bgTransform = {xWidth, 0.0f, 0.0f, 0.0f, yWidth, 0.0f, xMin, yMin, 1.0f};
            this.mBgSubtractProgram.setHostValue("bg_fit_transform", bgTransform);
        }
    }

    private int pyramidLevel(int size) {
        return ((int) Math.floor(Math.log10(size) / Math.log10(2.0d))) - 1;
    }
}