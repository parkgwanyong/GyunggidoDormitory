package org.androidtown.prototype;

/**
 * Created by AndroidApp on 2017-08-28.
 */

public class BoardComment {
    public String comment;
    public String nickname;
    public String time;

    public BoardComment(){

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

    public BoardComment(String comment, String nickname, String time) {

        this.comment = comment;
        this.nickname = nickname;
        this.time = time;
    }
}

