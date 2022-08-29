package com.wanjy.dannie.rivchat.model;

public class AppVersion {

    private Double version;
    private String link;


    public AppVersion(){
    }

    public AppVersion(Double version, String link) {
        this.version = version;
        this.link= link;
    }


    public Double getVersion() {
        return version;
    }

    public String getLink() {
        return link;
    }



}