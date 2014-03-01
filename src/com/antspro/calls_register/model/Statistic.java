package com.antspro.calls_register.model;

import java.util.Date;

/**
 * Created by arna on 01.03.14.
 */
public class Statistic {
    private int duration;
    private Date date;
    private int callsCount;
    private boolean isPostedToServer;

    public Statistic(int duration, int callsCount, Date date, boolean isPostedToServer) {
        this.duration = duration;
        this.date = date;
        this.callsCount = callsCount;
        this.isPostedToServer = isPostedToServer;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void incDuration(int value) {
        this.duration += value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCallsCount() {
        return callsCount;
    }

    public void setCallsCount(int callsCount) {
        this.callsCount = callsCount;
    }

    public void incCallsCount(int value) {
        this.callsCount += value;
    }

    public boolean isPostedToServer() {
        return isPostedToServer;
    }

    public void setPostedToServer(boolean isPostedToServer) {
        this.isPostedToServer = isPostedToServer;
    }
}
