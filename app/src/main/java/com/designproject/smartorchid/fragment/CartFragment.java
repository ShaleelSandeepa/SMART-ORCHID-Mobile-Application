package com.designproject.smartorchid.fragment;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.designproject.smartorchid.BooksRecyclerActivity;
import com.designproject.smartorchid.MyViewModel;
import com.designproject.smartorchid.R;
import com.designproject.smartorchid.StationariesRecyclerActivity;
import com.designproject.smartorchid.activity.HomeActivity;
import com.designproject.smartorchid.data.DatabaseHelper;
import com.designproject.smartorchid.recycler.Item;
import com.designproject.smartorchid.recycler.MyAdapter;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class CartFragment extends Fragment implements BooksRecyclerActivity.ItemPositionListener, StationariesRecyclerActivity.ItemPositionListener {

    List<Item> cartItems;
    Item position;
    Cursor items, data;
    DatabaseHelper databaseHelper;
    MyAdapter adapter;
    TextView txtCartEmpty;
    int accountID;

    MyViewModel myViewModelCart;


    public Item getPosition() {
        return position;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        getParentFragmentManager().setFragmentResultListener("dataFromAddFragment", this, new FragmentResultListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle result) {

            }
        });

        return view;
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txtCartEmpty = view.findViewById(R.id.txtCartEmpty);

        RecyclerView recyclerCartView = view.findViewById(R.id.recyclerCartView);
        cartItems =new ArrayList<>();

        databaseHelper = new DatabaseHelper(getContext());
        displayCartItems(); //display the all cart items

        adapter = new MyAdapter(getContext(), cartItems, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item position, String clickType) {

                replaceFragment(new CartFragment());
                adapter.notifyDataSetChanged();
            }
        });
        recyclerCartView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCartView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void setPosition(Item position) {
        this.position = position;
    }

    //display the all cart items exist in cart table
    @SuppressLint("NotifyDataSetChanged")
    public void displayCartItems() {

        data = databaseHelper.getAllData();
        while (data.moveToNext()) {
            // this will get the account which is logged
            int status = data.getInt(6);
            if (status == 1) {
                //when logout, set the login states of account as 0
                accountID = data.getInt(0);
                break;
            }
        }

        // this will get cart details in local database
        items = databaseHelper.getCartItemDetails(accountID);
        if (items.getCount() == 0) {
            txtCartEmpty.setVisibility(View.VISIBLE);
        } else {
            txtCartEmpty.setVisibility(View.GONE);
            while (items.moveToNext()) {
                cartItems.add(new Item(Item.CART, items.getInt(2), items.getString(3), items.getString(4), items.getInt(5), items.getInt(6), items.getString(7)));
            }
        }

        myViewModelCart = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
        myViewModelCart.setCartCount(items.getCount());
    }

    //Method for replace fragments
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

}