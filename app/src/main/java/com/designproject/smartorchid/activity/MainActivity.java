package com.designproject.smartorchid.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

import com.designproject.smartorchid.R;
import com.designproject.smartorchid.data.DatabaseHelper;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    Boolean authentication = false;

    //Fingerprint related variables
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    DatabaseHelper databaseHelper;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //      hide the action bar from this activity
        getSupportActionBar().hide();

        databaseHelper = new DatabaseHelper(this);
        //generate settings from database
        cursor = databaseHelper.getSetting(1);
        if (cursor.getCount() == 0){
            databaseHelper.addSettings(1, "notification", 0);
        }
        cursor = databaseHelper.getSetting(2);
        if (cursor.getCount() == 0){
            databaseHelper.addSettings(2, "lightSensor", 0);
        }
        cursor = databaseHelper.getSetting(3);
        if (cursor.getCount() == 0){
            databaseHelper.addSettings(3, "fingerPrint", 0);
            goWelcome();
        } else {
            // if fingerprint is on in database,
            if (cursor.moveToNext()) {
                if (cursor.getInt(2) == 1) {
                    //user have to authenticate his fingerprint for login
                    fingerPrint();
                } else {
                    goWelcome();
                }
            }
        }
        cursor.close();

    }

    public void fingerPrint() {
        // FINGERPRINT CODES
        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Device Doesn't have Fingerprint", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Not Working !", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No Fingerprint assigned !", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                break;
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
        }

        Executor executors = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(MainActivity.this, executors, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(MainActivity.this, "Authentication successful !", Toast.LENGTH_SHORT).show();
                authentication = true;
                checkAuthentication();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        promptInfo = new  BiometricPrompt.PromptInfo.Builder().setTitle("SMART ORCHID")
                .setDescription("Authentication Required !").setDeviceCredentialAllowed(true).build();

        try {
            biometricPrompt.authenticate(promptInfo);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    public void checkAuthentication() {
        goWelcome();
    }

    protected void goWelcome() {
        Intent intentWelcome = new Intent(this, WelcomeActivity.class);
        startActivity(intentWelcome);
    }

    //When try to back from starting screen this method will execute
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exit App");
        alertDialog.setMessage("Do you want to exit Smart Orchid ?");

        //When click "Yes" it will execute this
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                onStop();
                onDestroy();
            }
        });

        //When click "No" it will execute this
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                biometricPrompt.authenticate(promptInfo);
            }
        });
        alertDialog.show();
    }
}