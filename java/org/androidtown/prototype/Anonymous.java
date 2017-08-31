package org.androidtown.prototype;

/**
 * Created by AndroidApp on 2017-08-29.
 */

public class Anonymous {

    public String title;
    public String contents;
    public String name;
    public String userID;
    private String time;
    private String order_time;

    public Anonymous(){}

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public Anonymous(String title, String contents, String name, String userID, String time, String order_time) {

        this.title = title;
        this.contents = contents;
        this.name = name;
        this.userID = userID;
        this.time = time;
        this.order_time = order_time;
    }
}
