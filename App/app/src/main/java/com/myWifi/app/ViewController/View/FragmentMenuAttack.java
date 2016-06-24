package com.myWifi.app.ViewController.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Controler.ManagerWifi;
import com.myWifi.app.ViewController.View.Dialog.DialogDetailReconnectWifi;


public class                FragmentMenuAttack extends android.support.v4.app.Fragment {
    private final String    TAG = "FragmentMenuAttack";
    private ManagerWifi     wifiManager;
    private FragmentMenuAttack instance = this;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.w("FragmentMenuAttack", "createView");
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fgmt_menu_attack, container, false);
        wifiManager = ((MainActivityToFragment)getActivity()).getManagerWifi();

        rootView.findViewById(R.id.KarmaButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DialogDetailReconnectWifi dialog = new DialogDetailReconnectWifi(getContext(), getActivity());
                dialog.create().show();
                wifiManager.waitSyncServer(instance, dialog);
            }
        });
        rootView.findViewById(R.id.evillTwinAttack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                TODO:
                 */
            }
        });
        rootView.findViewById(R.id.networkAudit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                TODO:
                 */
            }
        });
        return rootView;
    }
    public void             errorConnection(final String errorMsg) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_LONG).show();
            }
        });
    }
    public void             successConnection() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "sucess connection");
                ((MainActivityToFragment)getActivity()).displayView(6);
            }
        });
    }
}
