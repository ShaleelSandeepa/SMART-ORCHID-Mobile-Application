package com.designproject.smartorchid.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_SMART_ORCHID = "SmartOrchid.db";

    //User accounts related database variables
    public static final String TABLE_ACCOUNTS = "accounts_table";
    public static final String ACCOUNT_ID = "ID";
    public static final String ACCOUNT_USER_NAME = "USER_NAME";
    public static final String ACCOUNT_EMAIL = "EMAIL";
    public static final String ACCOUNT_PHONE = "PHONE";
    public static final String ACCOUNT_PASSWORD = "PASSWORD";
    public static final String ACCOUNT_REMEMBER = "REMEMBER";
    public static final String ACCOUNT_LOGIN = "IS_LOGGED";

    //Remember me table related database variables
    public static final String TABLE_REMEMBER = "remember_me_table";
    public static final String REMEMBER_ID = "ID";
    public static final String REMEMBER_ACCOUNT_ID = "ACCOUNT_ID";
    public static final String REMEMBER_STATUS = "REMEMBER_ME";

    // table related database variables
    public static final String TABLE_CART = "cart_table";
    public static final String CART_ITEM_ID = "ITEM_ID"; //0
    public static final String CART_ACCOUNT_ID = "ACCOUNT_ID"; //1
    public static final String CART_ITEM_TYPE = "ITEM_TYPE";  //2
    public static final String CART_ITEM_NAME = "ITEM_NAME";  //3
    public static final String CART_ITEM_PRICE = "ITEM_PRICE";  //4
    public static final String CART_ITEM_IMAGE = "ITEM_IMAGE";  //5
    public static final String CART_ITEM_QTY = "ITEM_QTY";  //6
    public static final String CART_ITEM_IMG_URI = "ITEM_IMG_URI";  //6

    //Add Orders table related database variables
    public static final String TABLE_ORDERS = "orders_table";
    public static final String ORDER_ITEM_ID = "ITEM_ID"; //0
    public static final String ORDER_CART_ITEM_ID = "CART_ITEM_ID"; //1
    public static final String ORDER_ITEM_NAME = "ITEM_NAME";  //2
    public static final String ORDER_ITEM_UNIT_PRICE = "ITEM_UNIT_PRICE";  //3
    public static final String ORDER_ITEM_QTY = "ITEM_QTY";  //4
    public static final String ORDER_ITEM_PRICE = "ITEM_PRICE";  //5

    //Order History table related database variables
    public static final String TABLE_ORDERS_HISTORY = "orders_history_table";
    public static final String ORDER_ID = "ORDER_ID"; //0
    public static final String ORDER_USER = "USER"; //1
    public static final String ORDER_DETAILS = "ORDER_DETAILS"; //2
    public static final String ORDER_COLOR = "ORDER_COLOR"; //3

    //Settings table related database variables
    public static final String TABLE_SETTINGS = "setting_table";
    public static final String SETTING_ID = "SETTING_ID";
    public static final String SETTING_NAME = "SETTING_NAME";
    public static final String SETTING_STATE = "SETTING_STATE";

    //Notification table related database variables
    public static final String TABLE_NOTIFICATIONS = "notification_table";
    public static final String NOTIFICATION_ID = "ID"; //0
    public static final String NOTIFICATION_ACCOUNT_ID = "ACCOUNT_ID"; //1
    public static final String NOTIFICATION_DATE = "DATE"; //2
    public static final String NOTIFICATION_TIME = "TIME"; //3
    public static final String NOTIFICATION_STATE = "STATE"; //4
    public static final String NOTIFICATION_TOPIC = "TOPIC"; //5
    public static final String NOTIFICATION_DETAILS = "DETAILS"; //6

    //User profile details related database variables
    public static final String TABLE_USER_DETAILS = "user_details_table";
    public static final String USER_ID = "USER_ID"; //0
    public static final String USER_FULL_NAME = "USER_FULL_NAME"; //1
    public static final String USER_GENDER = "USER_GENDER"; //2
    public static final String USER_TEL = "USER_TEL"; //3
    public static final String USER_ADDRESS = "USER_ADDRESS"; //4
    public static final String USER_POSTAL_CODE = "USER_POSTAL_CODE"; //5

    public DatabaseHelper(Context context) {
        super(context, DATABASE_SMART_ORCHID, null, 1);
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        //create accounts table
        String CREATE_ACCOUNTS_TABLE_QUERY = "CREATE TABLE " + TABLE_ACCOUNTS + " (" +
                ACCOUNT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ACCOUNT_USER_NAME + " TEXT, " +
                ACCOUNT_EMAIL + " TEXT, " +
                ACCOUNT_PHONE + " TEXT, " +
                ACCOUNT_PASSWORD + " TEXT, " +
                ACCOUNT_REMEMBER + " INTEGER, " +
                ACCOUNT_LOGIN + " INTEGER)";
        db.execSQL(CREATE_ACCOUNTS_TABLE_QUERY);

        //create remember me table
        String CREATE_REMEMBER_TABLE_QUERY = "CREATE TABLE " + TABLE_REMEMBER + " (" +
                REMEMBER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                REMEMBER_ACCOUNT_ID + " INTEGER, " +
                REMEMBER_STATUS + " INTEGER)";
        db.execSQL(CREATE_REMEMBER_TABLE_QUERY);

        //create cart table
        String CREATE_CART_TABLE_QUERY = "CREATE TABLE " + TABLE_CART + " (" +
                CART_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CART_ACCOUNT_ID + " INTEGER, " +
                CART_ITEM_TYPE + " INTEGER, " +
                CART_ITEM_NAME + " TEXT, " +
                CART_ITEM_PRICE + " TEXT, " +
                CART_ITEM_IMAGE + " INTEGER, " +
                CART_ITEM_QTY + " INTEGER, " +
                CART_ITEM_IMG_URI + " TEXT)";
        db.execSQL(CREATE_CART_TABLE_QUERY);

        //create orders table
        String CREATE_ORDERS_TABLE_QUERY = "CREATE TABLE " + TABLE_ORDERS + " (" +
                ORDER_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ORDER_CART_ITEM_ID + " INTEGER, " +
                ORDER_ITEM_NAME + " TEXT, " +
                ORDER_ITEM_UNIT_PRICE + " TEXT, " +
                ORDER_ITEM_QTY + " INTEGER, " +
                ORDER_ITEM_PRICE + " TEXT)";
        db.execSQL(CREATE_ORDERS_TABLE_QUERY);

        //create orders history table
        String CREATE_ORDERS_HISTORY_TABLE_QUERY = "CREATE TABLE " + TABLE_ORDERS_HISTORY + " (" +
                ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ORDER_USER + " TEXT, " +
                ORDER_DETAILS + " TEXT, " +
                ORDER_COLOR + " INTEGER)";
        db.execSQL(CREATE_ORDERS_HISTORY_TABLE_QUERY);

        //create notification table
        String CREATE_NOTIFICATION_TABLE_QUERY = "CREATE TABLE " + TABLE_NOTIFICATIONS + " (" +
                NOTIFICATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                NOTIFICATION_ACCOUNT_ID + " INTEGER, " +
                NOTIFICATION_DATE + " TEXT, " +
                NOTIFICATION_TIME + " TEXT, " +
                NOTIFICATION_STATE + " TEXT, " +
                NOTIFICATION_TOPIC + " TEXT, " +
                NOTIFICATION_DETAILS + " TEXT)";
        db.execSQL(CREATE_NOTIFICATION_TABLE_QUERY);

        //create user details table
        String CREATE_USER_DETAILS_TABLE_QUERY = "CREATE TABLE " + TABLE_USER_DETAILS + " (" +
                USER_ID + " INTEGER PRIMARY KEY, " +
                USER_FULL_NAME + " TEXT, " +
                USER_GENDER + " TEXT, " +
                USER_TEL + " TEXT, " +
                USER_ADDRESS + " TEXT, " +
                USER_POSTAL_CODE + " TEXT)";
        db.execSQL(CREATE_USER_DETAILS_TABLE_QUERY);

        //create settings table
        String CREATE_SETTINGS_TABLE_QUERY = "CREATE TABLE " + TABLE_SETTINGS + " (" +
                SETTING_ID + " INTEGER PRIMARY KEY, " +
                SETTING_NAME + " TEXT, " +
                SETTING_STATE + " INTEGER)";
        db.execSQL(CREATE_SETTINGS_TABLE_QUERY);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ACCOUNTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMEMBER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CART);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS_HISTORY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_DETAILS);
        onCreate(db);
    }

    ///////////////////////// USER REGISTER & LOGIN /////////////////////////

    //insert new user details when registering
    public boolean registerUser(String userName, String email, String phone, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_USER_NAME, userName);
        contentValues.put(ACCOUNT_EMAIL, email);
        contentValues.put(ACCOUNT_PHONE, phone);
        contentValues.put(ACCOUNT_PASSWORD, password);
        contentValues.put(ACCOUNT_REMEMBER, String.valueOf(0));
        contentValues.put(ACCOUNT_LOGIN, String.valueOf(0));
        long result = db.insert(TABLE_ACCOUNTS, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    // update the account password using account id
    public boolean updatePassword(String id, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ACCOUNT_PASSWORD, password);
        long count = db.update(TABLE_ACCOUNTS, contentValues,
                "ID = ?", new String[]{id});
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    // update login status as 1 in logged in account
    public void updateLoginStatus(String id , int status){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACCOUNT_LOGIN, status);
        db.update(TABLE_ACCOUNTS , values , "ID=?" , new String[]{String.valueOf(id)});
    }

    //delete account from account table
    public boolean deleteAccount(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (id > -1) {
            long is_deleted = db.delete(TABLE_ACCOUNTS, "ID = ? ", new String[]{String.valueOf(id)});
            return is_deleted > 0;
        }
        return false;
    }

    //get all the data from accounts table
    public Cursor getAllData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS, null);
        return result;
    }

    //get all the data of specific user from accounts table
    public Cursor getUserData(String userName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS + " WHERE " + ACCOUNT_USER_NAME + " = ?", new String[]{userName});
        return result;
    }

    //get the data of specific user by account ID
    public Cursor getUserDataById(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS + " WHERE " + ACCOUNT_ID + " = ?", new String[]{id});
        return result;
    }

    //get the logged in account ID
    public Cursor getLoggedInAccount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS + " WHERE " + ACCOUNT_LOGIN + " = 1", null);
        return result;
    }

    ///////////////////////// LOGIN VALIDATIONS /////////////////////////

    //check the username that enter by the user match with database
    public boolean checkUserName(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS + " WHERE " + ACCOUNT_USER_NAME + " = ?", new String[]{username});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }

    }

    //user name and password match together when login
    public boolean checkUserNamePassword(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS + " WHERE " + ACCOUNT_USER_NAME + " = ? AND " + ACCOUNT_PASSWORD + " = ?", new String[]{username, password});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }

    }

    public boolean checkPhone(String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS + " WHERE " + ACCOUNT_PHONE + " = ?", new String[]{phone});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }

    }

    //check username and phone match together when change password in forgot password interface
    public boolean checkUserNamePhone(String username, String phone) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_ACCOUNTS + " WHERE " + ACCOUNT_USER_NAME + " = ? AND " + ACCOUNT_PHONE + " = ?", new String[]{username, phone});
        if (cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }

    }

    ///////////////////////// REMEMBER ME OPTION RELATED METHODS & VALIDATIONS /////////////////////////

    //update status when user select "remember me" option in accounts table
    public void updateRemember(String id , int remember){
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(ACCOUNT_REMEMBER, remember);
        db.update(TABLE_ACCOUNTS , values , "ID=?" , new String[]{String.valueOf(id)});
    }

    //get the all raw data of remember me table and call this method when want to check remember me status
    public Cursor checkRememberMe() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_REMEMBER, null);
        return result;
    }

    //newly added account has inserted new raw data by this method
    public void insertRemember(String accId) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(REMEMBER_ACCOUNT_ID, accId);
        contentValues.put(REMEMBER_STATUS, String.valueOf(1));
        long result = db.insert(TABLE_REMEMBER, null, contentValues);

    }

    //when account deleted, raw data related that account will be deleted also
    public void deleteRemember(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (id > -1) {
            db.delete(TABLE_REMEMBER, "ACCOUNT_ID = ? ", new String[]{String.valueOf(id)});
        }
    }

    //update status when user select "remember me" option in remember me table
    public void updateRememberTableRemember(int id, int status) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(REMEMBER_STATUS, status);
        db.update(TABLE_REMEMBER , values , "ACCOUNT_ID=?" , new String[]{String.valueOf(id)});

    }

    public Cursor findAccountIdRememberTable(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_REMEMBER + " WHERE " + REMEMBER_ACCOUNT_ID + " = ?", new String[]{id});
        return result;
    }

    ///////////////////////// CART /////////////////////////

    // add item to cart
    public boolean addToCart(int accountID, int type, String name, String price, int image, int qty, String uri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CART_ACCOUNT_ID, String.valueOf(accountID));
        contentValues.put(CART_ITEM_TYPE, String.valueOf(type));
        contentValues.put(CART_ITEM_NAME, name);
        contentValues.put(CART_ITEM_PRICE, price);
        contentValues.put(CART_ITEM_IMAGE, String.valueOf(image));
        contentValues.put(CART_ITEM_QTY, String.valueOf(qty));
        contentValues.put(CART_ITEM_IMG_URI, uri);
        long result = db.insert(TABLE_CART, null, contentValues);
        return result != -1;
    }

    //get all the cart items from cart table
    public Cursor getAllCartItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_CART, null);
        return result;
    }

    // update quantity of cart items
    public void updateQuantity(int id, int qty) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(CART_ITEM_QTY, qty);
        db.update(TABLE_CART , values , "ITEM_ID = ?" , new String[]{String.valueOf(id)});
    }

    public void deleteCartItem(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (id > -1) {
            db.delete(TABLE_CART, "ITEM_ID = ? ", new String[]{String.valueOf(id)});
        }
    }

    public void deleteAllCartItemByAccountID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (id > -1) {
            db.delete(TABLE_CART, "ACCOUNT_ID = ? ", new String[]{String.valueOf(id)});
        }
    }

    //get the relevant cart item details by id
    public Cursor getCartItemDetails(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_CART + " WHERE " + CART_ACCOUNT_ID + " = ?", new String[]{String.valueOf(id)});
        return result;
    }

    ///////////////////////// MAKE ORDER /////////////////////////

    // add item to orders table
    public boolean addOrderItem(int cartItemID, String name, String unitPrice, String qty, String price) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ORDER_CART_ITEM_ID, String.valueOf(cartItemID));
        contentValues.put(ORDER_ITEM_NAME, name);
        contentValues.put(ORDER_ITEM_UNIT_PRICE, unitPrice);
        contentValues.put(ORDER_ITEM_QTY, qty);
        contentValues.put(ORDER_ITEM_PRICE, price);
        long result = db.insert(TABLE_ORDERS, null, contentValues);
        return result != -1;
    }

    //get all the order items from order table
    public Cursor getAllOrderItems() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_ORDERS, null);
        return result;
    }

    //cancel order in add order page
    public void cancelOrderItems(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (id > -1) {
            db.delete(TABLE_ORDERS, "ITEM_ID = ? ", new String[]{String.valueOf(id)});
        }
    }

    ///////////////////////// ORDER HISTORY TABLE /////////////////////////

    public boolean insertOrderHistory(String name, String details, int color) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(ORDER_USER, name);
        contentValues.put(ORDER_DETAILS, details);
        contentValues.put(ORDER_COLOR, color);
        long result = db.insert(TABLE_ORDERS_HISTORY, null, contentValues);
        return result != -1;
    }

    //get all the orders from order history table
    public Cursor getAllOrderHistory() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_ORDERS_HISTORY, null);
        return result;
    }

    //get order history by order ID
    public Cursor getOrderHistoryByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_ORDERS_HISTORY + " WHERE " + ORDER_ID + " = ?", new String[]{String.valueOf(id)});
        return result;
    }

    //get order history color by order ID
    public void updateOrderHistoryColor(int id, int color) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(ORDER_COLOR, color);
        db.update(TABLE_ORDERS_HISTORY , values , "ORDER_ID = ?" , new String[]{String.valueOf(id)});
    }

    public void deleteAllOrdersByUserName(String userName) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (!userName.isEmpty()) {
            db.delete(TABLE_ORDERS_HISTORY, "USER = ? ", new String[]{userName});
        }
    }

    ///////////////////////// NOTIFICATION TABLE /////////////////////////

    //add notifications
    public boolean addNotification(int accountId, String date, String time, String state, String topic, String details) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NOTIFICATION_ACCOUNT_ID, accountId);
        contentValues.put(NOTIFICATION_DATE, date);
        contentValues.put(NOTIFICATION_TIME, time);
        contentValues.put(NOTIFICATION_STATE, state);
        contentValues.put(NOTIFICATION_TOPIC, topic);
        contentValues.put(NOTIFICATION_DETAILS, details);
        long result = db.insert(TABLE_NOTIFICATIONS, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //get all notifications from notification table
    public Cursor getAllNotifications() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS, null);
        return result;
    }

    //delete all notifications
    public void deleteAllNotifications(int accountID) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (accountID > -1) {
            db.delete(TABLE_NOTIFICATIONS, "ACCOUNT_ID = ? ", new String[]{String.valueOf(accountID)});
        }
        db.close();
    }

    //get one notifications by ID
    public Cursor getNotificationByID(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE " + NOTIFICATION_ID + " = ?", new String[]{String.valueOf(id)});
        return result;
    }

    //get all notifications by AccountID
    public Cursor getNotificationByAccountID(int accountID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_NOTIFICATIONS + " WHERE " + NOTIFICATION_ACCOUNT_ID + " = ?", new String[]{String.valueOf(accountID)});
        return result;
    }

    //update notification state by Notification ID
    public void updateNotificationState(int id, String state) {
        SQLiteDatabase db = this.getReadableDatabase();
        ContentValues values = new ContentValues();
        values.put(NOTIFICATION_STATE, state);
        db.update(TABLE_NOTIFICATIONS , values , "ID = ?" , new String[]{String.valueOf(id)});
    }

    //delete notification one by one in notifications
    public void deleteNotification(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (id > -1) {
            db.delete(TABLE_NOTIFICATIONS, "ID = ? ", new String[]{String.valueOf(id)});
        }
    }

    //////////////////////////// USER DETAILS /////////////////////////////////////

    //insert profile details when registering
    public boolean insertProfileDetails(int id, String fullName, String gender, String tel, String address, String postalCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_ID, id);
        contentValues.put(USER_FULL_NAME, fullName);
        contentValues.put(USER_GENDER, gender);
        contentValues.put(USER_TEL, tel);
        contentValues.put(USER_ADDRESS, address);
        contentValues.put(USER_POSTAL_CODE, postalCode);
        long result = db.insert(TABLE_USER_DETAILS, null, contentValues);
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    //get the data of specific user profile by account ID
    public Cursor getUserProfileById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_USER_DETAILS + " WHERE " + USER_ID + " = ?", new String[]{String.valueOf(id)});
        return result;
    }

    // update the profile details using account id
    public boolean updateProfileDetails(int id, String fullName, String gender, String tel, String address, String postalCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_FULL_NAME, fullName);
        contentValues.put(USER_GENDER, gender);
        contentValues.put(USER_TEL, tel);
        contentValues.put(USER_ADDRESS, address);
        contentValues.put(USER_POSTAL_CODE, postalCode);
        long count = db.update(TABLE_USER_DETAILS, contentValues,
                "USER_ID = ?", new String[]{String.valueOf(id)});
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    //delete all profile details by user ID
    public void deleteProfileDetails(int accountID) {
        SQLiteDatabase db = this.getWritableDatabase();
        if (accountID > -1) {
            db.delete(TABLE_USER_DETAILS, "USER_ID = ? ", new String[]{String.valueOf(accountID)});
        }
        db.close();
    }

    ////////////////////////// SETTINGS ///////////////////////////////////////

    //add Image to user details table
    public void addSettings(int id, String name, int state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(SETTING_ID, id);
        values.put(SETTING_NAME, name);
        values.put(SETTING_STATE, state);
        long count = db.insert(TABLE_SETTINGS, null, values);
        if (count > 0) {
        } else {
        }
    }

    // update settings
    public boolean updateSettings(int id, int state) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(SETTING_ID, id);
        contentValues.put(SETTING_STATE, state);
        long count = db.update(TABLE_SETTINGS, contentValues,
                "SETTING_ID = ?", new String[]{String.valueOf(id)});
        if (count > 0) {
            return true;
        } else {
            return false;
        }
    }

    //get settings
    public Cursor getSetting(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor result = db.rawQuery("SELECT * FROM " + TABLE_SETTINGS + " WHERE " + SETTING_ID + " = ?", new String[]{String.valueOf(id)});
        return result;
    }

}
