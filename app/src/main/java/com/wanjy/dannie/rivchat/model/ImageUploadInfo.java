package com.wanjy.dannie.rivchat.model;

import java.util.List;

public class ImageUploadInfo {

    public String imageName;

    public String imageURL;

	public String key_post;

	public String pdf_url;
	
	public String date;
	
	public String des;
	
	public String user;

	public ImagesModel images;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String name, String url,String key,String date,String pdf_url,String description,String user,ImagesModel images) {

        this.imageName = name;
        this.imageURL= url;
		this.key_post=key;
		this.date=date;
		this.pdf_url=pdf_url;
		this.des=description;
		this.user=user;
		this.images = images;
    }

    public String getImageName() {
        return imageName;
    }

    public String getImageURL() {
        return imageURL;
    }

	public String get_key() {
        return key_post;
    }

	public String getDate() {
        return date;
    }
	
	public String getPdf_url() {
        return pdf_url;
    }
	
	public String getDes() {
        return des;
    }
	
	public String getUser(){
		return user;
	}

    public ImagesModel getImages(){
        return images;
    }

}
