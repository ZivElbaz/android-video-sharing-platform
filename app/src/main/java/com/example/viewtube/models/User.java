// User.java
package com.example.viewtube.models;

public class User {
    private final String username;
    private final String firstName;
    private final String lastName;
    private final String password;
    private String profilePictureUri; // Uri to the profile picture

    public User(String username, String firstName, String lastName, String password, String profilePictureUri) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.profilePictureUri = profilePictureUri;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getPassword() {
        return password;
    }

    public String getProfilePictureUri() {
        return profilePictureUri;
    }
}
