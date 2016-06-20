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

public class                DialogDetailFactory  {
    private Context         context;
    private String          TAG = "DialogDetailFactory";

    public                  DialogDetailFactory(Context context) {
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
    private ArrayAdapter<String> initArrayAdapter(Context context) {
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(context,
                android.R.layout.select_dialog_singlechoice);
        arrayAdapter.add("Hardik");
        arrayAdapter.add("Archit");
        arrayAdapter.add("Jignesh");
        arrayAdapter.add("Umang");
        arrayAdapter.add("Gatti");
        return arrayAdapter;
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
        ListAdapter adapter = new ArrayAdapter<Record>(context, R.layout.dialog_client_predator_detail, items) {
            class ViewHolder {
                TextView hostname;
                TextView path;
                ListView param;
            }
            ViewHolder      holder;
            public View     getView(int position, View convertView,
                                ViewGroup parent) {
                final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (convertView == null) {
                    convertView = inflater.inflate(R.layout.dialog_client_predator_detail, null);

                    holder.hostname = (TextView) convertView
                            .findViewById(R.id.Hostname);
                    holder.path = (TextView) convertView
                            .findViewById(R.id.Path);
                    holder.param = (ListView) convertView
                            .findViewById(R.id.listViewParam);
                    convertView.setTag(record);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                holder.hostname.setText(items[0].getHost());
                holder.path.setText(items[0].getPath());
                initListViewParamHttp(holder.param, items[0].getParam());
                return convertView;
            }
        };
        return adapter;
    }

    private void            initListViewParamHttp(ListView param, String params) {
        ArrayList<Param> listParam = new ArrayList<>();
        for (String paramTmp : params.split("&")) {
            listParam.add(
                    new Param(
                            paramTmp.substring(0, paramTmp.indexOf("")),
                            paramTmp.substring(paramTmp.indexOf("")+1, paramTmp.length())));
        }
        param.setAdapter(new AdapterHttpParam(context, listParam));
    }

    public AlertDialog      DialogDetailDetailClient(Record record) {
        AlertDialog.Builder builderDialog = new AlertDialog.Builder(context);
        builderDialog.setIcon(R.drawable.icon);
        builderDialog.setTitle("Detail request:");
        setNegativeButton(builderDialog, "Ok");
        setAdapter(builderDialog, createCustomAdapter(record));
        return builderDialog.create();
//        builderDialog.show();
    }
}