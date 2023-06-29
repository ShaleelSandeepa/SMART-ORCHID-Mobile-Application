package com.designproject.smartorchid.fragment;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.designproject.smartorchid.R;
import com.designproject.smartorchid.data.DatabaseHelper;
import com.designproject.smartorchid.recycler.OrderAdapter;
import com.designproject.smartorchid.recycler.ItemOrder;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddFragment extends Fragment implements View.OnClickListener {

    LinearLayout linearLayoutNoConnection;
    ConstraintLayout constraintLayout;
    BottomNavigationView bottomNavigationView;

    AlphaAnimation blinkAnimation;
    ImageView imgWifiOff;
    ConnectivityManager connectivityManager;
    Handler handler;
    Runnable runnable;
    AlertDialog.Builder alertDialog;
    StringBuilder stringBuilder;

    TextView totalAmount, profileName;
    ImageView imgInfo;
    Button addFromCart, btnCancel, btnConfirm;
    float totalFinalAmount;
    int accountID;
//    Bundle result;

    RecyclerView recyclerViewAddOrder;
    List<ItemOrder> orderItems;
    OrderAdapter orderAdapter;

    Cursor ordersCursor, data;
    DatabaseHelper databaseHelper;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        totalAmount = view.findViewById(R.id.txtTotalAmount);
        bottomNavigationView = view.findViewById(R.id.bottomNav);

        //blinking animation when connection is lost
        imgWifiOff = view.findViewById(R.id.imgWifiOff);
        blinkAnimation = new AlphaAnimation(0.0f, 1.0f);
        blinkAnimation.setDuration(500); // Set the duration of each animation cycle (in milliseconds)
        blinkAnimation.setRepeatCount(Animation.INFINITE); // Set the number of times to repeat (infinite in this case)
        blinkAnimation.setRepeatMode(Animation.REVERSE); // Reverse the animation when repeating

        linearLayoutNoConnection = view.findViewById(R.id.linearNoConnectionLayout);
        constraintLayout = view.findViewById(R.id.constraintOrderLayout);

        recyclerViewAddOrder = view.findViewById(R.id.recyclerOrderView);
        orderItems = new ArrayList<>();

        connectivityManager = (ConnectivityManager) requireContext().getSystemService(Context.CONNECTIVITY_SERVICE);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                //always check the connection is available or not
                checkInternetConnection();
                handler.postDelayed(this, 1000); // Repeat the check every 1 second
            }
        };

        addFromCart = view.findViewById(R.id.btnAddFromCart);
        btnCancel = view.findViewById(R.id.btnCancelOrder);
        btnConfirm = view.findViewById(R.id.btnConfirmOrder);
        imgInfo = view.findViewById(R.id.orderInfo);

        addFromCart.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        imgInfo.setOnClickListener(this);

        databaseHelper = new DatabaseHelper(getContext());
        ordersCursor = databaseHelper.getAllOrderItems();

        //set the profile name in order as logged user
        profileName = (TextView) view.findViewById(R.id.orderUserName);
        data = databaseHelper.getAllData();
        while (data.moveToNext()) {
            // this will get the account which is logged
            int status = data.getInt(6);
            if (status == 1) {
                //when logout, set the login states of account as 0
                String name = data.getString(1);
                profileName.setText(name);
                accountID = data.getInt(0);
                break;
            }
        }
        displayOrderItems();

        orderAdapter = new OrderAdapter(getContext(), orderItems, new OrderAdapter.OnOrderItemClickListener() {
            @Override
            public void onItemClick(ItemOrder position, int index) {

                //when long press on order item in AddOrder fragment screen, it will appear this code
                String itemId = orderItems.get(index).getItemId();
                databaseHelper.cancelOrderItems(Integer.parseInt(itemId));
                orderItems.remove(index);

                replaceFragment(new AddFragment());
                orderAdapter.notifyDataSetChanged();
            }
        });
        recyclerViewAddOrder.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewAddOrder.setAdapter(orderAdapter);
        orderAdapter.notifyDataSetChanged();

    }

    //display the all order items exist in order table
    @SuppressLint("DefaultLocale")
    public void displayOrderItems() {
        if (ordersCursor.getCount() == 0) {
            Toast.makeText(getContext(), "Order clear", Toast.LENGTH_SHORT).show();
        } else {
            //add order items to the ArrayList
            while (ordersCursor.moveToNext()) {
                orderItems.add(new ItemOrder(String.valueOf(ordersCursor.getInt(0)), ordersCursor.getString(2),
                        ordersCursor.getString(3), String.valueOf(ordersCursor.getInt(4)), ordersCursor.getString(5)));
                totalFinalAmount = totalFinalAmount + Float.parseFloat(ordersCursor.getString(5));
                //set final amount with two decimal points
                totalAmount.setText(String.format("%.2f", totalFinalAmount));
            }
        }
    }

    //delete orders from orderItems array List & database
    @SuppressLint("NotifyDataSetChanged")
    public void removeFromArrayList() {

        ////////////////Add order details to order history table///////////////

        //if there has no any order items, it will not add to orders history.
        if (stringBuilder != null) {
            boolean insert = databaseHelper.insertOrderHistory(String.valueOf(profileName.getText()), String.valueOf(stringBuilder), getResources().getColor(R.color.white));
            if (insert) {

                /////////////////////// add one notification to the database /////////////////////////
                // Get the current date and time
                Date currentDate = new Date();
                // Format the date
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String formattedDate = dateFormat.format(currentDate);
                // Format the time
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                String formattedTime = timeFormat.format(currentDate);
                boolean isAdded = databaseHelper.addNotification(accountID, formattedDate, formattedTime, "unread", "Order sent",
                        "Your order is successfully sent.\nPlease make payment via Online Banking or come to the shop & Collect your order.");
                if (isAdded) {
                    ////////////////////////// SEND NOTIFICATION ////////////////////////////

                    Cursor cursor = databaseHelper.getSetting(1);
                    if (cursor.moveToNext()) {
                        if (cursor.getInt(2) == 1) {
                            sendNotification("Your order is under progress...");
                        }
                    }
                    cursor.close();

                }
                //////////////////////////////////////////////////////////////////////////////

                Toast.makeText(getContext(), "Order saved", Toast.LENGTH_SHORT).show();
                // Set the selected tab programmatically
                replaceFragment(new NotificationFragment());
                replaceFragment(new AddFragment());
            } else {
                Toast.makeText(getContext(), "Order is not saved !", Toast.LENGTH_SHORT).show();
            }
        }

        ///////////////////////////////////////////////////////////////////////

        while (orderItems.size() > 0) {
            String itemId = orderItems.get(0).getItemId();
            databaseHelper.cancelOrderItems(Integer.parseInt(itemId));
            orderItems.remove(0);
            orderAdapter.notifyDataSetChanged();
        }
        //set the total amount as "0"
        totalAmount.setText("000.00");
        orderAdapter.notifyDataSetChanged();
    }

    //check the internet connection is available or not
    private void checkInternetConnection() {
        if (connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnectedOrConnecting()){
            constraintLayout.setVisibility(View.VISIBLE);
            linearLayoutNoConnection.setVisibility(View.GONE);
        } else {
            constraintLayout.setVisibility(View.GONE);
            linearLayoutNoConnection.setVisibility(View.VISIBLE);
            imgWifiOff.setVisibility(View.VISIBLE); // Make the ImageView visible
            imgWifiOff.startAnimation(blinkAnimation);
        }
    }

    //Method for replace fragments
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.postDelayed(runnable, 1000); // Start the periodic check
    }

    @Override
    public void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); // Stop the periodic check
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.orderInfo:
                Toast.makeText(getContext(), "Please long press to remove each item in order", Toast.LENGTH_LONG).show();
                break;

            case R.id.btnAddFromCart:
                replaceFragment(new CartFragment());
                break;

            case R.id.btnCancelOrder:
                if (orderItems.size() != 0) {
                    alertDialog = new AlertDialog.Builder(requireContext());
                    alertDialog.setTitle("Order Cancel");
                    alertDialog.setMessage("Are you sure to cancel order ?");

                    //When click "Yes" it will execute this
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeFromArrayList();
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
                } else {
                    Toast.makeText(getContext(), "Order Empty !", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btnConfirmOrder:
                if (orderItems.size() != 0) {
                    // Create a new StringBuffer
                    stringBuilder = new StringBuilder();
                    stringBuilder.append("*USER NAME : " + profileName.getText() + "*").append("\n\n");
                    int i = 0;
                    while (i < orderItems.size()) {
                        String item = orderItems.get(i).getItem();
                        String unitPrice = orderItems.get(i).getUnitPrice();
                        String qty = orderItems.get(i).getQty();
                        String price = orderItems.get(i).getPrice();

                        stringBuilder.append("Item : " + (i+1) + "\n")
                                .append("Item name : " + item + "\n")
                                .append("Unit Price : " + unitPrice + "\n")
                                .append("QTY : " + qty + "\n")
                                .append("Price : " + price + "\n\n");

                        i++;
                    }

                    //calculate the total amount of order
                    String finalAmount = String.valueOf(totalAmount.getText());
                    stringBuilder.append("*Total Amount : Rs. " + finalAmount + "*");

                    String orderMessage = stringBuilder.toString();

                    alertDialog = new AlertDialog.Builder(requireContext());
                    alertDialog.setTitle("Order Confirmation");
                    alertDialog.setMessage("Order ready to sent via Whatsapp, Are you sure to confirm ?");

                    //When click "Yes" it will execute this
                    alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sendOrder(orderMessage);
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
                break;
        }
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

    //send the order to the shop via whatsapp
    public void sendOrder(String orderMessage) {
        String phoneNumber = "+94764859595";
        PackageManager packageManager = requireContext().getPackageManager();
        //check the whatsapp is installed and intent
        if (isWhatsappInstalled(packageManager)) {
            Intent goToWhatsapp = new Intent(Intent.ACTION_VIEW);
            goToWhatsapp.setData(Uri.parse("https://api.whatsapp.com/send?phone=" + phoneNumber + "&text=" + Uri.encode(orderMessage)));
            goToWhatsapp.setPackage("com.whatsapp");
            startActivity(goToWhatsapp);
            removeFromArrayList();

            //check the whatsapp business app is installed and intent
        } else if (isWhatsappBusinessInstalled(packageManager)) {
            Intent goToWhatsappBusiness = new Intent(Intent.ACTION_VIEW);
            goToWhatsappBusiness.setData(Uri.parse("https://wa.me/" + phoneNumber + "&text=" + Uri.encode(orderMessage)));
            goToWhatsappBusiness.setPackage("com.whatsapp.w4b");
            startActivity(goToWhatsappBusiness);
            removeFromArrayList();

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
    }

    ////////////////////////// SEND NOTIFICATION ////////////////////////////
    public void sendNotification(String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            String channelId = "smartorchid";
            CharSequence channelName = "SMART ORCHID";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);

            // Register the channel with the system
            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(requireContext(), "smartorchid")
                .setSmallIcon(R.drawable.ic_menu_book)
                .setContentTitle("SMART ORCHID")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Set a large icon for the notification
        Bitmap largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.orchid);
        builder.setLargeIcon(largeIcon);

        int notificationId = 123;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(requireContext());
        notificationManager.notify(notificationId, builder.build());

    }

}