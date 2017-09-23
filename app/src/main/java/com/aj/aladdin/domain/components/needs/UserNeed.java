package com.aj.aladdin.domain.components.needs;

/**
 * Created by joan on 21/09/2017.
 */

class UserNeed {

    private String _id;
    private String title;
    private boolean active;

    UserNeed(
            String _id
            , String title
            , boolean active
    ) {
        this._id = _id;
        this.title = title;
        this.active = active;
    }


    String get_id() {
        return _id;
    }

    String getTitle() {
        return title;
    }

    boolean isActive() {
        return active;
    }
}
