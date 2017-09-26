package org.androidtown.prototype;

/**
 * Created by AndroidApp on 2017-08-20.
 */

public class RepairMainDisplay {

    public String title;
    public String status;
    public String contents;
    public String nickname;
    public String userID;
    private String time;
    private String order_time;
    private String image_uri;

    public RepairMainDisplay() {

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

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public RepairMainDisplay(String title, String status, String contents, String nickname, String userID, String time, String order_time, String image_uri) {
        this.title = title;
        this.status = status;
        this.contents = contents;
        this.nickname = nickname;
        this.userID = userID;
        this.time = time;
        this.order_time = order_time;
        this.image_uri = image_uri;


    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
