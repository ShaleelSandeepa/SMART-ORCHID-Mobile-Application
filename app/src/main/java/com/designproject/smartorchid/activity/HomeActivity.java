package com.designproject.smartorchid.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.designproject.smartorchid.MyViewModel;
import com.designproject.smartorchid.R;
import com.designproject.smartorchid.ViewPagerAdapter;
import com.designproject.smartorchid.data.DatabaseHelper;
import com.designproject.smartorchid.fragment.AddFragment;
import com.designproject.smartorchid.fragment.BooksFragment;
import com.designproject.smartorchid.fragment.CartFragment;
import com.designproject.smartorchid.fragment.HomeFragment;
import com.designproject.smartorchid.fragment.MenuFragment;
import com.designproject.smartorchid.fragment.NotificationFragment;
import com.designproject.smartorchid.fragment.StationariesFragment;
import com.designproject.smartorchid.recycler.CartDialog;
import com.designproject.smartorchid.recycler.ItemNotification;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity{

    // declare variables for tab bar and bottom navigation bar
    BottomNavigationView bottomNavigationView;
    BadgeDrawable badgeDrawableCart, badgeDrawableNotification;
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    ViewPagerAdapter viewPagerAdapter;
    FrameLayout frameLayout;
    MenuItem menuItemCart, menuItemNotification;
    DatabaseHelper databaseHelper;
    Cursor itemsCart, itemsNotification;
    int accountID;
    int cartCount, notificationCount;

    private MyViewModel myViewModelCart;
    private MyViewModel myViewModelNotification;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // To set action bar custom view
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        //Change action bar title
        AppCompatTextView textView = findViewById(R.id.title_actionbar);
        textView.setText("HOME");
        //change the status bar color
        getWindow().setStatusBarColor(getColor(R.color.DodgerBlue));
        //change the status bar to dark theme
        getWindow().getDecorView().setSystemUiVisibility(0);
        //Remove the action bar shadow
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setElevation(0);
        }

        // set variables for tab bar and bottom navigation bar
        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(viewPagerAdapter);
        bottomNavigationView = findViewById(R.id.bottomNav);
        frameLayout = findViewById(R.id.frameLayout);

        // Create a BadgeDrawable for the menu item
        menuItemCart = bottomNavigationView.getMenu().findItem(R.id.cart);
        badgeDrawableCart = bottomNavigationView.getOrCreateBadge(menuItemCart.getItemId());
        menuItemNotification = bottomNavigationView.getMenu().findItem(R.id.notification);
        badgeDrawableNotification = bottomNavigationView.getOrCreateBadge(menuItemNotification.getItemId());

        // get the id of logged in account
        databaseHelper = new DatabaseHelper(this);
        Cursor data = databaseHelper.getLoggedInAccount();
        while (data.moveToNext()) {
            //when logout, set the login states of account as 0
            accountID = data.getInt(0);
        }

        //////////////////// those are set the badge number at the beginning //////////////
        itemsCart = databaseHelper.getCartItemDetails(accountID);
        badgeDrawableCart.setNumber(itemsCart.getCount());
        itemsNotification = databaseHelper.getNotificationByAccountID(accountID);
        //get the notifications by account ID
        if (itemsNotification.getCount() == 0) {
            badgeDrawableNotification.setNumber(0);

        } else if (itemsNotification.moveToLast()) {
            int unread = 0;
            do {
                if (itemsNotification.getString(4).equals("unread")) {
                    unread++;
                }
            } while (itemsNotification.moveToPrevious());
            badgeDrawableNotification.setNumber(unread);
        }
        itemsNotification.close();
        ///////////////////////////////////////////////////////////////////////////////////

        myViewModelCart = new ViewModelProvider(this).get(MyViewModel.class);
        myViewModelNotification = new ViewModelProvider(this).get(MyViewModel.class);

        // Observe the myViewModelCart LiveData object
        myViewModelCart.getCartCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                cartCount = integer;
                badgeDrawableCart.setNumber(cartCount);
            }
        });
        // Observe the myViewModelNotification LiveData object
        myViewModelNotification.getNotificationCount().observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                notificationCount = integer;
                badgeDrawableNotification.setNumber(notificationCount);
            }
        });

        // Implementation of tab navigation
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.GONE);
                viewPager2.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager2.setVisibility(View.VISIBLE);
                frameLayout.setVisibility(View.GONE);
            }
        });

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                switch (position){
                    case 0:
                    case 1:
                    case 2:
                    case 3:
                        Objects.requireNonNull(tabLayout.getTabAt(position)).select();
                }
                super.onPageSelected(position);
            }
        });

        // Implementation of bottom navigation
        replaceFragment(new HomeFragment());
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceID")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                frameLayout.setVisibility(View.VISIBLE);
                viewPager2.setVisibility(View.GONE);

                switch (item.getItemId()){
                    case R.id.home:
                        replaceFragment(new BooksFragment());
                        tabLayout.setVisibility(View.VISIBLE);
                        textView.setText("HOME");
                        Objects.requireNonNull(tabLayout.getTabAt(0)).select();
                        return true;
                    case R.id.cart:
                        replaceFragment(new CartFragment());
                        tabLayout.setVisibility(View.GONE);
                        textView.setText("CART");
                        return true;
                    case R.id.add:
                        replaceFragment(new AddFragment());
                        tabLayout.setVisibility(View.GONE);
                        textView.setText("MAKE ORDER");
                        return true;
                    case R.id.notification:
                        replaceFragment(new NotificationFragment());
                        tabLayout.setVisibility(View.GONE);
                        textView.setText("NOTIFICATIONS");
                        return true;
                    case R.id.menu:
                        replaceFragment(new MenuFragment());
                        tabLayout.setVisibility(View.GONE);
                        textView.setText("MENU");
                        return true;

                }
                return false;
            }
        });
    }

    //Method for replace fragments
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    //When try to back from Login page this method will execute
    @Override
    public void onBackPressed() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Exit App");
        alertDialog.setMessage("Do you want to exit Smart Orchid ?");

        //When click "Yes" it will execute this
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
                onStop();
                onDestroy();
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
    }

}