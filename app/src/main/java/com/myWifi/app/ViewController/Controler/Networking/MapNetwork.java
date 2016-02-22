package com.myWifi.app.ViewController.Controler.Networking;

import android.content.Context;
import android.util.Log;
import com.myWifi.app.ViewController.Controler.Networking.BonjourService.BonjourManager;
import com.myWifi.app.ViewController.Model.Networking.ClientNatif;
import com.myWifi.app.ViewController.Model.Networking.StackClientNatif;
import com.myWifi.app.ViewController.View.FragmentDiscoverClientNatif;

import javax.jmdns.JmDNS;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;

public class                        MapNetwork {
    private String                  TAG = "MapNetwork";
    private StackClientNatif        listClient;
    private String                  routerIp;
    private Context                 context;
    private static MapNetwork       instance = null;
    private FragmentDiscoverClientNatif instanceF;

    private                         MapNetwork(Context context,
                                               String routerIp,
                                               FragmentDiscoverClientNatif instanceF,
                                               StackClientNatif listClient) {
        this.context = context;
        this.instanceF = instanceF;
        this.routerIp = routerIp;
        this.listClient = listClient;
        DiscoverNetwork();
    }
    public static synchronized MapNetwork getInstance(Context context,
                                                      String routerIp,
                                                      FragmentDiscoverClientNatif instanceF,
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
        final UtilsNetwork          utils = new UtilsNetwork();
        final String                ipBuild = routerIp.substring(0, routerIp.lastIndexOf('.'));

        new Thread( new Runnable() {
            @Override
            public void run() {
                ArrayList<Thread> listThread = new ArrayList<>();
                launchPingthread(ipBuild, listThread, utils);
                waitEndPingThread(listThread);
                new BonjourManager(instanceF.getActivity(), instanceF, listClient);
            }}).start();
        Log.w(TAG, "Stoping Discover Natif client in Network");
    }
    private void                    launchPingthread(String ipBuild,
                                                     ArrayList<Thread> listThread,
                                                     final UtilsNetwork utils) {
        Thread                      tmpThread;

        instanceF.advanceWaitBar("Discovering Host on network", "", 3);
        for (int rcx = 1; rcx <=255 ; rcx++) {
            final String ip = ipBuild + "." + rcx;
            tmpThread = new Thread (new Runnable() {
                @Override
                public void run() {
                    final ClientNatif clientTmp;
                    if (ping(ip)) {
                        clientTmp = new ClientNatif(ip, utils, listClient);
                        getJmDNSName(ip);
                        instanceF.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                instance.getStackclient().add(clientTmp);
                                instanceF.notifyAdapter(instance.getStackclient());
                            }
                        });
                    }}});
            listThread.add(tmpThread);
            tmpThread.start();
        }
        Log.w(TAG, "all thread lanched size :" + listThread.size());
    }
    private void                    waitEndPingThread(ArrayList<Thread> listThread) {
        int                         rcx = 0;
        String                      waitStyle =  ".";

        for (Thread thread : listThread) {
            rcx++;
            if (rcx == 32) {
                rcx = 0x0;
                final String waitStyleFinal = waitStyle;
                instanceF.advanceWaitBar("Discovering Host on network" + waitStyleFinal, "", 5);
                waitStyle = waitStyle + ".";
            }
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    private void                    purgeAndRestartService() {
        listClient = new StackClientNatif();
        DiscoverNetwork();
    }
    public  StackClientNatif        getStackclient() {
        return this.listClient;
    }
}
