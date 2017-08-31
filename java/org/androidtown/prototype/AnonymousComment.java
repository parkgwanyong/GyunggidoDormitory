package org.androidtown.prototype;

/**
 * Created by AndroidApp on 2017-08-28.
 */

public class AnonymousComment {
    public String comment;
    public String name;
    public String time;

    public AnonymousComment(){

    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String nickname) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public AnonymousComment(String comment, String nickname, String time) {

        this.comment = comment;
        this.name = name;
        this.time = time;
    }
}

