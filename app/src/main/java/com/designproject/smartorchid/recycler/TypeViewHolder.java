package com.designproject.smartorchid.recycler;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.designproject.smartorchid.R;

public class TypeViewHolder extends RecyclerView.ViewHolder {
    TextView itemType;

    public TypeViewHolder(@NonNull View itemView) {
        super(itemView);
        itemType = itemView.findViewById(R.id.itemType);
    }
}
