package com.wanjy.dannie.rivchat.ui;
import android.app.Activity;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import android.os.Bundle;
import com.google.zxing.Result;
import android.util.Log;
import android.widget.CheckBox;
import android.support.v4.app.ActivityCompat;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.widget.CompoundButton;
import android.view.View;
import android.widget.Toast;
import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.Manifest;
import com.wanjy.dannie.rivchat.ui.RegisterActivity;

import android.os.Handler;
import android.os.Looper;

public class DecoderActivity extends Activity implements ZXingScannerView.ResultHandler {
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    private ZXingScannerView mScannerView;
    
    
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }else{
            mScannerView = new ZXingScannerView(this);
            setContentView(mScannerView);


        }







    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }

    @Override
    public void handleResult(Result rawResult) {
        
        
        Log.v("log", rawResult.getText());
        Log.v("log", rawResult.getBarcodeFormat().toString());

        
        
        if(rawResult.getText().toString().contains("2020")&&rawResult.getText().toString().contains(",")&&rawResult.getText().toString().contains("/")){
            Toast.makeText(getApplicationContext(),"تم التحقق", Toast.LENGTH_LONG).show();
            mScannerView.stopCameraPreview();
            RegisterActivity.QRCode = rawResult.getText();
            RegisterActivity.enableDecodingCheckBox.setChecked(true);
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        }else{
            Toast.makeText(getApplicationContext(),"هذا الكود غير صالح , يجب استخدام بطاقة الترشيح", Toast.LENGTH_LONG).show();
            mScannerView.stopCameraPreview();
            
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mScannerView.resumeCameraPreview(DecoderActivity.this);
                    }
                }, 3000);
            
            
            
        }
        
        
        
        
        
    }
    
    
    
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
                mScannerView = new ZXingScannerView(this);
                setContentView(mScannerView);
                
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }
    
    
    
    
    
        
    
}
