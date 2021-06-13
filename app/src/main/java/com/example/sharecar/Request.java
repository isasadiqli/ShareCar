package com.example.sharecar;

import java.util.ArrayList;
import java.util.HashMap;

public class Request {
    private HashMap<String, User> pendingUsers;
    private ArrayList<String> cities;

    public Request(HashMap<String, User> pendingUsers, ArrayList<String> cities) {
        this.pendingUsers = pendingUsers;
        this.cities = cities;
    }

    public HashMap<String, User> getPendingUsers() {
        return pendingUsers;
    }

    public void setPendingUsers(HashMap<String, User> pendingUsers) {
        this.pendingUsers = pendingUsers;
    }

    public ArrayList<String> getCities() {
        return cities;
    }

    public void setCities(ArrayList<String> cities) {
        this.cities = cities;
    }

    @Override
    public String toString() {
        return "Request{" +
                "pendingUsers=" + pendingUsers +
                ", cities=" + cities +
                '}';
    }
}
