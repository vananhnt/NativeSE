package android.net.wifi;

import android.content.Context;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import com.android.internal.R;
import com.android.internal.util.AsyncChannel;
import com.android.internal.util.State;
import com.android.internal.util.StateMachine;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

/* loaded from: WifiApConfigStore.class */
class WifiApConfigStore extends StateMachine {
    private Context mContext;
    private static final String TAG = "WifiApConfigStore";
    private static final String AP_CONFIG_FILE = Environment.getDataDirectory() + "/misc/wifi/softap.conf";
    private static final int AP_CONFIG_FILE_VERSION = 1;
    private State mDefaultState;
    private State mInactiveState;
    private State mActiveState;
    private WifiConfiguration mWifiApConfig;
    private AsyncChannel mReplyChannel;

    WifiApConfigStore(Context context, Handler target) {
        super(TAG, target.getLooper());
        this.mDefaultState = new DefaultState();
        this.mInactiveState = new InactiveState();
        this.mActiveState = new ActiveState();
        this.mWifiApConfig = null;
        this.mReplyChannel = new AsyncChannel();
        this.mContext = context;
        addState(this.mDefaultState);
        addState(this.mInactiveState, this.mDefaultState);
        addState(this.mActiveState, this.mDefaultState);
        setInitialState(this.mInactiveState);
    }

    public static WifiApConfigStore makeWifiApConfigStore(Context context, Handler target) {
        WifiApConfigStore s = new WifiApConfigStore(context, target);
        s.start();
        return s;
    }

    /* loaded from: WifiApConfigStore$DefaultState.class */
    class DefaultState extends State {
        DefaultState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case 131097:
                case 131098:
                    Log.e(WifiApConfigStore.TAG, "Unexpected message: " + message);
                    return true;
                case 131099:
                    WifiApConfigStore.this.mReplyChannel.replyToMessage(message, 131100, WifiApConfigStore.this.mWifiApConfig);
                    return true;
                default:
                    Log.e(WifiApConfigStore.TAG, "Failed to handle " + message);
                    return true;
            }
        }
    }

    /* loaded from: WifiApConfigStore$InactiveState.class */
    class InactiveState extends State {
        InactiveState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case 131097:
                    WifiApConfigStore.this.mWifiApConfig = (WifiConfiguration) message.obj;
                    WifiApConfigStore.this.transitionTo(WifiApConfigStore.this.mActiveState);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* loaded from: WifiApConfigStore$ActiveState.class */
    class ActiveState extends State {
        ActiveState() {
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public void enter() {
            new Thread(new Runnable() { // from class: android.net.wifi.WifiApConfigStore.ActiveState.1
                @Override // java.lang.Runnable
                public void run() {
                    WifiApConfigStore.this.writeApConfiguration(WifiApConfigStore.this.mWifiApConfig);
                    WifiApConfigStore.this.sendMessage(131098);
                }
            }).start();
        }

        @Override // com.android.internal.util.State, com.android.internal.util.IState
        public boolean processMessage(Message message) {
            switch (message.what) {
                case 131097:
                    WifiApConfigStore.this.deferMessage(message);
                    return true;
                case 131098:
                    WifiApConfigStore.this.transitionTo(WifiApConfigStore.this.mInactiveState);
                    return true;
                default:
                    return false;
            }
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public void loadApConfiguration() {
        DataInputStream in = null;
        try {
            try {
                WifiConfiguration config = new WifiConfiguration();
                DataInputStream in2 = new DataInputStream(new BufferedInputStream(new FileInputStream(AP_CONFIG_FILE)));
                int version = in2.readInt();
                if (version != 1) {
                    Log.e(TAG, "Bad version on hotspot configuration file, set defaults");
                    setDefaultApConfiguration();
                    if (in2 != null) {
                        try {
                            in2.close();
                            return;
                        } catch (IOException e) {
                            return;
                        }
                    }
                    return;
                }
                config.SSID = in2.readUTF();
                int authType = in2.readInt();
                config.allowedKeyManagement.set(authType);
                if (authType != 0) {
                    config.preSharedKey = in2.readUTF();
                }
                this.mWifiApConfig = config;
                if (in2 != null) {
                    try {
                        in2.close();
                    } catch (IOException e2) {
                    }
                }
            } catch (IOException e3) {
                setDefaultApConfiguration();
                if (0 != 0) {
                    try {
                        in.close();
                    } catch (IOException e4) {
                    }
                }
            }
        } catch (Throwable th) {
            if (0 != 0) {
                try {
                    in.close();
                } catch (IOException e5) {
                }
            }
            throw th;
        }
    }

    /* JADX INFO: Access modifiers changed from: package-private */
    public Messenger getMessenger() {
        return new Messenger(getHandler());
    }

    /* JADX INFO: Access modifiers changed from: private */
    public void writeApConfiguration(WifiConfiguration config) {
        DataOutputStream out = null;
        try {
            try {
                out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(AP_CONFIG_FILE)));
                out.writeInt(1);
                out.writeUTF(config.SSID);
                int authType = config.getAuthType();
                out.writeInt(authType);
                if (authType != 0) {
                    out.writeUTF(config.preSharedKey);
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                    }
                }
            } catch (IOException e2) {
                Log.e(TAG, "Error writing hotspot configuration" + e2);
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e3) {
                    }
                }
            }
        } catch (Throwable th) {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e4) {
                }
            }
            throw th;
        }
    }

    private void setDefaultApConfiguration() {
        WifiConfiguration config = new WifiConfiguration();
        config.SSID = this.mContext.getString(R.string.wifi_tether_configure_ssid_default);
        config.allowedKeyManagement.set(4);
        String randomUUID = UUID.randomUUID().toString();
        config.preSharedKey = randomUUID.substring(0, 8) + randomUUID.substring(9, 13);
        sendMessage(131097, config);
    }
}