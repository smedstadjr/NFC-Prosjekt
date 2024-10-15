package com.example.nfcapplicationlocks;

public class Permitions {
    private User userId;

    public Permitions(User userId){
        this.userId = userId;
    }

    public User getUserId() {
        return userId;
    }

    public void setUserId(User userId) {
        this.userId = userId;
    }

}
