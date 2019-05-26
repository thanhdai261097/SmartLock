package com.example.administrator.qrcode.lib;

import android.annotation.SuppressLint;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class HandleDateTime {
    private static final HandleDateTime ourInstance = new HandleDateTime();

    public static HandleDateTime getInstance() {
        return ourInstance;
    }

    private HandleDateTime() {
    }
    @SuppressLint("SimpleDateFormat")

    public String milisToDate(Long mil, String dateFormat) {

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mil);
        return formatter.format(calendar.getTime());
    }
}
