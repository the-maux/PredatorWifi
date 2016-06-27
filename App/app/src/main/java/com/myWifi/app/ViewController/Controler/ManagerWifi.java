package com.myWifi.app.ViewController.Controler;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;
import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.ViewController.View.Dialog.DialogDetailReconnectWifi;
import com.myWifi.app.ViewController.View.FragmentMenuAttack;
import com.myWifi.app.ViewController.View.FragmentPredatorProbe;


public class                    ManagerWifi {
    private WifiConfiguration   conf = new WifiConfiguration();
    private String[]            ArrayMac = {"00:24:01:10:0c:70"};
    public enum                 networkType { OPEN, WEP, WPA}
    private WifiManager         wifiManager;
    private String              ssid = "PumpAP";
    private String              passwd = "";
    private Context             context;
    private FragmentActivity    activity;
    private FragmentPredatorProbe fragment;
    private DialogDetailReconnectWifi alertDialog;
    private String              TAG = "ManagerWifi";

    public ManagerWifi(Context context, FragmentActivity activity) {
        this.context = context;
        this.activity = activity;
    }
    private void                lauchWifi(networkType type) {
        Log.d(TAG, "launch wifi");
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
//        wifiManager.disconnect();
        int netId = wifiManager.addNetwork(conf);
        wifiManager.enableNetwork(netId, true);
    }
    public void                 setPasswd(String passwd) {
        this.passwd = passwd;
    }
    public void                 setSsid(String ssid) {
        this.ssid = ssid;
    }
    public String               getPasswd() {
        return this.passwd;
    }
    public String               getSsid() {
        return this.ssid;
    }
    public boolean              isItMyRadar() {
        InfoNetWork infoNet = new InfoNetWork(activity);

        try
        {
            for (String addrMac : ArrayMac) {
                if (infoNet.getBssid().contains(addrMac)) {
                    if (infoNet.getBssid().contains("00:00:00:00:00:00") ||
                            infoNet.getRealIp().contains("0.0.0.0") ||
                            infoNet.getGatewayIp().contains("0.0.0.0"))
                        return false;
                    return true;
                }
            }
            if (infoNet.getSsid().contains("PumpAP") ||
                    infoNet.getMacAddress().contains("00:24:01:10:0c:70")) {
                return true;
            }
        } catch (java.lang.NullPointerException e) {
            return false;
        }
        return false;
    }
    public void                 waitSyncServer(final FragmentMenuAttack MenuAttack,
                                               final DialogDetailReconnectWifi alert) {
        if (isItMyRadar()) {
            Log.d(TAG, "Sucess connection first");
            (new InfoNetWork(activity)).debugLog();
            alert.dismiss();
            MenuAttack.successConnection(0);
        }
        else  {
            lauchWifi(networkType.OPEN);
            (new Thread() {
                @Override
                public void run() {
                    int rcx = 0;
                    try {
                        while(true) {
                            sleep(2000);
                            if (isItMyRadar())  {
                                (new InfoNetWork(activity)).debugLog();
                                alert.dismiss();
                                MenuAttack.successConnection(rcx);
                                return ;
                            } else if (++rcx == 30) {
                                wifiManager.disconnect();
                                alert.addTimeToWaitBar(20);
                            } else if (rcx > 60){
                                alert.addTimeToWaitBar(20);
                                MenuAttack.errorConnection("ApWifi Unreachable: Timeout");
                                return ;
                            }
                            alert.addTimeToWaitBar(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(activity, "Server Unreachable", Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        }
    }
    private void                waitReSyncServer() {
        Log.d(TAG, "waitReSyncServer");
        if (isItMyRadar()) {
            Log.d(TAG, "Re synchronization succeed first time");
            (new InfoNetWork(activity)).debugLog();
            ((MainActivityToFragment)activity).displayView(2);
        }
        else  {
            lauchWifi(networkType.OPEN);
            (new Thread() {
                @Override
                public void run() {
                    int rcx = 0;
                    try {
                        while(true) {
                            sleep(2000);
                            if (isItMyRadar())  {
                                Log.d(TAG, "Re synchronization succeed");
                                (new InfoNetWork(activity)).debugLog();
                                ((MainActivityToFragment)activity).displayView(2);
                                return ;
                            } else if (rcx == 30) {
                                wifiManager.disconnect();
                                alertDialog.addTimeToWaitBar(20);
                            } else if (++rcx > 60){
                                alertDialog.addTimeToWaitBar(20);
                                fragment.errorConnection("ApWifi Unreachable: Timed out");
                                return ;
                            }
                            alertDialog.addTimeToWaitBar(1);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Toast.makeText(activity, "Server Unreachable", Toast.LENGTH_LONG).show();
                    }
                }
            }).start();
        }
    }

    public void                   reconnectToServerProbe(FragmentPredatorProbe fragment) {
        lauchWifi(networkType.OPEN);
        this.fragment = fragment;
        alertDialog = new DialogDetailReconnectWifi(fragment.getContext(), fragment.getActivity());
        alertDialog.create().show();
        waitReSyncServer();
    }
}
