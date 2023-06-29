package com.designproject.smartorchid.recycler;

public class ItemOrderHistory {

    String orderID;
    String orderDetails;
    int color;

    public ItemOrderHistory(String orderID, String orderDetails, int color) {
        this.orderID = orderID;
        this.orderDetails = orderDetails;
        this.color = color;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(String orderDetails) {
        this.orderDetails = orderDetails;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
