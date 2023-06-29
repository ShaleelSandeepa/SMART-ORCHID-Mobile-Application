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
import android.widget.Toast;

import com.designproject.smartorchid.BooksRecyclerActivity;
import com.designproject.smartorchid.MyViewModel;
import com.designproject.smartorchid.R;
import com.designproject.smartorchid.data.DatabaseHelper;

import java.util.Objects;

public class BooksFragment extends Fragment implements View.OnClickListener {

    CardView cardViewAtlas, cardViewPromate, cardViewRathna, cardViewSusara, cardViewAkura, cardViewPastPapers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_books, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        cardViewAtlas = view.findViewById(R.id.atlasCardView);
        cardViewPromate = view.findViewById(R.id.promateCardView);
        cardViewRathna = view.findViewById(R.id.rathnaCardView);
        cardViewSusara = view.findViewById(R.id.susaraCardView);
        cardViewAkura = view.findViewById(R.id.akuraCardView);
        cardViewPastPapers = view.findViewById(R.id.pastpaperCardView);

        cardViewAtlas.setOnClickListener(this);
        cardViewPromate.setOnClickListener(this);
        cardViewRathna.setOnClickListener(this);
        cardViewSusara.setOnClickListener(this);
        cardViewAkura.setOnClickListener(this);
        cardViewPastPapers.setOnClickListener(this);

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.atlasCardView:
                Intent intentAtlas = new Intent(getActivity(), BooksRecyclerActivity.class);
                startActivity(intentAtlas);
                BooksRecyclerActivity.actionBarTitleBooks = "Atlas";
                break;

            case R.id.promateCardView:
                Intent intentPromate = new Intent(getActivity(), BooksRecyclerActivity.class);
                startActivity(intentPromate);
                BooksRecyclerActivity.actionBarTitleBooks = "Promate";
                break;

            case R.id.rathnaCardView:
                Intent intentRathna = new Intent(getActivity(), BooksRecyclerActivity.class);
                startActivity(intentRathna);
                BooksRecyclerActivity.actionBarTitleBooks = "Rathna";
                break;

            case R.id.susaraCardView:
                Intent intentSusara = new Intent(getActivity(), BooksRecyclerActivity.class);
                startActivity(intentSusara);
                BooksRecyclerActivity.actionBarTitleBooks = "Susara";
                break;

            case R.id.akuraCardView:
                Intent intentAkura = new Intent(getActivity(), BooksRecyclerActivity.class);
                startActivity(intentAkura);
                BooksRecyclerActivity.actionBarTitleBooks = "Akura";
                break;

            case R.id.pastpaperCardView:
                Intent intentPastPapers = new Intent(getActivity(), BooksRecyclerActivity.class);
                startActivity(intentPastPapers);
                BooksRecyclerActivity.actionBarTitleBooks = "Past Papers";
                break;

        }
    }


}