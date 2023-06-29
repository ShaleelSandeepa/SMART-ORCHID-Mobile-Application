package com.designproject.smartorchid.activity;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.designproject.smartorchid.R;
import com.designproject.smartorchid.data.DatabaseHelper;
import com.designproject.smartorchid.fragment.MenuFragment;
import com.designproject.smartorchid.fragment.NotificationFragment;
import com.designproject.smartorchid.recycler.ItemOrder;
import com.designproject.smartorchid.recycler.ItemOrderHistory;
import com.designproject.smartorchid.recycler.OrderAdapter;
import com.designproject.smartorchid.recycler.OrderHistoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    TextView orderEmpty;
    RecyclerView recyclerViewOrderHistory;
    List<ItemOrderHistory> orderHistoryList;
    OrderHistoryAdapter orderHistoryAdapter;

    String profileName;
    Cursor ordersCursor, data;
    DatabaseHelper databaseHelper;

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);

        // To set action bar custom view
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        //Change action bar title
        AppCompatTextView textView = findViewById(R.id.title_actionbar);
        textView.setText("Orders");
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

        recyclerViewOrderHistory = findViewById(R.id.recyclerOrderHistory);
        orderEmpty = findViewById(R.id.txtOrderEmpty);

        orderHistoryList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(this);
        ordersCursor = databaseHelper.getAllOrderHistory();

        //set the profile name in order as logged user
        data = databaseHelper.getAllData();
        while (data.moveToNext()) {
            // this will get the account which is logged
            int status = data.getInt(6);
            if (status == 1) {
                //when logout, set the login states of account as 0
                profileName = data.getString(1);
                break;
            }
        }

        //add data of history table in database to List
        while (ordersCursor.moveToNext()) {
            if (ordersCursor.getString(1).contentEquals(profileName)) {
                orderHistoryList.add(new ItemOrderHistory(String.valueOf(ordersCursor.getInt(0)), ordersCursor.getString(2), ordersCursor.getInt(3)));
            }
        }
        if (orderHistoryList.size() == 0) {
            orderEmpty.setVisibility(View.VISIBLE);
        } else {
            orderEmpty.setVisibility(View.GONE);
        }

        orderHistoryAdapter = new OrderHistoryAdapter(this, orderHistoryList, new OrderHistoryAdapter.OnOrderClickListener() {
            @Override
            public void onItemClick(ItemOrderHistory position, int index, String clickType) {

                //when click "COMPLETED" update the color of order history item in database
                if (clickType.equals("txtComplete")) {
                    databaseHelper.updateOrderHistoryColor(Integer.parseInt(orderHistoryList.get(index).getOrderID()), getResources().getColor(R.color.LighterBlue2));
                    recreate();
                    orderHistoryAdapter.notifyDataSetChanged();
                }
            }
        });
//        recyclerViewOrderHistory.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerViewOrderHistory.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
//        recyclerViewOrderHistory.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerViewOrderHistory.setAdapter(orderHistoryAdapter);
//        TransitionManager.beginDelayedTransition(recyclerViewOrderHistory, new Fade());
        orderHistoryAdapter.notifyDataSetChanged();

    }

    //Method for replace fragments
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
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