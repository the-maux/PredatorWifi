package com.myWifi.app.ViewController.Model.Networking;

import android.util.Log;
import com.myWifi.app.ViewController.Controler.Networking.UtilsNetwork;

import java.util.ArrayList;

public class            ClientNatif {
    private String      TAG = "ClientNatif";
    private String      ip;
    private String      MacAdress;
    private String      Hostname;
    private clientType  type;
    private boolean     isServiceActiveOnHost = false;
    private ArrayList<Service>   ServiceActivOnHost = null;

    enum                clientType { Router, Mac, Pc, Android, Ios, Unknow,  }
    public              ClientNatif(String ip, UtilsNetwork utils,
                                    StackClientNatif listClient) {
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
        Log.w(TAG, "update Service from :" + ip + " and Service :" + service.getServiceName());
        ServiceActivOnHost.add(service);
    }
    public ArrayList    buildListInfo() {
        ArrayList<Service> listInfo = new ArrayList<>();

        listInfo.add(new Service("Ip: ", ip));
        listInfo.add(new Service("Mac Adress: ", MacAdress));
        listInfo.add(new Service("Hostname", Hostname));
        if (this.ServiceActivOnHost != null) {
            for (Service service : this.ServiceActivOnHost) {
                listInfo.add(new Service(service.getServiceType(), service.getServiceName()));
            }
        }
        return listInfo;
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
