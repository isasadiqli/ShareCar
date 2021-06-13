package com.example.sharecar;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Trip implements Comparable {
    private User tripSharer;
    private HashMap<String, User> passengers;
    private HashMap<String, User> pendingUsers;
    private HashMap<String, Integer> rates;
    private ArrayList<String> cities;
    private int cost;
    private MDate date;
    private String key;
    private int dateInt;

    public Trip() {
    }

    public Trip(User tripSharer, HashMap<String, User> passengers, HashMap<String, User> pendingUsers, HashMap<String, Integer> rates,
                ArrayList<String> cities, int cost, MDate date, String key) {
        this.tripSharer = tripSharer;
        this.passengers = passengers;
        this.cities = cities;
        this.cost = cost;
        this.date = date;
        this.key = key;
        this.pendingUsers = pendingUsers;
        this.rates = rates;

        this.dateInt = date.getDay() * date.getMonth() * date.getYear() * date.getHour() * date.getMinute();
    }

    public User getTripSharer() {
        return tripSharer;
    }

    public void setTripSharer(User tripSharer) {
        this.tripSharer = tripSharer;
    }

    public HashMap<String, User> getPassengers() {
        return passengers;
    }

    public void setPassengers(HashMap<String, User> passengers) {
        this.passengers = passengers;
    }

    public ArrayList<String> getCities() {
        return cities;
    }

    public void setCities(ArrayList<String> cities) {
        this.cities = cities;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }

    public MDate getDate() {
        return date;
    }

    public void setDate(MDate date) {
        this.date = date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public HashMap<String, User> getPendingUsers() {
        return pendingUsers;
    }

    public void setPendingUsers(HashMap<String, User> pendingUsers) {
        this.pendingUsers = pendingUsers;
    }

    public HashMap<String, Integer> getRates() {
        return rates;
    }

    public void setRates(HashMap<String, Integer> rates) {
        this.rates = rates;
    }

    public int getDateInt() {
        return dateInt;
    }

    public void setDateInt(){

        this.dateInt = (date.getYear() - 2000) * 100000000 + date.getMonth() * 1000000 +
                date.getDay() * 10000 + date.getHour() * 100 + date.getMinute();
    }

    public String getRoute() {
        return cities.get(0) + " " + cities.get(1) + " " + cities.get(0);
    }

    @Override
    public int compareTo(Object o) {
        int compareDateInt = ((Trip) o).getDateInt();
        return this.dateInt - compareDateInt;
    }

    @Override
    public String toString() {
        return "Trip{" +
                "tripSharer=" + tripSharer +
                ", passengers=" + passengers +
                ", pendingUsers=" + pendingUsers +
                ", rates=" + rates +
                ", cities=" + cities +
                ", cost=" + cost +
                ", date=" + date +
                ", key='" + key + '\'' +
                ", dateInt=" + dateInt +
                '}';
    }
}
