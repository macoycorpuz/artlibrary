package com.protobender.artlibary.model;

import com.protobender.artlibary.model.entity.Artwork;
import com.protobender.artlibary.model.entity.User;

import java.util.ArrayList;
import java.util.List;

public class CenterRepo {
    private static CenterRepo centerRepository;

    private List<User> userList = new ArrayList<>();
    private List<Artwork> artworkList = new ArrayList<>();
    private List<Artwork> discoveredArtwork = new ArrayList<>();

    public static CenterRepo getCenterRepo() {

        if (null == centerRepository) {
            centerRepository = new CenterRepo();
        }
        return centerRepository;
    }

    public List<User> getUserList() {
        return userList;
    }

    public void setUserList(List<User> userList) {
        this.userList = userList;
    }

    public List<Artwork> getArtworkList() {
        return artworkList;
    }

    public void setArtworkList(List<Artwork> artworkList) {
        this.artworkList = artworkList;
    }

    public List<Artwork> getDiscoveredArtwork() {
        return discoveredArtwork;
    }

    public void setDiscoveredArtwork(List<Artwork> discoveredArtwork) {
        this.discoveredArtwork = discoveredArtwork;
    }
}
