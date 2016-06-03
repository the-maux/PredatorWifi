package com.myWifi.app.ViewController.Controler.Networking.BonjourService;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import com.myWifi.app.ViewController.Model.Networking.ClientNatif;
import com.myWifi.app.ViewController.Model.Networking.Service;
import com.myWifi.app.ViewController.Model.Networking.StackClientNatif;

import java.net.InetAddress;

/**
 * Class qui sert à resolv les Services Dans les ClientNatif
 */
class                       ResolvListnr implements NsdManager.ResolveListener {
    private String          TAG = "ResolvListnr";
    private BonjourManager  manager;
    private StackClientNatif listClient;

    public ResolvListnr(BonjourManager manager, StackClientNatif listClient) {
        this.manager = manager;
        this.listClient = listClient;
    }

    @Override
    public void             onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
        // Called when the resolve fails. Use the error code to debug.
        Log.e(TAG, "Resolve failed " + errorCode);
        Log.e(TAG, "serivce = " + serviceInfo);
    }
    private ClientNatif getclientNatifByIp(String ip) {
        for ( Object o : this.listClient) {
            if (((ClientNatif)o).getIp().contains(ip))
                return (ClientNatif) o;
        }
        return null;
    }
    @Override
    public void             onServiceResolved(NsdServiceInfo service) {
        Log.d(TAG, "Resolve Succeeded. " + service);
        InetAddress addr = service.getHost();
        if (addr != null) {
            ClientNatif client = getclientNatifByIp(addr.getHostAddress());
            if (client != null) {
                client.updateServiceHost(
                        new Service(addr.getHostAddress(),
                                addr.getCanonicalHostName(),
                                addr.getAddress(),
                                addr.getHostName(),
                                "" + service.getPort(),
                                service.getServiceName(),
                                service.getServiceType(),
                                addr, service));
            }
        }
    }
    /*
    Log.d(TAG, "addr HostAddress: " + addr.getHostAddress());
        Log.d(TAG, "addr CanonicalHostName: " + addr.getCanonicalHostName());
        Log.d(TAG, "addr Address: " + addr.getAddress());
        Log.d(TAG, "addr HostName: " + addr.getHostName());
        Log.d(TAG, "service.getHost: " + service.getHost());
        Log.d(TAG, "service.getPort: " + service.getPort());
        Log.d(TAG, "service.getServiceName: " + service.getServiceName());
        Log.d(TAG, "service.getServiceType" + service.getServiceType());
        Log.d(TAG, "service: " + service);
     */
}

