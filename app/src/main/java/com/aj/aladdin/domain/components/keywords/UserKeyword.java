package com.aj.aladdin.domain.components.keywords;

/**
 * Created by joan on 21/09/2017.
 */

public class UserKeyword {

    private UserKeywordsActivity userKeywordsActivity;

    private String keyword;
    private boolean active;

    public UserKeyword(
            String keyword
            , boolean active
            , UserKeywordsActivity userKeywordsActivity
    ) {
        this.keyword = keyword;
        this.active = active;
        this.userKeywordsActivity = userKeywordsActivity;
    }

    public String getKeyword() {
        return keyword;
    }

    public boolean isActive() {
        return active;
    }

    public UserKeywordsActivity getActivity() {
        return userKeywordsActivity;
    }
}
