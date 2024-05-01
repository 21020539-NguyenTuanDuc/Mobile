package com.example.mobile.Model;

import com.google.firebase.Timestamp;

public class CoinTransactionModel {
    String ID;
    long amount;
    Timestamp timestamp;
    long coin;
    String token;
    String UserID;

    public CoinTransactionModel() {
    }

    public CoinTransactionModel(String ID, long amount, Timestamp timestamp, long coin, String token, String userID) {
        this.ID = ID;
        this.amount = amount;
        this.timestamp = timestamp;
        this.coin = coin;
        this.token = token;
        UserID = userID;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userID) {
        UserID = userID;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public long getCoin() {
        return coin;
    }

    public void setCoin(long coin) {
        this.coin = coin;
    }
}
