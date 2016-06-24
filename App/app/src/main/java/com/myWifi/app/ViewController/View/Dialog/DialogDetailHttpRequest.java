package com.myWifi.app.ViewController.View.Dialog;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Model.Param;
import com.myWifi.app.ViewController.Model.Record;
import com.myWifi.app.ViewController.View.Adapter.AdapterHttpParam;

import java.util.ArrayList;

public class                DialogDetailHttpRequest {
    private Context         context;
    private String          TAG = "DialogDetailHttpRequest";

    public                  DialogDetailHttpRequest(Context context) {
        this.context = context;
    }
    private void            setNegativeButton(AlertDialog.Builder builderDialog, String TextNegation) {
        builderDialog.setNegativeButton(TextNegation,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
    }
    private void            setAdapter(AlertDialog.Builder builderDialog, final ListAdapter arrayAdapter) {
        builderDialog.setAdapter(arrayAdapter,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "setAdapter::arrayAdapter::onClick");
                    }
                });
    }
    private ListAdapter     createCustomAdapter(final Record record) {
        final Record[] items = { record };

        class ViewHolder {
            TextView requestType;
            TextView hostname;
            TextView path;
            RelativeLayout layoutListView;
            ListView param;
        }
        ListAdapter adapter = new ArrayAdapter<Record>(context, R.layout.dialog_client_predator_detail, items) {

            public View     getView(int position, View convertView,
                                ViewGroup parent) {
                final LayoutInflater inflater = (LayoutInflater) context
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                ViewHolder      holder;

                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.dialog_client_predator_detail, null);

                    holder.layoutListView = (RelativeLayout) convertView.findViewById(R.id.layoutListViewParam);
                    holder.requestType = (TextView) convertView
                            .findViewById(R.id.requestType);
                    holder.hostname = (TextView) convertView
                            .findViewById(R.id.Hostname);
                    holder.path = (TextView) convertView
                            .findViewById(R.id.Path);
                    holder.param = (ListView) convertView
                            .findViewById(R.id.listViewParam);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                if (items[0].getTypeRecord() == Record.recordType.HttpGET)
                    holder.requestType.setText("HTTP GET REQUEST");
                else if (items[0].getTypeRecord() == Record.recordType.HttpPost)
                    holder.requestType.setText("HTTP Post REQUEST");
                else if (items[0].getTypeRecord() == Record.recordType.HttpCredit) {
                    holder.requestType.setText("HTTP Credentials REQUEST");
                    holder.hostname.setText(items[0].getRecord());
                    holder.path.setVisibility(View.INVISIBLE);
                    initListViewParamHttp(holder.param, items[0].getParam(), holder.layoutListView);
                    return convertView;
                }
                holder.hostname.setText(items[0].getHost());
                holder.path.setText(items[0].getPath());
                initListViewParamHttp(holder.param, items[0].getParam(), holder.layoutListView);
                return convertView;
            }
        };
        return adapter;
    }
    private void            initListViewParamHttp(ListView param, String params, RelativeLayout layoutListView) {
        if (params != null && !params.isEmpty() && params.contains("&")) {
            Log.d(TAG, "voici les param: " + params);
            ArrayList<Param> listParam = new ArrayList<>();
            for (String paramTmp : params.split("&")) {
                listParam.add(
                        new Param(
                                paramTmp.substring(0, paramTmp.indexOf("=")),
                                paramTmp.substring(paramTmp.indexOf("=") + 1, paramTmp.length())));
            }
            param.setAdapter(new AdapterHttpParam(context, listParam));
        } else {
            layoutListView.setVisibility(View.GONE);
        }
    }
    public AlertDialog      DialogDetailDetailClient(Record record) {
        AlertDialog.Builder builderDialog = new AlertDialog.Builder(context);
        builderDialog.setIcon(R.drawable.icon);
        builderDialog.setTitle("Detail request:");
        setNegativeButton(builderDialog, "Ok");
        setAdapter(builderDialog, createCustomAdapter(record));
        return builderDialog.create();
    }
}