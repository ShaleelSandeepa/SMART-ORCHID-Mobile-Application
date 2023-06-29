package com.designproject.smartorchid.recycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.designproject.smartorchid.R;

public class NotificationViewHolder extends RecyclerView.ViewHolder {

    LinearLayout layoutColor;
    TextView notificationTopic, notificationDetails, notificationDate, notificationTime;
    ImageView delete;

    public NotificationViewHolder(@NonNull View itemView) {
        super(itemView);

        layoutColor = itemView.findViewById(R.id.linearNotification);
        notificationTopic = itemView.findViewById(R.id.notificationTopic);
        notificationDetails = itemView.findViewById(R.id.txtNotificationDetails);
        notificationDate = itemView.findViewById(R.id.txtDate);
        notificationTime = itemView.findViewById(R.id.txtTime);
        delete = itemView.findViewById(R.id.notificationDelete);
    }
}
