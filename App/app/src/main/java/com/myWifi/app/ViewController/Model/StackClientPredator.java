package com.myWifi.app.ViewController.Model;

import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.ViewController.View.Adapter.AdapterLinkWifiPredator;

import java.util.ArrayList;


public class StackClientPredator extends ArrayList {
    public boolean              dhcp = true, dns = true, ssid = true, http = true;
    private AdapterLinkWifiPredator adapter;
    private MainActivityToFragment activity;
    private int                 nbrPersonneConnected = 0, nbrPersonneSearching = 0;

    public StackClientPredator(MainActivityToFragment activity) {
        this.activity = activity;
    }
    @Override
    public boolean              contains(Object object) {
        if (!this.isEmpty()) {
            for (Object client : this)
                if (((ClientPredator) object).getMacAddres().contains(((ClientPredator) client).getMacAddres())) {
                    return true;
                }
        }
        return false;
    }
    @Override
    public boolean              add(Object object) {
        boolean ret = super.add(object);
        if (((ClientPredator)object).isProbe())
            this.nbrPersonneSearching++;
        else
            this.nbrPersonneConnected++;
        adapter.notifyDataSetChanged();
        return ret;
    }
    public      void            setAdapter(AdapterLinkWifiPredator adapter) {
        this.adapter = adapter;
    }
    public      boolean         isAllowed(Record.recordType type) {
        return ((type == Record.recordType.HttpCredit && http)  ||
                 (type == Record.recordType.HttpPost && http)   ||
                 (type == Record.recordType.HttpGET && http)    ||
                 (type == Record.recordType.DnsService && dns)         ||
                 (type == Record.recordType.SSID && ssid)       ||
                 (type == Record.recordType.DHCP && dhcp));
    }
    public int                  getNbrPersonneConnected() {
        return nbrPersonneConnected;
    }
    public int                  getNbrPersonneSearching() {
        return nbrPersonneSearching;
    }
}
