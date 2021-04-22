package com.dimitrissioulas.covidapp;

public class Appointment {
    private String uid;
    private String centerId;
    private String centerName;
    private String date;

    public Appointment(){

    }

    public Appointment(String uid, String centerId, String centerName, String date) {
        this.uid = uid;
        this.centerId = centerId;
        this.centerName = centerName;
        this.date = date;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
