package com.protobender.artlibary.model.entity;

import com.google.gson.annotations.Expose;

import java.util.List;

public class Result {

    @Expose
    private Boolean error = false;
    @Expose
    private String message;
    @Expose
    private User user;
    @Expose
    private Artwork artwork;
    @Expose
    private List<User> users;
    @Expose
    private List<Artwork> artworks;

    public Boolean getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public User getUser() {
        return user;
    }

    public List<User> getUsers() {
        return users;
    }

    public Artwork getArtwork() {
        return artwork;
    }

    public List<Artwork> getArtworks() {
        return artworks;
    }
}
