package com.example.nfcapplicationlocks;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private final BluetoothSocket mSocket;
    private final InputStream mInStream;
    private final OutputStream mOutStream;
    private final Handler mHandler;
    private Map<Integer, Locks> locksMap;

    /*
    setting up socket, input stream, output stream and handler
    and sending to logg what happens if an error occur
    */
    public BluetoothService(BluetoothSocket socket, Handler handler, Map<Integer, Locks> locksMap) {
        mSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mHandler = handler;
        this.locksMap = locksMap;

        try {
            tmpIn = mSocket.getInputStream();
            tmpOut = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input and output streams", e);
        }

        mInStream = tmpIn;
        mOutStream = tmpOut;
    }

    /*
    reading information collected from the bluetooth connection
    sending status to log if reading failed
    */
    public void read() {
        byte[] buffer = new byte[1024];
        int bytes;

        while (true) {
            try {
                bytes = mInStream.read(buffer);
                mHandler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }
    private int parseLockId(byte[] buffer) {
        // Implement parsing logic here
        return 0; // Placeholder
    }

    private int parseBatteryLevel(byte[] buffer) {
        // Implement parsing logic here
        return 0; // Placeholder
    }

    private int parseLockStatus(byte[] buffer) {
        // Implement parsing logic here
        return 0; // Placeholder
    }
    private void updateOrCreateLock(int lockId, int batteryLevel, int lockStatus) {
        Locks lock = locksMap.get(lockId);
        if (lock == null) {
            lock = new Locks();
            lock.setLockId(lockId);
            locksMap.put(lockId, lock);
        }
        lock.setBatteryLevel(batteryLevel);
        lock.setLockStatus(lockStatus);
    }

    /*
    sending information over bluetooth to connected unit
    sending status to log if writing to unit failed
     */
    public void write(byte[] bytes) {
        try {
            mOutStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
        }
    }

    /*
    closing the bluetooth information transfer
    and sending a message to log when done or
    sending status to log if closing failed
     */
    public void closeConnection() {
        try {
            mSocket.close();
            Log.d(TAG, "closeConnection: Socket closed");
        } catch (IOException e) {
            Log.e(TAG, "closeConnection: Could not close socket", e);
        }
    }
}
