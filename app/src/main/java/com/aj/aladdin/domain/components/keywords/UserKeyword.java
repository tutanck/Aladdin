package com.aj.aladdin.domain.components.keywords;

/**
 * Created by joan on 21/09/2017.
 */

class UserKeyword {

    private UserKeywordsActivity userKeywordsActivity;

    private String keyword;
    private boolean active;

    UserKeyword(
            String keyword
            , boolean active
            , UserKeywordsActivity userKeywordsActivity
    ) {
        this.keyword = keyword;
        this.active = active;
        this.userKeywordsActivity = userKeywordsActivity;
    }

    String getKeyword() {
        return keyword;
    }

    boolean isActive() {
        return active;
    }

    UserKeywordsActivity getActivity() {
        return userKeywordsActivity;
    }
}
