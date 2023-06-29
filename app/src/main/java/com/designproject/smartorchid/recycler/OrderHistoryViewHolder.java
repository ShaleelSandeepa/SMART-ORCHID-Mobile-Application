package com.designproject.smartorchid.recycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.designproject.smartorchid.R;

public class OrderHistoryViewHolder extends RecyclerView.ViewHolder {

    TextView orderID, orderDetails, txtComplete, state;
    ImageView arrow;
    LinearLayout linearOrder;

    public OrderHistoryViewHolder(@NonNull View itemView) {
        super(itemView);
        orderID = itemView.findViewById(R.id.txtOrderID);
        orderDetails = itemView.findViewById(R.id.orderDetails);
        txtComplete = itemView.findViewById(R.id.txtComplete);
        arrow = itemView.findViewById(R.id.orderArrowRightDown);
        linearOrder = itemView.findViewById(R.id.linearOrder);
        state = itemView.findViewById(R.id.txtOrderCompleted);
    }
}
