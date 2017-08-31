package org.androidtown.prototype;

/**
 * Created by AndroidApp on 2017-08-22.
 */

public class RepairComment {
    public String comment;
    public String nickname;
    public String time;


    public RepairComment(){

    }

    public RepairComment(String comment, String nickname, String time) {
        this.comment = comment;
        this.nickname = nickname;
        this.time = time;
    }

    public String getComment() {

        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}

