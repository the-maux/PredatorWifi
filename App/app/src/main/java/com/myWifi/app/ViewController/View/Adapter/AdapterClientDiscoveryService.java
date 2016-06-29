package com.myWifi.app.ViewController.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Model.ClientNatif;
import com.myWifi.app.ViewController.Model.StackClientNatif;

public class AdapterClientDiscoveryService extends ArrayAdapter<ClientNatif> {
    StackClientNatif                stackClient;

    public AdapterClientDiscoveryService(Context context, StackClientNatif stackclient) {
        super(context, 0, stackclient);
        this.stackClient = stackclient;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ClientNatif client = (ClientNatif) this.stackClient.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_client_discover, parent, false);
        }
        ImageView idType = (ImageView) convertView.findViewById(R.id.idType);
        TextView hostname = (TextView) convertView.findViewById(R.id.hostname);
        TextView ip = (TextView) convertView.findViewById(R.id.ip);
        TextView macAddr = (TextView) convertView.findViewById(R.id.macAddr);

        idType.setBackgroundResource(R.drawable.account);
        hostname.setText(client.getHostname());
        ip.setText(client.getIp());
        macAddr.setText(client.getMacAdress());

        return convertView;
    }

}
