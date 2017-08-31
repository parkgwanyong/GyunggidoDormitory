package org.androidtown.prototype;

/**
 * Created by AndroidApp on 2017-08-17.
 */

public class SleepDisposal {
    private String destination;
    private String from;
    private String to;


    public SleepDisposal(String destination, String from, String to) {
        this.destination = destination;
        this.from = from;
        this.to = to;
    }
    public SleepDisposal(){

    }

    @Override
    public String toString() {
        return "SleepDisposal{" +
                "destination='" + destination + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                '}';
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
