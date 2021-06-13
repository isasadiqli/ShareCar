package com.example.sharecar;

import java.io.Serializable;

public class User implements Serializable {
    private String name, surname, email, userId;

    public User() {}

    public User(String name, String surname, String email, String userId) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserNameForSearch(){
        return name + " " + surname + " " + name;
    }

    public String getUserName(){
        return name + " " + surname;
    }

    @Override
    public String toString() {
        return "User{" +
                "name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", email='" + email + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
