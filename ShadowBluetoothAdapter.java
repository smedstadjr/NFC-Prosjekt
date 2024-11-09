package com.example.nfcapplicationlocks;

import android.bluetooth.BluetoothAdapter;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;

@Implements(BluetoothAdapter.class)
public class ShadowBluetoothAdapter {

    private static BluetoothAdapter mockBluetoothAdapter;

    public static void setMockBluetoothAdapter(BluetoothAdapter adapter) {
        mockBluetoothAdapter = adapter;
    }

    @Implementation
    public static BluetoothAdapter getDefaultAdapter() {
        return mockBluetoothAdapter;
    }
}