package com.mugosimon.nfc;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.nio.charset.Charset;

public class NFCReadActivity extends AppCompatActivity {

    private NfcAdapter nfcAdapter;
    private PendingIntent pendingIntent;
    private IntentFilter[] intentFilters;
    private TextView tvNFCData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcread);

        // Get the NFC adapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(this);

        // Check if NFC is enabled
        if (nfcAdapter == null || !nfcAdapter.isEnabled()) {
            Toast.makeText(this, "NFC is not enabled. Please enable it and try again.", Toast.LENGTH_SHORT).show();
            finish();
        }

        // Set up the pending intent and intent filters
        pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), PendingIntent.FLAG_IMMUTABLE);
        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        try {
            ndef.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("Failed to add MIME type to Intent filter", e);
        }
        intentFilters = new IntentFilter[]{ndef};

        // Get the TextView to display the NFC data
        tvNFCData = findViewById(R.id.tvNFCData);
    }

    @Override
    protected void onResume() {
        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pendingIntent, intentFilters, null);
    }

    @Override
    protected void onPause() {
        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        // Check if the intent is an NFC tag detection
        if (NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            readNFCTag(tag);
        }
    }

    private void readNFCTag(Tag tag) {
        try {
            Ndef ndefTag = Ndef.get(tag);
            if (ndefTag != null) {
                ndefTag.connect();
                NdefMessage ndefMessage = ndefTag.getNdefMessage();
                if (ndefMessage != null) {
                    NdefRecord[] records = ndefMessage.getRecords();
                    if (records != null && records.length > 0) {
                        String data = new String(records[0].getPayload(), Charset.forName("UTF-8"));
                        tvNFCData.setText(data);
                        Toast.makeText(this, "Data read from NFC tag successfully.", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                ndefTag.close();
            } else {
                Toast.makeText(this, "Unable to read NFC tag.", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (IOException | FormatException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error reading NFC tag: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
