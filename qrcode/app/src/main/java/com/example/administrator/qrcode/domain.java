package com.example.administrator.qrcode;


public class domain {
    private static final domain ourInstance = new domain();

    public static String ip = "http://192.168.0.109:3001";
    public static domain getInstance() {
        return ourInstance;
    }

    private domain() {
    }
}
