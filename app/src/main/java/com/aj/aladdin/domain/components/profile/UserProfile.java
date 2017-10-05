package com.aj.aladdin.domain.components.profile;

import android.content.Context;

import java.io.Serializable;

/**
 * Created by joan on 21/09/2017.
 */

public class UserProfile implements Serializable{

    private String _id;
    private String username;
    private int reputation;
    private boolean online;

    public UserProfile(
            String _id
            , String username
            , int reputation
            , boolean online
    ) {
        this._id = _id;
        this.username = username;
        this.reputation = reputation;
        this.online = online;
    }


    public String get_id() {
        return _id;
    }

    public String getUsername() {
        return username;
    }

    public int getReputation() {
        return reputation;
    }

    public boolean isOnline() {
        return online;
    }
}
