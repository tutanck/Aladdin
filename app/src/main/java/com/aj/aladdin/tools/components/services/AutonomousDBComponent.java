package com.aj.aladdin.tools.components.services;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by joan on 17/09/2017.
 */

public interface AutonomousDBComponent {

    void saveState();

    void loadState();

    void followState();

    void syncState();

    //boolean isStateValid(Object state, Class type, String rule); //todo later
}
