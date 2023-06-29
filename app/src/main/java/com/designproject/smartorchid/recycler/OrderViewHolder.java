package com.designproject.smartorchid.recycler;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.designproject.smartorchid.R;

public class OrderViewHolder extends RecyclerView.ViewHolder {

    TextView item, unitPrice, qty, price;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        item = itemView.findViewById(R.id.txtOrderItem);
        unitPrice = itemView.findViewById(R.id.txtOrderUnitPrice);
        qty = itemView.findViewById(R.id.txtOrderQTY);
        price = itemView.findViewById(R.id.txtOrderPrice);
    }
}
