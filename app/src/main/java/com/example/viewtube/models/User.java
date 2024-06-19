// User.java
package com.example.viewtube.models;

public class User {
    // Fields to store user information
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String password;
    private String profilePictureUri;

    // Constructor to initialize a User object
    public User(String username, String firstName, String lastName, String password, String profilePictureUri) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.profilePictureUri = profilePictureUri;
    }

    // Getter for username
    public String getUsername() {
        return username;
    }

    // Getter for first name
    public String getFirstName() {
        return firstName;
    }

    // Getter for last name
    public String getLastName() {
        return lastName;
    }

    // Getter for password
    public String getPassword() {
        return password;
    }

    // Getter for profile picture URI
    public String getProfilePictureUri() {
        return profilePictureUri;
    }
}
