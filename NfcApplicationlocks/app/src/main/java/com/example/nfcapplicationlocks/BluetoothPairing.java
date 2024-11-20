package com.example.nfcapplicationlocks;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.util.Log;
import androidx.core.app.ActivityCompat;
import java.io.IOException;
import java.util.UUID;

/**
 * The purpose of this class is to handle the bluetooth connection and pairing process.
 * This class is also where the broadcastreceiver is set up, to listen to state changes and create a log.
 *
 * In this class there are two methods that are being used to create a bluetooth connection.
 * startPairing and connect is the two main methodes in this class.
 * startPairing is used to locate another device and set up the pairing prosess.
 * The connect method is then used to finalise the connection between the devices.
 * In both the main methodes theres a permition check, because all bluetooth actions must
 * be accepted by the user to be executed.
 *
 */

public class BluetoothPairing {
    private static final String TAG = "BluetoothPairing";
    private static final UUID MY_UUID_INSECURE =
            UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private static final String RASPBERRY_PI_MAC_ADDRESS = "B8:27:EB:D5:9F:76"; // MAC-adress for Raspberry Pi

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothDevice mDevice;
    private BluetoothSocket mSocket;
    private Context mContext;

    public BluetoothPairing(Context context) {
        mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        mContext.registerReceiver(mBroadcastReceiver, filter);
    }

    public void setBluetoothAdapter(BluetoothAdapter bluetoothAdapter) {
        mBluetoothAdapter = bluetoothAdapter;
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "BroadcastReceiver: Permission not granted");
                    return;
                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    mDevice = device;
                    connect();
                }
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };

    public BluetoothSocket autoConnectToRaspberryPi() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "autoConnectToRaspberryPi: Permission not granted");
            return null;
        }
        mDevice = mBluetoothAdapter.getRemoteDevice(RASPBERRY_PI_MAC_ADDRESS);
        if (mDevice != null) {
            Log.d(TAG, "autoConnectToRaspberryPi: Found device " + mDevice.getName());
            startPairing(mDevice);
            // Kall connect() etter paring
            mSocket = connect();
        } else {
            Log.e(TAG, "autoConnectToRaspberryPi: Device not found");
        }
        return mSocket;
    }

    public void startPairing(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "startPairing: Permission not granted");
            return;
        }
        Log.d(TAG, "startPairing: Starting pairing with device " + device.getName());
        mDevice = device;
        mDevice.createBond();
    }

    protected BluetoothSocket connect() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "connect: Permission not granted");
            return null;
        }
        Log.d(TAG, "connect: Connecting to " + mDevice.getName());
        BluetoothSocket tmp = null;
        try {
            tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
            Log.d(TAG, "connect: RFComm socket created");
        } catch (IOException e) {
            Log.e(TAG, "connect: Could not create RFComm socket", e);
        }
        mSocket = tmp;

        mBluetoothAdapter.cancelDiscovery();

        try {
            mSocket.connect();
            Log.d(TAG, "connect: Connected to " + mDevice.getName());
        } catch (IOException e) {
            try {
                mSocket.close();
            } catch (IOException e1) {
                Log.e(TAG, "connect: Unable to close socket", e1);
            }
            Log.e(TAG, "connect: Could not connect to device", e);
            return null;
        }
        return mSocket;
    }

    public void closeConnection() {
        if (mSocket != null) {
            try {
                mSocket.close();
                Log.d(TAG, "closeConnection: Socket closed");
            } catch (IOException e) {
                Log.e(TAG, "closeConnection: Could not close socket", e);
            }
        } else {
            Log.e(TAG, "closeConnection: mSocket is null, cannot close connection");
        }
    }
}