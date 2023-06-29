package com.designproject.smartorchid.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.designproject.smartorchid.R;
import com.designproject.smartorchid.data.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

public class ForgotPassWordActivity extends AppCompatActivity {

    //CONNECTIVITY RELATED VARIABLES
    LinearLayout linearLayoutNoConnection;
    AlphaAnimation blinkAnimation;
    ImageView imgWifiOff;
    ConnectivityManager connectivityManager;
    Handler handlerForgot;
    Runnable runnable;
    ProgressDialog progressDialog;

    DatabaseHelper databaseHelper;
    FirebaseFirestore firestore;
    TextInputLayout edtForgotUsername, edtForgotPhone, edtForgotPassword, edtForgotConfirmPassword;
    String forgotUsername, forgotPhone, forgotPassword, forgotConfirmPassword;
    Button btnChangePassword;

    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^" +
                    "(?=.*[0-9])" +         //at least 1 digit
                    "(?=.*[a-z])" +         //at least 1 lower case letter
                    "(?=.*[A-Z])" +         //at least 1 upper case letter
                    //"(?=.*[a-zA-Z])" +      //any letter
                    "(?=.*[@#$%^&+=])" +    //at least 1 special character
                    "(?=\\S+$)" +           //no white spaces
                    ".{6,}" +               //at least 6 characters
                    "$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^\\+(?:[0-9] ?){10,10}[0-9]$");

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        //change the status bar to dark theme and change color
        getWindow().setStatusBarColor(getColor(R.color.DodgerBlue));
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

        linearLayoutNoConnection = findViewById(R.id.linearNoConnectionLayoutForgot);

        connectivityManager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        // this is must ro attach onResume and onPause methods
        handlerForgot = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                checkInternetConnection();
                handlerForgot.postDelayed(this, 1000); // Repeat the check every 1 second
            }
        };

        ////////////////////////////////////////////////////////////////////////////////////////

        //database declarations
        databaseHelper = new DatabaseHelper(this);
        firestore = FirebaseFirestore.getInstance();

        btnChangePassword = (Button) findViewById(R.id.btnChangePassword);

        edtForgotUsername = (TextInputLayout) findViewById(R.id.forgotUsername);
        edtForgotPhone = (TextInputLayout) findViewById(R.id.forgotPhone);
        edtForgotPassword = (TextInputLayout) findViewById(R.id.forgotPassword);
        edtForgotConfirmPassword = (TextInputLayout) findViewById(R.id.forgotConfirmPassword);

        Objects.requireNonNull(edtForgotUsername.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                forgotUsername = edtForgotUsername.getEditText().getText().toString();
                if (forgotUsername.isEmpty()) {
                    edtForgotUsername.setError("Field can't be empty");
                } else if (!forgotUsername.equals(forgotUsername.trim())) {
                    edtForgotUsername.setError("Remove white spaces");
                } else {
                    edtForgotUsername.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(edtForgotPhone.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                forgotPhone = edtForgotPhone.getEditText().getText().toString();
                if (forgotPhone.isEmpty()) {
                    edtForgotPhone.setError("Field can't be empty");
                } else if (!PHONE_PATTERN.matcher(forgotPhone).matches()) {
                    edtForgotPhone.setError("Invalid number");
                } else {
                    edtForgotPhone.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(edtForgotPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                forgotPassword = edtForgotPassword.getEditText().getText().toString();
                if (forgotPassword.isEmpty()) {
                    edtForgotPassword.setError("Field can't be empty");
                } else if (!PASSWORD_PATTERN.matcher(forgotPassword).matches()) {
                    edtForgotPassword.setError("Password too week");
                } else {
                    edtForgotPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Objects.requireNonNull(edtForgotConfirmPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                forgotConfirmPassword = edtForgotConfirmPassword.getEditText().getText().toString();
                if (forgotConfirmPassword.isEmpty()) {
                    edtForgotConfirmPassword.setError("Field can't be empty");
                } else if (!forgotConfirmPassword.equals(forgotPassword)) {
                    edtForgotConfirmPassword.setError("Please enter the same password");
                } else {
                    edtForgotConfirmPassword.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //////////////////////////// CHECK FIRE STORE WHEN CONNECTION IS AVAILABLE //////////////////////////////
                if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()) {

                    forgotUsername = edtForgotUsername.getEditText().getText().toString();
                    forgotPhone = edtForgotPhone.getEditText().getText().toString();
                    forgotPassword = edtForgotPassword.getEditText().getText().toString();
                    forgotConfirmPassword = edtForgotConfirmPassword.getEditText().getText().toString();

                    if (forgotUsername.isEmpty()) {
                        edtForgotUsername.setError("Field can't be empty");
                    } else {
                        edtForgotUsername.setError(null);
                    }
                    if (forgotPhone.isEmpty()) {
                        edtForgotPhone.setError("Field can't be empty");
                    } else {
                        edtForgotPhone.setError(null);
                    }
                    if (forgotPassword.isEmpty()) {
                        edtForgotPassword.setError("Field can't be empty");
                    } else {
                        edtForgotPassword.setError(null);
                    }
                    if (forgotConfirmPassword.isEmpty()) {
                        edtForgotConfirmPassword.setError("Field can't be empty");
                    } else {
                        edtForgotConfirmPassword.setError(null);
                    }


                    // check the both new password and confirm new password are same
                    if (forgotPassword.equals(forgotConfirmPassword) && !forgotPassword.isEmpty()) {

                        progressDialog = new ProgressDialog(ForgotPassWordActivity.this);
                        progressDialog.setTitle("Password Changing...");
                        progressDialog.show();

                        ////////////// validate user is already registered on firebase ////////////////
                        DocumentReference docRef = firestore.collection("users").document(forgotUsername);

                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot documentSnapshot = task.getResult();
                                    // if the user name is exist on firestore
                                    if (documentSnapshot.exists()) {
                                        // check the firebase phone is equal to edit text phone
                                        if (Objects.equals(documentSnapshot.get("phone"), forgotPhone)) {

                                            ////////////// change password on firebase ////////////////
                                            // Create a map with the fields to be updated
                                            Map<String, Object> updates = new HashMap<>();
                                            updates.put("password", forgotPassword);

                                            // Perform the update operation
                                            docRef.update(updates)
                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {

                                                                ////////////// change password on local database (SQLite) ////////////
                                                                if (databaseHelper.checkUserName(forgotUsername)) {
                                                                    if (databaseHelper.checkUserNamePhone(forgotUsername, forgotPhone)) {
                                                                        try {
                                                                            Cursor data = databaseHelper.getUserData(forgotUsername);
                                                                            if (data.moveToNext()) {
                                                                                String id = data.getString(0);
                                                                                boolean isUpdated = databaseHelper.updatePassword(id, forgotPassword);
                                                                                if (isUpdated) {
                                                                                    if (progressDialog.isShowing())
                                                                                        progressDialog.dismiss();
                                                                                    Toast.makeText(ForgotPassWordActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                                                                                    goLoginActivity();
                                                                                }
                                                                            } else {
                                                                                if (progressDialog.isShowing())
                                                                                    progressDialog.dismiss();
                                                                                Toast.makeText(ForgotPassWordActivity.this, "Password changed on cloud", Toast.LENGTH_SHORT).show();
                                                                                goLoginActivity();
                                                                            }
                                                                            data.close();
                                                                        } catch (Exception e) {
                                                                            e.printStackTrace();
                                                                        }
                                                                    } else {
                                                                        if (progressDialog.isShowing())
                                                                            progressDialog.dismiss();
                                                                        Toast.makeText(ForgotPassWordActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                                                                        goLoginActivity();
                                                                    }
                                                                } else {
                                                                    if (progressDialog.isShowing())
                                                                        progressDialog.dismiss();
                                                                    Toast.makeText(ForgotPassWordActivity.this, "Password changed", Toast.LENGTH_SHORT).show();
                                                                    goLoginActivity();
                                                                }


                                                            } else {
                                                                if (progressDialog.isShowing())
                                                                    progressDialog.dismiss();
                                                                Toast.makeText(ForgotPassWordActivity.this, "Password not changed", Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });

                                        } else {
                                            if (progressDialog.isShowing())
                                                progressDialog.dismiss();
                                            edtForgotUsername.setError("Field can't match");
                                            edtForgotPhone.setError("Field can't match");
                                        }

                                    } else if (!forgotUsername.isEmpty()) {
                                        if (progressDialog.isShowing())
                                            progressDialog.dismiss();
                                        edtForgotUsername.setError("Invalid username");
                                    }

                                } else {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                    Toast.makeText(ForgotPassWordActivity.this, "Please check your connection!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    } else {
                        edtForgotConfirmPassword.setError("Enter the same password");
                    }

                } else {
                    Toast.makeText(ForgotPassWordActivity.this, "Connection Lost !", Toast.LENGTH_SHORT).show();
                }

            }
        });

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

    public void goLoginActivity(View view) {
        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);
    }

    public void goLoginActivity() {
        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);
    }

    @Override
    public void onResume() {
        super.onResume();
        handlerForgot.postDelayed(runnable, 1000); // Start the periodic check
    }

    @Override
    public void onPause() {
        super.onPause();
        handlerForgot.removeCallbacks(runnable); // Stop the periodic check
    }
}