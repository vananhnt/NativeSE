package com.android.internal.telephony;

import android.telephony.Rlog;
import java.util.ArrayList;
import java.util.List;

/* loaded from: Call.class */
public abstract class Call {
    protected final String LOG_TAG = "Call";
    public State mState = State.IDLE;
    public ArrayList<Connection> mConnections = new ArrayList<>();
    protected boolean mIsGeneric = false;

    public abstract List<Connection> getConnections();

    public abstract Phone getPhone();

    public abstract boolean isMultiparty();

    public abstract void hangup() throws CallStateException;

    /* loaded from: Call$State.class */
    public enum State {
        IDLE,
        ACTIVE,
        HOLDING,
        DIALING,
        ALERTING,
        INCOMING,
        WAITING,
        DISCONNECTED,
        DISCONNECTING;

        public boolean isAlive() {
            return (this == IDLE || this == DISCONNECTED || this == DISCONNECTING) ? false : true;
        }

        public boolean isRinging() {
            return this == INCOMING || this == WAITING;
        }

        public boolean isDialing() {
            return this == DIALING || this == ALERTING;
        }
    }

    public boolean hasConnection(Connection c) {
        return c.getCall() == this;
    }

    public boolean hasConnections() {
        List<Connection> connections = getConnections();
        return connections != null && connections.size() > 0;
    }

    public State getState() {
        return this.mState;
    }

    public boolean isIdle() {
        return !getState().isAlive();
    }

    public Connection getEarliestConnection() {
        long time = Long.MAX_VALUE;
        Connection earliest = null;
        List<Connection> l = getConnections();
        if (l.size() == 0) {
            return null;
        }
        int s = l.size();
        for (int i = 0; i < s; i++) {
            Connection c = l.get(i);
            long t = c.getCreateTime();
            if (t < time) {
                earliest = c;
                time = t;
            }
        }
        return earliest;
    }

    public long getEarliestCreateTime() {
        long time = Long.MAX_VALUE;
        List<Connection> l = getConnections();
        if (l.size() == 0) {
            return 0L;
        }
        int s = l.size();
        for (int i = 0; i < s; i++) {
            Connection c = l.get(i);
            long t = c.getCreateTime();
            time = t < time ? t : time;
        }
        return time;
    }

    public long getEarliestConnectTime() {
        long time = Long.MAX_VALUE;
        List<Connection> l = getConnections();
        if (l.size() == 0) {
            return 0L;
        }
        int s = l.size();
        for (int i = 0; i < s; i++) {
            Connection c = l.get(i);
            long t = c.getConnectTime();
            time = t < time ? t : time;
        }
        return time;
    }

    public boolean isDialingOrAlerting() {
        return getState().isDialing();
    }

    public boolean isRinging() {
        return getState().isRinging();
    }

    public Connection getLatestConnection() {
        List<Connection> l = getConnections();
        if (l.size() == 0) {
            return null;
        }
        long time = 0;
        Connection latest = null;
        int s = l.size();
        for (int i = 0; i < s; i++) {
            Connection c = l.get(i);
            long t = c.getCreateTime();
            if (t > time) {
                latest = c;
                time = t;
            }
        }
        return latest;
    }

    public boolean isGeneric() {
        return this.mIsGeneric;
    }

    public void setGeneric(boolean generic) {
        this.mIsGeneric = generic;
    }

    public void hangupIfAlive() {
        if (getState().isAlive()) {
            try {
                hangup();
            } catch (CallStateException ex) {
                Rlog.w("Call", " hangupIfActive: caught " + ex);
            }
        }
    }
}