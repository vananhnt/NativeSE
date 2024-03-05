package android.location;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Printer;
import android.util.TimeUtils;
import gov.nist.core.Separators;
import java.text.DecimalFormat;
import java.util.StringTokenizer;

/* loaded from: Location.class */
public class Location implements Parcelable {
    public static final int FORMAT_DEGREES = 0;
    public static final int FORMAT_MINUTES = 1;
    public static final int FORMAT_SECONDS = 2;
    public static final String EXTRA_COARSE_LOCATION = "coarseLocation";
    public static final String EXTRA_NO_GPS_LOCATION = "noGPSLocation";
    private String mProvider;
    private long mTime;
    private long mElapsedRealtimeNanos;
    private double mLatitude;
    private double mLongitude;
    private boolean mHasAltitude;
    private double mAltitude;
    private boolean mHasSpeed;
    private float mSpeed;
    private boolean mHasBearing;
    private float mBearing;
    private boolean mHasAccuracy;
    private float mAccuracy;
    private Bundle mExtras;
    private boolean mIsFromMockProvider;
    private double mLat1;
    private double mLon1;
    private double mLat2;
    private double mLon2;
    private float mDistance;
    private float mInitialBearing;
    private final float[] mResults;
    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() { // from class: android.location.Location.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Location createFromParcel(Parcel in) {
            String provider = in.readString();
            Location l = new Location(provider);
            l.mTime = in.readLong();
            l.mElapsedRealtimeNanos = in.readLong();
            l.mLatitude = in.readDouble();
            l.mLongitude = in.readDouble();
            l.mHasAltitude = in.readInt() != 0;
            l.mAltitude = in.readDouble();
            l.mHasSpeed = in.readInt() != 0;
            l.mSpeed = in.readFloat();
            l.mHasBearing = in.readInt() != 0;
            l.mBearing = in.readFloat();
            l.mHasAccuracy = in.readInt() != 0;
            l.mAccuracy = in.readFloat();
            l.mExtras = in.readBundle();
            l.mIsFromMockProvider = in.readInt() != 0;
            return l;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    public Location(String provider) {
        this.mTime = 0L;
        this.mElapsedRealtimeNanos = 0L;
        this.mLatitude = 0.0d;
        this.mLongitude = 0.0d;
        this.mHasAltitude = false;
        this.mAltitude = 0.0d;
        this.mHasSpeed = false;
        this.mSpeed = 0.0f;
        this.mHasBearing = false;
        this.mBearing = 0.0f;
        this.mHasAccuracy = false;
        this.mAccuracy = 0.0f;
        this.mExtras = null;
        this.mIsFromMockProvider = false;
        this.mLat1 = 0.0d;
        this.mLon1 = 0.0d;
        this.mLat2 = 0.0d;
        this.mLon2 = 0.0d;
        this.mDistance = 0.0f;
        this.mInitialBearing = 0.0f;
        this.mResults = new float[2];
        this.mProvider = provider;
    }

    public Location(Location l) {
        this.mTime = 0L;
        this.mElapsedRealtimeNanos = 0L;
        this.mLatitude = 0.0d;
        this.mLongitude = 0.0d;
        this.mHasAltitude = false;
        this.mAltitude = 0.0d;
        this.mHasSpeed = false;
        this.mSpeed = 0.0f;
        this.mHasBearing = false;
        this.mBearing = 0.0f;
        this.mHasAccuracy = false;
        this.mAccuracy = 0.0f;
        this.mExtras = null;
        this.mIsFromMockProvider = false;
        this.mLat1 = 0.0d;
        this.mLon1 = 0.0d;
        this.mLat2 = 0.0d;
        this.mLon2 = 0.0d;
        this.mDistance = 0.0f;
        this.mInitialBearing = 0.0f;
        this.mResults = new float[2];
        set(l);
    }

    public void set(Location l) {
        this.mProvider = l.mProvider;
        this.mTime = l.mTime;
        this.mElapsedRealtimeNanos = l.mElapsedRealtimeNanos;
        this.mLatitude = l.mLatitude;
        this.mLongitude = l.mLongitude;
        this.mHasAltitude = l.mHasAltitude;
        this.mAltitude = l.mAltitude;
        this.mHasSpeed = l.mHasSpeed;
        this.mSpeed = l.mSpeed;
        this.mHasBearing = l.mHasBearing;
        this.mBearing = l.mBearing;
        this.mHasAccuracy = l.mHasAccuracy;
        this.mAccuracy = l.mAccuracy;
        this.mExtras = l.mExtras == null ? null : new Bundle(l.mExtras);
        this.mIsFromMockProvider = l.mIsFromMockProvider;
    }

    public void reset() {
        this.mProvider = null;
        this.mTime = 0L;
        this.mElapsedRealtimeNanos = 0L;
        this.mLatitude = 0.0d;
        this.mLongitude = 0.0d;
        this.mHasAltitude = false;
        this.mAltitude = 0.0d;
        this.mHasSpeed = false;
        this.mSpeed = 0.0f;
        this.mHasBearing = false;
        this.mBearing = 0.0f;
        this.mHasAccuracy = false;
        this.mAccuracy = 0.0f;
        this.mExtras = null;
        this.mIsFromMockProvider = false;
    }

    public static String convert(double coordinate, int outputType) {
        if (coordinate < -180.0d || coordinate > 180.0d || Double.isNaN(coordinate)) {
            throw new IllegalArgumentException("coordinate=" + coordinate);
        }
        if (outputType != 0 && outputType != 1 && outputType != 2) {
            throw new IllegalArgumentException("outputType=" + outputType);
        }
        StringBuilder sb = new StringBuilder();
        if (coordinate < 0.0d) {
            sb.append('-');
            coordinate = -coordinate;
        }
        DecimalFormat df = new DecimalFormat("###.#####");
        if (outputType == 1 || outputType == 2) {
            int degrees = (int) Math.floor(coordinate);
            sb.append(degrees);
            sb.append(':');
            coordinate = (coordinate - degrees) * 60.0d;
            if (outputType == 2) {
                int minutes = (int) Math.floor(coordinate);
                sb.append(minutes);
                sb.append(':');
                coordinate = (coordinate - minutes) * 60.0d;
            }
        }
        sb.append(df.format(coordinate));
        return sb.toString();
    }

    public static double convert(String coordinate) {
        double min;
        if (coordinate == null) {
            throw new NullPointerException("coordinate");
        }
        boolean negative = false;
        if (coordinate.charAt(0) == '-') {
            coordinate = coordinate.substring(1);
            negative = true;
        }
        StringTokenizer st = new StringTokenizer(coordinate, Separators.COLON);
        int tokens = st.countTokens();
        if (tokens < 1) {
            throw new IllegalArgumentException("coordinate=" + coordinate);
        }
        try {
            String degrees = st.nextToken();
            if (tokens == 1) {
                double val = Double.parseDouble(degrees);
                return negative ? -val : val;
            }
            String minutes = st.nextToken();
            int deg = Integer.parseInt(degrees);
            double sec = 0.0d;
            if (st.hasMoreTokens()) {
                min = Integer.parseInt(minutes);
                String seconds = st.nextToken();
                sec = Double.parseDouble(seconds);
            } else {
                min = Double.parseDouble(minutes);
            }
            boolean isNegative180 = negative && deg == 180 && min == 0.0d && sec == 0.0d;
            if (deg < 0.0d || (deg > 179 && !isNegative180)) {
                throw new IllegalArgumentException("coordinate=" + coordinate);
            }
            if (min < 0.0d || min > 59.0d) {
                throw new IllegalArgumentException("coordinate=" + coordinate);
            }
            if (sec < 0.0d || sec > 59.0d) {
                throw new IllegalArgumentException("coordinate=" + coordinate);
            }
            double val2 = (((deg * 3600.0d) + (min * 60.0d)) + sec) / 3600.0d;
            return negative ? -val2 : val2;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("coordinate=" + coordinate);
        }
    }

    private static void computeDistanceAndBearing(double lat1, double lon1, double lat2, double lon2, float[] results) {
        double f = (6378137.0d - 6356752.3142d) / 6378137.0d;
        double aSqMinusBSqOverBSq = ((6378137.0d * 6378137.0d) - (6356752.3142d * 6356752.3142d)) / (6356752.3142d * 6356752.3142d);
        double L = (lon2 * 0.017453292519943295d) - (lon1 * 0.017453292519943295d);
        double A = 0.0d;
        double U1 = Math.atan((1.0d - f) * Math.tan(lat1 * 0.017453292519943295d));
        double U2 = Math.atan((1.0d - f) * Math.tan(lat2 * 0.017453292519943295d));
        double cosU1 = Math.cos(U1);
        double cosU2 = Math.cos(U2);
        double sinU1 = Math.sin(U1);
        double sinU2 = Math.sin(U2);
        double cosU1cosU2 = cosU1 * cosU2;
        double sinU1sinU2 = sinU1 * sinU2;
        double sigma = 0.0d;
        double deltaSigma = 0.0d;
        double cosLambda = 0.0d;
        double sinLambda = 0.0d;
        double lambda = L;
        for (int iter = 0; iter < 20; iter++) {
            double lambdaOrig = lambda;
            cosLambda = Math.cos(lambda);
            sinLambda = Math.sin(lambda);
            double t1 = cosU2 * sinLambda;
            double t2 = (cosU1 * sinU2) - ((sinU1 * cosU2) * cosLambda);
            double sinSqSigma = (t1 * t1) + (t2 * t2);
            double sinSigma = Math.sqrt(sinSqSigma);
            double cosSigma = sinU1sinU2 + (cosU1cosU2 * cosLambda);
            sigma = Math.atan2(sinSigma, cosSigma);
            double sinAlpha = sinSigma == 0.0d ? 0.0d : (cosU1cosU2 * sinLambda) / sinSigma;
            double cosSqAlpha = 1.0d - (sinAlpha * sinAlpha);
            double cos2SM = cosSqAlpha == 0.0d ? 0.0d : cosSigma - ((2.0d * sinU1sinU2) / cosSqAlpha);
            double uSquared = cosSqAlpha * aSqMinusBSqOverBSq;
            A = 1.0d + ((uSquared / 16384.0d) * (4096.0d + (uSquared * ((-768.0d) + (uSquared * (320.0d - (175.0d * uSquared)))))));
            double B = (uSquared / 1024.0d) * (256.0d + (uSquared * ((-128.0d) + (uSquared * (74.0d - (47.0d * uSquared))))));
            double C = (f / 16.0d) * cosSqAlpha * (4.0d + (f * (4.0d - (3.0d * cosSqAlpha))));
            double cos2SMSq = cos2SM * cos2SM;
            deltaSigma = B * sinSigma * (cos2SM + ((B / 4.0d) * ((cosSigma * ((-1.0d) + (2.0d * cos2SMSq))) - ((((B / 6.0d) * cos2SM) * ((-3.0d) + ((4.0d * sinSigma) * sinSigma))) * ((-3.0d) + (4.0d * cos2SMSq))))));
            lambda = L + ((1.0d - C) * f * sinAlpha * (sigma + (C * sinSigma * (cos2SM + (C * cosSigma * ((-1.0d) + (2.0d * cos2SM * cos2SM)))))));
            double delta = (lambda - lambdaOrig) / lambda;
            if (Math.abs(delta) < 1.0E-12d) {
                break;
            }
        }
        float distance = (float) (6356752.3142d * A * (sigma - deltaSigma));
        results[0] = distance;
        if (results.length > 1) {
            float initialBearing = (float) Math.atan2(cosU2 * sinLambda, (cosU1 * sinU2) - ((sinU1 * cosU2) * cosLambda));
            results[1] = (float) (initialBearing * 57.29577951308232d);
            if (results.length > 2) {
                float finalBearing = (float) Math.atan2(cosU1 * sinLambda, ((-sinU1) * cosU2) + (cosU1 * sinU2 * cosLambda));
                results[2] = (float) (finalBearing * 57.29577951308232d);
            }
        }
    }

    public static void distanceBetween(double startLatitude, double startLongitude, double endLatitude, double endLongitude, float[] results) {
        if (results == null || results.length < 1) {
            throw new IllegalArgumentException("results is null or has length < 1");
        }
        computeDistanceAndBearing(startLatitude, startLongitude, endLatitude, endLongitude, results);
    }

    public float distanceTo(Location dest) {
        float f;
        synchronized (this.mResults) {
            if (this.mLatitude != this.mLat1 || this.mLongitude != this.mLon1 || dest.mLatitude != this.mLat2 || dest.mLongitude != this.mLon2) {
                computeDistanceAndBearing(this.mLatitude, this.mLongitude, dest.mLatitude, dest.mLongitude, this.mResults);
                this.mLat1 = this.mLatitude;
                this.mLon1 = this.mLongitude;
                this.mLat2 = dest.mLatitude;
                this.mLon2 = dest.mLongitude;
                this.mDistance = this.mResults[0];
                this.mInitialBearing = this.mResults[1];
            }
            f = this.mDistance;
        }
        return f;
    }

    public float bearingTo(Location dest) {
        float f;
        synchronized (this.mResults) {
            if (this.mLatitude != this.mLat1 || this.mLongitude != this.mLon1 || dest.mLatitude != this.mLat2 || dest.mLongitude != this.mLon2) {
                computeDistanceAndBearing(this.mLatitude, this.mLongitude, dest.mLatitude, dest.mLongitude, this.mResults);
                this.mLat1 = this.mLatitude;
                this.mLon1 = this.mLongitude;
                this.mLat2 = dest.mLatitude;
                this.mLon2 = dest.mLongitude;
                this.mDistance = this.mResults[0];
                this.mInitialBearing = this.mResults[1];
            }
            f = this.mInitialBearing;
        }
        return f;
    }

    public String getProvider() {
        return this.mProvider;
    }

    public void setProvider(String provider) {
        this.mProvider = provider;
    }

    public long getTime() {
        return this.mTime;
    }

    public void setTime(long time) {
        this.mTime = time;
    }

    public long getElapsedRealtimeNanos() {
        return this.mElapsedRealtimeNanos;
    }

    public void setElapsedRealtimeNanos(long time) {
        this.mElapsedRealtimeNanos = time;
    }

    public double getLatitude() {
        return this.mLatitude;
    }

    public void setLatitude(double latitude) {
        this.mLatitude = latitude;
    }

    public double getLongitude() {
        return this.mLongitude;
    }

    public void setLongitude(double longitude) {
        this.mLongitude = longitude;
    }

    public boolean hasAltitude() {
        return this.mHasAltitude;
    }

    public double getAltitude() {
        return this.mAltitude;
    }

    public void setAltitude(double altitude) {
        this.mAltitude = altitude;
        this.mHasAltitude = true;
    }

    public void removeAltitude() {
        this.mAltitude = 0.0d;
        this.mHasAltitude = false;
    }

    public boolean hasSpeed() {
        return this.mHasSpeed;
    }

    public float getSpeed() {
        return this.mSpeed;
    }

    public void setSpeed(float speed) {
        this.mSpeed = speed;
        this.mHasSpeed = true;
    }

    public void removeSpeed() {
        this.mSpeed = 0.0f;
        this.mHasSpeed = false;
    }

    public boolean hasBearing() {
        return this.mHasBearing;
    }

    public float getBearing() {
        return this.mBearing;
    }

    public void setBearing(float bearing) {
        while (bearing < 0.0f) {
            bearing += 360.0f;
        }
        while (bearing >= 360.0f) {
            bearing -= 360.0f;
        }
        this.mBearing = bearing;
        this.mHasBearing = true;
    }

    public void removeBearing() {
        this.mBearing = 0.0f;
        this.mHasBearing = false;
    }

    public boolean hasAccuracy() {
        return this.mHasAccuracy;
    }

    public float getAccuracy() {
        return this.mAccuracy;
    }

    public void setAccuracy(float accuracy) {
        this.mAccuracy = accuracy;
        this.mHasAccuracy = true;
    }

    public void removeAccuracy() {
        this.mAccuracy = 0.0f;
        this.mHasAccuracy = false;
    }

    public boolean isComplete() {
        return (this.mProvider == null || !this.mHasAccuracy || this.mTime == 0 || this.mElapsedRealtimeNanos == 0) ? false : true;
    }

    public void makeComplete() {
        if (this.mProvider == null) {
            this.mProvider = Separators.QUESTION;
        }
        if (!this.mHasAccuracy) {
            this.mHasAccuracy = true;
            this.mAccuracy = 100.0f;
        }
        if (this.mTime == 0) {
            this.mTime = System.currentTimeMillis();
        }
        if (this.mElapsedRealtimeNanos == 0) {
            this.mElapsedRealtimeNanos = SystemClock.elapsedRealtimeNanos();
        }
    }

    public Bundle getExtras() {
        return this.mExtras;
    }

    public void setExtras(Bundle extras) {
        this.mExtras = extras == null ? null : new Bundle(extras);
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append("Location[");
        s.append(this.mProvider);
        s.append(String.format(" %.6f,%.6f", Double.valueOf(this.mLatitude), Double.valueOf(this.mLongitude)));
        if (this.mHasAccuracy) {
            s.append(String.format(" acc=%.0f", Float.valueOf(this.mAccuracy)));
        } else {
            s.append(" acc=???");
        }
        if (this.mTime == 0) {
            s.append(" t=?!?");
        }
        if (this.mElapsedRealtimeNanos == 0) {
            s.append(" et=?!?");
        } else {
            s.append(" et=");
            TimeUtils.formatDuration(this.mElapsedRealtimeNanos / 1000000, s);
        }
        if (this.mHasAltitude) {
            s.append(" alt=").append(this.mAltitude);
        }
        if (this.mHasSpeed) {
            s.append(" vel=").append(this.mSpeed);
        }
        if (this.mHasBearing) {
            s.append(" bear=").append(this.mBearing);
        }
        if (this.mIsFromMockProvider) {
            s.append(" mock");
        }
        if (this.mExtras != null) {
            s.append(" {").append(this.mExtras).append('}');
        }
        s.append(']');
        return s.toString();
    }

    public void dump(Printer pw, String prefix) {
        pw.println(prefix + toString());
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(this.mProvider);
        parcel.writeLong(this.mTime);
        parcel.writeLong(this.mElapsedRealtimeNanos);
        parcel.writeDouble(this.mLatitude);
        parcel.writeDouble(this.mLongitude);
        parcel.writeInt(this.mHasAltitude ? 1 : 0);
        parcel.writeDouble(this.mAltitude);
        parcel.writeInt(this.mHasSpeed ? 1 : 0);
        parcel.writeFloat(this.mSpeed);
        parcel.writeInt(this.mHasBearing ? 1 : 0);
        parcel.writeFloat(this.mBearing);
        parcel.writeInt(this.mHasAccuracy ? 1 : 0);
        parcel.writeFloat(this.mAccuracy);
        parcel.writeBundle(this.mExtras);
        parcel.writeInt(this.mIsFromMockProvider ? 1 : 0);
    }

    public Location getExtraLocation(String key) {
        if (this.mExtras != null) {
            Parcelable value = this.mExtras.getParcelable(key);
            if (value instanceof Location) {
                return (Location) value;
            }
            return null;
        }
        return null;
    }

    public void setExtraLocation(String key, Location value) {
        if (this.mExtras == null) {
            this.mExtras = new Bundle();
        }
        this.mExtras.putParcelable(key, value);
    }

    public boolean isFromMockProvider() {
        return this.mIsFromMockProvider;
    }

    public void setIsFromMockProvider(boolean isFromMockProvider) {
        this.mIsFromMockProvider = isFromMockProvider;
    }
}