package android.support.v4.net;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;

/* loaded from: ConnectivityManagerCompat.class */
public class ConnectivityManagerCompat {
    private static final ConnectivityManagerCompatImpl IMPL;

    /* loaded from: ConnectivityManagerCompat$BaseConnectivityManagerCompatImpl.class */
    static class BaseConnectivityManagerCompatImpl implements ConnectivityManagerCompatImpl {
        BaseConnectivityManagerCompatImpl() {
        }

        @Override // android.support.v4.net.ConnectivityManagerCompat.ConnectivityManagerCompatImpl
        public boolean isActiveNetworkMetered(ConnectivityManager connectivityManager) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo == null) {
                return true;
            }
            switch (activeNetworkInfo.getType()) {
                case 0:
                    return true;
                case 1:
                    return false;
                default:
                    return true;
            }
        }
    }

    /* loaded from: ConnectivityManagerCompat$ConnectivityManagerCompatImpl.class */
    interface ConnectivityManagerCompatImpl {
        boolean isActiveNetworkMetered(ConnectivityManager connectivityManager);
    }

    /* loaded from: ConnectivityManagerCompat$GingerbreadConnectivityManagerCompatImpl.class */
    static class GingerbreadConnectivityManagerCompatImpl implements ConnectivityManagerCompatImpl {
        GingerbreadConnectivityManagerCompatImpl() {
        }

        @Override // android.support.v4.net.ConnectivityManagerCompat.ConnectivityManagerCompatImpl
        public boolean isActiveNetworkMetered(ConnectivityManager connectivityManager) {
            return ConnectivityManagerCompatGingerbread.isActiveNetworkMetered(connectivityManager);
        }
    }

    /* loaded from: ConnectivityManagerCompat$HoneycombMR2ConnectivityManagerCompatImpl.class */
    static class HoneycombMR2ConnectivityManagerCompatImpl implements ConnectivityManagerCompatImpl {
        HoneycombMR2ConnectivityManagerCompatImpl() {
        }

        @Override // android.support.v4.net.ConnectivityManagerCompat.ConnectivityManagerCompatImpl
        public boolean isActiveNetworkMetered(ConnectivityManager connectivityManager) {
            return ConnectivityManagerCompatHoneycombMR2.isActiveNetworkMetered(connectivityManager);
        }
    }

    /* loaded from: ConnectivityManagerCompat$JellyBeanConnectivityManagerCompatImpl.class */
    static class JellyBeanConnectivityManagerCompatImpl implements ConnectivityManagerCompatImpl {
        JellyBeanConnectivityManagerCompatImpl() {
        }

        @Override // android.support.v4.net.ConnectivityManagerCompat.ConnectivityManagerCompatImpl
        public boolean isActiveNetworkMetered(ConnectivityManager connectivityManager) {
            return ConnectivityManagerCompatJellyBean.isActiveNetworkMetered(connectivityManager);
        }
    }

    static {
        if (Build.VERSION.SDK_INT >= 16) {
            IMPL = new JellyBeanConnectivityManagerCompatImpl();
        } else if (Build.VERSION.SDK_INT >= 13) {
            IMPL = new HoneycombMR2ConnectivityManagerCompatImpl();
        } else if (Build.VERSION.SDK_INT >= 8) {
            IMPL = new GingerbreadConnectivityManagerCompatImpl();
        } else {
            IMPL = new BaseConnectivityManagerCompatImpl();
        }
    }

    public static NetworkInfo getNetworkInfoFromBroadcast(ConnectivityManager connectivityManager, Intent intent) {
        NetworkInfo networkInfo = (NetworkInfo) intent.getParcelableExtra("networkInfo");
        if (networkInfo != null) {
            return connectivityManager.getNetworkInfo(networkInfo.getType());
        }
        return null;
    }

    public static boolean isActiveNetworkMetered(ConnectivityManager connectivityManager) {
        return IMPL.isActiveNetworkMetered(connectivityManager);
    }
}