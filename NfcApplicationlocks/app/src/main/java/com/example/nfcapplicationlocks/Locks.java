package com.example.nfcapplicationlocks;

public class Locks {
    private int batteryLevel;
    private int lockId;
    private NfcTaggs nfcTagg;
    private int lockStatus;

    public Locks(int lockId, int batteryLevel, int lockStatus, NfcTaggs nfcTagg) {
        this.lockId = lockId;
        this.batteryLevel = batteryLevel;
        this.lockStatus = lockStatus;
        this.nfcTagg = nfcTagg;
    }
    public Locks(){}

    public int getBatteryLevel() {
        return batteryLevel;
    }

    public void setBatteryLevel(int batteryLevel) {
        this.batteryLevel = batteryLevel;
    }

    public int getLockId() {
        return lockId;
    }

    public void setLockId(int lockId) {
        this.lockId = lockId;
    }

    public NfcTaggs getNfcTagg() {
        return nfcTagg;
    }

    public void setNfcTagg(NfcTaggs nfcTagg) {
        this.nfcTagg = nfcTagg;
    }

    public int getLockStatus() {
        return lockStatus;
    }

    public void setLockStatus(int lockStatus) {
        this.lockStatus = lockStatus;
    }
}
