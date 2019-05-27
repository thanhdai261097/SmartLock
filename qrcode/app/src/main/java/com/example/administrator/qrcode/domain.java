package com.example.administrator.qrcode;


public class domain {
    private static final domain ourInstance = new domain();

//    public static String ip = "https://server-smart-lock.herokuapp.com";
    public static String ip = "http://172.16.5.173:3001";
    public static domain getInstance() {
        return ourInstance;
    }

    private domain() {
    }
}
