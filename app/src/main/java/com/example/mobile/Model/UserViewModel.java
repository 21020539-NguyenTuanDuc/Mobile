package com.example.mobile.Model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.mobile.Model.UserModel;

public class UserViewModel extends ViewModel {
    private final MutableLiveData<UserModel> currentUser = new MutableLiveData<>();

    public void setCurrentUser(UserModel user) {
        currentUser.setValue(user);
    }

    public LiveData<UserModel> getCurrentUser() {
        return currentUser;
    }
}