package com.designproject.smartorchid.fragment;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.gridlayout.widget.GridLayout;

import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.ChangeTransform;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.designproject.smartorchid.NetworkRecyclerActivity;
import com.designproject.smartorchid.R;

public class NetworkFragment extends Fragment implements View.OnClickListener {

    CardView cardViewDialog, cardViewMobitel, cardViewHutch, cardViewAirtel, cardViewBell, cardViewTelecom;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_network, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        cardViewDialog = view.findViewById(R.id.cardDialog);
        cardViewMobitel = view.findViewById(R.id.cardMobitel);
        cardViewHutch = view.findViewById(R.id.cardHutch);
        cardViewAirtel = view.findViewById(R.id.cardAirtel);
        cardViewBell = view.findViewById(R.id.cardBell);
        cardViewTelecom = view.findViewById(R.id.cardTelecom);

        cardViewDialog.setOnClickListener(this);
        cardViewMobitel.setOnClickListener(this);
        cardViewHutch.setOnClickListener(this);
        cardViewAirtel.setOnClickListener(this);
        cardViewBell.setOnClickListener(this);
        cardViewTelecom.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.cardDialog:
                Intent intentDialog = new Intent(getActivity(), NetworkRecyclerActivity.class);
                startActivity(intentDialog);
                NetworkRecyclerActivity.actionBarTitle = "DIALOG";
                NetworkRecyclerActivity.btn1 = "DATA PACKAGES";
                NetworkRecyclerActivity.btn2 = "VOICE PACKAGES";
                NetworkRecyclerActivity.btn3 = "COMBO PACKAGES";
                break;

            case R.id.cardMobitel:
                Intent intentMobitel = new Intent(getActivity(), NetworkRecyclerActivity.class);
                startActivity(intentMobitel);
                NetworkRecyclerActivity.actionBarTitle = "MOBITEL";
                NetworkRecyclerActivity.btn1 = "DATA PACKAGES";
                NetworkRecyclerActivity.btn2 = "VOICE PACKAGES";
                NetworkRecyclerActivity.btn3 = "SPECIAL PACKAGES";
                break;

            case R.id.cardHutch:
                Intent intentHutch = new Intent(getActivity(), NetworkRecyclerActivity.class);
                startActivity(intentHutch);
                NetworkRecyclerActivity.actionBarTitle = "HUTCH";
                NetworkRecyclerActivity.btn1 = "DATA PACKAGES";
                NetworkRecyclerActivity.btn2 = "VOICE PACKAGES";
                NetworkRecyclerActivity.btn3 = "SPECIAL PACKAGES";
                break;

            case R.id.cardAirtel:
                Intent intentAirtel= new Intent(getActivity(), NetworkRecyclerActivity.class);
                startActivity(intentAirtel);
                NetworkRecyclerActivity.actionBarTitle = "AIRTEL";
                NetworkRecyclerActivity.btn1 = "FREEDOM PACKAGES";
                NetworkRecyclerActivity.btn2 = "BASIC PACKAGES";
                NetworkRecyclerActivity.btn3 = "SPECIAL PACKAGES";
                break;

            case R.id.cardBell:
                Intent intentBell = new Intent(getActivity(), NetworkRecyclerActivity.class);
                startActivity(intentBell);
                NetworkRecyclerActivity.actionBarTitle = "BELL 4G";
                NetworkRecyclerActivity.btn1 = "DATA PACKAGES";
                NetworkRecyclerActivity.btn2 = "PREPAID PACKAGES";
                NetworkRecyclerActivity.btn3 = "POSTPAID PACKAGES";
                break;

            case R.id.cardTelecom:
                Intent intentTelecom = new Intent(getActivity(), NetworkRecyclerActivity.class);
                startActivity(intentTelecom);
                NetworkRecyclerActivity.actionBarTitle = "TELECOM";
                NetworkRecyclerActivity.btn1 = "DATA PACKAGES";
                NetworkRecyclerActivity.btn2 = "VOICE PACKAGES";
                NetworkRecyclerActivity.btn3 = "PEO TV PACKAGES";
                break;

        }
    }
}