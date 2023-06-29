package com.designproject.smartorchid.recycler;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.designproject.smartorchid.BooksRecyclerActivity;
import com.designproject.smartorchid.R;

import java.util.ArrayList;
import java.util.List;

public class CartDialog extends AppCompatDialogFragment {

    ImageView btnPlus, btnMinus;
    EditText itemQty;
    CartDialogListener cartDialogListener;
    Item position;

    public CartDialog(Item position) {
        this.position = position;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.layout_cart_dialog, null);

        btnPlus = (ImageView) view.findViewById(R.id.qtyPlus);
        btnMinus = (ImageView) view.findViewById(R.id.qtyMinus);
        itemQty = (EditText) view.findViewById(R.id.itemCartQty);

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = Integer.parseInt( (String) itemQty.getText().toString());
                itemQty.setText(String.valueOf(++i));
            }
        });
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int i = Integer.parseInt( (String) itemQty.getText().toString());
                if (i > 1) {
                    itemQty.setText(String.valueOf(--i));
                }
            }
        });

        builder.setView(view)
                .setTitle("Enter the Quantity")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        String qty = (String) itemQty.getText().toString();
                        cartDialogListener.setQty(qty);

                    }
                });

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            cartDialogListener = (CartDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement CartDialogListener");
        }
    }

    //interface for send the quantity of cart items
    public interface CartDialogListener {
        void setQty(String qty);
    }
}
