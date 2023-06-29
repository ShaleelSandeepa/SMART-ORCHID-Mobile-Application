package com.designproject.smartorchid.recycler;

public class ItemOrder {

    String itemId;
    String item;
    String unitPrice;
    String qty;
    String price;

    public ItemOrder(String itemId, String item, String unitPrice, String qty, String price) {
        this.itemId = itemId;
        this.item = item;
        this.unitPrice = unitPrice;
        this.qty = qty;
        this.price = price;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItem() {
        return item;
    }

    public void setItem(String item) {
        this.item = item;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }

    public String getQty() {
        return qty;
    }

    public void setQty(String qty) {
        this.qty = qty;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
