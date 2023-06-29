package com.designproject.smartorchid.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.designproject.smartorchid.R;
import com.designproject.smartorchid.data.DatabaseHelper;

public class PaymentActivity extends AppCompatActivity {

    public int PICK_IMAGE_REQUEST_CODE = 11;
    DatabaseHelper databaseHelper;
    Cursor cursor;
    String userName;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        // To set action bar custom view
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        //Change action bar title
        AppCompatTextView textView = findViewById(R.id.title_actionbar);
        textView.setText("Payments");
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

        databaseHelper = new DatabaseHelper(this);
        cursor = databaseHelper.getLoggedInAccount();
        if (cursor.moveToNext()) {
            userName = cursor.getString(1);
        }

        Button sendReceipt = findViewById(R.id.sendReceipt);
        sendReceipt.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("IntentReset")
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(PaymentActivity.this);
                alertDialog.setTitle("Send Payment Receipt");
                alertDialog.setMessage("Do you have SMART ORCHID mobile number ?");

                //When click "Yes" it will execute this
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        chooseImageFromGallery();
                    }
                });
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String message = "*Please fill your details here with payment receipt*\n\n" + "User Name : " + userName +
                                "\nFull Name : \nEmail : \nAmount : ";
                        goToSmartOrchidPrivateChat(message);
                    }
                });
                alertDialog.show();

            }
        });

    }

    ////////////////////////////////////////// Send image on whatsapp //////////////////////////////////////////
    private void chooseImageFromGallery() {
        activityResultLauncher.launch("image/*");
    }

    private final ActivityResultLauncher<String> activityResultLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
                @SuppressLint("IntentReset")
                @Override
                public void onActivityResult(Uri result) {

                    String message = "*Fill your details here*\n\n" + "User Name : " + userName  + "\nFull Name : \nEmail : \nAmount : ";
                    PackageManager packageManager = getApplication().getPackageManager();
                    if (isWhatsappInstalled(packageManager)) {
                        Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                        whatsappIntent.putExtra(Intent.EXTRA_TEXT, message)
                                .putExtra(Intent.EXTRA_STREAM, result)
                                .setType("text/plain")
                                .setType("image/jpeg")
//                                .setData(Uri.parse("smsto:" + "+94764859595"))
                                .setPackage("com.whatsapp");
                        startActivity(whatsappIntent);

                    } else if (isWhatsappBusinessInstalled(packageManager)) {
                        Intent goToWhatsappBusiness = new Intent(Intent.ACTION_SEND);
                        goToWhatsappBusiness.putExtra(Intent.EXTRA_TEXT, message)
                                .putExtra(Intent.EXTRA_STREAM, result)
                                .setType("text/plain")
                                .setType("image/jpeg")
//                                .setData(Uri.parse("smsto:" + "+94764859595"))
                                .setPackage("com.whatsapp.w4b");
                        startActivity(goToWhatsappBusiness);
                    }

                }
            });
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public void goToSmartOrchidPrivateChat(String orderMessage) {
        String phoneNumber = "+94764859595";
        PackageManager packageManager = this.getPackageManager();
        //check the whatsapp is installed and intent
        if (isWhatsappInstalled(packageManager)) {
            Intent goToWhatsapp = new Intent(Intent.ACTION_VIEW);
            goToWhatsapp.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(orderMessage)));
            goToWhatsapp.setPackage("com.whatsapp");
            startActivity(goToWhatsapp);

            //check the whatsapp business app is installed and intent
        } else if (isWhatsappBusinessInstalled(packageManager)) {
            Intent goToWhatsappBusiness = new Intent(Intent.ACTION_VIEW);
            goToWhatsappBusiness.setData(Uri.parse("https://wa.me/" + phoneNumber + "&text=" + Uri.encode(orderMessage)));
            goToWhatsappBusiness.setPackage("com.whatsapp.w4b");
            startActivity(goToWhatsappBusiness);

        } else {
            try {
                ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.whatsapp.w4b", 0);
                String whatsappPackageName = applicationInfo.packageName;
                // The variable "whatsappPackageName" now holds the package name of WhatsApp
                Toast.makeText(this, whatsappPackageName, Toast.LENGTH_SHORT).show();

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }



    //check the whatsapp app is installed and return true if it is installed
    private boolean isWhatsappInstalled(PackageManager packageManager) {
        boolean whatsappInstalled;
        try {
            packageManager.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
            whatsappInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            whatsappInstalled = false;
        }
        return whatsappInstalled;
    }

    //check the whatsapp business app is installed and return true if it is installed
    private boolean isWhatsappBusinessInstalled(PackageManager packageManager) {
        boolean whatsappBusinessInstalled;
        try {
            packageManager.getPackageInfo("com.whatsapp.w4b", PackageManager.GET_ACTIVITIES);
            whatsappBusinessInstalled = true;
        } catch (PackageManager.NameNotFoundException e) {
            whatsappBusinessInstalled = false;
        }
        return whatsappBusinessInstalled;
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
}