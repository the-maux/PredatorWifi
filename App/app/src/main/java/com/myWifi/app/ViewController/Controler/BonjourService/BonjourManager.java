package com.myWifi.app.ViewController.Controler.BonjourService;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import com.myWifi.app.ViewController.Model.StackClientNatif;
import com.myWifi.app.ViewController.View.FragmentWifiDiscovery;

import java.util.HashMap;

public class                    BonjourManager  {
    private String              TAG = "BonjourManager";
    private NsdManager          mNsdManager;
    private ResolvListener      ResolvServiceToClient;
    private HashMap<String, DiscoveryListenr> listDiscoveryListener = new HashMap<>();
    private BonjourManager      instance = this;
    private String[]            listServiceType;
    private int                 offsetServiceType = 0;
    private String[]            getAllType() {
        //all in mac _services._dns-sd._udp.local.
        // _workstation._tcp.local.
        return (new String[]{"_workstation._tcp",
                "_airplay._tcp.",
                "_raop._tcp", //Also known as AirTunes.
                "_screencast._udp",
                "_airplay._tcp",
                "_nfs._tcp", //The Finder browses for NFS servers starting in Mac OS X 10.2.
                "_ftp._tcp", //
                "_http._tcp", //
                "_telnet._tcp", //
                "_printer._tcp", //
                "_ipp._tcp", //Print Center browses for IPP printers starting in Mac OS X 10.2.
                "_daap._tcp", //Also known as iTunes Music Sharing.
                "_dpap._tcp", //Also known as iPhoto Photo Sharing.
                "_airport._tcp", //sed by the AirPort Admin Utility
                "_mdns._tcp",
                "_mdnsresponder._tcp",
                "_ssh._tcp"});
    }
    private FragmentWifiDiscovery fragment;
    private StackClientNatif    listClient;

    public                      BonjourManager(Activity activity, FragmentWifiDiscovery fragment,
                                               StackClientNatif listClient) {
        mNsdManager = (NsdManager) activity.getSystemService(Context.NSD_SERVICE);
        this.listServiceType = getAllType();
        this.ResolvServiceToClient = new ResolvListener(this, listClient);
        this.fragment = fragment;
        this.listClient = listClient;
        createListListener();
    }
    public void                 createListListener() {
        if (offsetServiceType < listServiceType.length) {
            String type = listServiceType[offsetServiceType++];
            //TODO:maybe cant be 1 DiscoveryListener for all Servicediscovery
            DiscoveryListenr listener = new DiscoveryListenr(instance, type);
            mNsdManager.discoverServices(type,
                    NsdManager.PROTOCOL_DNS_SD, listener);
            listDiscoveryListener.put(type, listener);
        } else {
           fragment.notifiyServiceAllScaned();
        }
    }
    public void                 stopServiceDiscovery(NsdManager.DiscoveryListener listene) {
        this.mNsdManager.stopServiceDiscovery(listene);
    }
    public void                 resolveService(NsdServiceInfo service) {
        this.mNsdManager.resolveService(service,  this.ResolvServiceToClient);
    }
}
