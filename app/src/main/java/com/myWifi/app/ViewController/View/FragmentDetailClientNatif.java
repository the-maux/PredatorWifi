package com.myWifi.app.ViewController.View;


import android.widget.ListView;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.ViewController.View.Adapter.Networking.ClientNatifDetailAdapter;
import com.myWifi.app.R;

import java.util.ArrayList;

public class                    FragmentDetailClientNatif extends android.support.v4.app.Fragment {
    private final String        TAG = "FragDetailClientNat";
    private ListView            listViewDetailClient;

    public                      FragmentDetailClientNatif() {

    }
    @Override
    public View                 onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fgmt_detail_client_natif, container, false);
        listViewDetailClient = (ListView) rootView.findViewById(R.id.listViewDetailClient);

        // cette liste est créer à partir de toutes les infos contenue dans Client Natif
        // Tu dois aussi remplir se fragment avec le nom du client et link la listView

        ArrayList listAllDetailInfo = ((MainActivityToFragment)getActivity()).clientNatif.buildListInfo();
        ClientNatifDetailAdapter adapter = new ClientNatifDetailAdapter(getContext(), listAllDetailInfo);
        listViewDetailClient.setAdapter(adapter);
        Log.w(TAG, "createView");
        return rootView;
    }


}
