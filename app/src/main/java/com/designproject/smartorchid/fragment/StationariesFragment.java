package com.designproject.smartorchid.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.designproject.smartorchid.MyViewModel;
import com.designproject.smartorchid.R;
import com.designproject.smartorchid.StationariesRecyclerActivity;
import com.designproject.smartorchid.data.DatabaseHelper;

public class StationariesFragment extends Fragment implements View.OnClickListener {

    CardView pensCardView, colorProductsCardView, schoolProductsCardView, officeProductsCardView, highlighterMarkerCardView, paperProductsCardView;
    MyViewModel myViewModelCart;
    DatabaseHelper databaseHelper;
    int accountID;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stationaries, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        databaseHelper = new DatabaseHelper(getContext());

        pensCardView = view.findViewById(R.id.pensCardView);
        colorProductsCardView = view.findViewById(R.id.colorProductsCardView);
        schoolProductsCardView = view.findViewById(R.id.schoolProductsCardView);
        officeProductsCardView = view.findViewById(R.id.officeProductsCardView);
        highlighterMarkerCardView = view.findViewById(R.id.highlighterMarkerCardView);
        paperProductsCardView = view.findViewById(R.id.paperProductsCardView);

        pensCardView.setOnClickListener(this);
        colorProductsCardView.setOnClickListener(this);
        schoolProductsCardView.setOnClickListener(this);
        officeProductsCardView.setOnClickListener(this);
        highlighterMarkerCardView.setOnClickListener(this);
        paperProductsCardView.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pensCardView:
                Intent intentPens = new Intent(getActivity(), StationariesRecyclerActivity.class);
                startActivity(intentPens);
                StationariesRecyclerActivity.actionBarTitleStationaries = "Pens";
                break;

            case R.id.colorProductsCardView:
                Intent intentColor = new Intent(getActivity(), StationariesRecyclerActivity.class);
                startActivity(intentColor);
                StationariesRecyclerActivity.actionBarTitleStationaries = "Color Products";
                break;

            case R.id.schoolProductsCardView:
                Intent intentSchool = new Intent(getActivity(), StationariesRecyclerActivity.class);
                startActivity(intentSchool);
                StationariesRecyclerActivity.actionBarTitleStationaries = "School Products";
                break;

            case R.id.officeProductsCardView:
                Intent intentOffice = new Intent(getActivity(), StationariesRecyclerActivity.class);
                startActivity(intentOffice);
                StationariesRecyclerActivity.actionBarTitleStationaries = "Office Products";
                break;

            case R.id.highlighterMarkerCardView:
                Intent intentHighlighter = new Intent(getActivity(), StationariesRecyclerActivity.class);
                startActivity(intentHighlighter);
                StationariesRecyclerActivity.actionBarTitleStationaries = "HighLighter & Markers";
                break;

            case R.id.paperProductsCardView:
                Intent intentPapers = new Intent(getActivity(), StationariesRecyclerActivity.class);
                startActivity(intentPapers);
                StationariesRecyclerActivity.actionBarTitleStationaries = "Papers Products";
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // get the id of logged in account
        Cursor data = databaseHelper.getLoggedInAccount();
        while (data.moveToNext()) {
            //when logout, set the login states of account as 0
            accountID = data.getInt(0);
        }
        data.close();

        Cursor itemsCart = databaseHelper.getCartItemDetails(accountID);
        myViewModelCart = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        myViewModelCart.setCartCount(itemsCart.getCount());
        itemsCart.close();
    }
}