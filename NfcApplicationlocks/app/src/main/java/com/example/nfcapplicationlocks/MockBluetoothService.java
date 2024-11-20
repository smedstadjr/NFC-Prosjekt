
package com.example.nfcapplicationlocks;

import android.os.Handler;
import android.util.Log;

import java.util.Arrays;
import java.util.Map;

/**
 * this calss is sett up as a mock for bluetooth service.
 * the class provides dummy information for a prototype to show functionality.
 *
 */

public class MockBluetoothService extends BluetoothService {
    private static final String TAG = "MockBluetoothService";

    public MockBluetoothService(Handler handler, Map<Integer, Locks> locksMap) {
        super(null, handler, locksMap); // Pass null for socket
    }
    

    @Override
    public void read() {
        byte[] buffer = new byte[12];
        buffer[0] = 0;
        buffer[1] = 0;
        buffer[2] = 0;
        buffer[3] = 1; // lockId = 1
        buffer[4] = 0;
        buffer[5] = 0;
        buffer[6] = 0;
        buffer[7] = 50; // batteryLevel = 50
        buffer[8] = 0;
        buffer[9] = 0;
        buffer[10] = 0;
        buffer[11] = 1; // lockStatus = 1

        try {
            // Simulerer lesing av data
            int lockId = parseLockId(buffer);
            int batteryLevel = parseBatteryLevel(buffer);
            int lockStatus = parseLockStatus(buffer);
            updateOrCreateLock(lockId, batteryLevel, lockStatus);
            getHandler().obtainMessage(1, buffer.length, -1, buffer).sendToTarget();
            Log.d(TAG, "Mock read: Data read and message sent to handler");
        } catch (Exception e) {
            Log.e(TAG, "Mock read: Exception occurred", e);
        }
    }

    @Override
    public void write(byte[] bytes) {
        try {
            // Simulerer sending av data
            Log.d(TAG, "Mock write: " + Arrays.toString(bytes));
        } catch (Exception e) {
            Log.e(TAG, "Mock write: Exception occurred", e);
        }
    }
}
