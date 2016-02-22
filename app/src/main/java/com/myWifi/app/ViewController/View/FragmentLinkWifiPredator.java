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
import com.myWifi.app.ViewController.View.Adapter.Predator.ClientProbeAdapter;
import com.myWifi.app.ViewController.Controler.Predator.LinkWifiPredator;
import com.myWifi.app.ViewController.Model.Predator.StackClientPredator;

public class                FragmentLinkWifiPredator extends android.support.v4.app.Fragment {
    private final String    TAG = "FragLinkWifiPredator";
    private TextView        serverVizu;

    public void             initialisation(View rootView) {
        LinkWifiPredator    linkServer;
        StackClientPredator listClient;
        TextView            nbDevicesOnNetWork = (TextView) rootView.findViewById(R.id.NbDevicesOnNetWork);
        TextView            nbDevicesProbe = (TextView) rootView.findViewById(R.id.NbDevicesProbe);
        serverVizu = (TextView) rootView.findViewById(R.id.serverVizu);
        ListView            listViewClient = (ListView) rootView.findViewById(R.id.listViewIdClient);
        ImageView           replay = (ImageView) rootView.findViewById(R.id.replay);

        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivityToFragment)getActivity()).displayView(2);
            }
        });

        if (((MainActivityToFragment)getActivity()).myListClient == null) {
            listClient = new StackClientPredator((MainActivityToFragment) getActivity());
            ((MainActivityToFragment) getActivity()).myListClient = listClient;
        } else listClient = ((MainActivityToFragment) getActivity()).myListClient;

        ClientProbeAdapter adapter = new ClientProbeAdapter(getContext(), listClient, nbDevicesOnNetWork, serverVizu,
                nbDevicesProbe, (MainActivityToFragment) getActivity());
        listClient.setAdapter(adapter);
        if (((MainActivityToFragment)getActivity()).getLinkWifiPredator() == null) {
            linkServer = new LinkWifiPredator("10.0.0.1", 42429, listClient, this);
            linkServer.execute();
        }
        else
            linkServer = ((MainActivityToFragment)getActivity()).getLinkWifiPredator();
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
}
