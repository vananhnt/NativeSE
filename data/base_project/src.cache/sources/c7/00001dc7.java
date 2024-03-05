package com.android.server.am;

/* loaded from: ProcessMemInfo.class */
public class ProcessMemInfo {
    final String name;
    final int pid;
    final int oomAdj;
    final int procState;
    final String adjType;
    final String adjReason;
    long pss;

    public ProcessMemInfo(String _name, int _pid, int _oomAdj, int _procState, String _adjType, String _adjReason) {
        this.name = _name;
        this.pid = _pid;
        this.oomAdj = _oomAdj;
        this.procState = _procState;
        this.adjType = _adjType;
        this.adjReason = _adjReason;
    }
}