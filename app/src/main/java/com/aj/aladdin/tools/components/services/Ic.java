package com.aj.aladdin.tools.components.services;

import com.aj.aladdin.R;
import com.aj.aladdin.tools.oths.utils.__;

/**
 * Created by joan on 02/10/2017.
 */

public class Ic {

    public static int icon(String key) {
        switch (key) {
            case "username":
                return R.drawable.ic_person_24dp;
            case "resume":
                return R.drawable.ic_work_24dp;
            case "tariff":
                return R.drawable.ic_euro_symbol_24dp;
            default: throw new RuntimeException("Unknown icon key");
        }
    }
}
