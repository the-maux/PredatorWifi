package com.myWifi.app.ViewController.Controler;

import android.app.Activity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;

public class                    InfoNetWork {
    private String              TAG = "InfoNetWork";
    private static Activity     Instance = null;
    private static InfoNetWork  managerInstance = null;
    private WifiInfo            info;
    private String              ipAddress = "192.168.0.42";
    private String              macAddress = "42:42:42:42:42:42";
    private String              netmaskIp = "255.255.255.0";
    private int                 speed = 42;
    private String              bssid = "coucouBSSID";
    private String              ssid = "coucouSSID";
    private String              gatewayIp = "192.168.0.53";

    public                      InfoNetWork(Activity activity) {
        Instance = activity;
        initInfoNet();
    }
    private void                initInfoNet() {
       WifiManager wifi = (WifiManager) Instance.getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            Log.w(TAG, "init info net");
            this.info = wifi.getConnectionInfo();
            this.speed = this.info.getLinkSpeed();
            this.ssid = this.info.getSSID();
            this.bssid = this.info.getBSSID();
            this.macAddress = this.info.getMacAddress();
            this.gatewayIp = getIpFromIntSigned(wifi.getDhcpInfo().gateway);
            this.ipAddress = getMyIp(this.info.getIpAddress());
            this.netmaskIp = getIpFromIntSigned(wifi.getDhcpInfo().netmask);
            debugLog();
        }
    }
    private void                debugLog() {
        Log.w(TAG, "speed: " + speed);
        Log.w(TAG, "ssid: " + ssid);
        Log.w(TAG, "bssid: " + bssid);
        Log.w(TAG, "macAddress: " + macAddress);
        Log.w(TAG, "gatewayIp: " + gatewayIp);
        Log.w(TAG, "my Ip: " + ipAddress);
        Log.w(TAG, "netmask: " + netmaskIp);
    }
    private static String       getIpFromIntSigned(int ip_int) {
        String ip = "";
        for (int k = 0; k < 4; k++) {
            ip = ip + ((ip_int >> (k * 8)) & MotionEventCompat.ACTION_MASK) + ".";
        }
        return ip.substring(0, ip.length() - 1);
    }
    private String              getMyIp(int ip) {
        int intMyIp3 = ip / 0x01000000;
        int intMyIp3mod = ip % 0x01000000;
        int intMyIp2 = intMyIp3mod / 0x00010000;
        int intMyIp2mod = intMyIp3mod % 0x00010000;
        int intMyIp1 = intMyIp2mod / 0x00000100;
        int intMyIp0 = intMyIp2mod % 0x00000100;
        return String.valueOf(intMyIp0) + "." + String.valueOf(intMyIp1) + "." + String.valueOf(intMyIp2) + "." + String.valueOf(intMyIp3);
    }
    public String               getIpAddress() {
        return ipAddress;
    }
    public String               getMacAddress() {
        return macAddress;
    }
    public int                  getSpeed() {
        return speed;
    }
    public String               getSsid() {
        return ssid;
    }
    public String               getBssid() {
        return bssid;
    }
    public String               getNetmaskIp() {
        return netmaskIp;
    }
    public String               getGatewayIp() {
        return gatewayIp;
    }
    public String               getRealIp() { return ipAddress; }
}
