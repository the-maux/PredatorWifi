package com.myWifi.app.ViewController.Controler.Predator;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;


public class                    MyWifiConfiguration {
    private WifiConfiguration   conf = new WifiConfiguration();
    public enum                 networkType { OPEN, WEP, WPA}
    private WifiManager         wifiManager;

    public                      MyWifiConfiguration(String ssid, String passwd, networkType type, Context context) {
        conf.SSID = "\"" + ssid + "\"";
        switch (type) {
            case OPEN:
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                break;
            case WEP:
                conf.wepKeys[0] = "\"" + passwd + "\"";
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case WPA:
                conf.preSharedKey = "\""+ passwd +"\"";
                break;
        }
        wifiManager = (WifiManager)context.getSystemService(Context.WIFI_SERVICE);
        int netId = wifiManager.addNetwork(conf);
        wifiManager.disconnect();
        wifiManager.enableNetwork(netId, true);
        wifiManager.reconnect();
    }
}
