package com.myWifi.app.ViewController.View.Adapter.Networking;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Model.Networking.Service;


import java.util.ArrayList;

public class                ClientNatifDetailAdapter extends ArrayAdapter<Service> {
    ArrayList<Service>      stackInfo;

    public                  ClientNatifDetailAdapter(Context context,
                                                     ArrayList<Service> infoService) {
        super(context, 0, infoService);
        this.stackInfo = infoService;
    }

    @Override
    public View             getView(final int position,
                                    View convertView,
                                    ViewGroup parent) {
        final Service info = (Service) this.stackInfo.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_client_natif_detail_all, parent, false);
        }
        ((TextView) convertView.findViewById(R.id.typeDetail)).setText(info.getServiceType());
        ((TextView) convertView.findViewById(R.id.contenuDetail)).setText(info.getServiceName());

        return convertView;
    }

}
