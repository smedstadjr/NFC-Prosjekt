package com.example.nfcapplicationlocks;

import static org.mockito.Mockito.*;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class BluetoothServiceTest {

    @Mock
    private BluetoothSocket mockSocket;
    @Mock
    private InputStream mockInputStream;
    @Mock
    private OutputStream mockOutputStream;
    @Mock
    private Handler mockHandler;

    private BluetoothService bluetoothService;
    private Map<Integer, Locks> locksMap;

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);

        when(mockSocket.getInputStream()).thenReturn(mockInputStream);
        when(mockSocket.getOutputStream()).thenReturn(mockOutputStream);

        locksMap = new HashMap<>();
        bluetoothService = new BluetoothService(mockSocket, mockHandler, locksMap);
    }

    @Test
    public void testParseLockId() {
        byte[] buffer = new byte[]{0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0};
        int lockId = bluetoothService.parseLockId(buffer);
        assert(lockId == 1);
    }

    @Test
    public void testParseBatteryLevel() {
        byte[] buffer = new byte[]{0, 0, 0, 0, 0, 0, 0, 50, 0, 0, 0, 0};
        int batteryLevel = bluetoothService.parseBatteryLevel(buffer);
        assert(batteryLevel == 50);
    }

    @Test
    public void testParseLockStatus() {
        byte[] buffer = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1};
        int lockStatus = bluetoothService.parseLockStatus(buffer);
        assert(lockStatus == 1);
    }

    @Test
    public void testWrite() throws IOException {
        byte[] data = new byte[]{1, 2, 3, 4};
        bluetoothService.write(data);
        verify(mockOutputStream).write(data);
    }

    @Test
    public void testRead() throws IOException {
        byte[] buffer = new byte[]{0, 0, 0, 1, 0, 0, 0, 50, 0, 0, 0, 1};
        when(mockInputStream.read(any(byte[].class))).thenAnswer(invocation -> {
            byte[] buf = invocation.getArgument(0);
            System.arraycopy(buffer, 0, buf, 0, buffer.length);
            return buffer.length;
        });

        Thread readThread = new Thread(() -> bluetoothService.read());
        readThread.start();

        ArgumentCaptor<Message> messageCaptor = ArgumentCaptor.forClass(Message.class);
        verify(mockHandler, timeout(1000).atLeastOnce()).obtainMessage(eq(1), eq(buffer.length), eq(-1), any(byte[].class));
    }
}