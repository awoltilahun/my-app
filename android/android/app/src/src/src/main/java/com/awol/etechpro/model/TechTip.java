package com.awol.etechpro.model;

import com.google.gson.annotations.SerializedName;

public class TechTip {

    @SerializedName("id")
    private Long id;
    @SerializedName("title")
    private String title;
    @SerializedName("description")
    private String description;
    @SerializedName("videoLink")
    private String videoLink;

    public TechTip() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getVideoLink() { return videoLink; }
    public void setVideoLink(String videoLink) { this.videoLink = videoLink; }

    public String getYoutubeVideoId() {
        if (videoLink == null || videoLink.isEmpty()) return null;
        if (videoLink.contains("youtube.com/watch?v="))
            return videoLink.split("v=")[1].split("&")[0];
        else if (videoLink.contains("youtu.be/"))
            return videoLink.split("youtu.be/")[1].split("\\?")[0];
        return null;
    }
}
