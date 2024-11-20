package com.example.nfcapplicationlocks;

import static org.junit.Assert.*;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import java.io.IOException;
import java.util.UUID;

/**
 * This class purpose is to test is to test bluetooth connection without an actual device.
 * this class was supposed to use shadow bluetoothadapter and shadow bluetoothdevice insted of mocking.
 * these tests will not be successful because its hard to set up bluetooth connection without a actual device.
 * this is a try but it has shown itself to be too difficult right now
 */

@RunWith(RobolectricTestRunner.class)
@Config(manifest = Config.NONE, shadows = {com.example.nfcapplicationlocks.ShadowBluetoothAdapter.class, com.example.nfcapplicationlocks.ShadowBDevice.class})
public class BluetoothPairingTest {

    private BluetoothPairing bluetoothPairing;
    private BluetoothAdapter bluetoothAdapter;
    private com.example.nfcapplicationlocks.ShadowBluetoothAdapter shadowBluetoothAdapter;

    @Before
    public void setUp() {
        Context context = RuntimeEnvironment.getApplication();
        bluetoothPairing = new BluetoothPairing(context);
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        shadowBluetoothAdapter = new com.example.nfcapplicationlocks.ShadowBluetoothAdapter();
        shadowBluetoothAdapter.setRealAdapter(bluetoothAdapter);
        bluetoothPairing.setBluetoothAdapter(bluetoothAdapter);
    }

    @Test
    public void testStartPairing() {
        // Simuler en Bluetooth-enhet
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("00:11:22:33:AA:BB");
        ShadowBDevice shadowDevice = new ShadowBDevice();

        // Initialiser mock-bondstate
        shadowDevice.setBondState(BluetoothDevice.BOND_NONE);

        // Kjør metoden som testes
        bluetoothPairing.startPairing(device);

        // Sjekk at metoden oppdaterer bond-state korrekt
        assertEquals(BluetoothDevice.BOND_BONDING, shadowDevice.getBondState());
    }

    @Test
    public void testConnect() throws IOException {
        // Simuler en Bluetooth-enhet
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("00:11:22:33:AA:BB");
        com.example.nfcapplicationlocks.ShadowBDevice shadowDevice = new com.example.nfcapplicationlocks.ShadowBDevice();
        shadowDevice.setRealDevice(device);
        shadowDevice.setBondState(BluetoothDevice.BOND_BONDED);

        shadowBluetoothAdapter.addBondedDevice(device);

        bluetoothPairing.startPairing(device);
        BluetoothSocket socket = bluetoothPairing.connect();

        assertNotNull(socket);
        assertTrue(socket.isConnected());
    }

    @Test
    public void testAutoConnectToRaspberryPi() {
        // Simuler en Bluetooth-enhet
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice("B8:27:EB:D5:9F:76");
        com.example.nfcapplicationlocks.ShadowBDevice shadowDevice = new com.example.nfcapplicationlocks.ShadowBDevice();
        shadowDevice.setRealDevice(device);
        shadowDevice.setBondState(BluetoothDevice.BOND_BONDED);

        shadowBluetoothAdapter.addBondedDevice(device);

        BluetoothSocket socket = bluetoothPairing.autoConnectToRaspberryPi();

        assertNotNull(socket);
        // Vi kan ikke sjekke om socket er tilkoblet uten en ekte enhet, men vi kan sjekke at den ikke er null
    }
}