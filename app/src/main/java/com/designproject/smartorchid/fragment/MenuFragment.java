package com.designproject.smartorchid.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.designproject.smartorchid.MyViewModel;
import com.designproject.smartorchid.activity.AboutUsActivity;
import com.designproject.smartorchid.activity.DeliveryActivity;
import com.designproject.smartorchid.activity.LoginActivity;
import com.designproject.smartorchid.activity.OrdersActivity;
import com.designproject.smartorchid.R;
import com.designproject.smartorchid.activity.PaymentActivity;
import com.designproject.smartorchid.activity.ProfileActivity;
import com.designproject.smartorchid.activity.SettingsActivity;
import com.designproject.smartorchid.data.DatabaseHelper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class MenuFragment extends Fragment implements View.OnClickListener {

    CardView orders, payments, delivery, settings, about, logout;
    DatabaseHelper databaseHelper;
    Cursor data;
    TextView profileName, viewProfile;
    ImageView profilePicture;

    MyViewModel myViewModelNotification;
    int accountID;

    StorageReference storageReference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(getContext());

        profilePicture = view.findViewById(R.id.profilePicture);

        //set the profile name in menu as logged user
        profileName = (TextView) view.findViewById(R.id.profileName);
        data = databaseHelper.getAllData();
        while (data.moveToNext()) {
            // this will get the account which is logged
            int status = data.getInt(6);
            if (status == 1) {
                //when logout, set the login states of account as 0
                String name = data.getString(1);
                profileName.setText(name);
                break;
            }
        }

        orders = view.findViewById(R.id.orders);
        payments = view.findViewById(R.id.payments);
        delivery = view.findViewById(R.id.delivery);
        settings = view.findViewById(R.id.settings);
        about = view.findViewById(R.id.about);
        logout = view.findViewById(R.id.logout);
        viewProfile = view.findViewById(R.id.viewProfile);

        //when click the each item in menu, it will go to relevant screen
        orders.setOnClickListener(this);
        payments.setOnClickListener(this);
        delivery.setOnClickListener(this);
        settings.setOnClickListener(this);
        about.setOnClickListener(this);
        logout.setOnClickListener(this);
        viewProfile.setOnClickListener(this);

        retrieveImageFromFirebase();

    }

    // THIS ONLY USE FOR WHEN WANT TO MOVE TO THE FRAGMENT FILE
    //Method for replace fragments
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.commit();
    }
    public void goLogin() {
        Intent intentLogin = new Intent(getActivity(), LoginActivity.class);
        startActivity(intentLogin);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.orders:
                Intent intentOrders = new Intent(getActivity(), OrdersActivity.class);
                startActivity(intentOrders);
                break;

            case R.id.payments:
                Intent intentPayments = new Intent(getActivity(), PaymentActivity.class);
                startActivity(intentPayments);
                break;

            case R.id.delivery:
                Intent intentDelivery = new Intent(getActivity(), DeliveryActivity.class);
                startActivity(intentDelivery);
                break;

            case R.id.settings:
                Intent intentSettings = new Intent(getActivity(), SettingsActivity.class);
                startActivity(intentSettings);
                break;

            case R.id.about:
                Intent intentAbout = new Intent(getActivity(), AboutUsActivity.class);
                startActivity(intentAbout);
                break;

            case R.id.logout:
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Logout");
                alertDialog.setMessage("Do you want to logout Smart Orchid ?");

                //When click "Yes" it will execute this
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        data = databaseHelper.getAllData();
                        while (data.moveToNext()) {
                            // this will get the account which is logged
                            int status = data.getInt(6);
                            if (status == 1) {
                                //when logout, set the login states of account as 0
                                databaseHelper.updateLoginStatus(data.getString(0), 0);
                                break;
                            }
                        }

                        goLogin();
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
                break;

            case R.id.viewProfile:
                Intent intentProfile = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intentProfile);
                break;
        }
    }

    // this will get the profile picture of logged user account
    public void retrieveImageFromFirebase() {

        storageReference = FirebaseStorage.getInstance().getReference("userProfileImages/"+profileName.getText());

        //get picture from firebase storage
        storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {

                // set the image as profile picture in menu interface.
                String image = uri.toString();
                Picasso.get().load(image).into(profilePicture);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @SuppressLint("UseCompatLoadingForDrawables")
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        // This code will be executed when returning to the fragment

        Cursor data = databaseHelper.getLoggedInAccount();
        if (data.moveToFirst()) {
            accountID = data.getInt(0);
        }

        //get the unread notification count.
        data = databaseHelper.getNotificationByAccountID(accountID);
        if (data.moveToLast()) {
            int unread = 0;
            do {
                if (data.getString(4).equals("unread")) {
                    unread++;
                }
            } while (data.moveToPrevious());
            //set the count via view model
            myViewModelNotification = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
            myViewModelNotification.setNotificationCount(unread);
        }
        data.close();
    }


}