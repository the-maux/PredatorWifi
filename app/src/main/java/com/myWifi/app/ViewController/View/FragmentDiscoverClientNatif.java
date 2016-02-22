package com.myWifi.app.ViewController.View;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.View.Adapter.Networking.clientNatifAdapter;
import com.myWifi.app.ViewController.Controler.Networking.MapNetwork;
import com.myWifi.app.ViewController.Model.Networking.StackClientNatif;

public class                        FragmentDiscoverClientNatif extends android.support.v4.app.Fragment {
    private final String            TAG = "FragmentDiscoverClientNatif";
    private MapNetwork              mapNetwork;
    private StackClientNatif        listClient;
    private ListView                listViewClientNatifs;
    private clientNatifAdapter      adapter;
    TextView                        TVNameNetwork, typeNetwork, TvServiceType, TvServiceName;
    ProgressBar                     progressBTotal, progressBarWaitting;
    RelativeLayout                  LogWaitLayout;

    @Override
    public void             onCreate(Bundle bundle) {
        super.onCreate(bundle);
        listClient = new StackClientNatif();
        mapNetwork = MapNetwork.getInstance(getContext(),
                ((MainActivityToFragment)getActivity()).infoConfNetWork.getGatewayIp(), this, listClient);
    }

    @Override
    public View             onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fgmt_discover_wifi, container, false);
        listViewClientNatifs = (ListView) rootView.findViewById(R.id.LVNatifClient);
        adapter = new clientNatifAdapter(getContext(), mapNetwork.getStackclient(), this);
        listViewClientNatifs.setAdapter(adapter);
        initXml(rootView);
        return rootView;
    }

    private void            initXml(View rootView) {
        TVNameNetwork = (TextView) rootView.findViewById(R.id.TVNameNetwork);
        typeNetwork = (TextView) rootView.findViewById(R.id.typeNetwork);
        TvServiceType = (TextView) rootView.findViewById(R.id.TvServiceType);
        TvServiceName = (TextView) rootView.findViewById(R.id.TvServiceName);
        progressBTotal = (ProgressBar) rootView.findViewById(R.id.progressBTotal);
        progressBarWaitting = (ProgressBar) rootView.findViewById(R.id.progressBarWaitting);
        LogWaitLayout = (RelativeLayout) rootView.findViewById(R.id.LogWaitLayout);
        if ((getActivity().getSystemService(Context.WIFI_SERVICE)) != null)
            TVNameNetwork.setText(((WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE))
                    .getConnectionInfo().getSSID().replace("\"", ""));
    }

    public void            advanceWaitBar(final String nameLog, final String nameService, final int progress) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TvServiceName.setText(nameService);
                TvServiceType.setText(nameLog);
                progressBTotal.setProgress(progressBTotal.getProgress() + progress);
            }
        });
    }
    public void             notifyAdapter(StackClientNatif listClient) {
        this.listClient = listClient;
        adapter.notifyDataSetChanged();
    }

    public void             notifiyServiceAllScaned() {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getContext(), "Bienjoué ma guelle ta listen tous les services", Toast.LENGTH_LONG).show();
                LogWaitLayout.setVisibility(View.GONE);
            }
        });
    }
    @Override
    public void             onPause () {
        super.onPause();
    }
    @Override
    public void             onResume() {
        super.onResume();

    }

}
