package com.awol.etechpro.model;

import com.google.gson.annotations.SerializedName;

public class AppLink {

    @SerializedName("id")
    private Long id;
    @SerializedName("name")
    private String name;
    @SerializedName("playstoreUrl")
    private String playstoreUrl;

    public AppLink() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPlaystoreUrl() { return playstoreUrl; }
    public void setPlaystoreUrl(String playstoreUrl) { this.playstoreUrl = playstoreUrl; }
}
