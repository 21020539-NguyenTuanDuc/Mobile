package com.example.mobile.Model;

import java.io.Serializable;
import java.util.List;

public class ChapterModel implements Serializable {
    private String id;
    private List<String> list;
    private long price;
    private List<String> users;

    public ChapterModel() {
    }

    public ChapterModel(String id, List<String> list, long price) {
        this.id= id;
        this.list= list;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
    public long getPrice() {return price;}
    public void setPrice(long price) { this.price = price; }
    public List<String> getUsers() {
        return users;
    }

    public void setUsers(List<String> users) {
        this.users = users;
    }
}
