package com.example.mobile.MainActivityPackage.SearchActivity;

public class Comic {
    private String name;
    private String chapter;
    private String imageURL;

    public Comic() {

    }

    public Comic(String name, String chapter, String imageURL) {
        this.name = name;
        this.chapter = chapter;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
