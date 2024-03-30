package com.example.mobile.Model;


import android.content.Context;

public class MangaModel {
    private String id;
    private String name;
    private String image;

    public MangaModel() {
    }

    public MangaModel(String id, String name, String image) {
        this.id = id;
        this.name = name;
        this.image = image;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int getImageResourceId(Context context) {
        return context.getResources().getIdentifier(image, "drawable", context.getPackageName());
    }
}
