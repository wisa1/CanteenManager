package com.example.canteenchecker.canteenmanager.core;
public class Canteen {

    private String id;
    private String name;
    private String phoneNumber;
    private String website;
    private String setMeal;
    private float setMealPrice;
    private float averageRating;
    private String location;
    private int averageWaitingTime;

    public Canteen(String id, String name, String phoneNumber,
                   String website, String setMeal, float setMealPrice, float averageRating,
                   String location, int averageWaitingTime) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.setMeal = setMeal;
        this.setMealPrice = setMealPrice;
        this.averageRating = averageRating;
        this.location = location;
        this.averageWaitingTime = averageWaitingTime;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    public void setName(String value){
        name = value;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getWebsite() {
        return website;
    }
    public void setWebsite(String value){website = value;}

    public String getSetMeal() {
        return setMeal;
    }
    public void setSetMeal(String value){ setMeal = value;}

    public void setSetMealPrice(float value) {setMealPrice = value;}
    public float getSetMealPrice() {
        return setMealPrice;
    }

    public float getAverageRating() {
        return averageRating;
    }

    public String getLocation() {
        return location;
    }
    public void setLocation(String value){location = value;}

    public int getAverageWaitingTime() {
        return averageWaitingTime;
    }
    public void setAverageWaitingTime(int value){averageWaitingTime = value;}

}
