package com.example.nfcapplicationlocks;

import android.Manifest;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int REQUEST_BLUETOOTH_PERMISSIONS = 1;
    private BluetoothPairing bluetoothPairing;
    private BluetoothService bluetoothService;
    private TextView batteryTextView;
    private Button lockButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        batteryTextView = findViewById(R.id.battery);
        lockButton = findViewById(R.id.lockbtn);

        // Be om Bluetooth- og plasseringstillatelser
        requestBluetoothPermissions();

        setupLockButton();
    }

    private void requestBluetoothPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    REQUEST_BLUETOOTH_PERMISSIONS);
        } else {
            initializeBluetooth();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_BLUETOOTH_PERMISSIONS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Tillatelser gitt
                initializeBluetooth();
            } else {
                // Tillatelser ikke gitt
                Toast.makeText(this, "Bluetooth permissions are required for this app", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeBluetooth() {
        Log.d(TAG, "Initializing Bluetooth...");
        bluetoothPairing = new BluetoothPairing(this);
        BluetoothSocket socket = bluetoothPairing.autoConnectToRaspberryPi();


        if (socket != null) {
            Log.d(TAG, "Connected to Raspberry Pi");
            bluetoothService = new BluetoothService(socket, new Handler(new Handler.Callback() {
                @Override
                public boolean handleMessage(Message msg) {
                    byte[] buffer = (byte[]) msg.obj;
                    int batteryLevel = bluetoothService.parseBatteryLevel(buffer);
                    Log.d(TAG, "Battery Level: " + batteryLevel + "%");
                    batteryTextView.setText("Battery Level: " + batteryLevel + "%");
                    return true;
                }
            }), new HashMap<>());

            startBluetoothReading();
        } else {
            Log.e(TAG, "Failed to connect to Raspberry Pi");
        }
    }

    private void startBluetoothReading() {
        Log.d(TAG, "Starting Bluetooth reading...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (bluetoothService != null) {
                    bluetoothService.read();
                }
            }
        }).start();
    }

    private void setupLockButton() {
        lockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Lock button clicked");
                if (bluetoothService != null) {
                    sendData(1, 75, 1); // Eksempeldata: lockID=1, batteryLevel=75, lockStatus=1
                } else {
                    Log.e(TAG, "BluetoothService is not initialized");
                    Toast.makeText(MainActivity.this, "Bluetooth is not connected", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendData(int lockID, int batteryLevel, int lockStatus) {
        // Opprett en byte-array på 12 bytes
        byte[] data = new byte[12];

        // Konverter int-verdiene til byte-representasjoner og fyll byte-arrayen
        String lockIDStr = String.format("%04d", lockID);
        String batteryLevelStr = String.format("%04d", batteryLevel);
        String lockStatusStr = String.format("%04d", lockStatus);

        System.arraycopy(lockIDStr.getBytes(), 0, data, 0, 4);
        System.arraycopy(batteryLevelStr.getBytes(), 0, data, 4, 4);
        System.arraycopy(lockStatusStr.getBytes(), 0, data, 8, 4);

        if (bluetoothService != null) {
            bluetoothService.write(data);
        } else {
            Log.e(TAG, "BluetoothService is not initialized");
        }
    }
}
