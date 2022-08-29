package com.wanjy.dannie.rivchat.model;

public class NotificationModel {


    private String senderId;
    private String notifText;
    private String noifId;
    private String notifDate;


    public NotificationModel(){
    }
    public NotificationModel(String senderId, String notifText,String noifId,String notifDate) {
        this.senderId = senderId;
        this.notifText= notifText;
        this.noifId=noifId;
        this.notifDate=notifDate;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getNotifText() {
        return notifText;
    }

    public String getNoifId() {
        return noifId;
    }

    public String getNotifDate() {
        return notifDate;
    }


}
