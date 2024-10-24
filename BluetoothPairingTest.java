package com.example.nfcapplicationlocks;

import static org.mockito.Mockito.*;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 30, manifest = Config.NONE, shadows = {ShadowBluetoothAdapter.class})
public class BluetoothPairingTest {

    @Mock
    Context mockContext;

    @Mock
    BluetoothAdapter mockBluetoothAdapter;

    @Mock
    BluetoothDevice mockDevice;

    @Mock
    BluetoothSocket mockSocket;

    private BluetoothPairing bluetoothPairing;

    private static final String MOCK_BLUETOOTH_CONNECT_PERMISSION = "android.permission.BLUETOOTH_CONNECT";

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        ShadowBluetoothAdapter.setMockBluetoothAdapter(mockBluetoothAdapter);
        when(mockContext.getApplicationContext()).thenReturn(mockContext);
        when(mockBluetoothAdapter.getRemoteDevice(anyString())).thenReturn(mockDevice);
        when(mockDevice.createRfcommSocketToServiceRecord(any(UUID.class))).thenReturn(mockSocket);
        bluetoothPairing = new BluetoothPairing(mockContext);
    }

    @Test
    public void testAutoConnectToRaspberryPi_SuccessfulConnection() throws Exception {
        when(ActivityCompat.checkSelfPermission(mockContext, MOCK_BLUETOOTH_CONNECT_PERMISSION))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        Method method = BluetoothPairing.class.getDeclaredMethod("autoConnectToRaspberryPi");
        method.setAccessible(true);
        method.invoke(bluetoothPairing);

        verify(mockBluetoothAdapter).getRemoteDevice(anyString());
        verify(mockDevice).createBond();
    }

    @Test
    public void testStartPairing_SuccessfulBonding() {
        when(ActivityCompat.checkSelfPermission(mockContext, MOCK_BLUETOOTH_CONNECT_PERMISSION))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        bluetoothPairing.startPairing(mockDevice);

        verify(mockDevice).createBond();
    }

    @Test
    public void testConnect_SuccessfulConnection() throws Exception {
        when(ActivityCompat.checkSelfPermission(mockContext, MOCK_BLUETOOTH_CONNECT_PERMISSION))
                .thenReturn(PackageManager.PERMISSION_GRANTED);

        bluetoothPairing.startPairing(mockDevice);

        Method method = BluetoothPairing.class.getDeclaredMethod("connect");
        method.setAccessible(true);
        method.invoke(bluetoothPairing);

        verify(mockSocket).connect();
    }

    @Test
    public void testCloseConnection_SuccessfulDisconnection() throws IOException {
        bluetoothPairing.closeConnection();

        verify(mockSocket).close();
    }
}