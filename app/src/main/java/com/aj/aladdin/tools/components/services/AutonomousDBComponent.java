package com.aj.aladdin.tools.components.services;

import android.content.Context;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by joan on 17/09/2017.
 */

public interface AutonomousDBComponent {

    public void loadState();

    public void saveState();

    public void followState();

}
