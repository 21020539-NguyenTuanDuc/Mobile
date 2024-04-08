package com.example.mobile.Model;

import java.io.Serializable;
import java.util.List;

public class ChapterModel implements Serializable {
    private String id;
    private List<String> list;

    public ChapterModel() {
    }

    public ChapterModel(String id, List<String> list ) {
        this.id= id;
        this.list= list;
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
}
