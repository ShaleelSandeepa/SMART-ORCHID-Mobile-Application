package com.designproject.smartorchid.activity;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
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

public class RegisterActivity extends AppCompatActivity {

    //CONNECTIVITY RELATED VARIABLES
    LinearLayout linearLayoutNoConnection;
    AlphaAnimation blinkAnimation;
    ImageView imgWifiOff;
    ConnectivityManager connectivityManager;
    Handler handler;
    Runnable runnable;

    DatabaseHelper databaseHelper;
    TextInputLayout edtUserName, edtEmail, edtPhone, edtPassword, edtConfirmPassword;
    Button btnRegister;
    String userName, email, phone, password, confirmPassword;
    boolean validity = false;
    private boolean isUserExist = false;

    Cursor data;

    //FIREBASE VARIABLE
    FirebaseFirestore firestore;
    Map<String, Object> users;

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
        setContentView(R.layout.activity_register);

        //FIREBASE DECLARATION
        firestore = FirebaseFirestore.getInstance();
        users = new HashMap<>();

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

        databaseHelper = new DatabaseHelper(this);

        btnRegister = (Button) findViewById(R.id.btnRegister);

        edtUserName = (TextInputLayout) findViewById(R.id.regUserName);
        edtEmail = (TextInputLayout) findViewById(R.id.regEmail);
        edtPhone = (TextInputLayout) findViewById(R.id.forgotPhone);
        edtPassword = (TextInputLayout) findViewById(R.id.forgotPassword);
        edtConfirmPassword = (TextInputLayout) findViewById(R.id.forgotConfirmPassword);

        Objects.requireNonNull(edtUserName.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                userName = edtUserName.getEditText().getText().toString();
                if (!userName.isEmpty()){
                    checkFireStore(userName);
                } else {
                    validUserName(userName);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(edtEmail.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                email = edtEmail.getEditText().getText().toString();
                validateEmail(email);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(edtPhone.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                phone = edtPhone.getEditText().getText().toString();
                validPhone(phone);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(edtPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                password = edtPassword.getEditText().getText().toString();
                validatePassword(password);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        Objects.requireNonNull(edtConfirmPassword.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                confirmPassword = edtConfirmPassword.getEditText().getText().toString();
                password = edtPassword.getEditText().getText().toString();
                confirmPassword(confirmPassword, password);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        registerUser();
    }

    public void goLogin() {
        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);
    }

    public void registerUser() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // CHECK THE CONNECTION IS AVAILABLE
                if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()) {

                    userName = edtUserName.getEditText().getText().toString();
                    email = edtEmail.getEditText().getText().toString();
                    phone = edtPhone.getEditText().getText().toString();
                    password = edtPassword.getEditText().getText().toString();
                    confirmPassword = edtConfirmPassword.getEditText().getText().toString();

                    Boolean validUserName = validUserName(userName);
                    Boolean validEmail = validateEmail(email);
                    Boolean validPhone = validPhone(phone);
                    Boolean validPassword = validatePassword(password);
                    Boolean validConfirm = confirmPassword(confirmPassword, password);

                    //If enter valid inputs then run the codes below
                    if (validUserName && validEmail && validPhone && validPassword && validConfirm) {

                        //////////////////////  ADD USERS TO FIREBASE DATABASE ////////////////////
                        users.put("userName", userName);
                        users.put("email", email);
                        users.put("phone", phone);
                        users.put("password", password);

                        //Check the user data is entered the fire store
                        firestore.collection("users").document(userName).set(users).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "Registration successful", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(RegisterActivity.this, "Account added to cloud store", Toast.LENGTH_SHORT).show();

                                    //////////////////////  ADD USERS TO SQLITE DATABASE ////////////////////
                                    boolean isInsertedToSQLite = databaseHelper.registerUser(userName, email, phone, password);
                                    if (isInsertedToSQLite) {
                                        goLogin();
                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Failed to added local database", Toast.LENGTH_SHORT).show();
                                    }

                                    // If not data added to firebase fireStore those are toasting.
                                } else {
                                    Toast.makeText(RegisterActivity.this, "Registration fail", Toast.LENGTH_SHORT).show();
                                    Toast.makeText(RegisterActivity.this, "Please Check Your Connection !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    } else {
                        Toast.makeText(RegisterActivity.this, "Validation failed!", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    Toast.makeText(RegisterActivity.this, "Connection Lost !", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //validate the email is in standard form
    public boolean validateEmail(String email) {
        if (email.isEmpty()) {
            edtEmail.setError("Field can't be empty");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Please enter a valid email address");
            return false;
        } else {
            edtEmail.setError(null);
            validity = true;
            return true;
        }
    }

    //validate password if it is in strong password
    public boolean validatePassword(String password) {
        if (password.isEmpty()) {
            edtPassword.setError("Field can't be empty");
            return false;
        } else if (!PASSWORD_PATTERN.matcher(password).matches()) {
            edtPassword.setError("Password too week");
            return false;
        } else {
            edtPassword.setError(null);
            validity = true;
            return true;
        }
    }

    //check the password and confirm password are same
    public boolean confirmPassword(String confirmPassword, String password) {
        if (!confirmPassword.equals(password)) {
            edtConfirmPassword.setError("Please enter the same password");
            return false;
        } else {
            edtConfirmPassword.setError(null);
            validity = true;
            return true;
        }
    }

    public boolean validUserName(String userName) {
        if (userName.isEmpty()) {
            edtUserName.setError("Field can't be empty");
            return false;
        } else if (!userName.equals(userName.trim())) {
            edtUserName.setError("Remove white spaces");
            return false;
        } else if (userName.length() > 20) {
            edtUserName.setError("User name too long");
            return false;
        } else if (databaseHelper.checkUserName(userName)) {
            edtUserName.setError("User name already exist");
            return false;
        } else if (isUserExist()) {
            edtUserName.setError("User name already exist");
            return false;
        } else {
            edtUserName.setError(null);
            return true;
        }
    }

    public boolean validPhone(String phone) {
        if (phone.isEmpty()) {
            edtPhone.setError("Field can't be empty");
            return false;
        } else if (!PHONE_PATTERN.matcher(phone).matches()) {
            edtPhone.setError("Invalid number");
            return false;
        } else {
            edtPhone.setError(null);
            return true;
        }
    }

    public void goLoginActivity(View view) {
        Intent intentLogin = new Intent(this, LoginActivity.class);
        startActivity(intentLogin);
    }

    public boolean isUserExist() {
        return isUserExist;
    }

    public void setUserExist(boolean userExist) {
        isUserExist = userExist;
    }

    //check the username is already exist in the firestore database
    public void checkFireStore(String userName) {
        DocumentReference docRef = firestore.collection("users").document(userName);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if (documentSnapshot.exists()){
                        setUserExist(true);
                    } else {
                        setUserExist(false);
                    }
                }
                validUserName(userName);
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