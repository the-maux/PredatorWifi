package com.myWifi.app.ViewController.View.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Controler.ApiConnectionManager;
import com.myWifi.app.ViewController.Model.ClientPredator;
import com.myWifi.app.ViewController.Model.StackClientPredator;
import com.myWifi.app.ViewController.View.Dialog.DialogDetailReconnectWifi;
import com.myWifi.app.ViewController.View.FragmentPredatorProbe;


public class                        AdapterProbeClients extends ArrayAdapter<ClientPredator> {
    private StackClientPredator     listClient;
    private TextView                nbDevicesProbe, serverVizu;
    private MainActivityToFragment  instance;
    private ApiConnectionManager    managerApi;
    private FragmentPredatorProbe   fragment;

    public AdapterProbeClients(Context context, StackClientPredator listClient,
                               TextView serverVizu, TextView nbDeviceProbe, MainActivityToFragment instance,
                               ApiConnectionManager managerApi, FragmentPredatorProbe fragment) {
        super(context, 0, listClient);
        this.listClient = listClient;
        this.nbDevicesProbe = nbDeviceProbe;
        this.instance = instance;
        this.serverVizu = serverVizu;
        this.managerApi = managerApi;
        this.fragment = fragment;
    }

    @Override
    public View                     getView(final int position, View convertView, ViewGroup parent) {
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

        nbDevicesProbe.setText("" + listClient.getNbrPersonneSearching());
        serverVizu.setBackgroundColor(Color.GREEN);
        return convertView;
    }
    private AdapterView.OnClickListener initBehaviorKarmaAttack(final ClientPredator clientPredator) {
        return new AdapterView.OnClickListener() {
            @Override
            public void             onClick(View v) {
                DialogDetailReconnectWifi dialog = new DialogDetailReconnectWifi(fragment.getContext(), fragment.getActivity());
                dialog.create().show();
                managerApi.stopProbeMonitor();
                managerApi.changeApName(clientPredator.getSSID());
                managerApi.reconnectToserverProbe(fragment, dialog);
            }
        };
    }
    private void                        setDetailclientFragmentLauncher(final ClientPredator clientPredator, RelativeLayout rel) {
        AdapterView.OnClickListener AttackKarmaProbeRequest = initBehaviorKarmaAttack(clientPredator);
        rel.setOnClickListener(AttackKarmaProbeRequest);
    }
    private void                        setUIClient(View convertView, ClientPredator clientPredator,
                                        TextView SSID, TextView time, ImageView idType) {
        ((TextView) convertView.findViewById(R.id.nameDevice)).setText(clientPredator.getNameDevice());
        ((TextView) convertView.findViewById(R.id.macAddr)).setText(clientPredator.getMacAddres());
        if (clientPredator.isProbe()) {
            SSID.setText(clientPredator.getSSID());
            time.setText(clientPredator.getTime());
            idType.setBackgroundResource(R.drawable.access_point);
        }
    }

}
