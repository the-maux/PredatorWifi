package com.myWifi.app.ViewController.View;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;
import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.View.Adapter.clientDiscoverNetAdapter;
import com.myWifi.app.ViewController.Controler.BonjourService.BonjourManager;
import com.myWifi.app.ViewController.Controler.MapNetwork;
import com.myWifi.app.ViewController.Model.StackClientNatif;

public class FragmentWifiDiscovery extends android.support.v4.app.Fragment {
    private final String            TAG = "FragmentWifiDiscovery";
    private MapNetwork              mapNetwork;
    private StackClientNatif        listClient;
    private ListView                listViewClientNatifs;
    private clientDiscoverNetAdapter adapter;
    private BonjourManager          BonjourManager;

    @Override
    public void             onCreate(Bundle bundle) {
        super.onCreate(bundle);
        listClient = new StackClientNatif();
        mapNetwork = MapNetwork.getInstance(getContext(),
                ((MainActivityToFragment)getActivity()).infoNetWork.getGatewayIp(), this, listClient);
        BonjourManager = new BonjourManager(getActivity(), this, listClient);
    }

    @Override
    public View             onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fgmt_discover_wifi, container, false);
        listViewClientNatifs = (ListView) rootView.findViewById(R.id.LVNatifClient);
        adapter = new clientDiscoverNetAdapter(getContext(), mapNetwork.getStackclient());
        listViewClientNatifs.setAdapter(adapter);
        return rootView;
    }

    public void             notifyNew(StackClientNatif listClient) {
        this.listClient = listClient;
        adapter.notifyDataSetChanged();
    }

    public void             notifiyServiceAllScaned() {
        Toast.makeText(getContext(), "Discovery service ended", Toast.LENGTH_LONG).show();
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
