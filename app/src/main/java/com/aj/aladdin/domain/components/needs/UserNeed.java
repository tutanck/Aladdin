package com.aj.aladdin.domain.components.needs;

/**
 * Created by joan on 21/09/2017.
 */

public class UserNeed {

    private String _id;
    private String title;
    private boolean active;

    public UserNeed(
            String _id
            , String title
            , boolean active
    ) {
        this._id = _id;
        this.title = title;
        this.active = active;
    }


    public String get_id() {
        return _id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isActive() {
        return active;
    }
}
