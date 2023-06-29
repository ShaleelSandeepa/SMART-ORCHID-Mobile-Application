package com.designproject.smartorchid.recycler;

public class ItemNotification {

    int notificationID, notificationColor;
    String notificationTopic, notificationDetails, notificationDate, notificationTime, notificationState;

    public ItemNotification(int notificationID, String notificationTopic, String notificationDetails, String notificationDate, String notificationTime) {
        this.notificationID = notificationID;
        this.notificationTopic = notificationTopic;
        this.notificationDetails = notificationDetails;
        this.notificationDate = notificationDate;
        this.notificationTime = notificationTime;
    }

    public int getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(int notificationID) {
        this.notificationID = notificationID;
    }

    public int getNotificationColor() {
        return notificationColor;
    }

    public void setNotificationColor(int notificationColor) {
        this.notificationColor = notificationColor;
    }

    public String getNotificationTopic() {
        return notificationTopic;
    }

    public void setNotificationTopic(String notificationTopic) {
        this.notificationTopic = notificationTopic;
    }

    public String getNotificationDetails() {
        return notificationDetails;
    }

    public void setNotificationDetails(String notificationDetails) {
        this.notificationDetails = notificationDetails;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public void setNotificationDate(String notificationDate) {
        this.notificationDate = notificationDate;
    }

    public String getNotificationTime() {
        return notificationTime;
    }

    public void setNotificationTime(String notificationTime) {
        this.notificationTime = notificationTime;
    }

    public String getNotificationState() {
        return notificationState;
    }

    public void setNotificationState(String notificationState) {
        this.notificationState = notificationState;
    }
}
