package com.designproject.smartorchid.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.designproject.smartorchid.R;
import com.designproject.smartorchid.data.DatabaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileActivity extends AppCompatActivity {

    TextView txtUserFullName, txtUserGender, txtUserMobile, txtUserTel, txtUserAddress, txtUserPostalCode;
    EditText edtUserFullName, edtUserGender, edtUserMobile, edtUserTel, edtUserAddress, edtUserPostalCode;
    String userFullName, userGender, userMobile, userTel, userAddress, userPostalCode;
    Button editDetails;
    ImageView profilePicture, profilePictureEdit;
    TextView userName,userEmail;
    int accountID;
    String accountUserName;

    DatabaseHelper databaseHelper;
    Cursor cursor;

    Uri imageUri;
    StorageReference storageReference;
    ProgressDialog progressDialog;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // To set action bar custom view
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        //Change action bar title
        AppCompatTextView textView = findViewById(R.id.title_actionbar);
        textView.setText("USER PROFILE");
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

        userName = findViewById(R.id.userName);
        userEmail = findViewById(R.id.userEmail);

        editDetails = findViewById(R.id.editDetails);
        profilePicture = findViewById(R.id.profilePicture);
        profilePictureEdit = findViewById(R.id.profilePictureEdit);

        txtUserFullName = findViewById(R.id.txtUserFullName);
        txtUserGender = findViewById(R.id.txtUserGender);
        txtUserMobile = findViewById(R.id.txtUserMobile);
        txtUserTel = findViewById(R.id.txtUserTel);
        txtUserAddress = findViewById(R.id.txtUserAddress);
        txtUserPostalCode = findViewById(R.id.txtUserPostalCode);

        edtUserFullName = findViewById(R.id.edtUserFullName);
        edtUserGender = findViewById(R.id.edtUserGender);
        edtUserMobile = findViewById(R.id.edtUserMobile);
        edtUserTel = findViewById(R.id.edtUserTel);
        edtUserAddress = findViewById(R.id.edtUserAddress);
        edtUserPostalCode = findViewById(R.id.edtUserPostalCode);

        //check the logged in account and get ID and Username
        cursor = databaseHelper.getLoggedInAccount();
        if (cursor.moveToNext()) {
            accountID = cursor.getInt(0);
            accountUserName = cursor.getString(1);
            userName.setText(String.valueOf(cursor.getString(1)));
            userEmail.setText(String.valueOf(cursor.getString(2)));
            //user can not update registered mobile here, so default set mobile as registered phone
            txtUserMobile.setText(String.valueOf(cursor.getString(3)));
            userMobile = String.valueOf(cursor.getString(3));
        }

        // get the user profile details by ID
        Cursor data = databaseHelper.getUserProfileById(accountID);
        if (data.getCount() != 0) {
            if (data.moveToNext()) {
                userFullName = String.valueOf(data.getString(1));
                userGender = String.valueOf(data.getString(2));
//                userMobile = String.valueOf(data.getString(4));
                userTel = String.valueOf(data.getString(3));
                userAddress = String.valueOf(data.getString(4));
                userPostalCode = String.valueOf(data.getString(5));
                displayProfile();
            }
        }

        //when on create the user profile activity, it will retrieve user image if it is in firebase.
        retrieveImageFromFirebase();

        profilePictureEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, 222);
            }
        });

        // user can not change registered mobile number, when click it, it will generate toast message.
        txtUserMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ProfileActivity.this, "Can not change registered mobile number !", Toast.LENGTH_SHORT).show();
            }
        });

        editDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editDetails.getText().equals("Edit Details")) {
                    txtUserFullName.setVisibility(View.GONE);
                    txtUserGender.setVisibility(View.GONE);
//                    txtUserMobile.setVisibility(View.GONE); // user can not change registered mobile number
                    txtUserTel.setVisibility(View.GONE);
                    txtUserAddress.setVisibility(View.GONE);
                    txtUserPostalCode.setVisibility(View.GONE);

                    edtUserFullName.setVisibility(View.VISIBLE);
                    edtUserGender.setVisibility(View.VISIBLE);
//                    edtUserMobile.setVisibility(View.VISIBLE); // user can not change registered mobile number
                    edtUserTel.setVisibility(View.VISIBLE);
                    edtUserAddress.setVisibility(View.VISIBLE);
                    edtUserPostalCode.setVisibility(View.VISIBLE);

                    if (!txtUserFullName.getText().equals("-"))
                        edtUserFullName.setText(txtUserFullName.getText());
                    if (!txtUserGender.getText().equals("-"))
                        edtUserGender.setText(txtUserGender.getText());
//                    if (!txtUserMobile.getText().equals("-")) // user can not change registered mobile number
//                        edtUserMobile.setText(txtUserMobile.getText());
                    if (!txtUserTel.getText().equals("-"))
                        edtUserTel.setText(txtUserTel.getText());
                    if (!txtUserAddress.getText().equals("-"))
                        edtUserAddress.setText(txtUserAddress.getText());
                    if (!txtUserPostalCode.getText().equals("-"))
                        edtUserPostalCode.setText(txtUserPostalCode.getText());

                    editDetails.setText("Save Details");
                } else if (editDetails.getText().equals("Save Details")) {

                    userFullName = String.valueOf(edtUserFullName.getText());
                    userGender = String.valueOf(edtUserGender.getText());
//                    userMobile = String.valueOf(edtUserMobile.getText()); // user can not change registered mobile number
                    userTel = String.valueOf(edtUserTel.getText());
                    userAddress = String.valueOf(edtUserAddress.getText());
                    userPostalCode = String.valueOf(edtUserPostalCode.getText());

                    Cursor data = databaseHelper.getUserProfileById(accountID);
                    if (data.getCount() == 0) {
                        boolean isInsert = databaseHelper.insertProfileDetails(accountID, userFullName, userGender, userTel, userAddress, userPostalCode);
                        if (isInsert) {
                            displayProfile();
                            Toast.makeText(ProfileActivity.this, "Details saved", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Details not saved !", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        boolean isUpdate = databaseHelper.updateProfileDetails(accountID, userFullName, userGender, userTel, userAddress, userPostalCode);
                        if (isUpdate) {
                            displayProfile();
                            Toast.makeText(ProfileActivity.this, "Details updated", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Details not update !", Toast.LENGTH_SHORT).show();
                        }
                    }

                }
            }
        });

    }

    //display profile details in user profile interface.
    public void displayProfile() {
        if (userFullName.equals("") || userFullName.equals("null")) {
            txtUserFullName.setText("-");
        } else {
            txtUserFullName.setText(userFullName);
        }

        if (userGender.equals("") || userGender.equals("null")) {
            txtUserGender.setText("-");
        } else {
            txtUserGender.setText(userGender);
        }

        // user can not change registered mobile number, that is why this code was commented.
        // if want to allow to change mobile, this code should uncomment.
//        if (userMobile.equals("")) {
//            txtUserMobile.setText("-");
//        } else {
//            txtUserMobile.setText(userMobile);
//        }

        if (userTel.equals("") || userTel.equals("null")) {
            txtUserTel.setText("-");
        } else {
            txtUserTel.setText(userTel);
        }

        if (userAddress.equals("") || userAddress.equals("null")) {
            txtUserAddress.setText("-");
        } else {
            txtUserAddress.setText(userAddress);
        }

        if (userPostalCode.equals("") || userPostalCode.equals("null")) {
            txtUserPostalCode.setText("-");
        } else {
            txtUserPostalCode.setText(userPostalCode);
        }

        txtUserFullName.setVisibility(View.VISIBLE);
        txtUserGender.setVisibility(View.VISIBLE);
//        txtUserMobile.setVisibility(View.VISIBLE); // user can not change registered mobile number
        txtUserTel.setVisibility(View.VISIBLE);
        txtUserAddress.setVisibility(View.VISIBLE);
        txtUserPostalCode.setVisibility(View.VISIBLE);

        edtUserFullName.setVisibility(View.GONE);
        edtUserGender.setVisibility(View.GONE);
//        edtUserMobile.setVisibility(View.GONE); // user can not change registered mobile number
        edtUserTel.setVisibility(View.GONE);
        edtUserAddress.setVisibility(View.GONE);
        edtUserPostalCode.setVisibility(View.GONE);

        editDetails.setText("Edit Details");
    }

    //this is call back after select a photo from gallery.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 222 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            profilePicture.setImageURI(imageUri);
            try {
                //upload image to firebase fire store
                uploadImageToFirebase();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // this method will run when after choose image to upload to fire base.
    public void uploadImageToFirebase() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading file");
        progressDialog.show();

        storageReference = FirebaseStorage.getInstance().getReference("userProfileImages/"+accountUserName);

        storageReference.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        Toast.makeText(ProfileActivity.this, "Successfully uploaded", Toast.LENGTH_SHORT).show();
                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (progressDialog.isShowing())
                            progressDialog.dismiss();
                        Toast.makeText(ProfileActivity.this, "Failed to upload", Toast.LENGTH_SHORT).show();
                    }
                });

    }

    public void retrieveImageFromFirebase() {

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Loading...");
        progressDialog.show();

        storageReference = FirebaseStorage.getInstance().getReference("userProfileImages/"+accountUserName);

        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                String image = uri.toString();
                Picasso.get().load(image).into(profilePicture);
                if (progressDialog.isShowing())
                    progressDialog.dismiss();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onFailure(@NonNull Exception e) {

                if (progressDialog.isShowing())
                    progressDialog.dismiss();
                profilePicture.setImageDrawable(getResources().getDrawable(R.drawable.ic_user2));

            }
        });

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