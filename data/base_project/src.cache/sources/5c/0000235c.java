package java.math;

import android.media.MediaPlayer;
import com.android.internal.R;
import com.android.internal.telephony.RILConstants;
import com.android.server.MountService;
import com.android.server.NetworkManagementService;
import com.android.server.NsdService;
import java.util.Arrays;
import javax.sip.message.Response;

/* loaded from: Primality.class */
class Primality {
    private static final int[] primes = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97, 101, 103, 107, 109, 113, 127, 131, 137, 139, 149, 151, 157, 163, 167, 173, 179, 181, 191, 193, 197, 199, 211, 223, R.styleable.Theme_searchResultListItemHeight, R.styleable.Theme_windowSplitActionBar, 233, 239, R.styleable.Theme_actionModeShareDrawable, 251, 257, 263, R.styleable.Theme_findOnPageNextDrawable, 271, 277, 281, 283, 293, 307, 311, 313, 317, 331, 337, 347, 349, 353, 359, 367, 373, 379, 383, 389, 397, 401, 409, 419, Response.EXTENSION_REQUIRED, 431, 433, 439, 443, 449, 457, 461, 463, 467, 479, Response.REQUEST_TERMINATED, 491, 499, 503, 509, 521, 523, 541, 547, 557, 563, 569, 571, 577, 587, 593, 599, NetworkManagementService.NetdResponseCode.BandwidthControl, NsdService.NativeResponseCode.SERVICE_RESOLUTION_FAILED, NetworkManagementService.NetdResponseCode.InterfaceClassActivity, 617, 619, MountService.VoldResponseCode.VolumeDiskRemoved, 641, 643, 647, 653, 659, 661, 673, 677, 683, 691, MediaPlayer.MEDIA_INFO_BUFFERING_START, 709, 719, 727, 733, 739, 743, 751, 757, 761, 769, 773, 787, 797, 809, 811, 821, 823, 827, 829, 839, 853, 857, 859, 863, 877, 881, 883, 887, 907, 911, 919, 929, 937, 941, 947, 953, 967, 971, 977, 983, 991, 997, 1009, 1013, 1019, RILConstants.RIL_UNSOL_RESPONSE_NEW_BROADCAST_SMS};
    private static final BigInteger[] BIprimes = new BigInteger[primes.length];

    private Primality() {
    }

    static {
        for (int i = 0; i < primes.length; i++) {
            BIprimes[i] = BigInteger.valueOf(primes[i]);
        }
    }

    static BigInteger nextProbablePrime(BigInteger n) {
        int l;
        int[] modules = new int[primes.length];
        boolean[] isDivisible = new boolean[1024];
        BigInt ni = n.getBigInt();
        if (ni.bitLength() <= 10 && (l = (int) ni.longInt()) < primes[primes.length - 1]) {
            int i = 0;
            while (l >= primes[i]) {
                i++;
            }
            return BIprimes[i];
        }
        BigInt startPoint = ni.copy();
        BigInt probPrime = new BigInt();
        startPoint.addPositiveInt(BigInt.remainderByPositiveInt(ni, 2) + 1);
        for (int i2 = 0; i2 < primes.length; i2++) {
            modules[i2] = BigInt.remainderByPositiveInt(startPoint, primes[i2]) - 1024;
        }
        while (true) {
            Arrays.fill(isDivisible, false);
            for (int i3 = 0; i3 < primes.length; i3++) {
                modules[i3] = (modules[i3] + 1024) % primes[i3];
                int i4 = modules[i3] == 0 ? 0 : primes[i3] - modules[i3];
                while (true) {
                    int j = i4;
                    if (j < 1024) {
                        isDivisible[j] = true;
                        i4 = j + primes[i3];
                    }
                }
            }
            for (int j2 = 0; j2 < 1024; j2++) {
                if (!isDivisible[j2]) {
                    probPrime.putCopy(startPoint);
                    probPrime.addPositiveInt(j2);
                    if (probPrime.isPrime(100)) {
                        return new BigInteger(probPrime);
                    }
                }
            }
            startPoint.addPositiveInt(1024);
        }
    }
}