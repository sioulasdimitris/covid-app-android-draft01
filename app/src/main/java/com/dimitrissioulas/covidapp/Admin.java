package com.dimitrissioulas.covidapp;

public class Admin {
    private String center_id;
    private String id;
    private String email;

    public Admin(){

    }

    public Admin(String center_id, String id, String email) {
        this.center_id = center_id;
        this.id = id;
        this.email = email;
    }

    public String getCenter_id() {
        return center_id;
    }

    public void setCenter_id(String center_id) {
        this.center_id = center_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
