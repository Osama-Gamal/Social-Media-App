package com.wanjy.dannie.rivchat.model;

public class Student {
    public String name;
    public String email;
    public String avata;
    public String myid;


    public Student(){
        
    }
    public Student(String name, String email,String avata,String myid) {
        this.name = name;
        this.email= email;
        this.avata=avata;
        this.myid=myid;
    }
    public String getName() {
        return name;
    }
    public String getEmail() {
        return email;
    }
    public String getAvata() {
        return avata;
    }
    public String getId() {
        return myid;
    }

    


}
