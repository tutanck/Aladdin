package com.aj.aladdin.domain.components.needs;

import java.io.Serializable;

/**
 * Created by joan on 21/09/2017.
 */

class UserNeed implements Serializable {

    private String _id;
    private String title;
    private String searchText;
    private boolean active;

    UserNeed(
            String _id
            , String title
            , String search
            , boolean active
    ) {
        this._id = _id;
        this.title = title;
        this.searchText = search;
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

    public String getSearchText() {
        return searchText;
    }
}
