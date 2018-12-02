package com.notaprogrammer.popularmovies.object;

import com.notaprogrammer.popularmovies.utilities.NetworkUtils;

public class Video {

    public static String SITE_YOUTUBE = "YouTube";
    private static String YOUTUBE_VND= "vnd.youtube:";
    String id;
    String key;
    String name;
    String site;
    String type;

    public Video() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getThumbnail() {
        return NetworkUtils.buildYoutubeThumbnailUrl(key).toString();
    }

    public String getYoutubeLink(){
        return NetworkUtils.buildYoutubeUrl(key).toString();
    }

    public String getYoutubeVendorSpecificUri() {
        return YOUTUBE_VND + key;
    }
}
