package com.example.nfcapplicationlocks;

import android.bluetooth.BluetoothDevice;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

/**
 * used to simulate a bluetooth device without using mocking or creating a mocking class.
 * this did not work as intended but is a valid try to make bluetoothpairingtests work.
 */

@Implements(BluetoothDevice.class)
public class ShadowBDevice {
    public ShadowBDevice(){

    }

    @RealObject
    private BluetoothDevice realDevice;

    private int bondState = BluetoothDevice.BOND_NONE;

    @Implementation
    public int getBondState() {
        return bondState;
    }

    public void setBondState(int bondState) {
        this.bondState = bondState;
    }

    public void setRealDevice(BluetoothDevice device) {
        this.realDevice = device;
    }

    @Implementation
    public boolean createBond() {
        setBondState(BluetoothDevice.BOND_BONDING);
        return true; // Returns true to simulate bonding starting
    }
}