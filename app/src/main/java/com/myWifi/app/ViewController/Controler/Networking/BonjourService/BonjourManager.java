package com.myWifi.app.ViewController.Controler.Networking.BonjourService;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;

import com.myWifi.app.ViewController.Model.Networking.StackClientNatif;
import com.myWifi.app.ViewController.View.FragmentDiscoverClientNatif;

import java.util.HashMap;

public class                    BonjourManager  {
    private String              TAG = "BonjourManager";
    private NsdManager          mNsdManager;
    private ResolvListnr ResolvServiceToClient;
    private HashMap<String, DiscoveryListnr> listDiscoveryListener = new HashMap<>();
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
                "_nvstream._tcp", //
                "_printer._tcp", //
                "_ipp._tcp", //Print Center browses for IPP printers starting in Mac OS X 10.2.
                "_daap._tcp", //Also known as iTunes Music Sharing.
                "_dpap._tcp", //Also known as iPhoto Photo Sharing.
                "_airport._tcp", //sed by the AirPort Admin Utility
                "_mdns._tcp",
                "_mdnsresponder._tcp",
                "_ssh._tcp"});
    }
    private FragmentDiscoverClientNatif fragment;

    public                      BonjourManager(Activity activity, FragmentDiscoverClientNatif fragment,
                                               StackClientNatif listClient) {
        mNsdManager = (NsdManager) activity.getSystemService(Context.NSD_SERVICE);
        this.listServiceType = getAllType();
        this.ResolvServiceToClient = new ResolvListnr(this, listClient);
        this.fragment = fragment;
        createListListener();
    }
    /**
     * If Not All service alrady Checked
     * At every Call launch a new Listener on new Service
     */
    public void                 createListListener() {
        if (offsetServiceType < listServiceType.length) {
            String type = listServiceType[offsetServiceType++];
            fragment.advanceWaitBar("Service : ", type.substring(1, type.indexOf("._")).toUpperCase(), 4);
            DiscoveryListnr listener = new DiscoveryListnr(instance, type);
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
