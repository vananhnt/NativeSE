package android.net;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import gov.nist.core.Separators;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Locale;

/* loaded from: ProxyProperties.class */
public class ProxyProperties implements Parcelable {
    private String mHost;
    private int mPort;
    private String mExclusionList;
    private String[] mParsedExclusionList;
    private String mPacFileUrl;
    public static final String LOCAL_EXCL_LIST = "";
    public static final int LOCAL_PORT = -1;
    public static final String LOCAL_HOST = "localhost";
    public static final Parcelable.Creator<ProxyProperties> CREATOR = new Parcelable.Creator<ProxyProperties>() { // from class: android.net.ProxyProperties.1
        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProxyProperties createFromParcel(Parcel in) {
            String host = null;
            int port = 0;
            if (in.readByte() != 0) {
                String url = in.readString();
                int localPort = in.readInt();
                return new ProxyProperties(url, localPort);
            }
            if (in.readByte() != 0) {
                host = in.readString();
                port = in.readInt();
            }
            String exclList = in.readString();
            String[] parsedExclList = in.readStringArray();
            ProxyProperties proxyProperties = new ProxyProperties(host, port, exclList, parsedExclList);
            return proxyProperties;
        }

        /* JADX WARN: Can't rename method to resolve collision */
        @Override // android.os.Parcelable.Creator
        public ProxyProperties[] newArray(int size) {
            return new ProxyProperties[size];
        }
    };

    public ProxyProperties(String host, int port, String exclList) {
        this.mHost = host;
        this.mPort = port;
        setExclusionList(exclList);
    }

    public ProxyProperties(String pacFileUrl) {
        this.mHost = LOCAL_HOST;
        this.mPort = -1;
        setExclusionList("");
        this.mPacFileUrl = pacFileUrl;
    }

    public ProxyProperties(String pacFileUrl, int localProxyPort) {
        this.mHost = LOCAL_HOST;
        this.mPort = localProxyPort;
        setExclusionList("");
        this.mPacFileUrl = pacFileUrl;
    }

    private ProxyProperties(String host, int port, String exclList, String[] parsedExclList) {
        this.mHost = host;
        this.mPort = port;
        this.mExclusionList = exclList;
        this.mParsedExclusionList = parsedExclList;
        this.mPacFileUrl = null;
    }

    public ProxyProperties(ProxyProperties source) {
        if (source != null) {
            this.mHost = source.getHost();
            this.mPort = source.getPort();
            this.mPacFileUrl = source.getPacFileUrl();
            this.mExclusionList = source.getExclusionList();
            this.mParsedExclusionList = source.mParsedExclusionList;
        }
    }

    public InetSocketAddress getSocketAddress() {
        InetSocketAddress inetSocketAddress = null;
        try {
            inetSocketAddress = new InetSocketAddress(this.mHost, this.mPort);
        } catch (IllegalArgumentException e) {
        }
        return inetSocketAddress;
    }

    public String getPacFileUrl() {
        return this.mPacFileUrl;
    }

    public String getHost() {
        return this.mHost;
    }

    public int getPort() {
        return this.mPort;
    }

    public String getExclusionList() {
        return this.mExclusionList;
    }

    private void setExclusionList(String exclusionList) {
        this.mExclusionList = exclusionList;
        if (this.mExclusionList == null) {
            this.mParsedExclusionList = new String[0];
            return;
        }
        String[] splitExclusionList = exclusionList.toLowerCase(Locale.ROOT).split(Separators.COMMA);
        this.mParsedExclusionList = new String[splitExclusionList.length * 2];
        for (int i = 0; i < splitExclusionList.length; i++) {
            String s = splitExclusionList[i].trim();
            if (s.startsWith(Separators.DOT)) {
                s = s.substring(1);
            }
            this.mParsedExclusionList[i * 2] = s;
            this.mParsedExclusionList[(i * 2) + 1] = Separators.DOT + s;
        }
    }

    public boolean isExcluded(String url) {
        if (TextUtils.isEmpty(url) || this.mParsedExclusionList == null || this.mParsedExclusionList.length == 0) {
            return false;
        }
        Uri u = Uri.parse(url);
        String urlDomain = u.getHost();
        if (urlDomain == null) {
            return false;
        }
        for (int i = 0; i < this.mParsedExclusionList.length; i += 2) {
            if (urlDomain.equals(this.mParsedExclusionList[i]) || urlDomain.endsWith(this.mParsedExclusionList[i + 1])) {
                return true;
            }
        }
        return false;
    }

    public java.net.Proxy makeProxy() {
        java.net.Proxy proxy = java.net.Proxy.NO_PROXY;
        if (this.mHost != null) {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(this.mHost, this.mPort);
                proxy = new java.net.Proxy(Proxy.Type.HTTP, inetSocketAddress);
            } catch (IllegalArgumentException e) {
            }
        }
        return proxy;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.mPacFileUrl != null) {
            sb.append("PAC Script: ");
            sb.append(this.mPacFileUrl);
        } else if (this.mHost != null) {
            sb.append("[");
            sb.append(this.mHost);
            sb.append("] ");
            sb.append(Integer.toString(this.mPort));
            if (this.mExclusionList != null) {
                sb.append(" xl=").append(this.mExclusionList);
            }
        } else {
            sb.append("[ProxyProperties.mHost == null]");
        }
        return sb.toString();
    }

    public boolean equals(Object o) {
        if (o instanceof ProxyProperties) {
            ProxyProperties p = (ProxyProperties) o;
            if (!TextUtils.isEmpty(this.mPacFileUrl)) {
                return this.mPacFileUrl.equals(p.getPacFileUrl()) && this.mPort == p.mPort;
            } else if (!TextUtils.isEmpty(p.getPacFileUrl())) {
                return false;
            } else {
                if (this.mExclusionList == null || this.mExclusionList.equals(p.getExclusionList())) {
                    if (this.mHost != null && p.getHost() != null && !this.mHost.equals(p.getHost())) {
                        return false;
                    }
                    if (this.mHost == null || p.mHost != null) {
                        return (this.mHost != null || p.mHost == null) && this.mPort == p.mPort;
                    }
                    return false;
                }
                return false;
            }
        }
        return false;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    public int hashCode() {
        return (null == this.mHost ? 0 : this.mHost.hashCode()) + (null == this.mExclusionList ? 0 : this.mExclusionList.hashCode()) + this.mPort;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel dest, int flags) {
        if (this.mPacFileUrl != null) {
            dest.writeByte((byte) 1);
            dest.writeString(this.mPacFileUrl);
            dest.writeInt(this.mPort);
            return;
        }
        dest.writeByte((byte) 0);
        if (this.mHost != null) {
            dest.writeByte((byte) 1);
            dest.writeString(this.mHost);
            dest.writeInt(this.mPort);
        } else {
            dest.writeByte((byte) 0);
        }
        dest.writeString(this.mExclusionList);
        dest.writeStringArray(this.mParsedExclusionList);
    }
}