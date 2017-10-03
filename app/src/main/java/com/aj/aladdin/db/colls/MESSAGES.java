package com.aj.aladdin.db.colls;

import com.aj.aladdin.db.IO;
import com.aj.aladdin.db.colls.itf.Coll;
import com.aj.aladdin.tools.regina.Regina;

import com.aj.aladdin.tools.regina.ack._Ack;
import com.aj.aladdin.tools.utils.__;

import org.json.JSONException;

import java.util.Date;

/**
 * Created by joan on 02/10/2017.
 */

public class MESSAGES implements Coll {
    private static String coll = "MESSAGES";

    public final static String senderIDKey = "senderID";
    public final static String toIDKey = "toID";
    public final static String messageKey = "message";


    public static void loadMessages(String aID, String bID, _Ack ack) {
        try {
            IO.r.find(
                    coll
                    , __.jo().put(
                            "$or"
                            , __.jar()
                                    .put(__.jo().put(senderIDKey, aID).put(toIDKey, bID))
                                    .put(__.jo().put(toIDKey, aID).put(senderIDKey, bID))
                    )
                    , __.jo().put("sort", __.jo().put(dateKey, -1))
                    , __.jo(), ack
            );
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }


    public static void sendMessage(String senderID,String toID, String text, _Ack ack) {
        try {
            IO.r.insert(coll
                    , __.jo()
                            .put(senderIDKey, senderID).put(toIDKey, toID)
                            .put(messageKey, text).put(dateKey, new Date())//// TODO: 29/09/2017  date
                    , __.jo(), __.jo(), ack);
        } catch (JSONException | Regina.NullRequiredParameterException e) {
            __.fatal(e);
        }
    }



}
