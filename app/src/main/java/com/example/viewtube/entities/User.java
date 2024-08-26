// User.java
package com.example.viewtube.entities;
import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {
    @PrimaryKey(autoGenerate = false)
    private @NonNull String  username;
    private String firstName;
    private String lastName;
    private String password;
    private String image;


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
    public String getImage() {
        return image;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
