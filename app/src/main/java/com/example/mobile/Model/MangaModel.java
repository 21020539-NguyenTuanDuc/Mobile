package com.example.mobile.Model;


import android.content.Context;

import com.google.firebase.storage.StorageReference;
import com.google.firebase.firestore.DocumentReference;
import java.io.Serializable;

import java.util.List;

public class MangaModel implements Serializable {
    private String id;
    private String name;
    private String image;
    private String poster;
    private String author;
    private List<String> chapList;
    private String chapTotal;
    private String description;
    private List<String> genres;
    private int likes;
    private int currentChap;

    public MangaModel() {
    }

//    public MangaModel(String id, String name, String image) {
//        this.id = id;
//        this.name = name;
//        this.image = image;
//    }

//    public MangaModel(String id, String name, String image, String author, String description, List<String> genres, int likes) {
//        this.id = id;
//        this.name = name;
//        this.image = image;
//        this.author = author;
////        this.chapList = chapList;
//        this.description = description;
//        this.genres = genres;
//        this.likes = likes;
//    }
    public MangaModel(String id, String name, String image, String poster, String author, String description, List<String> chapList, List<String> genres, int likes) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.poster = poster;
        this.author = author;
        this.chapList = chapList;
        this.description = description;
        this.genres = genres;
        this.likes = likes;
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
    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

//    public List<StorageReference> getChapList() {
//        return chapList;
//    }
//
//    public void setChapList(List<StorageReference> chapList) {
//        this.chapList = chapList;
//    }
    public List<String> getChapList() {
        return chapList;
    }

    public void setChapList(List<String> chapList) {
        this.chapList = chapList;
    }

    public String getChapTotal() {
        return chapTotal;
    }

    public void setChapTotal(String chapTotal) {
        this.chapTotal = chapTotal;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getGenres() {
        return genres;
    }

    public void setGenres(List<String> genres) {
        this.genres = genres;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }
    public int getCurrentChap() {
        return currentChap;
    }

    public void setCurrentChap(int currentChap) {
        this.currentChap = currentChap;
    }

    public int getImageResourceId(Context context) {
        return context.getResources().getIdentifier(image, "drawable", context.getPackageName());
    }
}
