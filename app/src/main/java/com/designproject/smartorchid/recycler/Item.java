package com.designproject.smartorchid.recycler;

import android.net.Uri;

public class Item {

    public static final int BOOKS = 1;
    public static final int BOOKS_TYPE = 2;
    public static final int STATIONARY = 3;
    public static final int STATIONARY_TYPE = 4;
    public static final int PACKAGE = 5;
    public static final int PACKAGE_TYPE = 6;
    public static final int SHOW_MORE = 7;
    public static final int CART = 8;

    private final int viewType;

    //books & stationary related variables
    String name, price, type;
    int image, qty, itemType, cartItemID;
    String imagePath;

    //network related variables
    String packageName, packagePrice, packageValidity, packageInfo;
    int packageIcon, color, visibility;

    //book item view & stationary item view
    public Item(int viewType, String name, String price, int image) {
        this.viewType = viewType;
        this.name = name;
        this.price = price;
        this.image = image;
    }

    //book item view & stationary item view for firebase details
    public Item(int viewType, String name, String price, String imagePath) {
        this.viewType = viewType;
        this.name = name;
        this.price = price;
        this.imagePath = imagePath;
    }

    //book item view & stationary item view in cart
    public Item(int viewType, int itemType, String name, String price, int image, int qty, String imagePath) {
        this.viewType = viewType;
        this.itemType = itemType;
        this.name = name;
        this.price = price;
        this.image = image;
        this.qty = qty;
        this.imagePath = imagePath;

    }

    //book item type view & stationary item type view
    //network package type view
    public Item(int viewType, String type) {
        this.viewType = viewType;
        this.type = type;
    }

    //network package view
    public Item(int viewType, int visibility, String packageName, String packagePrice, String packageValidity, String packageInfo, int packageIcon, int color) {
        this.viewType = viewType;
        this.packageName = packageName;
        this.packagePrice = packagePrice;
        this.packageValidity = packageValidity;
        this.packageInfo = packageInfo;
        this.packageIcon = packageIcon;
        this.color = color;
        this.visibility = visibility;
    }

    //see more color change
    public Item(int viewType, String name, int color) {
        this.viewType = viewType;
        this.name = name;
        this.color = color;
    }

    //Books & Stationary related getters & setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getViewType() {
        return viewType;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    //Network related getters & setters

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackagePrice() {
        return packagePrice;
    }

    public void setPackagePrice(String packagePrice) {
        this.packagePrice = packagePrice;
    }

    public String getPackageValidity() {
        return packageValidity;
    }

    public void setPackageValidity(String packageValidity) {
        this.packageValidity = packageValidity;
    }

    public String getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(String packageInfo) {
        this.packageInfo = packageInfo;
    }

    public int getPackageIcon() {
        return packageIcon;
    }

    public void setPackageIcon(int packageIcon) {
        this.packageIcon = packageIcon;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public int getVisibility() {
        return visibility;
    }

    public void setVisibility(int visibility) {
        this.visibility = visibility;
    }


    //cart related getters & setters

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getCartItemID() {
        return cartItemID;
    }

    public void setCartItemID(int cartItemID) {
        this.cartItemID = cartItemID;
    }
}
