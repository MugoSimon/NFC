package com.mugosimon.nfc;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up the write card button
        Button btnWriteCard = findViewById(R.id.btnWriteCard);
        btnWriteCard.setOnClickListener(v -> startNFCWriteActivity());

        // Set up the read card button
        Button btnReadCard = findViewById(R.id.btnReadCard);
        btnReadCard.setOnClickListener(v -> startNFCReadActivity());
    }

    private void startNFCWriteActivity() {
        Intent intent = new Intent(this, NFCWriteActivity.class);
        startActivity(intent);
    }

    private void startNFCReadActivity() {
        Intent intent = new Intent(this, NFCReadActivity.class);
        startActivity(intent);
    }
}
