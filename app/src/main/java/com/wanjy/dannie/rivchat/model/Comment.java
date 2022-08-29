package com.wanjy.dannie.rivchat.model;
import com.google.firebase.database.*;

public class Comment {

    private String content,uid,comment_id,date,img,pdf,pdf_name,pdf_size;
    private String sound_name,sound_size,sound;


    public Comment() {
    }

    public Comment(String content, String uid, String comment_id, String date,String img,String pdf,String pdf_name,String pdf_size,String sound,String sound_name,String sound_size) {                        
        this.content = content;
        this.uid = uid;
        this.comment_id = comment_id;
        this.date = date;
		this.img=img;
        this.pdf=pdf;
        this.pdf_name=pdf_name;
        this.pdf_size=pdf_size;
        this.sound=sound;
        this.sound_name=sound_name;
        this.sound_size=sound_size;

    }

    
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCommentId() {
        return comment_id;
    }

    public void setCommentId(String commentid) {
        this.comment_id = commentid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
	public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
    public String getPdf() {
        return pdf;
    }

    public void setPdf(String pdf) {
        this.pdf = pdf;
    }
    public String getPdfName() {
        return pdf_name;
    }
    public void setPdfName(String pdf_name) {
        this.pdf_name = pdf_name;
    }
    public String getPdfSize() {
        return pdf_size;
    }
    public void setPdfSize(String pdf_size) {
        this.pdf_size = pdf_size;
    }
    public String getSound() {
        return sound;
    }
    public void setSound(String sound) {
        this.sound = sound;
    }
    public String getSoundName() {
        return sound_name;
    }
    public void setSoundName(String sound_name) {
        this.sound_name = sound_name;
    }
    public String getSoundSize() {
        return sound_size;
    }
    public void setSoundSize(String sound_size) {
        this.sound_size = sound_size;
    }
    
}
