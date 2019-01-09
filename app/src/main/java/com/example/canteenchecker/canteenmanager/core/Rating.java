package com.example.canteenchecker.canteenmanager.core;

public class Rating {
    private int ratingId;
    private String username;
    private String remark;
    private int ratingPoints;
    private long timestamp;

    public Rating(int ratingId, String username, String remark, int ratingPoints, long timestamp){
        this.ratingId = ratingId;
        this.username = username;
        this.remark = remark;
        this.ratingPoints = ratingPoints;
        this.timestamp = timestamp;
    }

    public int getRatingId(){return this.ratingId;}
    public String getUsername(){return this.username;}
    public String getRemark() {return this.remark;}
    public int getRatingPoints() {return this.ratingPoints;}
    public long getTimestamp() {return this.timestamp;}
}
