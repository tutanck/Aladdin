package com.aj.aladdin.tools.components.services;

import com.aj.aladdin.R;

/**
 * Created by joan on 23/09/2017.
 */

public class FormFieldKindTranslator {
    public static int tr(int kind){
        switch (kind) {
            case 0:
               return R.layout.fragment_form_field_multiline;
            case 1:
                return  R.layout.fragment_form_field_oneliner;
            case 20:
                return  R.layout.fragment_form_field_multiline_2;
            case 21:
                return  R.layout.fragment_form_field_oneliner_2;
            default:
                return R.layout.fragment_form_field_multiline;
        }
    }
}
