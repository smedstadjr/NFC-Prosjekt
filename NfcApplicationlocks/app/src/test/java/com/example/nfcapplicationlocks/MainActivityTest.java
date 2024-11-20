package com.example.nfcapplicationlocks;

import android.widget.Button;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = 28) // Angi SDK-versjonen du vil bruke for testen
public class MainActivityTest {

    private MainActivity mainActivity;
    private BluetoothService mockBluetoothService;

    @Before
    public void setUp() {
        mainActivity = Robolectric.buildActivity(MainActivity.class)
                .create()
                .start()
                .resume()
                .visible()
                .get();
        mockBluetoothService = Mockito.mock(BluetoothService.class);
        mainActivity.setBluetoothService(mockBluetoothService);
    }

    @Test
    public void testLockButton_click() {
        Button lockButton = mainActivity.findViewById(R.id.lockbtn);
        assertNotNull("Lock button should not be null", lockButton);

        lockButton.performClick();

        verify(mockBluetoothService).write(Mockito.any(byte[].class));
    }
}