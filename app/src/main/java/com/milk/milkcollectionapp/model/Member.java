package com.milk.milkcollectionapp.model;

/**
 * Created by hp on 7/16/2016.
 */
public class Member {

    String id = "";
    String code = "";
    String name = "";
    String mobile = "";
    String detail = "";
    String avgFat = "";
    String avgSnf = "";

    public String getName() {
        return name;
    }
    public void setName(String name) {this.name = name;}

    public String getID() {return id;}
    public void setID(String id) {
        this.id = id;
    }

    public String getCode() {return code;}
    public void setCode(String code) {
        this.code = code;
    }

    public String getMobile() {return mobile;}
    public void setMobile(String contact) {
        this.mobile = contact;
    }


    public String getAvgFat() {return mobile;}
    public void setAvgFat(String contact) {
        this.mobile = contact;
    }

    public String getAvgSnf() {return mobile;}
    public void setAvgSnf(String contact) {
        this.mobile = contact;
    }


    public String getDetail() {return detail;}
    public void setDetail(String detail) {this.detail = detail;
    }


}
