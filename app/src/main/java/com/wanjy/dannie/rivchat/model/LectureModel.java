package com.wanjy.dannie.rivchat.model;

public class LectureModel {
    public String nameLecture;
    public String desLecture;
    public String dateLecture;
    public String lecture;
    public String lectureId;
    public String publisherId;
    public String lectureSize;
    
    public LectureModel(){

    }

    public LectureModel(String nameLecture, String desLecture,String dateLecture,String lecture,String lectureId,String publisherId,String lectureSize) {                

        this.nameLecture = nameLecture;
        this.desLecture= desLecture;
        this.dateLecture=dateLecture;
        this.lecture=lecture;
        this.lectureId=lectureId;
        this.publisherId=publisherId;
        this.lectureSize=lectureSize;

    }

    public String getNameLecture() {
        return nameLecture;
    }
    public String getDesLecture() {
        return desLecture;
    }
    public String getDateLecture() {
        return dateLecture;
    }
    public String getLecture() {
        return lecture;
    }
    public String getLectureId() {
        return lectureId;
    }
    public String getPublisherId() {
        return publisherId;
    }
    public String getLectureSize() {
        return lectureSize;
    }
    
    
}
