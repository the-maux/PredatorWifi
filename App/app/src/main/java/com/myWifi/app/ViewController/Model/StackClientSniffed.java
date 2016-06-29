package com.myWifi.app.ViewController.Model;

import android.util.Log;
import android.widget.ArrayAdapter;
import com.myWifi.app.MainActivityToFragment;

import java.util.ArrayList;


public class StackClientSniffed extends ArrayList {
    public boolean              dhcp = true, dns = true, ssid = true, http = true;
    private ArrayAdapter        adapter;
    private MainActivityToFragment activity;
    private int                 nbrPersonneConnected = 0, nbrPersonneSearching = 0;
    private String              TAG = "StackClientSniffed";

    public StackClientSniffed(MainActivityToFragment activity) {
        this.activity = activity;
    }
    @Override
    public boolean              contains(Object object) {
        if (!this.isEmpty()) {
            for (Object client : this)
                if (((Client) object).getMacAddres().contains(((Client) client).getMacAddres())) {
                    Log.d(TAG, "Client already exist with " + ((Client) object).getMacAddres());
                    return true;
                }
        }
        Log.d(TAG, "new Probe client : " + ((Client) object).getMacAddres());
        return false;
    }
    @Override
    public boolean              add(Object object) {
        boolean ret = super.add(object);
        if (((Client)object).isProbe())
            this.nbrPersonneSearching++;
        else
            this.nbrPersonneConnected++;
        adapter.notifyDataSetChanged();
        return ret;
    }
    public      void            setAdapter(ArrayAdapter adapter) {
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
