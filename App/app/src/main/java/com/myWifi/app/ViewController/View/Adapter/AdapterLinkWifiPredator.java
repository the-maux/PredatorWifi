package com.myWifi.app.ViewController.View.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Controler.MyWifiConfiguration;
import com.myWifi.app.ViewController.Model.ClientPredator;
import com.myWifi.app.ViewController.Model.StackClientPredator;


public class AdapterLinkWifiPredator extends ArrayAdapter<ClientPredator> {
    private StackClientPredator listClient;
    private TextView        nbDevicesOnNetwork, nbDevicesProbe, serverVizu;
    private MainActivityToFragment instance;

    public AdapterLinkWifiPredator(
            Context context, StackClientPredator listClient, TextView nbDevicesOnNetWork, TextView serverVizu, TextView nbDeviceProbe, MainActivityToFragment instance) {
        super(context, 0, listClient);
        this.listClient = listClient;
        this.nbDevicesOnNetwork = nbDevicesOnNetWork;
        this.nbDevicesProbe = nbDeviceProbe;
        this.instance = instance;
        this.serverVizu = serverVizu;
    }

    @Override
    public View             getView(final int position, View convertView, ViewGroup parent) {
        final ClientPredator clientPredator = (ClientPredator) this.listClient.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_client_predator, parent, false);
        }
        TextView SSID = (TextView) convertView.findViewById(R.id.SSID);
        TextView time = (TextView) convertView.findViewById(R.id.time);
        ImageView idType = (ImageView) convertView.findViewById(R.id.idType);
        RelativeLayout rel = (RelativeLayout) convertView.findViewById(R.id.AllLayout);

        setUIClient(convertView, clientPredator, SSID, time, idType);
        setDetailclientFragmentLauncher(clientPredator, rel);

        nbDevicesOnNetwork.setText("" + listClient.getNbrPersonneConnected());
        nbDevicesProbe.setText("" + listClient.getNbrPersonneSearching());
        serverVizu.setBackgroundColor(Color.GREEN);
        return convertView;
    }
    private AdapterView.OnClickListener initBehaviorKarmaAttack(final ClientPredator clientPredator) {
        return new AdapterView.OnClickListener() {
            @Override
            public void             onClick(View v) {
                final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(instance);
                alertDialogBuilder.setTitle("Reconnect rogue AP with : " + clientPredator.getSSID());
                LayoutInflater inflater = (LayoutInflater) instance.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View layoutView = inflater.inflate(R.layout.alert_dialog_ssid, null);
                DialogInterface.OnClickListener behaviorSsidOK = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new MyWifiConfiguration(clientPredator.getSSID(), "nopasswd", MyWifiConfiguration.networkType.OPEN, getContext());
                        //TODO: attendre que le  Broadcast Receiver revienne ou timeout
                    } };
                DialogInterface.OnClickListener behaviorSsidKO = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                };
                alertDialogBuilder
                        .setCancelable(false)
                        .setView(layoutView)
                        .setPositiveButton("Yes", behaviorSsidOK)
                        .setNegativeButton("No", behaviorSsidKO);
            }
        };
    }
    private void            setDetailclientFragmentLauncher(final ClientPredator clientPredator, RelativeLayout rel) {
        AdapterView.OnClickListener AttackKarmaProbeRequest = initBehaviorKarmaAttack(clientPredator);
        if (!clientPredator.isProbe()) {
            rel.setOnClickListener(new AdapterView.OnClickListener() {
                @Override
                public void onClick(View v) {
                    instance.setActualClientPredator(clientPredator);
                    instance.displayView(4);
                }
            });
        } else rel.setOnClickListener(AttackKarmaProbeRequest);
    }
    private void            setUIClient(View convertView, ClientPredator clientPredator,
                                        TextView SSID, TextView time, ImageView idType) {
        ((TextView) convertView.findViewById(R.id.nameDevice)).setText(clientPredator.getNameDevice());
        ((TextView) convertView.findViewById(R.id.macAddr)).setText(clientPredator.getMacAddres());
        if (clientPredator.isProbe()) {
            SSID.setText(clientPredator.getSSID());
            time.setText(clientPredator.getTime());
            idType.setBackgroundResource(R.drawable.access_point);
        } else {
            SSID.setText(clientPredator.getIP());
            idType.setBackgroundResource(R.drawable.account);
        }
    }

}
