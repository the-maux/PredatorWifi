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
import com.myWifi.app.ViewController.View.Adapter.AdapterProbeClients;
import com.myWifi.app.ViewController.View.Adapter.AdapterSnifClients;

public class                FragmentPredatorProbe extends android.support.v4.app.Fragment {
    private final String    TAG = "FragLinkWifiPredator";
    private TextView        serverVizu, nbDevicesProbe;
    private ListView        listViewClient;
    private ImageView       replay;

    private void            initXML(View rootView) {
        rootView.findViewById(R.id.NbDevicesOnNetWork).setVisibility(View.INVISIBLE);
        nbDevicesProbe = (TextView) rootView.findViewById(R.id.NbDevicesProbe);
        serverVizu =        (TextView) rootView.findViewById(R.id.serverVizu);
        listViewClient = (ListView) rootView.findViewById(R.id.listViewIdClient);
        replay = (ImageView) rootView.findViewById(R.id.replay);
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* TODO: display new displayView*/
                ((MainActivityToFragment)getActivity()).displayView(2);
            }
        });
    }
    private void            initialisation(View rootView) {
        ApiConnectionManager ManageApi = ApiConnectionManager.getInstance(getContext(), getActivity());

        initXML(rootView);
        if (!ManageApi.HasClients())
            ManageApi.createListClient();

        AdapterProbeClients adapter =
                new AdapterProbeClients(
                        getContext(),
                        ManageApi.getListClients(),
                        serverVizu,
                        nbDevicesProbe,
                        (MainActivityToFragment) getActivity(),
                        ManageApi, this);
        ManageApi.linkAdapterListClient(adapter);
        if (!ManageApi.isApiLinked()) {
            ManageApi.connectApi();
        }
        ManageApi.startProbeMonitor();
        listViewClient.setAdapter(adapter);
    }
    @Override
    public View             onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fgmt_wifi_predator, container, false);
        initialisation(rootView);
        return rootView;
    }
    public void             errorConnection(String msg) {
        Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
        serverVizu.setBackground(new ColorDrawable(Color.RED));
    }
    public void             paramMenuClick() {
        //TODO: show dialog to enter param
    }
}
