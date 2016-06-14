package com.myWifi.app.ViewController.View;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.myWifi.app.R;


public class                FragmentApScanObsolet extends android.support.v4.app.Fragment {
    private final String    TAG = "FragmentApScanObsolet";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View rootView = inflater.inflate(R.layout.fgmt_home_wifi_detail, container, false);
        Log.w(TAG, "createView");
        //     return super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }
}
