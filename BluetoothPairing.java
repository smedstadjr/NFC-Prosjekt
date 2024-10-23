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
import java.util.Map;
import java.util.HashMap;
import android.os.Handler;

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
        //creating a broadcastrecevier to listen for bond state changes
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        mContext.registerReceiver(mBroadcastReceiver, filter);
        autoConnectToRaspberryPi(); // connect to prototype on startup
    }

    //setting up the broadcastrecevier to log intents.
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //checks if the app has permission to use bluetooth
                if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    Log.e(TAG, "BroadcastReceiver: Permission not granted");
                    return;
                }
                //checks if a bond has been established
                if (device.getBondState() == BluetoothDevice.BOND_BONDED) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDED.");
                    mDevice = device;
                    connect();
                }
                //checks if a bond is being established
                if (device.getBondState() == BluetoothDevice.BOND_BONDING) {
                    Log.d(TAG, "BroadcastReceiver: BOND_BONDING.");
                }
                //checks if there is noe bonding
                if (device.getBondState() == BluetoothDevice.BOND_NONE) {
                    Log.d(TAG, "BroadcastReceiver: BOND_NONE.");
                }
            }
        }
    };
    private void autoConnectToRaspberryPi() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "autoConnectToRaspberryPi: Permission not granted");
            return;
        }
        mDevice = mBluetoothAdapter.getRemoteDevice(RASPBERRY_PI_MAC_ADDRESS);
        if (mDevice != null) {
            Log.d(TAG, "autoConnectToRaspberryPi: Found device " + mDevice.getName());
            startPairing(mDevice);
        } else {
            Log.e(TAG, "autoConnectToRaspberryPi: Device not found");
        }
    }

    /*
    checks bluetooth permissions and starts pairing if permission is granted
    also logging if permission not granted and if pairing is starting with a given unit
     */
    public void startPairing(BluetoothDevice device) {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "startPairing: Permission not granted");
            return;
        }
        Log.d(TAG, "startPairing: Starting pairing with device " + device.getName());
        mDevice = device;
        mDevice.createBond();
    }

    /*
    again checking bluetooth permissions and logging if permission not granted.
    creating RFComm socket for connecting
     */
    private void connect() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "connect: Permission not granted");
            return;
        }
        Log.d(TAG, "connect: Connecting to " + mDevice.getName());
        BluetoothSocket tmp = null;
        try {
            tmp = mDevice.createRfcommSocketToServiceRecord(MY_UUID_INSECURE);
        } catch (IOException e) {
            Log.e(TAG, "connect: Could not create RFComm socket", e);
        }
        mSocket = tmp;

        // disabling descovery mode, helps with memory usage
        mBluetoothAdapter.cancelDiscovery();


        //making the connection and logging all results.
        try {
            mSocket.connect();
            Log.d(TAG, "connect: Connected to " + mDevice.getName());
            Map<Integer, Locks> locksMap = new HashMap<>(); // Create or pass an existing locksMap
            Handler handler = new Handler(); // Create or pass an existing Handler
            BluetoothService bluetoothService = new BluetoothService(mSocket, handler, locksMap);
            bluetoothService.read();
        } catch (IOException e) {
            try {
                mSocket.close();
            } catch (IOException e1) {
                Log.e(TAG, "connect: Unable to close socket", e1);
            }
            Log.e(TAG, "connect: Could not connect to device", e);
        }
    }

    //closing the socket connection
    public void closeConnection() {
        try {
            mSocket.close();
            Log.d(TAG, "closeConnection: Socket closed");
        } catch (IOException e) {
            Log.e(TAG, "closeConnection: Could not close socket", e);
        }
    }
}
