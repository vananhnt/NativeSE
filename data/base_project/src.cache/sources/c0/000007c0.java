package android.media;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/* loaded from: Rating.class */
public final class Rating implements Parcelable {
    private static final String TAG = "Rating";
    public static final int RATING_HEART = 1;
    public static final int RATING_THUMB_UP_DOWN = 2;
    public static final int RATING_3_STARS = 3;
    public static final int RATING_4_STARS = 4;
    public static final int RATING_5_STARS = 5;
    public static final int RATING_PERCENTAGE = 6;
    private static final float RATING_NOT_RATED = -1.0f;
    private final int mRatingStyle;
    private final float mRatingValue;
    public static final Parcelable.Creator<Rating> CREATOR = new Parcelable.Creator<Rating>() { // from class: android.media.Rating.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Rating createFromParcel(Parcel p) {
            return new Rating(p.readInt(), p.readFloat());
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Rating[] newArray(int size) {
            return new Rating[size];
        }
    };

    private Rating(int ratingStyle, float rating) {
        this.mRatingStyle = ratingStyle;
        this.mRatingValue = rating;
    }

    public String toString() {
        return "Rating:style=" + this.mRatingStyle + " rating=" + (this.mRatingValue < 0.0f ? "unrated" : String.valueOf(this.mRatingValue));
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return this.mRatingStyle;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.mRatingStyle);
        dest.writeFloat(this.mRatingValue);
    }

    public static Rating newUnratedRating(int ratingStyle) {
        switch (ratingStyle) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
                return new Rating(ratingStyle, -1.0f);
            default:
                return null;
        }
    }

    public static Rating newHeartRating(boolean hasHeart) {
        return new Rating(1, hasHeart ? 1.0f : 0.0f);
    }

    public static Rating newThumbRating(boolean thumbIsUp) {
        return new Rating(2, thumbIsUp ? 1.0f : 0.0f);
    }

    public static Rating newStarRating(int starRatingStyle, float starRating) {
        float maxRating;
        switch (starRatingStyle) {
            case 3:
                maxRating = 3.0f;
                break;
            case 4:
                maxRating = 4.0f;
                break;
            case 5:
                maxRating = 5.0f;
                break;
            default:
                Log.e(TAG, "Invalid rating style (" + starRatingStyle + ") for a star rating");
                return null;
        }
        if (starRating < 0.0f || starRating > maxRating) {
            Log.e(TAG, "Trying to set out of range star-based rating");
            return null;
        }
        return new Rating(starRatingStyle, starRating);
    }

    public static Rating newPercentageRating(float percent) {
        if (percent < 0.0f || percent > 100.0f) {
            Log.e(TAG, "Invalid percentage-based rating value");
            return null;
        }
        return new Rating(6, percent);
    }

    public boolean isRated() {
        return this.mRatingValue >= 0.0f;
    }

    public int getRatingStyle() {
        return this.mRatingStyle;
    }

    public boolean hasHeart() {
        return this.mRatingStyle == 1 && this.mRatingValue == 1.0f;
    }

    public boolean isThumbUp() {
        return this.mRatingStyle == 2 && this.mRatingValue == 1.0f;
    }

    public float getStarRating() {
        switch (this.mRatingStyle) {
            case 3:
            case 4:
            case 5:
                if (isRated()) {
                    return this.mRatingValue;
                }
                return -1.0f;
            default:
                return -1.0f;
        }
    }

    public float getPercentRating() {
        if (this.mRatingStyle != 6 || !isRated()) {
            return -1.0f;
        }
        return this.mRatingValue;
    }
}