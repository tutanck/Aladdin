package com.aj.aladdin.db;

import com.aj.aladdin.db.itf.MongoColl;
import com.aj.aladdin.tools.regina.Regina;
import com.aj.aladdin.tools.regina.ack.__Ack;
import com.aj.aladdin.utils.__;

import org.json.JSONException;

/**
 * Created by joan on 02/10/2017.
 */

public class USER_RATINGS implements MongoColl {
    private final static String coll = "USER_RATINGS";
    public final static String fromIDKey = "fromID";
    public final static String toIDKey = "toID";
    public final static String ratingKey = "rating";
    public final static String reputationKey = "reputation";


    public static void getUserRating(String fromID, String toID, __Ack ack) {
        try {
            IO.r.find(coll
                    , __.jo().put(fromIDKey, fromID).put(toIDKey, toID)
                    , __.jo().put(ratingKey, 1).put(MongoColl._idKey, 0), __.jo(), ack);
        } catch (JSONException | Regina.NullRequiredParameterException e) {
            __.fatal(e);
        }
    }


    public static void setUserRating(float rating, String fromID, String toID, __Ack ack) {
        try {
            IO.r.update(coll
                    , __.jo().put(fromIDKey, _idKey).put(toIDKey, toID)
                    , __.jo().put(fromIDKey, _idKey).put(toIDKey, toID).put(ratingKey, rating)
                    , __.jo().put("upsert", true), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


    public static void computeUserRating(String userID, __Ack ack) {
        try {
            IO.socket.emit("getUserRating", __.jo().put("userID", userID), ack);
        } catch (JSONException e) {
            __.fatal(e);
        }
    }

}