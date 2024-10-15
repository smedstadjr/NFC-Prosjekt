package com.example.nfcapplicationlocks;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class BluetoothService {
    private static final String TAG = "BluetoothService";
    private final BluetoothSocket mSocket;
    private final InputStream mInStream;
    private final OutputStream mOutStream;
    private final Handler mHandler;

    public BluetoothService(BluetoothSocket socket, Handler handler) {
        mSocket = socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;
        mHandler = handler;

        try {
            tmpIn = mSocket.getInputStream();
            tmpOut = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when creating input and output streams", e);
        }

        mInStream = tmpIn;
        mOutStream = tmpOut;
    }

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

    public void write(byte[] bytes) {
        try {
            mOutStream.write(bytes);
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);
        }
    }

    public void closeConnection() {
        try {
            mSocket.close();
            Log.d(TAG, "closeConnection: Socket closed");
        } catch (IOException e) {
            Log.e(TAG, "closeConnection: Could not close socket", e);
        }
    }
}
