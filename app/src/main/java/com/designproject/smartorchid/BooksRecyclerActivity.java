package com.designproject.smartorchid;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.cardview.widget.CardView;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.designproject.smartorchid.activity.HomeActivity;
import com.designproject.smartorchid.data.DatabaseHelper;
import com.designproject.smartorchid.recycler.CartDialog;
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
import java.util.Timer;
import java.util.TimerTask;

public class BooksRecyclerActivity extends AppCompatActivity implements CartDialog.CartDialogListener {

    public static String actionBarTitleBooks;

    private String quantity;
    public List<Item> itemsBooks;
    private Item position;

    CollectionReference collectionReference;

    ProgressDialog progressDialog;
    Timer timer;
    boolean isToastRunning;

    DatabaseHelper databaseHelper;
    RecyclerView recyclerBookView;
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

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_books_recycler);

        // To set action bar custom view
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.actionbar_layout);
        //Change action bar title
        AppCompatTextView textView = findViewById(R.id.title_actionbar);
        textView.setText(actionBarTitleBooks);
        //change the status bar color
        getWindow().setStatusBarColor(getColor(R.color.DodgerBlue));
        //change the status bar to dark theme
        getWindow().getDecorView().setSystemUiVisibility(0);
        //show the back button in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        timer = new Timer();

        databaseHelper = new DatabaseHelper(this);
        recyclerBookView = findViewById(R.id.recyclerBookView);
        itemsBooks =new ArrayList<>();

        //check the Books Card name
        switch (actionBarTitleBooks) {
            case "Atlas":
                atlasDetails(itemsBooks);
                break;
            case "Promate":
                promateDetails(itemsBooks);
                break;
            case "Rathna":
                rathnaDetails(itemsBooks);
                break;
            case "Susara":
                susaraDetails(itemsBooks);
                break;
            case "Akura":
                akuraDetails(itemsBooks);
                break;
            case "Past Papers":
                pastPapersDetails(itemsBooks);
                break;
        }

        adapter = new MyAdapter(BooksRecyclerActivity.this, itemsBooks, new MyAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Item position, String clickType) {
                setPosition(position);
                CartDialog cartDialog = new CartDialog(position);
                cartDialog.show(getSupportFragmentManager(), "cart dialog");
                adapter.notifyDataSetChanged();
            }
        });
        recyclerBookView.setLayoutManager(new LinearLayoutManager(this));
        recyclerBookView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //purpose of this intent is refresh the cart badge count
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
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

    public void atlasDetails(List<Item> itemsBooks) {

        itemsBooks.add(new Item(Item.BOOKS_TYPE, "CR Books"));
        itemsBooks.add(new Item(Item.BOOKS, "Atlas CR Single Ruled 40 Pages", "Rs. 145.00", R.drawable.atlas_cr_single_40));
        itemsBooks.add(new Item(Item.BOOKS, "Atlas CR Single Ruled 80 Pages", "Rs. 260.00", R.drawable.atlas_cr_single_80));
        itemsBooks.add(new Item(Item.BOOKS, "Atlas CR Single Ruled 120 Pages", "Rs. 350.00", R.drawable.atlas_cr_single_120));
        itemsBooks.add(new Item(Item.BOOKS, "Atlas CR Single Ruled 160 Pages", "Rs. 495.00", R.drawable.atlas_cr_single_160));
        itemsBooks.add(new Item(Item.BOOKS, "Atlas CR Single Ruled 200 Pages", "Rs. 595.00", R.drawable.atlas_cr_single_200));
        itemsBooks.add(new Item(Item.BOOKS, "Atlas CR Single Ruled 400 Pages", "Rs. 920.00", R.drawable.atlas_cr_single_400));
        itemsBooks.add(new Item(Item.BOOKS, "Atlas CR Square Ruled 40 Pages", "Rs. 145.00", R.drawable.atlas_cr_square_40));
        itemsBooks.add(new Item(Item.BOOKS, "Atlas CR Square Ruled 80 Pages", "Rs. 260.00", R.drawable.atlas_cr_square_80));
        itemsBooks.add(new Item(Item.BOOKS, "Atlas CR Square Ruled 120 Pages", "Rs. 350.00", R.drawable.atlas_cr_square_120));
        itemsBooks.add(new Item(Item.BOOKS, "Atlas CR Square Ruled 160 Pages", "Rs. 495.00", R.drawable.atlas_cr_square_160));
        itemsBooks.add(new Item(Item.BOOKS, "Atlas CR Square Ruled 200 Pages", "Rs. 595.00", R.drawable.atlas_cr_square_200));

    }

    public void promateDetails(List<Item> itemsBooks) {

        //THIS WILL LOAD EVERY ITEMS OF PAPER PRODUCTS FROM FIREBASE
        loadDetails(itemsBooks, "promate");

    }

    public void rathnaDetails(List<Item> itemsBooks) {

        //THIS WILL LOAD EVERY ITEMS OF PAPER PRODUCTS FROM FIREBASE
        loadDetails(itemsBooks, "rathna");

    }

    public void susaraDetails(List<Item> itemsBooks) {

        //THIS WILL LOAD EVERY ITEMS OF PAPER PRODUCTS FROM FIREBASE
        loadDetails(itemsBooks, "susara");

    }

    public void akuraDetails(List<Item> itemsBooks) {

        //THIS WILL LOAD EVERY ITEMS OF PAPER PRODUCTS FROM FIREBASE
        loadDetails(itemsBooks, "akura");

    }

    public void pastPapersDetails(List<Item> itemsBooks) {

        //THIS WILL LOAD EVERY ITEMS OF PAPER PRODUCTS FROM FIREBASE
        loadDetails(itemsBooks, "pastPapers");

    }

    //THIS WILL LOAD EVERY ITEMS OF EACH COLLECTION
    public void loadDetails(List<Item> items, String collection) {
        if (!progressDialog.isShowing()) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Loading...");
            progressDialog.show();
            isToastRunning = true;
        }

        //access to firebase
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
                            Toast.makeText(BooksRecyclerActivity.this, "No details !", Toast.LENGTH_SHORT).show();
                            isToastRunning = false;
                        }

                        String itemType = null;
                        int i = 0;
                        for (DocumentChange documentChange : value.getDocumentChanges()) {
                            if (documentChange.getType() == DocumentChange.Type.ADDED) {
                                String name = documentChange.getDocument().getString("name");
                                String price = documentChange.getDocument().getString("price");
                                String imagePath = documentChange.getDocument().getString("imagePath");
                                String type = documentChange.getDocument().getString("type");

                                //change the item type that displays on interface
                                if (itemType == null) {
                                    items.add(new Item(Item.BOOKS_TYPE, type));
                                    itemType = type;
                                } else if (!itemType.equals(type)){
                                    items.add(new Item(Item.BOOKS_TYPE, type));
                                    itemType = type;
                                }

                                // add items to array list to display recycler view
                                items.add(new Item(Item.BOOKS, name, price, imagePath));
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