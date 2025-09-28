package com.mults.parser;

public class Cartoon {
    private String title;
    private String url;
    private String imageUrl;
    private String year;
    
    public Cartoon() {}
    
    public Cartoon(String title, String url) {
        this.title = title;
        this.url = url;
    }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
}