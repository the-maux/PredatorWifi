package com.myWifi.app.ViewController.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Controler.Networking.InfoConfNetWork;


public class                FragmentWifiDetails extends android.support.v4.app.Fragment {
    private final String    TAG = "FragmentWifiDetails";
    private TextView        IpTextV, MacTextV, WifiSpeedTextV, WifiSSIDTextV, IpRouterTextV, DeviceConnectedTextV;

    private void            initTheView(View rootView) {
        IpTextV = (TextView) rootView.findViewById(R.id.ipTextV);
        MacTextV = (TextView) rootView.findViewById(R.id.MacTextV);
        WifiSpeedTextV =  (TextView) rootView.findViewById(R.id.WifiSpeedTextV);
        WifiSSIDTextV = (TextView) rootView.findViewById(R.id.WifiSSIDTextV);
        IpRouterTextV = (TextView) rootView.findViewById(R.id.IpRouterTextV);
        DeviceConnectedTextV = (TextView) rootView.findViewById(R.id.DeviceConnectedTextV);

        InfoConfNetWork wm = new InfoConfNetWork(getActivity());
        IpTextV.setText(wm.getRealIp());
        MacTextV.setText(wm.getMacAddress());
        WifiSpeedTextV.setText(wm.getSpeed() + "db");
        WifiSSIDTextV.setText(wm.getSsid());
        IpRouterTextV.setText(wm.getGatewayIp());
        DeviceConnectedTextV.setText(wm.getNetmaskIp());
        return ;
    }
    @Override
    public View             onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fgmt_home_wifi_detail, container, false);
        initTheView(rootView);
        Log.w(TAG, "createView");
        return rootView;
    }
}
