package com.aj.aladdin.db.colls;

import com.aj.aladdin.db.IO;
import com.aj.aladdin.db.colls.itf.Coll;
import com.aj.aladdin.tools.regina.Regina;
import com.aj.aladdin.tools.regina.ack._Ack;
import com.aj.aladdin.tools.utils.__;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Created by joan on 02/10/2017.
 */

public class PROFILES implements Coll {

    public final static String coll = "PROFILES";
    public final static String collTag = "#PROFILES/";

    public final static String authIDKey = "authID";
    public final static String typeKey = "type";
    public final static String usernameKey = "username";
    public final static String availKey = "avail";



    public static void setField(String _id, String key, Object val, _Ack ack) {
        try {
            IO.r.update(coll, __.jo().put(_idKey, _id)
                    , __.jo().put("$set", __.jo().put(key, val))
                    , __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }

    }


    public static void setStatus(String _id, int status, _Ack ack) {
        try {
            IO.r.update(coll, __.jo().put(_idKey, _id)
                    , __.jo().put("$set", __.jo().put(availKey, status))
                    , __.jo()
                    , __.jo().put(
                            "tags", __.jar().put(
                                    __.jo().put("val", collTag + _id)
                            )
                    )
                    , ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }

    }


    public static void getProfile(String _id, _Ack ack) {
        try {
            IO.r.find(coll, __.jo().put(_idKey, _id), __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }

    public static void getProfiles(JSONArray idList, _Ack ack) {
        try {
            IO.r.find(coll, __.jo().put(_idKey, __.jo().put("$in", idList)), __.jo(), __.jo(), ack);
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }

}
