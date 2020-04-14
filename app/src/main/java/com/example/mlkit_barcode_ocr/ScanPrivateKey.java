package com.example.mlkit_barcode_ocr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;


import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ScanPrivateKey extends AppCompatActivity  {
    String private_key;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scan_private_key);

        final Activity activity = this;
        IntentIntegrator integrator = new IntentIntegrator(activity);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
        integrator.setPrompt("Scan Private Key");
        integrator.setCameraId(0);
        integrator.setBeepEnabled(false);
        integrator.setBarcodeImageEnabled(false);
        integrator.initiateScan();

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //scanned private key
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        private_key = result.getContents().toString().trim();
        if(result != null){
            if(result.getContents()==null){
                //error message
                Toast.makeText(this, "You cancelled the scanning", Toast.LENGTH_LONG).show();
            }
        }
        else {
            //if scanned result is null, scan again
            super.onActivityResult(requestCode, resultCode, data);
        }
        //send scanned private key to Main Activity
        Intent send_intent = new Intent(ScanPrivateKey.this, MainActivity.class);
        send_intent.putExtra("private", result.getContents());
        startActivity(send_intent);
    }
}
