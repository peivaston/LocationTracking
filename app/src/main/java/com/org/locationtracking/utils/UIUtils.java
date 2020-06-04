package com.org.locationtracking.utils;

import java.util.regex.Pattern;


public class UIUtils

{



    //uses to verify email is valid or not
 //Source:
    public static boolean isValidEmailId(String email)

    {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                                       + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                       + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                                       + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                                       + "[0-9]{1,2}|-25[0-5]|2[0-4][0-9])){1}|"
                                       + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$")
                      .matcher(email)
                      .matches();
    }


}

