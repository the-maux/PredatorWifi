package com.myWifi.app.ViewController.View.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Model.Param;

import java.util.ArrayList;

public class            AdapterHttpParam extends ArrayAdapter<Param> {
    ArrayList<Param>    listParam;

    public              AdapterHttpParam(Context context, ArrayList<Param> listParam) {
        super(context, 0, listParam);
        this.listParam = listParam;
    }

    @Override
    public View         getView(final int position, View convertView, ViewGroup parent) {
        final Param param = (Param) this.listParam.get(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_http_param, parent, false);
        }
        TextView key, value;
        key = (TextView) convertView.findViewById(R.id.key);
        value = (TextView) convertView.findViewById(R.id.value);
        key.setText(param.getKey());
        value.setText(param.getValue());
        return convertView;
    }
}