package com.designproject.smartorchid.recycler;

import static com.designproject.smartorchid.recycler.Item.BOOKS;
import static com.designproject.smartorchid.recycler.Item.BOOKS_TYPE;
import static com.designproject.smartorchid.recycler.Item.CART;
import static com.designproject.smartorchid.recycler.Item.PACKAGE;
import static com.designproject.smartorchid.recycler.Item.PACKAGE_TYPE;
import static com.designproject.smartorchid.recycler.Item.SHOW_MORE;
import static com.designproject.smartorchid.recycler.Item.STATIONARY;
import static com.designproject.smartorchid.recycler.Item.STATIONARY_TYPE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentResultListener;
import androidx.recyclerview.widget.RecyclerView;

import com.designproject.smartorchid.R;
import com.designproject.smartorchid.data.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyAdapter extends RecyclerView.Adapter {

    Context context;
    List<Item> items;
    DatabaseHelper databaseHelper;
    Cursor data;
    MyAdapter.OnItemClickListener onItemClickListener;
    boolean changeView = false;

    //FIREBASE VARIABLE
    FirebaseFirestore firestore;
    Map<String, Object> orders;
    String user, itemId, item, unitPrice, qty, price, isOrderComplete = "no";
    int accountID;

    public MyAdapter(Context context, List<Item> items, OnItemClickListener onItemClickListener) {
        this.context = context;
        this.items = items;
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public int getItemViewType(int position) {
        switch (items.get(position).getViewType()) {
            case 1:
                return BOOKS;

            case 2:
                return BOOKS_TYPE;

            case 3:
                return STATIONARY;

            case 4:
                return STATIONARY_TYPE;

            case 5:
                return PACKAGE;

            case 6:
                return PACKAGE_TYPE;

            case 7:
                return SHOW_MORE;

            case 8:
                return CART;

            default:
                return -1;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case BOOKS:

            case STATIONARY:
                return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent, false));

            case BOOKS_TYPE:

            case STATIONARY_TYPE:

            case PACKAGE_TYPE:
                return new TypeViewHolder(LayoutInflater.from(context).inflate(R.layout.item_type_view, parent, false));

            case PACKAGE:
                return new PackageViewHolder(LayoutInflater.from(context).inflate(R.layout.item_package_view, parent, false));

            case SHOW_MORE:
                return new ShowMoreViewHolder(LayoutInflater.from(context).inflate(R.layout.item_show, parent, false));

            case CART:
                return new CartViewHolder(LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false));

            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //SQLite DECLARATION
        databaseHelper = new DatabaseHelper(context.getApplicationContext());
        //FIREBASE DECLARATION
        firestore = FirebaseFirestore.getInstance();
        orders = new HashMap<>();

        data = databaseHelper.getAllData();
        while (data.moveToNext()) {
            // this will get the account which is logged
            int status = data.getInt(6);
            if (status == 1) {
                //when logout, set the login states of account as 0
                accountID = data.getInt(0);
                break;
            }
        }

        switch (items.get(position).getViewType()) {
            case BOOKS:

            case STATIONARY:
                ((ViewHolder) holder).nameView.setText(items.get(position).getName());
                ((ViewHolder) holder).priceView.setText(items.get(position).getPrice());
                Picasso.get().load(items.get(position).getImagePath()).into(((ViewHolder) holder).imageView);
                if (items.get(position).getImagePath() == null) {
                    ((ViewHolder) holder).imageView.setImageResource(items.get(position).getImage());
                }
                ((ViewHolder) holder).cartIcon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(items.get(position), "cartIcon");
                    }
                });
                break;

            case BOOKS_TYPE:

            case STATIONARY_TYPE:

            case PACKAGE_TYPE:
                ((TypeViewHolder) holder).itemType.setText(items.get(position).getType());
                break;

            case PACKAGE:
                ((PackageViewHolder) holder).packageNameView.setText(items.get(position).getPackageName());
                ((PackageViewHolder) holder).packagePriceView.setText(items.get(position).getPackagePrice());
                ((PackageViewHolder) holder).packageValidityView.setText(items.get(position).getPackageValidity());
                ((PackageViewHolder) holder).packageInfoView.setText(items.get(position).getPackageInfo());
                ((PackageViewHolder) holder).packageIconView.setImageResource(items.get(position).getPackageIcon());
                ((PackageViewHolder) holder).packageCardView.setCardBackgroundColor(items.get(position).getColor());
                if (items.get(position).getVisibility() == View.VISIBLE) {
                    ((PackageViewHolder) holder).packageCardView.setVisibility(View.VISIBLE);
                } else {
                    ((PackageViewHolder) holder).packageCardView.setVisibility(View.GONE);
                }
                break;

            case SHOW_MORE:
                ((ShowMoreViewHolder) holder).showMore.setText(items.get(position).getName());
                ((ShowMoreViewHolder) holder).showMore.setTextColor(items.get(position).getColor());
                ((ShowMoreViewHolder) holder).showMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(items.get(position), "showmore");
                        ((ShowMoreViewHolder) holder).showMore.setText(items.get(position).getName());
                        changeView = true;
                    }
                });
                break;

            case CART:
                ((CartViewHolder) holder).itemName.setText(items.get(position).getName());
                ((CartViewHolder) holder).itemPrice.setText(items.get(position).getPrice());
                ((CartViewHolder) holder).itemQty.setText(String.valueOf(items.get(position).getQty()));
                Picasso.get().load(items.get(position).getImagePath()).into(((CartViewHolder) holder).itemImage);
                if (items.get(position).getImagePath() == null) {
                    ((CartViewHolder) holder).itemImage.setImageResource(items.get(position).getImage());
                }
                ((CartViewHolder) holder).minus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i = Integer.parseInt( (String) ((CartViewHolder) holder).itemQty.getText().toString());
                        if (i > 1) {
                            ((CartViewHolder) holder).itemQty.setText(String.valueOf(--i));
                            updateQuantity(position, i);
                        }
                    }
                });
                ((CartViewHolder) holder).plus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int i = Integer.parseInt( (String) ((CartViewHolder) holder).itemQty.getText().toString());
                        ((CartViewHolder) holder).itemQty.setText(String.valueOf(++i));
                        updateQuantity(position, i);
                    }
                });
                ((CartViewHolder) holder).delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(items.get(position), "delete");
                        deleteItem(position);
                    }
                });
                ((CartViewHolder) holder).buy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addOrdersToSQLite(position);
                    }
                });
                ((CartViewHolder) holder).relativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((CartViewHolder) holder).itemQty.clearFocus();
                    }
                });
        }

    }

    public interface OnItemClickListener {
        void onItemClick(Item position, String clickType);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public void updateQuantity(int position, int i) {
        data = databaseHelper.getCartItemDetails(accountID);
        if (data != null && data.moveToPosition(position)) {
            databaseHelper.updateQuantity(data.getInt(0), i);
        }
    }

    public void deleteItem(int position) {
        data = databaseHelper.getCartItemDetails(accountID);
        if (data != null && data.moveToPosition(position)) {
            databaseHelper.deleteCartItem(data.getInt(0));
            Toast.makeText(context, "Item deleted", Toast.LENGTH_SHORT).show();
            //after deletion, there should refresh the cart fragment
        } else {
            Toast.makeText(context, "error !", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("DefaultLocale")
    public void addOrdersToFirestore(int position) {
        data = databaseHelper.getAllData();
        while (data.moveToNext()) {
            // this will get the account which is logged
            int status = data.getInt(6);
            if (status == 1) {
                //when logout, set the login states of account as 0
                user = data.getString(1);
                break;
            }
        }

        data = databaseHelper.getAllCartItems();
        if (data != null && data.moveToPosition(position)) {
            itemId = String.valueOf(data.getInt(0));
            item = data.getString(3);
            unitPrice = data.getString(4);
            qty = String.valueOf(data.getInt(6));

            //calculate final price of selected cart item
            String result = unitPrice.substring(unitPrice.indexOf(' ') + 1);
            float amount = Float.parseFloat(result);
            float priceFloat = amount * Integer.parseInt(qty);
            price = String.format("%.2f", priceFloat);

            //////////////////////  ADD ORDERS TO FIREBASE DATABASE ////////////////////
            orders.put("user", user);
            orders.put("item", item);
            orders.put("unitPrice", unitPrice);
            orders.put("qty", qty);
            orders.put("price", price);

            firestore.collection("orders").document("1").collection(itemId).document(item).set(orders).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        Toast.makeText(context, "Item added to order", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(context, "data not found", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("DefaultLocale")
    public void addOrdersToSQLite(int position) {

        //get selected cart item from SQLite database
        data = databaseHelper.getCartItemDetails(accountID);
        if (data != null && data.moveToPosition(position)) {
            itemId = String.valueOf(data.getInt(0));
            item = data.getString(3);
            unitPrice = data.getString(4);
            qty = String.valueOf(data.getInt(6));

            //calculate final price of selected cart item
            String result = unitPrice.substring(unitPrice.indexOf(' ') + 1);
            float amount = Float.parseFloat(result);
            float priceFloat = amount * Integer.parseInt(qty);
            price = String.format("%.2f", priceFloat);

            //add order item to SQLite database
            boolean isAdded = databaseHelper.addOrderItem(data.getInt(0), item, unitPrice, qty, price);
            if (isAdded) {
                Toast.makeText(context, "Item added to order", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Item is not add to order", Toast.LENGTH_SHORT).show();
            }
        }

    }
}
