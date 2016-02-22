package com.myWifi.app.ViewController.Model;

import android.graphics.drawable.Drawable;

public class NavDrawerItem {

    private String title;
    private Drawable icon;
    private String count = "0";
    private boolean isCounterVisible = false;

    public NavDrawerItem(String navMenuTitle){
        this.title = navMenuTitle;
    }

    public NavDrawerItem(String title, Drawable icon){
        this.title = title;
        this.icon = icon;
    }

    public NavDrawerItem(String title, boolean isCounterVisible, String count){
        this.title = title;
        this.isCounterVisible = isCounterVisible;
        this.count = count;
    }

    public String getTitle()                        {
        return this.title;
    }

    public  Drawable getIcon(){
        return this.icon;
    }

    public String getCount(){
        return this.count;
    }

    public boolean getCounterVisibility(){
        return this.isCounterVisible;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setIcon( Drawable icon){
        this.icon = icon;
    }

}