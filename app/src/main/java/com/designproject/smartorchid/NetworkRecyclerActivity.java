package com.designproject.smartorchid;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.AutoTransition;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.transition.ChangeBounds;
import android.transition.ChangeClipBounds;
import android.transition.ChangeScroll;
import android.transition.ChangeTransform;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.designproject.smartorchid.recycler.Item;
import com.designproject.smartorchid.recycler.MyAdapter;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class NetworkRecyclerActivity extends AppCompatActivity implements View.OnClickListener {

    public static String actionBarTitle;
    public static String btn1, btn2, btn3;

    public int activeBtn = 1;
    int visibilityCount;

    List<Item> itemsPackages1, itemsPackages2, itemsPackages3;
    MyAdapter adapter1, adapter2, adapter3;
    Button button1, button2, button3;
    RecyclerView recyclerPackageView1, recyclerPackageView2, recyclerPackageView3;

    CollectionReference collectionReference;
    ProgressDialog progressDialog;
    Timer timer;
    boolean isToastRunning;

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_network_recycler);

        // To set action bar custom view
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        //Change action bar title
        AppCompatTextView textView = findViewById(R.id.title_actionbar);
        textView.setText(actionBarTitle);
        //change the status bar color
        getWindow().setStatusBarColor(getColor(R.color.DodgerBlue));
        //change the status bar to dark theme
        getWindow().getDecorView().setSystemUiVisibility(0);
        //show the back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        timer = new Timer();

        button1 = findViewById(R.id.btn1);
        button2 = findViewById(R.id.btn2);
        button3 = findViewById(R.id.btn3);
        button1.setText(btn1);
        button2.setText(btn2);
        button3.setText(btn3);

        recyclerPackageView1 = findViewById(R.id.recyclerNetworkView1);
        recyclerPackageView2 = findViewById(R.id.recyclerNetworkView2);
        recyclerPackageView3 = findViewById(R.id.recyclerNetworkView3);

        itemsPackages1 =new ArrayList<>();
        itemsPackages2 =new ArrayList<>();
        itemsPackages3 =new ArrayList<>();

        //check the network name
        switch (actionBarTitle) {
            case "DIALOG":
                dialogDetails(itemsPackages1, itemsPackages2, itemsPackages3);
                break;
            case "MOBITEL":
                mobitelDetails(itemsPackages1, itemsPackages2, itemsPackages3);
                break;
            case "HUTCH":
                hutchDetails(itemsPackages1, itemsPackages2, itemsPackages3);
                break;
            case "AIRTEL":
                airtelDetails(itemsPackages1, itemsPackages2, itemsPackages3);
                break;
            case "BELL 4G":
                bellDetails(itemsPackages1, itemsPackages2, itemsPackages3);
                break;
            case "TELECOM":
                telecomDetails(itemsPackages1, itemsPackages2, itemsPackages3);
                break;
        }

        //ADAPTERS
        adapter1 = new MyAdapter(NetworkRecyclerActivity.this, itemsPackages1, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item position, String clickType) {

                //set the visibility of package item when clicking shore more
                int i = itemsPackages1.indexOf(position);
                int jTemp = 0;
                for (int j = i; j >= 0; j--) {
                    if (itemsPackages1.get(j).getViewType()==6) {
                        jTemp = j;
                        break;
                    }
                }
                //after 3rd item, items are hidden, when click shore more they all are visible
                for (int k = jTemp + 4; k < i; k++) {
                    int v = (itemsPackages1.get(k).getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
                    itemsPackages1.get(k).setVisibility(v);
                }

                //change the view of shore more and shore less
                String name = (position.getName().equals("Show more"))? "Show less": "Show more";
                position.setName(name);
                adapter1.notifyDataSetChanged();
            }
        });
        adapter2 = new MyAdapter(NetworkRecyclerActivity.this, itemsPackages2, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item position, String clickType) {
                int i = itemsPackages2.indexOf(position);
                int jTemp = 0;
                for (int j = i; j >= 0; j--) {
                    if (itemsPackages2.get(j).getViewType()==6) {
                        jTemp = j;
                        break;
                    }
                }
                //after 3rd item, items are hidden, when click shore more they all are visible
                for (int k = jTemp + 4; k < i; k++) {
                    int v = (itemsPackages2.get(k).getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
                    itemsPackages2.get(k).setVisibility(v);
                }

                //change the view of shore more and shore less
                String name = (position.getName().equals("Show more"))? "Show less": "Show more";
                position.setName(name);
                adapter2.notifyDataSetChanged();
            }
        });
        adapter3 = new MyAdapter(NetworkRecyclerActivity.this, itemsPackages3, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item position, String clickType) {
                int i = itemsPackages3.indexOf(position);
                int jTemp = 0;
                for (int j = i; j >= 0; j--) {
                    if (itemsPackages3.get(j).getViewType()==6) {
                        jTemp = j;
                        break;
                    }
                }
                //after 3rd item, items are hidden, when click shore more they all are visible
                for (int k = jTemp + 4; k < i; k++) {
                    int v = (itemsPackages3.get(k).getVisibility() == View.GONE)? View.VISIBLE: View.GONE;
                    itemsPackages3.get(k).setVisibility(v);
                }

                //change the view of shore more and shore less
                String name = (position.getName().equals("Show more"))? "Show less": "Show more";
                position.setName(name);
                adapter3.notifyDataSetChanged();
            }
        });

        recyclerPackageView1.setLayoutManager(new LinearLayoutManager(this));
        recyclerPackageView1.setAdapter(adapter1);
        recyclerPackageView2.setLayoutManager(new LinearLayoutManager(this));
        recyclerPackageView2.setAdapter(adapter2);
        recyclerPackageView3.setLayoutManager(new LinearLayoutManager(this));
        recyclerPackageView3.setAdapter(adapter3);

        adapter1.notifyDataSetChanged();
        adapter2.notifyDataSetChanged();
        adapter3.notifyDataSetChanged();

        button1.setBackground(getDrawable(R.drawable.rounded_dialog2));
        button1.setTextColor(getColor(R.color.white));

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
    }

    //This method contains all airtel package details
    public void dialogDetails(List<Item> itemsPackages1, List<Item> itemsPackages2, List<Item> itemsPackages3) {

        itemsPackages1.add(new Item(Item.PACKAGE_TYPE, "Anytime"));
        itemsPackages1.add(new Item(Item.PACKAGE, View.VISIBLE, "Anytime", "Rs. 77", "7 days (one time)", "0.7 GB", R.drawable.ic_anytime, getResources().getColor(R.color.DarkRose)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.VISIBLE, "Anytime", "Rs. 105", "10 days (one time)", "1 GB", R.drawable.ic_anytime, getResources().getColor(R.color.DarkRose)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.VISIBLE, "Anytime", "Rs. 155", "14 days (one time)", "1.5 GB", R.drawable.ic_anytime, getResources().getColor(R.color.DarkRose)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.GONE, "Anytime", "Rs. 195", "21 days (one time)", "2 GB", R.drawable.ic_anytime, getResources().getColor(R.color.DarkRose)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.GONE, "Anytime", "Rs. 277", "30 days (one time)", "3 GB", R.drawable.ic_anytime, getResources().getColor(R.color.DarkRose)));
        itemsPackages1.add(new Item(Item.SHOW_MORE, "Show more", getResources().getColor(R.color.DarkRose)));
        itemsPackages1.add(new Item(Item.PACKAGE_TYPE, "TimeBased"));
        itemsPackages1.add(new Item(Item.PACKAGE, View.VISIBLE, "Anytime", "Rs. 24 + tax", "1 hour (one time)", "1 hour", R.drawable.ic_timebase, getResources().getColor(R.color.DarkRose)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.VISIBLE, "Anytime", "Rs. 72 + tax", "4 hour (one time)", "4 hour", R.drawable.ic_timebase, getResources().getColor(R.color.DarkRose)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.VISIBLE, "Anytime", "Rs. 120 + tax", "2 days (one time)", "2 days", R.drawable.ic_timebase, getResources().getColor(R.color.DarkRose)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.GONE, "Anytime", "Rs. 240 + tax", "5 days (one time)", "5 days", R.drawable.ic_timebase, getResources().getColor(R.color.DarkRose)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.GONE, "Anytime", "Rs. 480 tax", "10 days (one time)", "10 days", R.drawable.ic_timebase, getResources().getColor(R.color.DarkRose)));
        itemsPackages1.add(new Item(Item.SHOW_MORE, "Show more", getResources().getColor(R.color.DarkRose)));

        itemsPackages2.add(new Item(Item.PACKAGE_TYPE, "Voice"));
        itemsPackages2.add(new Item(Item.PACKAGE, View.VISIBLE, "Any net 250", "Rs. 163", "30 days (one time)", "250 mins", R.drawable.ic_time, getResources().getColor(R.color.DarkPurple)));
        itemsPackages2.add(new Item(Item.PACKAGE, View.VISIBLE, "Any net Unlimited", "Rs. 650", "30 days (one time)", "Unlimited", R.drawable.ic_unlimited, getResources().getColor(R.color.DarkPurple)));
        itemsPackages2.add(new Item(Item.PACKAGE, View.VISIBLE, "Budget Pack", "Rs. 240 + tax", "30 days (one time)", "400 mins", R.drawable.ic_time, getResources().getColor(R.color.DarkPurple)));
        itemsPackages2.add(new Item(Item.PACKAGE, View.VISIBLE, "Daily Blaster", "Rs. 8 + tax", "1 days (recurring)", "15 mins\n15 MB\n7.5 MB (4G)\n15 MB (Night)", R.drawable.ic_time, getResources().getColor(R.color.DarkPurple)));
        itemsPackages2.add(new Item(Item.PACKAGE, View.VISIBLE, "Daily Blaster", "Rs. 16 + tax", "1 days (recurring)", "50 mins\n50 sms\n50 MB\n25 MB (4G)\n50 MB (Night)", R.drawable.ic_time, getResources().getColor(R.color.DarkPurple)));
        itemsPackages2.add(new Item(Item.PACKAGE, View.VISIBLE, "Unlimited", "Rs. 495", "30 days (one time)", "Unlimited", R.drawable.ic_unlimited, getResources().getColor(R.color.DarkPurple)));

        itemsPackages3.add(new Item(Item.PACKAGE_TYPE, "Combo"));
        itemsPackages3.add(new Item(Item.PACKAGE, View.VISIBLE, "Data Blaster", "Rs. 173", "7 days (one time)", "250 mins\n250 sms\n1 GB", R.drawable.ic_time, getResources().getColor(R.color.Purple)));
        itemsPackages3.add(new Item(Item.PACKAGE, View.VISIBLE, "Triple Blaster", "Rs. 469", "30 days (one time)", "1150 mins\n1150 sms\n1.5 GB", R.drawable.ic_time, getResources().getColor(R.color.Purple)));
        itemsPackages3.add(new Item(Item.PACKAGE, View.VISIBLE, "Triple Blaster", "Rs. 577", "30 days (one time)", "1000 mins (D2D)\n1000 mins (Any)\n1000 sms (D2D)\n1000 sms (Any)\n2 GB", R.drawable.ic_time, getResources().getColor(R.color.Purple)));
        itemsPackages3.add(new Item(Item.PACKAGE, View.VISIBLE, "Triple Blaster", "Rs. 690", "30 days (one time)", "1000 mins (D2D)\n1000 mins (Any)\n1000 sms (D2D)\n1000 sms (Any)\n3.5 GB", R.drawable.ic_time, getResources().getColor(R.color.Purple)));
        itemsPackages3.add(new Item(Item.PACKAGE, View.VISIBLE, "Rs. 120Combo", "Rs. 120 + tax", "7 days (recurring)", "200 min\n200 sms\n200 MB\n100 MB (4G)\n200 MB (Night)", R.drawable.ic_time, getResources().getColor(R.color.Purple)));
    }

    public void mobitelDetails(List<Item> itemsPackages1, List<Item> itemsPackages2, List<Item> itemsPackages3) {

        itemsPackages1.add(new Item(Item.PACKAGE_TYPE, "Anytime"));
        itemsPackages1.add(new Item(Item.PACKAGE, View.VISIBLE, "Internet Chooty", "Rs. 6.38", "24 hours", "43 MB", R.drawable.ic_anytime, getResources().getColor(R.color.Green)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.VISIBLE, "3 Day Internet", "Rs. 35", "3 days", "296 MB", R.drawable.ic_anytime, getResources().getColor(R.color.Green)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.VISIBLE, "7 Day Internet", "Rs. 59", "7 days", "805 MB", R.drawable.ic_anytime, getResources().getColor(R.color.Green)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.GONE, "21 Day Internet", "Rs. 119", "21 days", "1596 MB", R.drawable.ic_anytime, getResources().getColor(R.color.Green)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.GONE, "3.39 GB", "Rs. 239", "30 days", "3.39 GB", R.drawable.ic_anytime, getResources().getColor(R.color.Green)));
        itemsPackages1.add(new Item(Item.SHOW_MORE, "Show more", getResources().getColor(R.color.Green)));
        itemsPackages1.add(new Item(Item.PACKAGE_TYPE, "TimeBased"));
        itemsPackages1.add(new Item(Item.PACKAGE, View.VISIBLE, "30 Minutes Unlimited", "Rs. 27.68", "30 mins", "30 mins", R.drawable.ic_timebase, getResources().getColor(R.color.Green)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.VISIBLE, "3 Hours Unlimited", "Rs. 68.06", "3 hour", "3 hour", R.drawable.ic_timebase, getResources().getColor(R.color.Green)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.VISIBLE, "Nonstop NightTime Data", "Rs. 105", "3 nights", "3 nights", R.drawable.ic_timebase, getResources().getColor(R.color.Green)));
        itemsPackages1.add(new Item(Item.PACKAGE, View.GONE, "Nonstop NightTime Data", "Rs. 180", "7 nights", "7 nights", R.drawable.ic_timebase, getResources().getColor(R.color.Green)));
        itemsPackages1.add(new Item(Item.SHOW_MORE, "Show more", getResources().getColor(R.color.Green)));

        itemsPackages2.add(new Item(Item.PACKAGE_TYPE, "Under Processing...."));

        itemsPackages3.add(new Item(Item.PACKAGE_TYPE, "Work & Learn"));
        itemsPackages3.add(new Item(Item.PACKAGE, View.VISIBLE, "Work & Learn 220", "Rs. 220", "30 days (one time)", "25 GB", R.drawable.ic_time, getResources().getColor(R.color.LightGreen)));
        itemsPackages3.add(new Item(Item.PACKAGE, View.VISIBLE, "Work & Learn 855", "Rs. 855", "30 days (one time)", "nonstop data\n(FUP Applies)*", R.drawable.ic_time, getResources().getColor(R.color.LightGreen)));
        itemsPackages3.add(new Item(Item.PACKAGE_TYPE, "Social Media"));
        itemsPackages3.add(new Item(Item.PACKAGE, View.VISIBLE, "Unlimited YouTube", "Rs. 360", "30 days (one time)", "Unlimited", R.drawable.ic_time, getResources().getColor(R.color.LightGreen)));
        itemsPackages3.add(new Item(Item.PACKAGE, View.VISIBLE, "Social Networking Unlimited", "Rs. 145", "30 days (one time)", "Facebook\nInstagram\nLinkedIn\nTwitter", R.drawable.ic_time, getResources().getColor(R.color.LightGreen)));
        itemsPackages3.add(new Item(Item.PACKAGE, View.VISIBLE, "Non-Stop Lokka Unlimited", "Rs. 520 + tax", "30 days", "YouTube\nFacebook\nInstagram\nWhatsApp\nViber\nIMO\nFacebook Messenger\nLinkedIn\nTwitter", R.drawable.ic_time, getResources().getColor(R.color.LightGreen)));
    }

    public void hutchDetails(List<Item> itemsPackages1, List<Item> itemsPackages2, List<Item> itemsPackages3) {
        loadDetails(itemsPackages1, "HutchDataPackages", getResources().getColor(R.color.Orange));
        loadDetails(itemsPackages2, "HutchVoicePackages", getResources().getColor(R.color.DarkYellow));
        loadDetails(itemsPackages3, "HutchSpecialPackages", getResources().getColor(R.color.Yellow));
    }

    public void airtelDetails(List<Item> itemsPackages1, List<Item> itemsPackages2, List<Item> itemsPackages3) {
        loadDetails(itemsPackages1, "AirtelFreedomPackages", getResources().getColor(R.color.DarkRed));
        loadDetails(itemsPackages2, "AirtelBasicPackages", getResources().getColor(R.color.Red));
        loadDetails(itemsPackages3, "AirtelSpecialPackages", getResources().getColor(R.color.LightRed));
    }

    public void bellDetails(List<Item> itemsPackages1, List<Item> itemsPackages2, List<Item> itemsPackages3) {
        loadDetails(itemsPackages1, "BellDataPackages", getResources().getColor(R.color.DarkBlue));
        loadDetails(itemsPackages2, "BellPrepaidPackages", getResources().getColor(R.color.Blue));
        loadDetails(itemsPackages3, "BellPostpaidPackages", getResources().getColor(R.color.DodgerBlue));
    }

    public void telecomDetails(List<Item> itemsPackages1, List<Item> itemsPackages2, List<Item> itemsPackages3) {
        loadDetails(itemsPackages1, "TelecomDataPackages", getResources().getColor(R.color.LightBlue300));
        loadDetails(itemsPackages1, "TelecomVoicePackages", getResources().getColor(R.color.LightBlue200));
        loadDetails(itemsPackages1, "TelecomPeoTVPackages", getResources().getColor(R.color.LightBlue100));
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForDrawables"})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                button1.setBackground(getDrawable(R.drawable.rounded_dialog2));
                button1.setTextColor(getColor(R.color.white));
                button2.setBackground(getDrawable(R.drawable.rounded_dialog));
                button2.setTextColor(getColor(R.color.DarkGray));
                button3.setBackground(getDrawable(R.drawable.rounded_dialog));
                button3.setTextColor(getColor(R.color.DarkGray));
                activeBtn = 1;
                TransitionManager.beginDelayedTransition(recyclerPackageView1, new ChangeBounds());
                recyclerPackageView1.setVisibility(View.VISIBLE);
                recyclerPackageView2.setVisibility(View.GONE);
                recyclerPackageView3.setVisibility(View.GONE);
                break;

            case R.id.btn2:
                button1.setBackground(getDrawable(R.drawable.rounded_dialog));
                button1.setTextColor(getColor(R.color.DarkGray));
                button2.setBackground(getDrawable(R.drawable.rounded_dialog2));
                button2.setTextColor(getColor(R.color.white));
                button3.setBackground(getDrawable(R.drawable.rounded_dialog));
                button3.setTextColor(getColor(R.color.DarkGray));
                activeBtn = 2;
                TransitionManager.beginDelayedTransition(recyclerPackageView1, new ChangeBounds());
                recyclerPackageView1.setVisibility(View.GONE);
                recyclerPackageView2.setVisibility(View.VISIBLE);
                recyclerPackageView3.setVisibility(View.GONE);
                break;

            case R.id.btn3:
                button1.setBackground(getDrawable(R.drawable.rounded_dialog));
                button1.setTextColor(getColor(R.color.DarkGray));
                button2.setBackground(getDrawable(R.drawable.rounded_dialog));
                button2.setTextColor(getColor(R.color.DarkGray));
                button3.setBackground(getDrawable(R.drawable.rounded_dialog2));
                button3.setTextColor(getColor(R.color.white));
                activeBtn = 3;
                TransitionManager.beginDelayedTransition(recyclerPackageView1, new ChangeBounds());
                recyclerPackageView1.setVisibility(View.GONE);
                recyclerPackageView2.setVisibility(View.GONE);
                recyclerPackageView3.setVisibility(View.VISIBLE);
                break;
        }
    }

    //THIS WILL LOAD EVERY ITEMS OF EACH COLLECTION
    public void loadDetails(List<Item> items, String collection, int packageColor) {

        if (!progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading...");
            progressDialog.show();
            isToastRunning = true;
        }

            collectionReference = FirebaseFirestore.getInstance().collection(collection);
            collectionReference.orderBy("type", Query.Direction.ASCENDING)
                    .addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                                Log.e("Firestore error", error.getMessage());
                                return;
                            }

                            // if there are no any details in firebase after 2 seconds progress dialog will dismiss with toast message
                            if (value.size() == 0 && isToastRunning) {
                                if (progressDialog.isShowing()) {
                                    timer.schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            progressDialog.dismiss();
                                        }
                                    }, 2000); // delay 5 seconds
                                }
                                Toast.makeText(NetworkRecyclerActivity.this, "Some collections are empty", Toast.LENGTH_SHORT).show();
                                isToastRunning = false;
                            }

                            String itemType = null;
                            int i = 0;
                            for (DocumentChange documentChange : value.getDocumentChanges()) {
                                if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                    String name = documentChange.getDocument().getString("name");
                                    String price = documentChange.getDocument().getString("price");
                                    String validity = documentChange.getDocument().getString("validity");
                                    String info = documentChange.getDocument().getString("info").replaceAll("\\\\n", "\n");
                                    String icon = documentChange.getDocument().getString("icon");
                                    String type = documentChange.getDocument().getString("type");

                                    //change the item type that displays on interface
                                    if (itemType == null) {
                                        items.add(new Item(Item.PACKAGE_TYPE, type));
                                        itemType = type;
                                        visibilityCount = 0;
                                    } else if (!itemType.equals(type)) {
                                        // if last item of packages added, then show more text will create
                                        items.add(new Item(Item.SHOW_MORE, "Show more", packageColor));
                                        items.add(new Item(Item.PACKAGE_TYPE, type));
                                        itemType = type;
                                        visibilityCount = 0;
                                    }

                                    // add items to array list to display recycler view
                                    if (visibilityCount < 3) {
                                        switch (Objects.requireNonNull(icon)) {
                                            case "anytime":
                                                items.add(new Item(Item.PACKAGE, View.VISIBLE, name, price, validity, info, R.drawable.ic_anytime, packageColor));
                                                break;
                                            case "limited":
                                                items.add(new Item(Item.PACKAGE, View.VISIBLE, name, price, validity, info, R.drawable.ic_time, packageColor));
                                                break;
                                            case "unlimited":
                                                items.add(new Item(Item.PACKAGE, View.VISIBLE, name, price, validity, info, R.drawable.ic_unlimited, packageColor));
                                                break;
                                        }
                                        visibilityCount++;
                                    } else {
                                        //this will only run when item count of each type is more than 3
                                        switch (Objects.requireNonNull(icon)) {
                                            case "anytime":
                                                items.add(new Item(Item.PACKAGE, View.GONE, name, price, validity, info, R.drawable.ic_anytime, packageColor));
                                                break;
                                            case "limited":
                                                items.add(new Item(Item.PACKAGE, View.GONE, name, price, validity, info, R.drawable.ic_time, packageColor));
                                                break;
                                            case "unlimited":
                                                items.add(new Item(Item.PACKAGE, View.GONE, name, price, validity, info, R.drawable.ic_unlimited, packageColor));
                                                break;
                                        }
                                        visibilityCount++;
                                    }
                                    i++;
                                    // this "show more" will only add after last item of last type
                                    if (i == value.size() && visibilityCount > 3) {
                                        items.add(new Item(Item.SHOW_MORE, "Show more", packageColor));
                                    }

                                }
                                adapter1.notifyDataSetChanged();
                                adapter2.notifyDataSetChanged();
                                adapter3.notifyDataSetChanged();
                                if (i == value.size()) {
                                    if (progressDialog.isShowing())
                                        progressDialog.dismiss();
                                }
                            }

                        }
                    });

    }

}