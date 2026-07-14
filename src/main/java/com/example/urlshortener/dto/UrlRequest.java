package com.example.urlshortener.dto;

public class UrlRequest {
    private String url;
    private String customAlias;

    public UrlRequest() {}

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }
    
    public String getCustomAlias() { return customAlias; }
    public void setCustomAlias(String customAlias) { this.customAlias = customAlias; }
}
