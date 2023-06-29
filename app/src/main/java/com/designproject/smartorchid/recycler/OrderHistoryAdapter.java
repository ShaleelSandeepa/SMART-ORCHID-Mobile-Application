package com.designproject.smartorchid.recycler;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.transition.AutoTransition;
import android.transition.ChangeBounds;
import android.transition.ChangeScroll;
import android.transition.ChangeTransform;
import android.transition.Fade;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.designproject.smartorchid.R;
import com.designproject.smartorchid.data.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderHistoryAdapter extends RecyclerView.Adapter<OrderHistoryViewHolder> {

    Context context;
    List<ItemOrderHistory> orderHistoryList;
    OrderHistoryAdapter.OnOrderClickListener onOrderClickListener;

    Cursor cursor;
    DatabaseHelper databaseHelper;
    AlertDialog.Builder alertDialog;

    CardView orderCard;
    LinearLayout linearOrderDetails;
    LinearLayout linearOrder;
    ImageView imgArrow;
    int completedColor;
    int accountID;

    public OrderHistoryAdapter(Context context, List<ItemOrderHistory> orderHistoryList, OnOrderClickListener onOrderClickListener) {
        this.context = context;
        this.orderHistoryList = orderHistoryList;
        this.onOrderClickListener = onOrderClickListener;
    }

    @NonNull
    @Override
    public OrderHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderHistoryViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderHistoryViewHolder holder, @SuppressLint("RecyclerView") int position) {

        databaseHelper = new DatabaseHelper(context);
        cursor = databaseHelper.getLoggedInAccount();
        if (cursor.moveToFirst()) {
            accountID = cursor.getInt(0);
        }
        completedColor = ContextCompat.getColor(context, R.color.LighterBlue2);

        holder.orderID.setText(orderHistoryList.get(position).getOrderID());
        holder.orderDetails.setText(orderHistoryList.get(position).getOrderDetails());
        holder.linearOrder.setBackgroundColor(orderHistoryList.get(position).getColor());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                onOrderClickListener.onItemClick(orderHistoryList.get(position), position, "itemView");

                orderCard = v.findViewById(R.id.orderCard);
                linearOrderDetails = v.findViewById(R.id.linearOrderDetails);
                linearOrder = v.findViewById(R.id.linearOrder);
                imgArrow = v.findViewById(R.id.orderArrowRightDown);

                if (linearOrderDetails.getVisibility() == View.GONE) {
                    linearOrderDetails.setVisibility(View.VISIBLE);
                    imgArrow.setImageResource(R.drawable.ic_round_arrow_drop_down);
                } else {
                    linearOrderDetails.setVisibility(View.GONE);
                    imgArrow.setImageResource(R.drawable.ic_round_arrow_right);
                }
            }
        });
        holder.txtComplete.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {

                alertDialog = new AlertDialog.Builder(context);
                alertDialog.setTitle("Complete Order");
                alertDialog.setMessage("Are you sure to complete order ?");

                //When click "Yes" it will execute this
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //this will appear on onClickListener in where called Adapter class
                        onOrderClickListener.onItemClick(orderHistoryList.get(position), position, "txtComplete");

                        holder.linearOrder.setBackgroundColor(orderHistoryList.get(position).getColor());
                        holder.txtComplete.setVisibility(View.GONE);

                        /////////////////////// add one notification to the database /////////////////////////
                        // Get the current date and time
                        Date currentDate = new Date();
                        // Format the date
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                        String formattedDate = dateFormat.format(currentDate);
                        // Format the time
                        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                        String formattedTime = timeFormat.format(currentDate);
                        String notificationTopic = "Order " + orderHistoryList.get(position).getOrderID() + " completed";
                        boolean isAdded = databaseHelper.addNotification(accountID, formattedDate, formattedTime, "unread", notificationTopic,
                                "Your order is successfully completed.\nEvery details are in orders section.\nThank you for the response");
                        if (isAdded) {
                            ////////////////////////// SEND NOTIFICATION ////////////////////////////

                            Cursor cursor = databaseHelper.getSetting(1);
                            if (cursor.moveToNext()) {
                                if (cursor.getInt(2) == 1) {
                                    sendNotification("Your order is completed.");
                                }
                            }
                            cursor.close();

                        }
                        //////////////////////////////////////////////////////////////////////////////
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
        });

        //hide "COMPLETED" blue text & set state as Completed if color is Lighter Blue
        if (orderHistoryList.get(position).getColor() == completedColor) {
            holder.txtComplete.setVisibility(View.GONE);
            holder.state.setText("Completed");
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
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "smartorchid")
                .setSmallIcon(R.drawable.ic_menu_book)
                .setContentTitle("SMART ORCHID")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        // Set a large icon for the notification
        Bitmap largeIcon = BitmapFactory.decodeResource(context.getResources(), R.drawable.orchid);
        builder.setLargeIcon(largeIcon);

        int notificationId = 123;
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(notificationId, builder.build());

    }

    @Override
    public int getItemCount() {
        return orderHistoryList.size();
    }

    public interface OnOrderClickListener {
        void onItemClick(ItemOrderHistory position, int index, String clickType);
    }
}
