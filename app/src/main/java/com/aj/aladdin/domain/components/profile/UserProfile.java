package com.aj.aladdin.domain.components.profile;

import android.content.Context;

/**
 * Created by joan on 21/09/2017.
 */

public class UserProfile {

    private Context context;

    private String _id;
    private String username;
    private int reputation;
    private boolean online;

    UserProfile(
            String _id
            , String username
            , int reputation
            , boolean online
            , Context context
    ) {
        this.context = context;
        this._id = _id;
        this.username = username;
        this.reputation = reputation;
        this.online = online;
    }

    public Context getContext() {
        return context;
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
