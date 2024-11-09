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
                //while reading information using parse methods as parameters for the updateOrCreate method
                int lockId = parseLockId(buffer);
                int batteryLevel = parseBatteryLevel(buffer);
                int lockStatus = parseLockStatus(buffer);
                updateOrCreateLock(lockId, batteryLevel, lockStatus);
                mHandler.obtainMessage(1, bytes, -1, buffer).sendToTarget();
            } catch (IOException e) {
                Log.e(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }
    public int parseLockId(byte[] buffer) {
        // Assuming lockId is stored in the first 4 bytes of the buffer
        return ((buffer[0] & 0xFF) << 24) | ((buffer[1] & 0xFF) << 16) | ((buffer[2] & 0xFF) << 8) | (buffer[3] & 0xFF);
    }

    public int parseBatteryLevel(byte[] buffer) {
        // Assuming batteryLevel is stored in the next 4 bytes (bytes 4 to 7)
        return ((buffer[4] & 0xFF) << 24) | ((buffer[5] & 0xFF) << 16) | ((buffer[6] & 0xFF) << 8) | (buffer[7] & 0xFF);
    }

    public int parseLockStatus(byte[] buffer) {
        // Assuming lockStatus is stored in the next 4 bytes (bytes 8 to 11)
        return ((buffer[8] & 0xFF) << 24) | ((buffer[9] & 0xFF) << 16) | ((buffer[10] & 0xFF) << 8) | (buffer[11] & 0xFF);
    }

    //takes three values as parameter to create or update a existing lock based on locksMap
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
    /*
    public void closeConnection() {
        try {
            mSocket.close();
            Log.d(TAG, "closeConnection: Socket closed");
        } catch (IOException e) {
            Log.e(TAG, "closeConnection: Could not close socket", e);
        }
    }
     */
}
