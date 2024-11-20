package com.example.nfcapplicationlocks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.annotation.RealObject;

import java.util.HashSet;
import java.util.Set;

/**
 * used to simulate a bluetooth adpater without using mocking or creating a mocking class.
 * this did not work as intended but is a valid try to make bluetoothpairingtests work.
 */
@Implements(BluetoothAdapter.class)
public class ShadowBluetoothAdapter {

    @RealObject
    private BluetoothAdapter realAdapter;

    private Set<BluetoothDevice> bondedDevices = new HashSet<>();

    @Implementation
    public Set<BluetoothDevice> getBondedDevices() {
        return bondedDevices;
    }

    public void addBondedDevice(BluetoothDevice device) {
        bondedDevices.add(device);
    }

    public void setRealAdapter(BluetoothAdapter adapter) {
        this.realAdapter = adapter;
    }
}