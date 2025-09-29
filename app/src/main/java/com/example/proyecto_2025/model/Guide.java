package com.example.proyecto_2025.model;

import java.io.Serializable;
import java.util.List;

public class Guide implements Serializable {
    public enum Language {ES, EN, FR, PT, DE}
    private String id;
    private String name;
    private List<Language> languages;
    private String photoUrl;
    private String bio;
    private float rating;
    private String zone;

    public Guide(String id, String name, List<Language> languages, String photoUrl, String bio, float rating, String zone) {
        this.id = id; this.name = name; this.languages = languages;
        this.photoUrl = photoUrl; this.bio = bio; this.rating = rating; this.zone = zone;
    }
    public String getId(){return id;}
    public String getName(){return name;}
    public List<Language> getLanguages(){return languages;}
    public String getPhotoUrl(){return photoUrl;}
    public String getBio(){return bio;}
    public float getRating(){return rating;}
    public String getZone(){return zone;}
}
