package com.designproject.smartorchid.recycler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.designproject.smartorchid.R;
import com.designproject.smartorchid.data.DatabaseHelper;

import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderViewHolder> {

    Context context;
    List<ItemOrder> orderItems;
    OrderAdapter.OnOrderItemClickListener onOrderItemClickListener;

    public OrderAdapter(Context context, List<ItemOrder> orderItems, OnOrderItemClickListener onOrderItemClickListener) {
        this.context = context;
        this.orderItems = orderItems;
        this.onOrderItemClickListener = onOrderItemClickListener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new OrderViewHolder(LayoutInflater.from(context).inflate(R.layout.item_order, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.item.setText(orderItems.get(position).getItem());
        holder.unitPrice.setText(orderItems.get(position).getUnitPrice());
        holder.qty.setText(orderItems.get(position).getQty());
        holder.price.setText(orderItems.get(position).getPrice());
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onOrderItemClickListener.onItemClick(orderItems.get(position), position);
                return false;
            }
        });
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Long press to remove", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    public interface OnOrderItemClickListener {
        void onItemClick(ItemOrder position, int index);
    }
}
