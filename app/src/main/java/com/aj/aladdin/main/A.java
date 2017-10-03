package com.aj.aladdin.main;

import android.app.Activity;
import android.app.Application;

import com.aj.aladdin.db.IO;
import com.aj.aladdin.db.itf.MongoColl;
import com.aj.aladdin.utils.__;
import com.aj.aladdin.tools.regina.Regina;
import com.aj.aladdin.tools.regina.ack.UIAck;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class A extends Application {

    public static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;

    String user_id = null;

    public String getUser_id() {
        return user_id;
    }

    public void resetUser_id(Activity caller, String authID) {
        try {
            IO.r.find("PROFILES"
                    , __.jo().put("authID", authID)
                    , __.jo().put("authID", 1), __.jo()
                    , new UIAck(caller) {
                        @Override
                        protected void onRes(Object res, JSONObject ctx) {
                            JSONArray userArray = ((JSONArray) res);

                            if (userArray.length() > 1)
                                __.fatal("MainActivity::onStart : multiple users with the same authID");

                            if (userArray.length() == 1) try {
                                user_id = userArray.getJSONObject(0).getString(MongoColl._idKey);
                                MainActivity.start(caller);
                            } catch (JSONException e) {
                                __.fatal(e);
                            }
                            else try {
                                IO.r.insert("PROFILES"
                                        , __.jo().put("authID", authID)
                                        , __.jo(), __.jo()
                                        , new UIAck(caller) {
                                            @Override
                                            protected void onRes(Object res, JSONObject ctx) {
                                                resetUser_id(caller, authID);
                                            }
                                        });
                            } catch (Regina.NullRequiredParameterException | JSONException e) {
                                __.fatal(e);
                            }
                        }
                    });
        } catch (Regina.NullRequiredParameterException | JSONException e) {
            __.fatal(e);
        }
    }
}