package com.myWifi.app.ViewController.Controler.Networking;

import android.util.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by maxim_000 on 04/02/2016.
 */
public  class                   UtilsNetwork {
    final private String        TAGClass = "UtilsNetwork";
    private final static String MAC_RE = "^%s\\s+0x1\\s+0x2\\s+([:0-9a-fA-F]+)\\s+\\*\\s+\\w+$";
    private final static int    BUF = 8 * 1024;

    public String               getMacAddressFromIpByARP(String ip) {
        String              TAG = TAGClass + "getHardwareAddress";
        String              hw = "00:00:00:00:00:00";
        String              line;
        Matcher             matcher;
        BufferedReader      bufferedReader = null;
        try {
            if (ip != null) {
                String ptrn = String.format(MAC_RE, ip.replace(".", "\\."));
                Pattern pattern = Pattern.compile(ptrn);
                bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"), BUF);
                while ((line = bufferedReader.readLine()) != null) {
                    matcher = pattern.matcher(line);
                    //Log.d("Test", line);
                    if (matcher.matches()) {
                        hw = matcher.group(1);
                        break;
                    }
                }
            } else Log.e(TAG, "ip is null");
        } catch (IOException e) {
            Log.e(TAG, "Can't open/read file ARP: " + e.getMessage());
            return hw;
        } finally {
            try  { if(bufferedReader != null) bufferedReader.close(); }
             catch (IOException e) { Log.e(TAG, e.getMessage()); }
        }

        return hw;
    }
    public static String        getHostnameReverseDns(String ip) {
        String                  hostname = "";
        try {
            hostname = InetAddress.getByName(ip).getHostAddress();
            //Log.d("ReverseDNS", "Reverse DNS for 8.8.8.8 is: " + hostname);
        } catch (UnknownHostException e) {
            Log.e("ReverseDNS", "Oh no, 8.8.8.8 has no reverse DNS record!");
        }
        return hostname;
    }
}
