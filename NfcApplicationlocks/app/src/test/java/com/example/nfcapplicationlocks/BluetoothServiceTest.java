package com.example.nfcapplicationlocks;

import static org.junit.Assert.*;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.util.HashMap;
import java.util.Map;

/**
 * this class tests the handling of the information that is suppsed to be retrieved from a bluetooth connection.
 * input and output stream is not set up here because setting up that in a mocking scenario makes it so the test never finishes.
 * this calss uses dummy data to test the information handling.
 *
 * there is also a method in this class to check if the update or create method works as intended with dummy data.
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE)
public class BluetoothServiceTest {

    private BluetoothService bluetoothService;
    private Map<Integer, Locks> locksMap;
    private Handler handler;

    @Before
    public void setUp() {
        locksMap = new HashMap<>();
        handler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {

            }
        };
        bluetoothService = new BluetoothService(handler, locksMap);
    }

    @Test
    public void testUpdateOrCreateLock() {
        int lockId = 1;
        int batteryLevel = 50;
        int lockStatus = 1;

        bluetoothService.updateOrCreateLock(lockId, batteryLevel, lockStatus);

        Locks lock = locksMap.get(lockId);
        assertNotNull(lock);
        assertEquals(lockId, lock.getLockId());
        assertEquals(batteryLevel, lock.getBatteryLevel());
        assertEquals(lockStatus, lock.getLockStatus());
    }

    @Test
    public void testParseLockId() {
        byte[] buffer = new byte[12];
        buffer[0] = 0;
        buffer[1] = 0;
        buffer[2] = 0;
        buffer[3] = 1; // lockId = 1

        int lockId = bluetoothService.parseLockId(buffer);
        assertEquals(1, lockId);
    }

    @Test
    public void testParseBatteryLevel() {
        byte[] buffer = new byte[12];
        buffer[4] = 0;
        buffer[5] = 0;
        buffer[6] = 0;
        buffer[7] = 50; // batteryLevel = 50

        int batteryLevel = bluetoothService.parseBatteryLevel(buffer);
        assertEquals(50, batteryLevel);
    }

    @Test
    public void testParseLockStatus() {
        byte[] buffer = new byte[12];
        buffer[8] = 0;
        buffer[9] = 0;
        buffer[10] = 0;
        buffer[11] = 1; // lockStatus = 1

        int lockStatus = bluetoothService.parseLockStatus(buffer);
        assertEquals(1, lockStatus);
    }
}