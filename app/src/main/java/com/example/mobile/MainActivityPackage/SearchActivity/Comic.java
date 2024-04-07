package com.example.mobile.MainActivityPackage.SearchActivity;

import org.json.JSONException;
import org.json.JSONObject;

public class Comic {
    private String name;
    private String chapter;
    private String imageURL;


    public Comic() {

    }

    public Comic(JSONObject o) throws JSONException {
        name = o.getString("name");
        chapter = o.getString("chapter");
        imageURL = o.getString("imageURL");
    }

    public Comic(String name, String chapter, String imageURL) {
        this.name = name;
        this.chapter = chapter;
        this.imageURL = imageURL;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        if (name.length() <= 20) {
            int remain_space_count = 25 - name.length();
            String emptyString = "";
            for (int i = 0; i < remain_space_count; i++) {
                emptyString += " ";
            }
            return getName() + emptyString;
        }
        return name.substring(0, 20) + "...";
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
