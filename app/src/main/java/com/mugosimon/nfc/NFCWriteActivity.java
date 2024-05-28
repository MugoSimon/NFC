package com.mugosimon.nfc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class NFCWriteActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private static final int REQUEST_CODE_NFC_PERMISSION = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcwrite);

        // Get the NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Check if NFC is enabled
        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is not enabled. Please enable it and try again.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Check if the necessary permissions are granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.NFC) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.NFC}, REQUEST_CODE_NFC_PERMISSION);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Check if the intent is an NFC tag detection
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);

            // Write data to the NFC tag
            writeToNFCTag(tag, "Hello, Writing this dummy data just for my own consumption");
        }
    }

    private void writeToNFCTag(Tag tag, String data) {
        try {
            Ndef ndefTag = Ndef.get(tag);
            if (ndefTag != null) {
                ndefTag.connect();

                NdefMessage message = new NdefMessage(new NdefRecord[]{
                        NdefRecord.createTextRecord("en", data)
                });

                ndefTag.writeNdefMessage(message);
                ndefTag.close();

                // Display a success message and transition back to MainActivity
                Toast.makeText(this, "Data written to NFC tag successfully.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                NdefFormatable ndefFormatableTag = NdefFormatable.get(tag);
                if (ndefFormatableTag != null) {
                    ndefFormatableTag.connect();
                    ndefFormatableTag.format(new NdefMessage(new NdefRecord[]{
                            NdefRecord.createTextRecord("en", data)
                    }));
                    ndefFormatableTag.close();

                    // Display a success message and transition back to MainActivity
                    Toast.makeText(this, "Data written to NFC tag successfully.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    // Display a failure message and transition back to MainActivity
                    Toast.makeText(this, "Unable to write to NFC tag.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        } catch (IOException | FormatException e) {
            e.printStackTrace();
            // Display a failure message and transition back to MainActivity
            Toast.makeText(this, "Error writing to NFC tag: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
