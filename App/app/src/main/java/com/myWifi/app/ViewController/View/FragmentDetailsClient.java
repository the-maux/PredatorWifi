package com.myWifi.app.ViewController.View;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Controler.ApiConnectionManager;
import com.myWifi.app.ViewController.View.Adapter.AdapterClientDetail;
import com.myWifi.app.ViewController.Model.ClientPredator;
import com.myWifi.app.ViewController.Model.StackClientPredator;


public class                FragmentDetailsClient extends android.support.v4.app.Fragment {
    private final String    TAG = "FragmentMyNetWork";
    private StackClientPredator clientStack;
    private AdapterClientDetail adapter;
    private TextView        nameDevice;
    private LinearLayout    layoutFilter;

    private void            initializeBtns(View rootView) {
        final RadioButton btnDHCP = (RadioButton) rootView.findViewById(R.id.radioButtonDHCP);
        final RadioButton btnSSID = (RadioButton) rootView.findViewById(R.id.radioButtonSSI);
        final RadioButton btnDNS = (RadioButton) rootView.findViewById(R.id.radioButtonDNS);
        final RadioButton btnHTTP = (RadioButton) rootView.findViewById(R.id.radioButtonHTTP);
        layoutFilter = (LinearLayout) rootView.findViewById(R.id.layoutFilter);

        btnDHCP.setChecked(true);
        btnSSID.setChecked(true);
        btnDNS.setChecked(true);
        btnHTTP.setChecked(true);
        btnHTTP.setTextColor(Color.WHITE);
        btnDNS.setTextColor(Color.GREEN);
        btnDHCP.setTextColor(Color.YELLOW);
        btnSSID.setTextColor(Color.RED);
        btnDHCP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientStack.dhcp = !clientStack.dhcp;
                btnDHCP.setChecked(clientStack.dhcp);
                adapter.notifyDataSetChanged();
            }           });
        btnSSID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientStack.ssid = !clientStack.ssid;
                btnSSID.setChecked(clientStack.ssid);
                adapter.notifyDataSetChanged();
            }           });
        btnDNS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientStack.dns = !clientStack.dns;
                btnDNS.setChecked(clientStack.dns);
                adapter.notifyDataSetChanged();
            }           });
        btnHTTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clientStack.http = !clientStack.http;
                btnHTTP.setChecked(clientStack.http);
                adapter.notifyDataSetChanged();
            }           });
    }
    private void            initialize(View rootView) {
        clientStack = ApiConnectionManager.getInstance(getContext(), getActivity()).getListClients();
        initializeBtns(rootView);
        ListView clientRecordListView = (ListView) rootView.findViewById(R.id.listViewRedcordClient);
        ClientPredator actualClientPredator = ((MainActivityToFragment) getActivity()).getActualClientPredator();
        if (actualClientPredator == null) ((MainActivityToFragment) getActivity()).displayView(2);
        else {
            ((TextView) rootView.findViewById(R.id.nameDevice)).setText(actualClientPredator.getNameDevice());
            adapter = new AdapterClientDetail(getContext(), actualClientPredator, clientStack);
            clientRecordListView.setAdapter(adapter);
            actualClientPredator.setAdapterDetailClient(adapter);
        }
    }
    @Override
    public View             onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fgmt_detail_client_predator, container, false);
        initialize(rootView);
        return rootView;
    }
    public void             paramMenuClick() {
        if (layoutFilter.isShown()) {
            layoutFilter.setVisibility(View.GONE);
        } else {
            layoutFilter.setVisibility(View.VISIBLE);
        }
    }
}
