package com.designproject.smartorchid.fragment;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.designproject.smartorchid.R;

public class CommunicationFragment extends Fragment implements View.OnClickListener {

    LinearLayout printoutDetails, photocopyDetails, laminatingDetails;
    LinearLayout layoutPrint, layoutPhotocopy, layoutLaminating;
    CardView cardPrint, cardWhatsapp, cardPhotocopy, cardLaminating;
    String phoneNumber;
    ImageView dropDownUpPrint, dropDownUpPhotocopy, dropDownUpLaminating;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_communication, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardWhatsapp = view.findViewById(R.id.cardWhatsapp);
        phoneNumber = "+94764859595";

        cardPrint = view.findViewById(R.id.CardPrint);
        printoutDetails = view.findViewById(R.id.details);
        layoutPrint = view.findViewById(R.id.linearPrint);
        layoutPrint.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        dropDownUpPrint = view.findViewById(R.id.printoutDropDownUp);

        cardPhotocopy = view.findViewById(R.id.CardPhotocopy);
        photocopyDetails = view.findViewById(R.id.detailsPhotocopy);
        layoutPhotocopy = view.findViewById(R.id.linearPhotocopy);
        layoutPhotocopy.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        dropDownUpPhotocopy = view.findViewById(R.id.photocopyDropDownUp);

        cardLaminating = view.findViewById(R.id.CardLaminating);
        laminatingDetails = view.findViewById(R.id.detailsLaminating);
        layoutLaminating = view.findViewById(R.id.linearLaminating);
        layoutLaminating.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        dropDownUpLaminating = view.findViewById(R.id.laminatingDropDownUp);

        cardWhatsapp.setOnClickListener(this);
        cardPrint.setOnClickListener(this);
        cardPhotocopy.setOnClickListener(this);
        cardLaminating.setOnClickListener(this);

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

    private void clickCommunicationItem(LinearLayout Details, LinearLayout layout, ImageView dropDownUp) {
        //Handle communication option click here
        if (Details.getVisibility() == View.GONE) {
            Details.setVisibility(View.VISIBLE);
            dropDownUp.setImageResource(R.drawable.ic_round_arrow_drop_up);
        } else {
            Details.setVisibility(View.GONE);
            dropDownUp.setImageResource(R.drawable.ic_round_arrow_drop_down);
        }
        TransitionManager.beginDelayedTransition(layout, new AutoTransition());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cardWhatsapp:
                PackageManager packageManager = requireContext().getPackageManager();
                //check the whatsapp is installed and intent
                if (isWhatsappInstalled(packageManager)) {
                    Intent goToWhatsapp = new Intent(Intent.ACTION_VIEW);
                    goToWhatsapp.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber));
                    goToWhatsapp.setPackage("com.whatsapp");
                    startActivity(goToWhatsapp);

                    //check the whatsapp business app is installed and intent
                } else if (isWhatsappBusinessInstalled(packageManager)) {
                    Intent goToWhatsappBusiness = new Intent(Intent.ACTION_VIEW);
                    goToWhatsappBusiness.setData(Uri.parse("https://wa.me/" + phoneNumber));
                    goToWhatsappBusiness.setPackage("com.whatsapp.w4b");
                    startActivity(goToWhatsappBusiness);

                } else {
                    try {
                        ApplicationInfo applicationInfo = packageManager.getApplicationInfo("com.whatsapp.w4b", 0);
                        String whatsappPackageName = applicationInfo.packageName;
                        // The variable "whatsappPackageName" now holds the package name of WhatsApp
                        Toast.makeText(getContext(), whatsappPackageName, Toast.LENGTH_SHORT).show();

                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case R.id.CardPrint:
                clickCommunicationItem(printoutDetails, layoutPrint, dropDownUpPrint);
                break;

            case R.id.CardPhotocopy:
                clickCommunicationItem(photocopyDetails, layoutPhotocopy, dropDownUpPhotocopy);
                break;

            case R.id.CardLaminating:
                clickCommunicationItem(laminatingDetails, layoutLaminating, dropDownUpLaminating);
                break;
        }
    }
}