package java.util.concurrent;

import android.text.format.DateUtils;

/* JADX WARN: Classes with same name are omitted:
  
 */
/* loaded from: TimeUnit.class */
public enum TimeUnit {
    DAYS,
    HOURS,
    MICROSECONDS,
    MILLISECONDS,
    MINUTES,
    NANOSECONDS,
    SECONDS;

    public long convert(long sourceDuration, TimeUnit sourceUnit) {
        throw new RuntimeException("Stub!");
    }

    public long toNanos(long duration) {
        throw new RuntimeException("Stub!");
    }

    public long toMicros(long duration) {
        throw new RuntimeException("Stub!");
    }

    public long toMillis(long duration) {
        throw new RuntimeException("Stub!");
    }

    public long toSeconds(long duration) {
        throw new RuntimeException("Stub!");
    }

    public long toMinutes(long duration) {
        throw new RuntimeException("Stub!");
    }

    public long toHours(long duration) {
        throw new RuntimeException("Stub!");
    }

    public long toDays(long duration) {
        throw new RuntimeException("Stub!");
    }

    public void timedWait(Object obj, long timeout) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public void timedJoin(Thread thread, long timeout) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    public void sleep(long timeout) throws InterruptedException {
        throw new RuntimeException("Stub!");
    }

    /* renamed from: java.util.concurrent.TimeUnit$1  reason: invalid class name */
    /* loaded from: TimeUnit$1.class */
    enum AnonymousClass1 extends TimeUnit {
        AnonymousClass1(String str, int i) {
            super(str, i, null);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toNanos(long d) {
            return d;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMicros(long d) {
            return d / 1000;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMillis(long d) {
            return d / TimeUnit.C2;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toSeconds(long d) {
            return d / TimeUnit.C3;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMinutes(long d) {
            return d / TimeUnit.C4;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toHours(long d) {
            return d / TimeUnit.C5;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toDays(long d) {
            return d / TimeUnit.C6;
        }

        @Override // java.util.concurrent.TimeUnit
        public long convert(long d, TimeUnit u) {
            return u.toNanos(d);
        }

        int excessNanos(long d, long m) {
            return (int) (d - (m * TimeUnit.C2));
        }
    }

    /* renamed from: java.util.concurrent.TimeUnit$2  reason: invalid class name */
    /* loaded from: TimeUnit$2.class */
    enum AnonymousClass2 extends TimeUnit {
        AnonymousClass2(String str, int i) {
            super(str, i, null);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toNanos(long d) {
            return x(d, 1000L, 9223372036854775L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMicros(long d) {
            return d;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMillis(long d) {
            return d / 1000;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toSeconds(long d) {
            return d / TimeUnit.C2;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMinutes(long d) {
            return d / 60000000;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toHours(long d) {
            return d / 3600000000L;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toDays(long d) {
            return d / 86400000000L;
        }

        @Override // java.util.concurrent.TimeUnit
        public long convert(long d, TimeUnit u) {
            return u.toMicros(d);
        }

        int excessNanos(long d, long m) {
            return (int) ((d * 1000) - (m * TimeUnit.C2));
        }
    }

    /* renamed from: java.util.concurrent.TimeUnit$3  reason: invalid class name */
    /* loaded from: TimeUnit$3.class */
    enum AnonymousClass3 extends TimeUnit {
        AnonymousClass3(String str, int i) {
            super(str, i, null);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toNanos(long d) {
            return x(d, TimeUnit.C2, 9223372036854L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMicros(long d) {
            return x(d, 1000L, 9223372036854775L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMillis(long d) {
            return d;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toSeconds(long d) {
            return d / 1000;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMinutes(long d) {
            return d / DateUtils.MINUTE_IN_MILLIS;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toHours(long d) {
            return d / 3600000;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toDays(long d) {
            return d / 86400000;
        }

        @Override // java.util.concurrent.TimeUnit
        public long convert(long d, TimeUnit u) {
            return u.toMillis(d);
        }

        int excessNanos(long d, long m) {
            return 0;
        }
    }

    /* renamed from: java.util.concurrent.TimeUnit$4  reason: invalid class name */
    /* loaded from: TimeUnit$4.class */
    enum AnonymousClass4 extends TimeUnit {
        AnonymousClass4(String str, int i) {
            super(str, i, null);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toNanos(long d) {
            return x(d, TimeUnit.C3, 9223372036L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMicros(long d) {
            return x(d, TimeUnit.C2, 9223372036854L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMillis(long d) {
            return x(d, 1000L, 9223372036854775L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toSeconds(long d) {
            return d;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMinutes(long d) {
            return d / 60;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toHours(long d) {
            return d / 3600;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toDays(long d) {
            return d / 86400;
        }

        @Override // java.util.concurrent.TimeUnit
        public long convert(long d, TimeUnit u) {
            return u.toSeconds(d);
        }

        int excessNanos(long d, long m) {
            return 0;
        }
    }

    /* renamed from: java.util.concurrent.TimeUnit$5  reason: invalid class name */
    /* loaded from: TimeUnit$5.class */
    enum AnonymousClass5 extends TimeUnit {
        AnonymousClass5(String str, int i) {
            super(str, i, null);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toNanos(long d) {
            return x(d, TimeUnit.C4, 153722867L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMicros(long d) {
            return x(d, 60000000L, 153722867280L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMillis(long d) {
            return x(d, DateUtils.MINUTE_IN_MILLIS, 153722867280912L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toSeconds(long d) {
            return x(d, 60L, 153722867280912930L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMinutes(long d) {
            return d;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toHours(long d) {
            return d / 60;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toDays(long d) {
            return d / 1440;
        }

        @Override // java.util.concurrent.TimeUnit
        public long convert(long d, TimeUnit u) {
            return u.toMinutes(d);
        }

        int excessNanos(long d, long m) {
            return 0;
        }
    }

    /* renamed from: java.util.concurrent.TimeUnit$6  reason: invalid class name */
    /* loaded from: TimeUnit$6.class */
    enum AnonymousClass6 extends TimeUnit {
        AnonymousClass6(String str, int i) {
            super(str, i, null);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toNanos(long d) {
            return x(d, TimeUnit.C5, 2562047L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMicros(long d) {
            return x(d, 3600000000L, 2562047788L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMillis(long d) {
            return x(d, 3600000L, 2562047788015L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toSeconds(long d) {
            return x(d, 3600L, 2562047788015215L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMinutes(long d) {
            return x(d, 60L, 153722867280912930L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toHours(long d) {
            return d;
        }

        @Override // java.util.concurrent.TimeUnit
        public long toDays(long d) {
            return d / 24;
        }

        @Override // java.util.concurrent.TimeUnit
        public long convert(long d, TimeUnit u) {
            return u.toHours(d);
        }

        int excessNanos(long d, long m) {
            return 0;
        }
    }

    /* renamed from: java.util.concurrent.TimeUnit$7  reason: invalid class name */
    /* loaded from: TimeUnit$7.class */
    enum AnonymousClass7 extends TimeUnit {
        AnonymousClass7(String str, int i) {
            super(str, i, null);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toNanos(long d) {
            return x(d, TimeUnit.C6, 106751L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMicros(long d) {
            return x(d, 86400000000L, 106751991L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMillis(long d) {
            return x(d, 86400000L, 106751991167L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toSeconds(long d) {
            return x(d, 86400L, 106751991167300L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toMinutes(long d) {
            return x(d, 1440L, 6405119470038038L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toHours(long d) {
            return x(d, 24L, 384307168202282325L);
        }

        @Override // java.util.concurrent.TimeUnit
        public long toDays(long d) {
            return d;
        }

        @Override // java.util.concurrent.TimeUnit
        public long convert(long d, TimeUnit u) {
            return u.toDays(d);
        }

        int excessNanos(long d, long m) {
            return 0;
        }
    }
}