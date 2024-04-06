package com.example.mobile.Model;

import com.google.firebase.Timestamp;

public class SubTransactionModel {
    String ID;
    Timestamp timestamp;
    long amount;
    String token;
    int packageType;
    String UserID;

    public SubTransactionModel() {
    }

    public SubTransactionModel(String ID, Timestamp timestamp, long amount, String token, int packageType, String userID) {
        this.ID = ID;
        this.timestamp = timestamp;
        this.amount = amount;
        this.token = token;
        this.packageType = packageType;
        UserID = userID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public int getPackageType() {
        return packageType;
    }

    public void setPackageType(int packageType) {
        this.packageType = packageType;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
