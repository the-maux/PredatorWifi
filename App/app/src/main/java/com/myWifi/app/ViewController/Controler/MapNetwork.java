package com.myWifi.app.ViewController.Controler;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import com.myWifi.app.ViewController.Model.ClientNatif;
import com.myWifi.app.ViewController.Model.StackClientNatif;
import com.myWifi.app.ViewController.View.FragmentDiscoverWifi;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.ArrayList;

public class                        MapNetwork {
    private String                  TAG = "MapNetwork";
    private StackClientNatif        listClient;
    private String                  routerIp;
    private Context                 context;
    private static MapNetwork       instance = null;
    private FragmentDiscoverWifi    instanceF;

    private                         MapNetwork(Context context,
                                               String routerIp,
                                               FragmentDiscoverWifi instanceF,
                                               StackClientNatif listClient) {
        this.context = context;
        this.instanceF = instanceF;
        this.routerIp = routerIp;
        this.listClient = listClient;
        DiscoverNetwork();
    }
    public static synchronized MapNetwork getInstance(Context context,
                                                      String routerIp,
                                                      FragmentDiscoverWifi instanceF,
                                                      StackClientNatif listClient) {
        if (instance == null) {
            instance = new MapNetwork(context, routerIp, instanceF, listClient);
        }
        return instance;
    }
    private void                    getJmDNSName(String ip) {
        try {
            InetAddress localHost = InetAddress.getByName(ip);
            JmDNS jmdns = JmDNS.create(localHost);
            Log.d("MapNetWork", "add Client [" + ip + "]" + jmdns.getHostName());
        } catch (UnknownHostException e) {
            e.printStackTrace();
            Log.d(TAG, "UnknowHostException at JmDNS");
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "IO EXCEPTION AT JmDNS");
        }
    }
    public boolean                  ping(String host) {
        try       {
            Process  mIpAddrProcess = Runtime.getRuntime().exec("/system/bin/ping -c 1 " + host);
            if( mIpAddrProcess.waitFor() == 0){
                return true;
            } else {
                return false;
            }
        }
        catch (InterruptedException ignore)        {
            Log.w("MapNetwork", "not reachable interupted " + host);
            ignore.printStackTrace();
        }
        catch (IOException e)        {
            Log.w("MapNetwork", "not reachable IO " + host);
            e.printStackTrace();
        }
        return false;
    }
    private void                    DiscoverNetwork() {
        Log.w(TAG, "Starting Discover Natif client in Network");
        final UtilsNetwork utils = new UtilsNetwork();
        final String ipBuild = routerIp.substring(0, routerIp.lastIndexOf('.'));
        for (int rcx = 1; rcx <=255 ; rcx++) {
            final int copyFinalRcx = rcx;
            final String ip = ipBuild + "." + copyFinalRcx;
            new Thread (new Runnable() {
                @Override
                public void run() {
                    if (ping(ip)) {
                        final ClientNatif clientTmp = new ClientNatif(ip, utils, instanceF, listClient);
                        getJmDNSName(ip);
                        instanceF.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                instance.getStackclient().add(clientTmp);
                                instanceF.notifyNew(instance.getStackclient());
                            }
                        });
            }}}).start();
        }
        Log.w(TAG, "Stoping Discover Natif client in Network");
    }
    private void                    purgeAndRestartService() {
        listClient = new StackClientNatif();
        DiscoverNetwork();
    }
    public  StackClientNatif        getStackclient() {
        return this.listClient;
    }
}
