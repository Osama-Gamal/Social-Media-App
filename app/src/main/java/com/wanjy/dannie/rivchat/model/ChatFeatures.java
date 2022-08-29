package com.wanjy.dannie.rivchat.model;

public class ChatFeatures {

    private boolean image;
    private boolean sound;
    private boolean book;


    public ChatFeatures(){
    }

    public ChatFeatures(boolean image,boolean sound, boolean book) {
        this.image = image;
        this.sound= sound;
        this.book= book;

    }


    public Boolean getImage() {
        return image;
    }

    public Boolean getSound() {
        return sound;
    }

    public Boolean getBook() {
        return book;
    }


}