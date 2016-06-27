package com.myWifi.app.ViewController.Model;

import android.util.Log;
import android.widget.ArrayAdapter;
import com.myWifi.app.MainActivityToFragment;

import java.util.ArrayList;


public class                    StackClientProbe extends ArrayList {
    private ArrayAdapter        adapter;
    private MainActivityToFragment activity;
    private int                 nbrPersonneSearching = 0;
    private String              TAG = "StackClientProbe";

    public StackClientProbe(MainActivityToFragment activity) {
        this.activity = activity;
    }
    @Override
    public boolean              contains(Object object) {
        if (!this.isEmpty()) {
            for (Object client : this)
                if (((Client) object).getMacAddres().contains(((Client) client).getMacAddres())) {
//                    Log.d(TAG, "Client Probe already exist with " + ((Client) object).getMacAddres());
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
        adapter.notifyDataSetChanged();
        return ret;
    }
    public      void            setAdapter(ArrayAdapter adapter) {
        this.adapter = adapter;
    }
    public int                  getNbrPersonneSearching() {
        return nbrPersonneSearching;
    }
}
