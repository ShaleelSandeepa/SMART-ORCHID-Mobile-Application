package com.designproject.smartorchid;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.designproject.smartorchid.activity.HomeActivity;
import com.designproject.smartorchid.data.DatabaseHelper;
import com.designproject.smartorchid.recycler.CartDialog;
import com.designproject.smartorchid.recycler.Item;
import com.designproject.smartorchid.recycler.MyAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firestore.v1.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class StationariesRecyclerActivity extends AppCompatActivity implements CartDialog.CartDialogListener {

    public static String actionBarTitleStationaries;

    private String quantity;
    public List<Item> itemsStationaries;
    private Item position;

    CollectionReference collectionReference;
    private String imageUri;

    ProgressDialog progressDialog;
    Timer timer;
    boolean isToastRunning;

    DatabaseHelper databaseHelper;
    RecyclerView recyclerStationaryView;
    MyAdapter adapter;

    public Item getPosition() {
        return position;
    }

    public void setPosition(Item position) {
        this.position = position;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationaries_recycler);

        // To set action bar custom view
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        //Change action bar title
        AppCompatTextView textView = findViewById(R.id.title_actionbar);
        textView.setText(actionBarTitleStationaries);
        //change the status bar color
        getWindow().setStatusBarColor(getColor(R.color.DodgerBlue));
        //change the status bar to dark theme
        getWindow().getDecorView().setSystemUiVisibility(0);
        //show the back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        timer = new Timer();

        databaseHelper = new DatabaseHelper(this);
        recyclerStationaryView = findViewById(R.id.recyclerStationariesView);
        itemsStationaries =new ArrayList<>();

        //check the Books Card name
        switch (actionBarTitleStationaries) {
            case "Pens":
                pensDetails(itemsStationaries);
                break;
            case "Color Products":
                colorProductsDetails(itemsStationaries);
                break;
            case "School Products":
                schoolProductsDetails(itemsStationaries);
                break;
            case "Office Products":
                officeProductsDetails(itemsStationaries);
                break;
            case "HighLighter & Markers":
                highlighterMarkerDetails(itemsStationaries);
                break;
            case "Papers Products":
                paperProductsDetails(itemsStationaries);
                break;
        }

        adapter = new MyAdapter(StationariesRecyclerActivity.this, itemsStationaries, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item position, String clickType) {
                setPosition(position);
                CartDialog cartDialog = new CartDialog(position);
                cartDialog.show(getSupportFragmentManager(), "cart dialog");
                adapter.notifyDataSetChanged();
            }
        });
        recyclerStationaryView.setLayoutManager(new LinearLayoutManager(this));
        recyclerStationaryView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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

    @Override
    public void setQty(String qty) {
        setQuantity(qty);

        //get the items details that want to add to the cart
        int type = position.getViewType();
        String name = position.getName();
        String price = position.getPrice();
        int image = position.getImage();
        int quantity = Integer.parseInt(getQuantity());
        String uri = position.getImagePath();

        //call below method for add item to cart table
        if (quantity > 0) {
            addToCart(type, name, price, image, quantity, uri);
        } else {
            Toast.makeText(this, "Please enter at least 1", Toast.LENGTH_SHORT).show();
        }
    }

    // interface to send the position of item that want to add to cart
    public interface ItemPositionListener {
        void setPosition(Item position);
    }

    public void addToCart(int type, String name, String price, int image, int qty, String uri) {
        // get the id of logged in account
        Cursor data = databaseHelper.getLoggedInAccount();
        while (data.moveToNext()) {
            int accountID = data.getInt(0);
            // add items to the cart table
            boolean isAdded = databaseHelper.addToCart(accountID, type, name, price, image, qty, uri);
            if (isAdded) {
                Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Failed added to cart", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void pensDetails(List<Item> items) {

        items.add(new Item(Item.STATIONARY_TYPE, "Pens"));
        items.add(new Item(Item.STATIONARY, "Atlas Chooty Pen Blue", "Rs. 35.00", R.drawable.atlas_pen_chooty_blue));
        items.add(new Item(Item.STATIONARY,"Atlas Chooty Pen Black", "Rs. 35.00", R.drawable.atlas_pen_chooty_black));
        items.add(new Item(Item.STATIONARY,"Atlas Chooty Pen Red", "Rs. 35.00", R.drawable.atlas_pen_chooty_red));
        items.add(new Item(Item.STATIONARY,"Atlas MAX Pen Blue", "Rs. 25.00", R.drawable.atlas_pen_max_blue));
        items.add(new Item(Item.STATIONARY,"Atlas MAX Pen Black", "Rs. 25.00", R.drawable.atlas_pen_max_black));
        items.add(new Item(Item.STATIONARY,"Atlas MAX Pen Red", "Rs. 25.00", R.drawable.atlas_pen_max_red));
        items.add(new Item(Item.STATIONARY,"Atlas Pen Butter Gel Blue\nPack of 3", "Rs. 25.00", R.drawable.atlas_pen_buttergel_pack_blue));
        items.add(new Item(Item.STATIONARY,"Atlas Pen Butter Gel Black\nPack of 3", "Rs. 25.00", R.drawable.atlas_pen_buttergel_pack_black));
        items.add(new Item(Item.STATIONARY,"Atlas Pen Chooty Multicolor\nPack of 3", "Rs. 25.00", R.drawable.atlas_pen_multicolor_pack));
        items.add(new Item(Item.STATIONARY,"Atlas Pen Chooty Gel Multicolor\nPack of 3", "Rs. 25.00", R.drawable.atlas_pen_multicolor_pack_gel));
        items.add(new Item(Item.STATIONARY,"Atlas Pen Chooty Gel Gold\nPack of 3", "Rs. 25.00", R.drawable.atlas_pen_chooty_gel_gold_pack));
        items.add(new Item(Item.STATIONARY,"Atlas Pen Chooty Gel Silver\nPack of 3", "Rs. 25.00", R.drawable.atlas_pen_chooty_gel_silver_pack));

    }

    public void colorProductsDetails(List<Item> items) {

        //THIS WILL LOAD EVERY ITEMS OF COLOR PRODUCTS FROM FIREBASE
        loadDetails(items, "colorProducts");

    }

    public void schoolProductsDetails(List<Item> items) {

        //THIS WILL LOAD EVERY ITEMS OF SCHOOL PRODUCTS FROM FIREBASE
        loadDetails(items, "schoolProducts");

    }

    public void officeProductsDetails(List<Item> items) {

        //THIS WILL LOAD EVERY ITEMS OF OFFICE PRODUCTS FROM FIREBASE
        loadDetails(items, "officeProducts");

    }

    public void highlighterMarkerDetails(List<Item> items) {

        //THIS WILL LOAD EVERY ITEMS OF HIGHLIGHTERS & MARKERS FROM FIREBASE
        loadDetails(items, "highlightersAndMarkers");

    }

    public void paperProductsDetails(List<Item> items) {

        //THIS WILL LOAD EVERY ITEMS OF PAPER PRODUCTS FROM FIREBASE
        loadDetails(items, "paperProducts");

    }

    //THIS WILL LOAD EVERY ITEMS OF EACH COLLECTION
    public void loadDetails(List<Item> items, String collection) {
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
                            Toast.makeText(StationariesRecyclerActivity.this, "No Details !", Toast.LENGTH_SHORT).show();
                            isToastRunning = false;
                        }

                        String itemType = null;
                        int i = 0 ;
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                String name = documentChange.getDocument().getString("name");
                                String price = documentChange.getDocument().getString("price");
                                String imagePath = documentChange.getDocument().getString("imagePath");
                                String type = documentChange.getDocument().getString("type");

                                //change the item type that displays on interface
                                if (itemType == null) {
                                    items.add(new Item(Item.STATIONARY_TYPE, type));
                                    itemType = type;
                                } else if (!itemType.equals(type)){
                                    items.add(new Item(Item.STATIONARY_TYPE, type));
                                    itemType = type;
                                }

                                // add items to array list to display recycler view
                                items.add(new Item(Item.STATIONARY, name, price, imagePath));
                                i++;

                            }
                            adapter.notifyDataSetChanged();
                            if (i == value.size()) {
                                if (progressDialog.isShowing())
                                    progressDialog.dismiss();
                            }

                        }

                    }
                });
    }

}