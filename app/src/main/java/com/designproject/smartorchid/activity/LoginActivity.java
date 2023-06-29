package com.designproject.smartorchid.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.designproject.smartorchid.R;
import com.designproject.smartorchid.data.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    //CONNECTIVITY RELATED VARIABLES
    LinearLayout linearLayoutNoConnection;
    AlphaAnimation blinkAnimation;
    ImageView imgWifiOff;
    ConnectivityManager connectivityManager;
    Handler handler;
    Runnable runnable;

    DatabaseHelper databaseHelper;
    MaterialCheckBox rememberMe;
    Cursor data, dataById, dataRemember, dataStatus, findId;
    TextInputLayout edtLoginUserName, edtLoginPassword;
    String loginUserName, loginPassword;
    Button btnLogin;
    ProgressDialog progressDialog;

    //FIREBASE VARIABLE
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    @SuppressLint("ClickableViewAccessibility")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        databaseHelper = new DatabaseHelper(this);

        //change the status bar color
        getWindow().setStatusBarColor(getColor(R.color.DodgerBlue));
        //change the status bar to dark theme
        getWindow().getDecorView().setSystemUiVisibility(0);
        //Set the action bar title as null
        setTitle("");
        //Remove the action bar shadow
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }

        /////////////////////////////////// CHECK CONNECTIVITY /////////////////////////////////

        imgWifiOff = findViewById(R.id.imgWifiOff);
        blinkAnimation = new AlphaAnimation(0.0f, 1.0f);
        blinkAnimation.setDuration(500); // Set the duration of each animation cycle (in milliseconds)
        blinkAnimation.setRepeatCount(Animation.INFINITE); // Set the number of times to repeat (infinite in this case)
        blinkAnimation.setRepeatMode(Animation.REVERSE); // Reverse the animation when repeating

        linearLayoutNoConnection = findViewById(R.id.linearNoConnectionLayout);

        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternetConnection();
                handler.postDelayed(this, 1000); // Repeat the check every 1 second
            }
        };

        ////////////////////////////////////////////////////////////////////////////////////////

        btnLogin = (Button) findViewById(R.id.btnLogin);

        edtLoginUserName = (TextInputLayout) findViewById(R.id.loginUserName);
        edtLoginPassword = (TextInputLayout) findViewById(R.id.loginPassword);

        //set onclick listener for checkbox
        rememberMe = findViewById(R.id.checkBoxRememberMe);
        rememberMe.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    rememberMe.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.DodgerBlue)));
                } else {
                    rememberMe.setButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.DarkGray)));
                }
            }
        });

        //set login status = 0 in all the data raw in accounts table
        dataStatus = databaseHelper.getAllData();
        while (dataStatus.moveToNext()) {
            if (dataStatus.getInt(5) == 1) {
                databaseHelper.updateLoginStatus(dataStatus.getString(0), 0);
            }
        }
        dataStatus.close();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUserName = edtLoginUserName.getEditText().getText().toString();
                loginPassword = edtLoginPassword.getEditText().getText().toString();
//
                if (loginUserName.isEmpty()) {
                    edtLoginUserName.setError("Username required");
                } else {
                    edtLoginUserName.setError(null);
                }
                if (loginPassword.isEmpty()) {
                    edtLoginPassword.setError("Password required");
                } else {
                    edtLoginPassword.setError(null);
                }

                //////////////////////////// CHECK FIRE STORE WHEN CONNECTION IS AVAILABLE //////////////////////////////
                if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()) {

                    if (!databaseHelper.checkUserName(loginUserName) && !loginUserName.isEmpty()) {

                        progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setTitle("Login...");
                        progressDialog.show();

                        DocumentReference docRef = firestore.collection("users").document(loginUserName);
                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    if (documentSnapshot.exists()) {
                                        String userName = documentSnapshot.getString("userName");
                                        String password = documentSnapshot.getString("password");
                                        String phone = documentSnapshot.getString("phone");
                                        String email = documentSnapshot.getString("email");

                                        // check the fire store account password same as the input password
                                        if (password != null && password.equals(loginPassword)) {
                                            databaseHelper.registerUser(userName, email, phone, password);
                                            checkSQLiteForLogin();
                                        } else {
                                            if (loginPassword.isEmpty()) {
                                                if (progressDialog.isShowing())
                                                    progressDialog.dismiss();
                                                edtLoginPassword.setError("Password required");
                                                Toast.makeText(LoginActivity.this, "Password Required !", Toast.LENGTH_SHORT).show();
                                            } else {
                                                if (progressDialog.isShowing())
                                                    progressDialog.dismiss();
                                                edtLoginPassword.setError("Invalid password");
                                                Toast.makeText(LoginActivity.this, "Invalid Password !", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } else {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        checkSQLiteForLogin();
                                    }
                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    Toast.makeText(LoginActivity.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                        //////////////////////////////////////////////////////////////////////////////

                    } else {
                        checkSQLiteForLogin();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Connection Lost !", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //THIS SECTION WILL SET EDIT TEXT FILLED BY DATA WHICH IS IN STATE AS 1
        dataRemember = databaseHelper.checkRememberMe();
        if (dataRemember != null) {
            while (dataRemember.moveToNext()) {
                //check the data raw which is status = 1
                if (dataRemember.getInt(2) == 1) {
                    dataById = databaseHelper.getUserDataById(dataRemember.getString(1));
                    if (dataById.moveToNext()) {
                        edtLoginUserName.getEditText().setText(dataById.getString(1));
                        edtLoginPassword.getEditText().setText(dataById.getString(4));
                    }
                    int remember = dataRemember.getInt(2);
                    rememberMe.setChecked(toBoolean(remember));
                }
            }
            dataRemember.close();
        }

    }

    public void goHomeActivity() {
        Intent intentHome = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intentHome);
    }

    public void goToRegister(View view) {
        Intent intentRegister = new Intent(this, RegisterActivity.class);
        startActivity(intentRegister);
    }

    //When try to back from Login page this method will execute
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
            }
        });
        alertDialog.show();
    }

    public void goForgotPassWordActivity(View view) {
        Intent intentForgot = new Intent(this, ForgotPassWordActivity.class);
        startActivity(intentForgot);
    }

    //this can convert int value to boolean
    public boolean toBoolean(int num){
        return num!=0;
    }

    public void checkSQLiteForLogin() {
        if (databaseHelper.checkUserName(loginUserName)) {
            if (databaseHelper.checkUserNamePassword(loginUserName, loginPassword)) {

                data = databaseHelper.getUserData(loginUserName);
                dataRemember = databaseHelper.checkRememberMe();
                if (data.moveToFirst()) {
                    if (rememberMe.isChecked()) {
                        if (data.getInt(5) == 0) {

                            while (dataRemember.moveToNext()) {
                                //set all the states as 0
                                if (dataRemember.getInt(2) == 1) {
                                    databaseHelper.updateRememberTableRemember(dataRemember.getInt(1), 0);
                                    databaseHelper.updateRemember(dataRemember.getString(1), 0);
                                }
                            }

                            //find any accountId exist in remember table is equals to the current UserID
                            findId = databaseHelper.findAccountIdRememberTable(data.getString(0));
                            if (findId.getCount() > 0) {
                                if (findId.moveToNext()) {
                                    //If found, update the states as 1
                                    databaseHelper.updateRememberTableRemember(data.getInt(0), 1);
                                    databaseHelper.updateRemember(data.getString(0), 1);
                                }
                            } else {
                                //if there does not exist, insert new data raw to the remember table
                                databaseHelper.insertRemember(data.getString(0)); // default status is 1 when inserting
                                databaseHelper.updateRemember(data.getString(0), 1);
                            }
                            findId.close();
                        }
                        dataRemember.close();

                    } else {
                        databaseHelper.updateRemember(data.getString(0), 0);
                        if (dataRemember.getCount() > 0) {
                            // Check the remember me table if already exist a raw relevant current user ID
                            while (dataRemember.moveToNext()) {
                                //If it is found, update status as 0
                                if (dataRemember.getInt(1) == data.getInt(0)) {
                                    databaseHelper.updateRememberTableRemember(data.getInt(0), 0);
                                }
                            }
                        }
                        dataRemember.close();
                    }
                    //when login set the login_status as 1
                    databaseHelper.updateLoginStatus(data.getString(0), 1);
                    data.close();
                }

                //finally it goes to home activity
                goHomeActivity();
                Toast.makeText(LoginActivity.this, "Login Successful !", Toast.LENGTH_SHORT).show();

            } else {
                if (loginPassword.isEmpty()) {
                    edtLoginPassword.setError("Password required");
                    Toast.makeText(LoginActivity.this, "Password Required !", Toast.LENGTH_SHORT).show();
                } else {
                    edtLoginPassword.setError("Invalid password");
                    Toast.makeText(LoginActivity.this, "Invalid Password !", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (!loginUserName.isEmpty()){
            edtLoginUserName.setError("Invalid user name");
        }
    }

    //check the internet connection is available or not
    private void checkInternetConnection() {
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()){
            linearLayoutNoConnection.setVisibility(View.GONE);
        } else {
            linearLayoutNoConnection.setVisibility(View.VISIBLE);
            imgWifiOff.setVisibility(View.VISIBLE); // Make the ImageView visible
            imgWifiOff.startAnimation(blinkAnimation);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 1000); // Start the periodic check
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Stop the periodic check
    }

}