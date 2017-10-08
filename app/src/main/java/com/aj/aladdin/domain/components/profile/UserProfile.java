package com.aj.aladdin.domain.components.profile;

import java.io.Serializable;

/**
 * Created by joan on 21/09/2017.
 */

public class UserProfile implements Serializable{

    private String _id;
    private String username;
    private int reputation;
    private int availability;

    public UserProfile(
            String _id
            , String username
            , int reputation
            , int availability
    ) {
        this._id = _id;
        this.username = username;
        this.reputation = reputation;
        this.availability = availability;
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

    public int getAvailability() {
        return availability;
    }
}
