package com.wanjy.dannie.rivchat.model;

public class Videos {

    private String title;
    private String thumbnail_url;
    private String videoID;
	private Long views_num;

    public Videos(String title, String thumbnail_url, String videoid, Long views_num) {
        this.title = title;
        this.thumbnail_url= thumbnail_url;
        this.videoID = videoid;
		this.views_num = views_num;
    }

    public String getTitle() {
        return title;
    }
    public String getThumbnailUrl() {
        return thumbnail_url;
    }
    public String getVideoID() {
        return videoID;
    }
	public Long getViews_num(){
		return views_num;
	}


}


