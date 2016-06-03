package com.myWifi.app.ViewController.View.Adapter.Networking;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.larvalabs.svgandroid.SVGParser;
import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Model.Networking.ClientNatif;
import com.myWifi.app.ViewController.Model.Networking.StackClientNatif;
import com.myWifi.app.ViewController.View.FragmentClientPredatorDetail;
import com.myWifi.app.ViewController.View.FragmentDiscoverClientNatif;

public class                        clientNatifAdapter extends ArrayAdapter<ClientNatif> {
    StackClientNatif                stackClient;
    FragmentDiscoverClientNatif     fragment;

    public                          clientNatifAdapter(Context context,
                                        StackClientNatif stackclient,
                                        FragmentDiscoverClientNatif fragment) {
        super(context, 0, stackclient);
        this.stackClient = stackclient;
        this.fragment = fragment;
    }

    private void                    setImgForVendor(String MacAddr, ImageView idType) {
        idType.setBackgroundResource(R.drawable.account);
//TODO: MacAddr Vendor here        if ()
    }

    @Override
    public View                     getView(final int position, View convertView, ViewGroup parent) {
        final ClientNatif client = (ClientNatif) this.stackClient.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_client_natif_pinged, parent, false);
        }
        ImageView idType = (ImageView) convertView.findViewById(R.id.idType);
        TextView hostname = (TextView) convertView.findViewById(R.id.hostname);
        TextView ip = (TextView) convertView.findViewById(R.id.ip);
        TextView macAddr = (TextView) convertView.findViewById(R.id.macAddr);
        RelativeLayout rel = (RelativeLayout) convertView.findViewById(R.id.AllLayout);
        String ipString = client.getIp();

        if (ipString.substring(ipString.lastIndexOf("." + 1, ipString.length())).equals("1"))//if router
            idType.setImageDrawable(
                    SVGParser.getSVGFromResource(
                            fragment.getResources(), R.raw.router)
                            .createPictureDrawable());
        if (!ipString.contains(client.getHostname())) { //if Service discovery ResolveName
            hostname.setText(client.getHostname());
            hostname.setTextColor(0xff9f37);
        }
        setImgForVendor(client.getMacAdress(), idType);
        ip.setText(ipString);
        macAddr.setText(client.getMacAdress());
        rel.setOnClickListener(new AdapterView.OnClickListener() {
            @Override
            public void             onClick(View v) {
                ((MainActivityToFragment)fragment.getActivity()).displayView(4);
                ((MainActivityToFragment)fragment.getActivity()).clientNatif = client;
            }
        });

        return convertView;
    }

}
