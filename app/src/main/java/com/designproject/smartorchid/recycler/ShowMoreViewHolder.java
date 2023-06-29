package com.designproject.smartorchid.recycler;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.designproject.smartorchid.R;

public class ShowMoreViewHolder extends RecyclerView.ViewHolder {
    TextView showMore;

    public ShowMoreViewHolder(@NonNull View itemView) {
        super(itemView);

        showMore = itemView.findViewById(R.id.showMore);
    }

}
