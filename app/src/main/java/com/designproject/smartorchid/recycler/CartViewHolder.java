package com.designproject.smartorchid.recycler;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.designproject.smartorchid.R;

public class CartViewHolder extends RecyclerView.ViewHolder {

    ImageView minus, plus, delete, itemImage;
    Button buy;
    TextView itemName, itemPrice;
    EditText itemQty;
    RelativeLayout relativeLayout;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);

        minus = itemView.findViewById(R.id.qtyMinus);
        plus = itemView.findViewById(R.id.qtyPlus);
        delete = itemView.findViewById(R.id.deleteCartItem);
        buy = itemView.findViewById(R.id.btnBuy);

        itemQty = itemView.findViewById(R.id.itemCartQty);
        itemImage = itemView.findViewById(R.id.itemImage);
        itemName = itemView.findViewById(R.id.itemName);
        itemPrice = itemView.findViewById(R.id.itemPrice);
        relativeLayout = itemView.findViewById(R.id.itemCartLayout);

    }
}
