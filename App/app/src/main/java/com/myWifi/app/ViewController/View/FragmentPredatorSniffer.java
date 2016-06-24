package com.myWifi.app.ViewController.View;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Controler.ApiConnectionManager;
import com.myWifi.app.ViewController.View.Adapter.AdapterSnifClients;

public class                FragmentPredatorSniffer extends android.support.v4.app.Fragment {
    private final String    TAG = "FragLinkWifiPredator";
    private TextView        serverVizu, nbDevicesOnNetWork, nbDevicesProbe;
    private ListView        listViewClient;
    private ImageView       replay;

    private void            initXML(View rootView) {
        nbDevicesOnNetWork = (TextView) rootView.findViewById(R.id.NbDevicesOnNetWork);
        nbDevicesProbe = (TextView) rootView.findViewById(R.id.NbDevicesProbe);
        serverVizu =        (TextView) rootView.findViewById(R.id.serverVizu);
        listViewClient = (ListView) rootView.findViewById(R.id.listViewIdClient);
        replay = (ImageView) rootView.findViewById(R.id.replay);
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivityToFragment)getActivity()).displayView(2);
            }
        });
    }
    public void             initialisation(View rootView) {
        ApiConnectionManager ManageApi = ApiConnectionManager.getInstance(getContext(), getActivity());

        initXML(rootView);
        if (!ManageApi.HasClients())
            ManageApi.createListClient();

        AdapterSnifClients adapter =
                new AdapterSnifClients(
                        getContext(),
                        ManageApi.getListClients(),
                        nbDevicesOnNetWork,
                        serverVizu,
                        nbDevicesProbe,
                        (MainActivityToFragment) getActivity());
        ManageApi.linkAdapterListClient(adapter);
        ManageApi.connectApi();
        listViewClient.setAdapter(adapter);
    }
    @Override
    public View             onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fgmt_wifi_predator, container, false);
        initialisation(rootView);
        return rootView;
    }
    public void             errorConnection() {
        Toast.makeText(getActivity(), "Server Unreachable", Toast.LENGTH_LONG).show();
        serverVizu.setBackground(new ColorDrawable(Color.RED));
    }
    public void             paramMenuClick() {
        //TODO: show dialog to enter param
    }
}
