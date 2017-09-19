package com.aj.aladdin.tools.oths.utils;

import android.content.Context;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by joan on 19/09/2017.
 */

public class __ {

    public static final void showToast(Context ctx, String text) {
        Toast.makeText(ctx, text, Toast.LENGTH_LONG).show();
    }



    public static final JSONObject jo() {
        return new JSONObject();
    }

    public static final JSONArray jar() {
        return new JSONArray();
    }

    public static void fatalError(Throwable throwable) {
        throw new RuntimeException(throwable);
    }

    public static void fatalError(String message) {
        throw new RuntimeException(message);
    }
}
