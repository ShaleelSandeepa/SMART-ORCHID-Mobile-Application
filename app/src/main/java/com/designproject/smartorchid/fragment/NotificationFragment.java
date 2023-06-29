package com.designproject.smartorchid.fragment;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.designproject.smartorchid.MyViewModel;
import com.designproject.smartorchid.R;
import com.designproject.smartorchid.data.DatabaseHelper;
import com.designproject.smartorchid.recycler.ItemNotification;
import com.designproject.smartorchid.recycler.NotificationAdapter;
import com.designproject.smartorchid.recycler.OrderHistoryAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerViewNotification;
    List<ItemNotification> notificationList;
    NotificationAdapter notificationAdapter;

    Cursor cursor, data;
    DatabaseHelper databaseHelper;
    int accountID;
    Button readAll, deleteAll;
    TextView textEmpty;
    AlertDialog.Builder alertDialog;
    MyViewModel myViewModelNotification;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notification, container, false);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get the current date and time
        Date currentDate = new Date();
        // Format the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String formattedDate = dateFormat.format(currentDate);
        // Format the time
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String formattedTime = timeFormat.format(currentDate);

        textEmpty = view.findViewById(R.id.txtNotificationEmpty);
        readAll = view.findViewById(R.id.btnReadAll);
        deleteAll = view.findViewById(R.id.btnDeleteAll);
        readAll.setOnClickListener(this);
        deleteAll.setOnClickListener(this);

        recyclerViewNotification = view.findViewById(R.id.recyclerNotification);
        notificationList = new ArrayList<>();
        databaseHelper = new DatabaseHelper(getContext());

        //check the account ID which is logged in
        cursor = databaseHelper.getLoggedInAccount();
        if (cursor.moveToFirst()) {
            accountID = cursor.getInt(0);
        }

        //get the notifications by account ID
        cursor = databaseHelper.getNotificationByAccountID(accountID);
        if (cursor.getCount() == 0) {
            textEmpty.setVisibility(View.VISIBLE);
        } else {
            textEmpty.setVisibility(View.GONE);
        }
        if (cursor.moveToLast()) {
            int unread = 0;
            do {
                //add notifications to the recycler view when onViewCreated notification fragment
                notificationList.add(new ItemNotification(cursor.getInt(0), cursor.getString(5), cursor.getString(6),
                        cursor.getString(2), cursor.getString(3)));
                if (cursor.getString(4).equals("unread")) {
                    unread++;
                }
            } while (cursor.moveToPrevious());
            //send the count of unread notifications to home activity via view model
            myViewModelNotification = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
            myViewModelNotification.setNotificationCount(unread);
        }
        cursor.close();

        notificationAdapter = new NotificationAdapter(getContext(), notificationList, new NotificationAdapter.OnNotificationClickListener() {
            @Override
            public void onItemClick(ItemNotification position, int index, String clickType) {

                if (clickType.equals("delete")) {
                    replaceFragment(new NotificationFragment());
                }

                //when read the notification it will refresh badge count from executing this codes
                if (clickType.equals("itemView")) {
                    Cursor data = databaseHelper.getNotificationByAccountID(accountID);
                    if (data.moveToLast()) {
                        int unread = 0;
                        do {
                            if (data.getString(4).equals("unread")) {
                                unread++;
                            }
                        } while (data.moveToPrevious());
                        //send the count of unread notifications to home activity via view model
                        myViewModelNotification = new ViewModelProvider(requireActivity()).get(MyViewModel.class);
                        myViewModelNotification.setNotificationCount(unread);
                    }
                }
            }
        });

        recyclerViewNotification.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerViewNotification.setAdapter(notificationAdapter);
        notificationAdapter.notifyDataSetChanged();

    }

    //Method for replace fragments
    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getParentFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.commit();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btnReadAll:
                if (notificationList.size() == 0) {
                    Toast.makeText(getContext(), "Notifications empty !", Toast.LENGTH_SHORT).show();
                    break;
                }
                alertDialog = new AlertDialog.Builder(requireContext());
                alertDialog.setTitle("Mark all as Read");
                alertDialog.setMessage("Are you sure to mark all as read?");

                //When click "Yes" it will execute this
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int i = 0;
                        while (i < notificationList.size()) {
                            databaseHelper.updateNotificationState(notificationList.get(i).getNotificationID(), "read");
                            i++;
                        }
                        replaceFragment(new NotificationFragment());
                    }
                });

                //When click "No" it will execute this
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
                break;

            case R.id.btnDeleteAll:
                if (notificationList.size() == 0) {
                    Toast.makeText(getContext(), "Notifications empty !", Toast.LENGTH_SHORT).show();
                    break;
                }
                alertDialog = new AlertDialog.Builder(requireContext());
                alertDialog.setTitle("Delete All Notifications");
                alertDialog.setMessage("Are you sure to delete all notifications ?");

                //When click "Yes" it will execute this
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        databaseHelper.deleteAllNotifications(accountID);
                        replaceFragment(new NotificationFragment());
                        Toast.makeText(getContext(), "All notifications deleted", Toast.LENGTH_SHORT).show();
                    }
                });

                //When click "No" it will execute this
                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialog.show();
                break;
        }
    }
}