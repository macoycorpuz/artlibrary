package com.protobender.artlibary.model.entity;

import com.google.gson.annotations.Expose;

public class Artwork {
    @Expose
    private int artworkId;
    @Expose
    private String artworkName;
    @Expose
    private String author;
    @Expose
    private String date;
    @Expose
    private String description;
    @Expose
    private String artworkUrl;
    @Expose
    private String deviceName;
    @Expose
    private int userId;
    @Expose
    private User user;

    public Artwork() {
    }

    public Artwork(String artworkName, String author, String date, String description, String deviceName, int userId) {
        this.artworkName = artworkName;
        this.author = author;
        this.date = date;
        this.description = description;
        this.deviceName = deviceName;
        this.userId = userId;
    }

    public int getArtworkId() {
        return artworkId;
    }

    public String getArtworkName() {
        return artworkName;
    }

    public String getAuthor() {
        return author;
    }

    public String getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getArtworkUrl() {
        return artworkUrl;
    }

    public int getUserId() {
        return userId;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public User getUser() {
        return user;
    }

    public void setArtworkId(int artworkId) {
        this.artworkId = artworkId;
    }

    public void setArtworkName(String artworkName) {
        this.artworkName = artworkName;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setArtworkUrl(String artworkUrl) {
        this.artworkUrl = artworkUrl;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
