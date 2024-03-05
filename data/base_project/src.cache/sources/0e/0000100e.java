package android.support.v4.media.routing;

import android.media.MediaRouter;

/* loaded from: MediaRouterJellybeanMr2.class */
class MediaRouterJellybeanMr2 extends MediaRouterJellybeanMr1 {

    /* loaded from: MediaRouterJellybeanMr2$RouteInfo.class */
    public static final class RouteInfo {
        public static CharSequence getDescription(Object obj) {
            return ((MediaRouter.RouteInfo) obj).getDescription();
        }

        public static boolean isConnecting(Object obj) {
            return ((MediaRouter.RouteInfo) obj).isConnecting();
        }
    }

    /* loaded from: MediaRouterJellybeanMr2$UserRouteInfo.class */
    public static final class UserRouteInfo {
        public static void setDescription(Object obj, CharSequence charSequence) {
            ((MediaRouter.UserRouteInfo) obj).setDescription(charSequence);
        }
    }

    MediaRouterJellybeanMr2() {
    }

    public static void addCallback(Object obj, int i, Object obj2, int i2) {
        ((MediaRouter) obj).addCallback(i, (MediaRouter.Callback) obj2, i2);
    }

    public static Object getDefaultRoute(Object obj) {
        return ((MediaRouter) obj).getDefaultRoute();
    }
}