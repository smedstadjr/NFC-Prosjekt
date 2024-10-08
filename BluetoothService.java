package com.example.nfcapplicationlocks;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;

public class BluetoothService {

    private static final String TAG = "BluetoothService";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    public BluetoothService(Context context) {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // Metode for å koble til en Bluetooth-enhet ved hjelp av dens adresse
    public boolean connectToDevice(String deviceAddress) {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(deviceAddress);
        try {
            // Opprett en RFCOMM BluetoothSocket
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            // Koble til enheten
            bluetoothSocket.connect();
            // Hent input- og output-strømmer for kommunikasjon
            inputStream = bluetoothSocket.getInputStream();
            outputStream = bluetoothSocket.getOutputStream();
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error connecting to device", e);
            return false;
        }
    }

    // Metode for å lese data fra Bluetooth-enheten
    public String readData() {
        try {
            byte[] buffer = new byte[1024]; // Buffer for å lagre innkommende data
            int bytes = inputStream.read(buffer); // Les data fra input-strømmen
            return new String(buffer, 0, bytes); // Konverter byte-array til String
        } catch (IOException e) {
            Log.e(TAG, "Error reading data", e);
            return null;
        }
    }

    // Metode for å sende data til Bluetooth-enheten
    public boolean sendData(String data) {
        try {
            outputStream.write(data.getBytes()); // Skriv data til output-strømmen
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error sending data", e);
            return false;
        }
    }

    // Metode for å lukke Bluetooth-tilkoblingen
    public void closeConnection() {
        try {
            if (inputStream != null) inputStream.close(); // Lukk input-strømmen
            if (outputStream != null) outputStream.close(); // Lukk output-strømmen
            if (bluetoothSocket != null) bluetoothSocket.close(); // Lukk BluetoothSocket
        } catch (IOException e) {
            Log.e(TAG, "Error closing connection", e);
        }
    }
}