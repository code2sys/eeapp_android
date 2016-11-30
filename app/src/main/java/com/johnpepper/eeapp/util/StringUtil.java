package com.johnpepper.eeapp.util;

/**
 * Created by borysrosicky on 11/2/15.
 */
public class StringUtil {
    public static String userImageURLFromUserID(String userID){
        return "http://ec2-52-36-3-127.us-west-2.compute.amazonaws.com/photo/"+userID+"/profile_thumb.jpg";
    }

    public static String companyImageURLFromCompanyID(String companyID){
        return "http://ec2-52-36-3-127.us-west-2.compute.amazonaws.com/company_logo/"+companyID+"/profile.jpg";
    }

    public static String addSuffixToNumber (int number) {
        String suffix;
        int ones = number % 10;
        int tens = (number/10) % 10;

        if (tens ==1) {
            suffix = "th";
        } else if (ones ==1){
            suffix = "st";
        } else if (ones ==2){
            suffix = "nd";
        } else if (ones ==3){
            suffix = "rd";
        } else {
            suffix = "th";
        }

        return number + suffix;
    }
}
