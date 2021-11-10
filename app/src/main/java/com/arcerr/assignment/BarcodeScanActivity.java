package com.arcerr.assignment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.arcerr.assignment.databinding.ActivityBarcodeScanBinding;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class BarcodeScanActivity extends AppCompatActivity {
    int MY_PERMISSIONS_REQUEST_CAMERA=0;
    ActivityBarcodeScanBinding binding;
    CodeScanner mCodeScanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityBarcodeScanBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        requestCameraPermission();
    }

    private void startScan() {
        mCodeScanner = new CodeScanner(this, binding.scannerView);
        mCodeScanner.startPreview();
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent();
                        intent.putExtra("val", result.getText());
                        Log.v("Tag",result.getText());
                        setResult(RESULT_OK, intent);
                        onBackPressed();
                    }
                });
            }
        });

    }

    private final ActivityResultLauncher<String[]> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {

                if (result.get("android.permission.CAMERA"))
                    startScan();
                else
                    requestCameraPermission();

            });

    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED){
            startScan();
        } else {
            requestPermissionLauncher.launch(new String[]{Manifest.permission.CAMERA});
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mCodeScanner!=null)
        mCodeScanner.startPreview();
    }

    @Override
    protected void onPause() {
        if (mCodeScanner!=null)
        mCodeScanner.releaseResources();
        super.onPause();
    }




}