package com.example.androidexpense.helperclass;

public class UserHelperClass {

    String email, password;

    public UserHelperClass() {

    }

    public UserHelperClass(String email) {
        this.email = email;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
