package com.dimitrissioulas.covidapp;



import android.icu.text.SimpleDateFormat;
import android.util.Log;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

public class Appointment {
    private String uid;
    private String centerId;
    private String centerName;
    private String date;
    private String date2;

    public Appointment(){

    }

    public Appointment(String uid, String centerId, String centerName, String date) throws ParseException {
        this.uid = uid;
        this.centerId = centerId;
        this.centerName = centerName;
        this.date = date;
        this.date2 = calculateSecondsDoseDate(date);
    }

    public Appointment(String uid, String centerId, String centerName, String date, String date2) throws ParseException {
        this.uid = uid;
        this.centerId = centerId;
        this.centerName = centerName;
        this.date = date;
        this.date2 = date2;
    }

    private String calculateSecondsDoseDate(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        try{
            //Setting the date to the given date
            c.setTime(sdf.parse(date));
        }catch(ParseException e){
            e.printStackTrace();
        }

        //Number of Days to add
        c.add(Calendar.DAY_OF_MONTH, 28);
        //Date after adding the days to the given date
        String newDate = sdf.format(c.getTime());


        return newDate;

    }

    public String getDate2() {
        return date2;
    }

    public void setDate2(String date2) {
        this.date2 = date2;
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
