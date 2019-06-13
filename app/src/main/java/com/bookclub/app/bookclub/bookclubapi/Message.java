package com.bookclub.app.bookclub.bookclubapi;

import java.util.Date;

/**
 * Created by pc on 9.04.2019.
 */

public class Message {

    String text;
    String name;
    long userID;
    String date;
    boolean isSeen;


    public Message(String text, String name, long userID, String date, boolean isSeen) {
        this.text = text;
        this.name = name;
        this.userID = userID;
        this.date = date;
        this.isSeen = isSeen;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getUserID() {
        return userID;
    }

    public void setUserID(long userID) {
        this.userID = userID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
