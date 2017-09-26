package org.androidtown.prototype;

/**
 * Created by AndroidApp on 2017-08-28.
 */

public class NoticeComment {
    public String comment;
    public String nickname;
    public String time;
    public String order_time;

    public String getOrder_time() {
        return order_time;
    }

    public void setOrder_time(String order_time) {
        this.order_time = order_time;
    }

    public NoticeComment(){

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

    public NoticeComment(String comment, String nickname, String time, String order_time) {

        this.comment = comment;
        this.nickname = nickname;
        this.time = time;
        this.order_time = order_time;
    }
}

