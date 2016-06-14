package com.myWifi.app.ViewController.Model;

import com.myWifi.app.ViewController.Controler.BonjourService.Service;
import com.myWifi.app.ViewController.Controler.UtilsNetwork;
import com.myWifi.app.ViewController.View.FragmentDiscoverWifi;

import java.util.ArrayList;

public class            ClientNatif {
    private String      ip;
    private String      MacAdress;
    private String      Hostname;
    private clientType  type;
    private boolean     isServiceActiveOnHost = false;
    private ArrayList<Service>   ServiceActivOnHost = null;



    enum                clientType { Router, Mac, Pc, Android, Ios, Unknow,  }
    public              ClientNatif(String ip, UtilsNetwork utils,
                                    FragmentDiscoverWifi instanceF, StackClientNatif listClient) {
        this.ip = ip;
        //TODO: maybe obsolete cause of ServiceDiscovery et JmDNS
        Hostname = utils.getHostnameReverseDns(ip);
        MacAdress = utils.getMacAddressFromIpByARP(ip);
        buildTypeHost();
    }

    /**
     * Starting fingerprint OS type
     */
    private void        buildTypeHost() {
        if (ip.substring(ip.lastIndexOf('.'), ip.length()).equals("1")
                || Hostname.contains("router") || Hostname.contains("box")) {
            type = clientType.Router;
        } else if (Hostname.isEmpty()) {
            type = clientType.Unknow;
        } else if (Hostname.contains("pc")) {
            type = clientType.Pc;
        } else if (Hostname.contains("android")) {
            type = clientType.Android;
        } else if (Hostname.contains("ios")) {
            type = clientType.Ios;
        } else if (Hostname.contains("mac") || Hostname.contains("os")) {
            type = clientType.Mac;
        }
    }

    /**
     * Update service by BonjourManager
     */
    public void         updateServiceHost(Service service) {
        if (ServiceActivOnHost == null) {
            ServiceActivOnHost = new ArrayList<>();
            isServiceActiveOnHost = true;
        }
        ServiceActivOnHost.add(service);
    }

    public String       getHostname() {
        return Hostname;
    }
    public String       getMacAdress() {
        return MacAdress;
    }
    public clientType   getType() {
        return type;
    }
    public String       getIp() {
        return ip;
    }
    public boolean      isServiceActiveOnHost() {
        return isServiceActiveOnHost;
    }
}
