package com.example.mobile.Model;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
    private String id;
    private String name;
    private String number;
    private String email;
    private boolean signIn = false;
    private List<String> favoriteList = new ArrayList<>();
    private List<String> historyList = new ArrayList<>();
    private long coin = 0;
    private long vipExpiredTimestamp = -1;

    public UserModel() {

    }

    public UserModel(String id, String name, String number, String email) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.email = email;
    }

    public UserModel(String name, String number, String email) {
        this.name = name;
        this.number = number;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isSignIn() {
        return signIn;
    }

    public void setSignIn(boolean signIn) {
        this.signIn = signIn;
    }

    public List<String> getFavoriteList() {
        return favoriteList;
    }

    public void setFavoriteList(List<String> favoriteList) {
        this.favoriteList = favoriteList;
    }

    public long getCoin() {
        return coin;
    }

    public void setCoin(long coin) {
        this.coin = coin;
    }

    public long getVipExpiredTimestamp() {
        return vipExpiredTimestamp;
    }

    public void setVipExpiredTimestamp(long vipExpiredTimestamp) {
        this.vipExpiredTimestamp = vipExpiredTimestamp;
    }

    public List<String> getHistoryList() {
        return historyList;
    }

    public void setHistoryList(List<String> historyList) {
        this.historyList = historyList;
    }
}
