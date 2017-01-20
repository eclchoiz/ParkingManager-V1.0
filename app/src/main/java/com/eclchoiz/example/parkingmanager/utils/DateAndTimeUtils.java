package com.eclchoiz.example.parkingmanager.utils;

/**
 * Created by SSENG on 2017-01-18.
 */

public class DateAndTimeUtils {

    public static String getToday() {
        return new java.text.SimpleDateFormat("yyyy.MM.dd").format(new java.util.Date());
    }

    public static String getTime() {
        return new java.text.SimpleDateFormat("HH:mm").format(new java.util.Date());
    }

    public static String addYear(String monthStr) {
        String returnValue = monthStr;
        if (monthStr.contains("월")) {
            String monthArray[] = monthStr.split("월");
            String month = monthArray[0];
            if (month.length() < 2) {
                month = "0" + month;
            }


            String dateArray[] = monthArray[1].split("일");
            String date = dateArray[0].trim();
            if (date.length() < 2) {
                date = "0" + date;
            }
            int numMonth = Integer.valueOf(month);
            if (numMonth > 10) {
                returnValue = "2016." + month + "." + date;
            } else {
                returnValue = "2017." + month + "." + date;
            }
        }
        return returnValue;
    }

    public static String addColon(String timeStr) {
        return timeStr.substring(0, 2) + ":" + timeStr.substring(2, 4);
    }

    /*public void selectMMS(int id) {

        switch (id) {
            case 0:
                mMessage = getString(R.string.mms_not_register_car);
                break;
            case 1:
                mMessage = getString(R.string.mms_no_register_card);
                break;
            case 2:
                mMessage = getString(R.string.mms_wrong_place);
                break;
            default:
                mMessage = null;
                break;
        }

        PackageManager pm = this.getPackageManager();

        if (!pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY) &&
                !pm.hasSystemFeature(PackageManager.FEATURE_TELEPHONY_CDMA)) {
            Toast.makeText(this, "본 기기에서는 문자 메세지를 사용할 수 없습니다.\n" + "MMS Sent to : " + mPhoneNumber + "\n" + mMessage, Toast.LENGTH_LONG).show();
        } else {
            sendMMS();
        }
    }*/
}
