package com.teamluck.drawdiculous.app.model;

import java.util.Random;

import lombok.Data;

/**
 * User, designed in singleton pattern.
 */
@Data
public class User {
    
    private static User userInstance = new User();
    private String username;
    private String emailAddress;
    private int id;
    
    private User() {
    }
    
    public static User getInstance() {
        return userInstance;
    }
    
    public static void setInstance(User user) {
        userInstance = user;
    }
    
    public void generateId() {
        this.id = new Random().nextInt();
    }
}