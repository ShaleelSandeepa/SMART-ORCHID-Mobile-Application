package com.designproject.smartorchid.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.SwitchCompat;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.Toast;

import com.designproject.smartorchid.R;
import com.designproject.smartorchid.data.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.concurrent.Executor;

public class SettingsActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager sensorManager;
    Sensor sensor;
    private boolean isRunning = false;
    private boolean isCancelAlert = false;
    private boolean isLightSensorOn = false;
    int accountID;

    CardView deleteAccount;
    ScrollView settingsScrollView;
    SwitchMaterial mySwitchFingerprint, mySwitchLight, mySwitchNotification;
    DatabaseHelper databaseHelper;
    Cursor data, cursor;

    //Fingerprint related variables
    BiometricPrompt biometricPrompt;
    BiometricPrompt.PromptInfo promptInfo;
    Boolean isFingerprintOn = false;
    Boolean isNotificationOn = false;

    //FIREBASE VARIABLE
    FirebaseFirestore firestore;
    boolean isDeleteFromFirebase;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        settingsScrollView = findViewById(R.id.settingsScrollView);
        deleteAccount = findViewById(R.id.deleteAccount);
        mySwitchFingerprint = findViewById(R.id.switchFingerprint);
        mySwitchLight = findViewById(R.id.switchLightSensor);
        mySwitchNotification = findViewById(R.id.switchNotification);

        databaseHelper = new DatabaseHelper(this);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        firestore = FirebaseFirestore.getInstance();

        // To set action bar custom view
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        //Change action bar title
        AppCompatTextView textView = findViewById(R.id.title_actionbar);
        textView.setText("Settings");
        //change the status bar color
        getWindow().setStatusBarColor(getColor(R.color.DodgerBlue));
        //change the status bar to dark theme
        getWindow().getDecorView().setSystemUiVisibility(0);
        //Enable the back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //Remove the action bar shadow
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }

        //generate settings from database
        cursor = databaseHelper.getSetting(1);
        if (cursor.moveToNext()){
            if (cursor.getInt(2) == 0) {
                isNotificationOn = false;
                mySwitchNotification.setChecked(false);
            } else if (cursor.getInt(2) == 1) {
                isNotificationOn = true;
                mySwitchNotification.setChecked(true);
            }
        }
        cursor = databaseHelper.getSetting(2);
        if (cursor.moveToNext()){
            if (cursor.getInt(2) == 0) {
                isLightSensorOn = false;
                mySwitchLight.setChecked(false);
            } else if (cursor.getInt(2) == 1) {
                isLightSensorOn = true;
                mySwitchLight.setChecked(true);
            }
        }
        cursor = databaseHelper.getSetting(3);
        if (cursor.moveToNext()){
            if (cursor.getInt(2) == 0) {
                isFingerprintOn = false;
                mySwitchFingerprint.setChecked(false);
            } else if (cursor.getInt(2) == 1) {
                isFingerprintOn = true;
                mySwitchFingerprint.setChecked(true);
            }
        }

        //if user want to delete account, first he should turn on fingerprint.
        deleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFingerprintOn) {
                    Toast.makeText(SettingsActivity.this, "Please Enable Fingerprint", Toast.LENGTH_SHORT).show();
                } else {
                    androidx.appcompat.app.AlertDialog.Builder alertDialog = new androidx.appcompat.app.AlertDialog.Builder(SettingsActivity.this);
                    alertDialog.setTitle("Delete Account");
                    alertDialog.setMessage("Your all data will be removed, Do you want to delete account ?");

                    //When click "Yes" it will execute this
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            fingerprint(true);
                        }
                    });

                    //When click "No" it will execute this
                    alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        });

        // Fingerprint Switch
        mySwitchFingerprint.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean isUpdated = databaseHelper.updateSettings(3, 1);
                    if (isUpdated) {
                        Toast.makeText(SettingsActivity.this, "Fingerprint Enabled", Toast.LENGTH_SHORT).show();
                        isFingerprintOn = true;
                    }
                } else {
                    // Switch is in the off state
                    boolean isUpdated = databaseHelper.updateSettings(3, 0);
                    if (isUpdated) {
                        Toast.makeText(SettingsActivity.this, "Fingerprint Disable", Toast.LENGTH_SHORT).show();
                        isFingerprintOn = false;
                    }
                }
            }
        });

        //Light Sensor Switch
        mySwitchLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean isUpdated = databaseHelper.updateSettings(2, 1);
                    if (isUpdated) {
                        isLightSensorOn = true;
                        Toast.makeText(SettingsActivity.this, "Light Sensor Enabled", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Switch is in the off state
                    boolean isUpdated = databaseHelper.updateSettings(2, 0);
                    if (isUpdated) {
                        isLightSensorOn = false;
                        Toast.makeText(SettingsActivity.this, "Light Sensor Disable", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Light Sensor Switch
        mySwitchNotification.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean isUpdated = databaseHelper.updateSettings(1, 1);
                    if (isUpdated) {
                        isNotificationOn = true;
                        Toast.makeText(SettingsActivity.this, "Notification Enabled", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // Switch is in the off state
                    boolean isUpdated = databaseHelper.updateSettings(1, 0);
                    if (isUpdated) {
                        isNotificationOn = false;
                        Toast.makeText(SettingsActivity.this, "Notification Disable", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //check the fingerprint state
        if (isFingerprintOn) { // if fingerprint on user have to authenticate to access settings
            mySwitchFingerprint.setChecked(true);
            fingerprint(false);
        } else {
            settingsScrollView.setVisibility(View.VISIBLE);
        }
    }

    public void fingerprint(boolean wantDelete) {
        // Switch is in the on state
        // FINGERPRINT CODES

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                Toast.makeText(this, "Device Doesn't have Fingerprint", Toast.LENGTH_SHORT).show();
                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                Toast.makeText(this, "Not Working !", Toast.LENGTH_SHORT).show();

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                Toast.makeText(this, "No Fingerprint assigned !", Toast.LENGTH_SHORT).show();
            case BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED:
                break;
            case BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED:
                break;
            case BiometricManager.BIOMETRIC_STATUS_UNKNOWN:
                break;
            case BiometricManager.BIOMETRIC_SUCCESS:
                break;
        }

        Executor executors = ContextCompat.getMainExecutor(this);

        biometricPrompt = new BiometricPrompt(SettingsActivity.this, executors, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                settingsScrollView.setVisibility(View.VISIBLE);
                Toast.makeText(SettingsActivity.this, "Authentication successful !", Toast.LENGTH_SHORT).show();

                if (wantDelete) {
                    data = databaseHelper.getAllData();
                    while (data.moveToNext()) {
                        // this will get the account which is logged
                        int status = data.getInt(6);
                        if (status == 1) {
                            String userName = data.getString(1);
                            accountID = data.getInt(0);
                            boolean is_deleted = databaseHelper.deleteAccount(data.getInt(0));
                            if (is_deleted) {
                                //if delete an account, this will be delete relevant data raw in REMEMBER ME table
                                databaseHelper.deleteRemember(accountID);
                                databaseHelper.deleteProfileDetails(accountID);
                                databaseHelper.deleteAllNotifications(accountID);
                                databaseHelper.deleteAllCartItemByAccountID(accountID);
                                databaseHelper.deleteAllOrdersByUserName(userName);

                                firestore.collection("users").document(userName).delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Cursor cursor = databaseHelper.getSetting(1);
                                            if (cursor.moveToNext()) {
                                                if (cursor.getInt(2) == 1) {
                                                    sendNotification("Your Account has been deleted !");
                                                }
                                            }
                                            cursor.close();
                                            Toast.makeText(SettingsActivity.this, "Account Successfully Deleted !", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                Intent intentLogin = new Intent(SettingsActivity.this, LoginActivity.class);
                                startActivity(intentLogin);
                            } else {
                                Toast.makeText(SettingsActivity.this, "Account Deletion Failed !", Toast.LENGTH_SHORT).show();
                            }
                            break;
                        }
                    }
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        promptInfo = new  BiometricPrompt.PromptInfo.Builder().setTitle("SMART ORCHID")
                .setDescription("Authentication Required !").setDeviceCredentialAllowed(true).build();

        biometricPrompt.authenticate(promptInfo);
    }

    // check the light intensity whether it is increased or not
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LIGHT) {
            float lightValue = event.values[0];
            if (event.values[0] < 100 && !isRunning && isLightSensorOn) {
                isRunning = true;
                isCancelAlert = false;
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
                alertDialog.setTitle("Alert");
                alertDialog.setMessage(lightValue + "\n" + "Decrease your Display Brightness !");
                //When click "Yes" it will execute this
                alertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        isCancelAlert = true;
                    }
                });
                alertDialog.show();
            } else if (event.values[0] >= 100 && isCancelAlert) {
                isRunning = false;
            }
        }
    }

    //when click back button, it will go previous item
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        // handle back button click
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this,sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    public void deleteFromFirebase(String userName) {
        firestore.collection("users").document(userName).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(SettingsActivity.this, "Account delete from cloud storage", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this, "Please check your connection !", Toast.LENGTH_SHORT).show();
            }
        });
    }

    ////////////////////////// SEND NOTIFICATION ////////////////////////////
    public void sendNotification(String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            String channelId = "smartorchid";
            CharSequence channelName = "SMART ORCHID";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

            // Register the channel with the system
            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "smartorchid")
                .setSmallIcon(R.drawable.ic_menu_book)
                .setContentTitle("SMART ORCHID")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Set a large icon for the notification
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.orchid);
        builder.setLargeIcon(largeIcon);

        int notificationId = 123;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(notificationId, builder.build());

    }
}