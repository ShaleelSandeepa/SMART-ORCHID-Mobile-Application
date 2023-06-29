package com.designproject.smartorchid.recycler;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.designproject.smartorchid.R;

public class PackageViewHolder extends RecyclerView.ViewHolder {

    TextView packageNameView, packagePriceView, packageValidityView, packageInfoView;
    ImageView packageIconView;
    CardView packageCardView;

    public PackageViewHolder(@NonNull View itemView) {
        super(itemView);
        packageNameView = itemView.findViewById(R.id.packageName);
        packagePriceView = itemView.findViewById(R.id.packagePrice);
        packageValidityView = itemView.findViewById(R.id.packageValidity);
        packageInfoView = itemView.findViewById(R.id.packageInfo);
        packageIconView = itemView.findViewById(R.id.packageIcon);
        packageCardView = itemView.findViewById(R.id.packageCard);
    }
}
