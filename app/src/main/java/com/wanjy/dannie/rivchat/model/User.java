package com.wanjy.dannie.rivchat.model;



public class User {
    private String name;
    private String email;
    private String password;
    private String avata;
	private String myid;
    private String qrcode;
    private String group;
    private String title;
    private String telegram;

    
    public User(){		
    }
	public User(String name, String email,String password,String avata,String myid,String qrcode,String group,String title,String telegram) {
        this.name = name;
        this.email= email;
        this.password=password;
        this.avata=avata;
        this.myid=myid;
        this.qrcode=qrcode;
        this.group=group;
        this.title=title;
        this.telegram=telegram;

    }
	
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAvata() {
        return avata;
    }

    public String getMyId() {
        return myid;
    }

    public String getQRCode() {
        return qrcode;
    }
    public String getGroup() {
        return group;
    }
    public String getTitle() {
        return title;
    }
    public String getTelegram() {
        return telegram;
    }


}
