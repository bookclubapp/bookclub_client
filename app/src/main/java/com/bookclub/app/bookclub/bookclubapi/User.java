/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bookclub.app.bookclub.bookclubapi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mosma
 */
public class User {

    private int id;
    private String username,
            password,
            mail,
            name,
            country,
            phoneNumber,
            profilePicture;

    private boolean onlineState;
    private Date dateOfBirth;
    private double lon, lat;

    public User(
            int id,
            String username,
            String password,
            String mail,
            String name,
            String country,
            String phoneNumber,
            String profilePicture,
            boolean onlineState,
            String dateOfBirth,
            double lon,
            double lat
    ) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.mail = mail;
        this.name = name;
        this.country = country;
        this.phoneNumber = phoneNumber;
        this.profilePicture = profilePicture;
        this.onlineState = onlineState;
        this.setDateOfBirth(dateOfBirth);
        this.lon = lon;
        this.lat = lat;
    }

    public User() {

    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return this.mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return this.country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNumber() {
        return this.phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public boolean getOnlineState() {
        return this.onlineState;
    }

    public void setOnlineState(boolean onlineState) {
        this.onlineState = onlineState;
    }

    public String getProfilePictureUrl() {
        return this.profilePicture;
    }

    public void setProfilePictureUrl(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Double getLon() {
        return this.lon;
    }

    public void setLon(Double lon) {
        this.lon = lon;
    }

    public Double getLat() {
        return this.lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Date getDateOfBirth() {
        return this.dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        // parameter should be in "yyyy-MM-dd" format

        try {
            this.dateOfBirth = new SimpleDateFormat("yyyy-MM-dd").parse(dateOfBirth);
        } catch (ParseException ex) {
            Logger.getLogger(Book.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String toString() {
        return "\nid: " + this.id
                + "\nusername: " + this.username
                + "\npassword: " + this.password
                + "\nmail: " + this.mail
                + "\nname: " + this.name
                + "\ncountry: " + this.country
                + "\nphoneNumber: " + this.phoneNumber
                + "\nprofilePicture: " + this.profilePicture
                + "\nonlineState: " + this.onlineState
                + "\ndateOfBirth: " + this.dateOfBirth
                + "\nlong: " + this.lon
                + "\nlat: " + this.lat;
    }

}
