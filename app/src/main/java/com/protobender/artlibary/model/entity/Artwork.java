package com.protobender.artlibary.model.entity;

import com.google.gson.annotations.Expose;

public class Artwork {
    @Expose
    private int artworkId;
    @Expose
    private int userId;
    @Expose
    private String deviceName;
    @Expose
    private String artworkName;
    @Expose
    private String author;
    @Expose
    private String date;
    @Expose
    private String description;
    @Expose
    private String price;
    @Expose
    private String location;
    @Expose
    private String artworkUrl;
    @Expose
    private String artworkAudio;
    @Expose
    private User user;
    @Expose
    private int rssi;

    public Artwork() {
    }

    public Artwork(int userId, String deviceName, String artworkName, String author, String date, String description, String price, String location) {
        this.userId = userId;
        this.deviceName = deviceName;
        this.artworkName = artworkName;
        this.author = author;
        this.date = date;
        this.description = description;
        this.price = price;
        this.location = location;
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

    public int getRssi() {
        return rssi;
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getArtworkAudio() {
        return artworkAudio;
    }

    public void setArtworkAudio(String artworkAudio) {
        this.artworkAudio = artworkAudio;
    }
}
