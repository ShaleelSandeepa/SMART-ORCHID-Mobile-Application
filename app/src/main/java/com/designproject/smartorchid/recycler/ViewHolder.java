package com.designproject.smartorchid.recycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.designproject.smartorchid.R;

public class ViewHolder extends RecyclerView.ViewHolder {

    TextView nameView, priceView;
    ImageView imageView, cartIcon;

    public ViewHolder(@NonNull View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.itemName);
        priceView = itemView.findViewById(R.id.itemPrice);
        imageView = itemView.findViewById(R.id.itemImage);
        cartIcon = itemView.findViewById(R.id.cartIcon);
    }
}
