package android.media;

/* compiled from: WebVttRenderer.java */
/* loaded from: TextTrackRegion.class */
class TextTrackRegion {
    static final int SCROLL_VALUE_NONE = 300;
    static final int SCROLL_VALUE_SCROLL_UP = 301;
    String mId = "";
    float mWidth = 100.0f;
    int mLines = 3;
    float mViewportAnchorPointX = 0.0f;
    float mAnchorPointX = 0.0f;
    float mViewportAnchorPointY = 100.0f;
    float mAnchorPointY = 100.0f;
    int mScrollValue = 300;

    public String toString() {
        StringBuilder res = new StringBuilder(" {id:\"").append(this.mId).append("\", width:").append(this.mWidth).append(", lines:").append(this.mLines).append(", anchorPoint:(").append(this.mAnchorPointX).append(", ").append(this.mAnchorPointY).append("), viewportAnchorPoints:").append(this.mViewportAnchorPointX).append(", ").append(this.mViewportAnchorPointY).append("), scrollValue:").append(this.mScrollValue == 300 ? "none" : this.mScrollValue == 301 ? "scroll_up" : "INVALID").append("}");
        return res.toString();
    }
}