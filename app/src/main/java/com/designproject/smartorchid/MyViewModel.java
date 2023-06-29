package com.designproject.smartorchid;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyViewModel extends ViewModel {

    private MutableLiveData<Integer> cartCount = new MutableLiveData<>();
    private MutableLiveData<Integer> notificationCount = new MutableLiveData<>();

    public MutableLiveData<Integer> getCartCount() {
        return cartCount;
    }

    public void setCartCount(int value) {
        cartCount.setValue(value);
    }

    public MutableLiveData<Integer> getNotificationCount() {
        return notificationCount;
    }

    public void setNotificationCount(int value) {
        notificationCount.setValue(value);
    }


}
