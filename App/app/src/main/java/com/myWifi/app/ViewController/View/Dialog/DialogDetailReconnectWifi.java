package com.myWifi.app.ViewController.View.Dialog;


import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Model.Record;

public class                DialogDetailReconnectWifi {
    private AlertDialog     instance;
    private Context         context;
    private Activity        activity;
    private AlertDialog.Builder builderDialog;
    private ViewHolder      holder;
    private ArrayAdapter     adapter;
    private String          TAG = "DialogDetailReconnectWifi";
    class                   ViewHolder {
        ProgressBar         waitBar;
        TextView            monitor;
    }

    public                  DialogDetailReconnectWifi(Context context, Activity activity) {
        this.context = context;
        builderDialog = new AlertDialog.Builder(context);
        builderDialog.setIcon(R.drawable.icon);
        builderDialog.setTitle("Reconnect to server:");
        builderDialog.setCancelable(false);
        adapter = createCustomAdapter(new Record("", "", Record.recordType.HttpGET));
        setAdapter(builderDialog, adapter);
        this.activity = activity;
/*
    TODO: créer l'adaptater avec une waitBar et le set
 */
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
    private ArrayAdapter     createCustomAdapter(final Record record) {
        final Record[] items = { record };

         return new ArrayAdapter<Record>(context, R.layout.dialog_client_predator_detail, items) {
            public View getView(int position, View convertView, ViewGroup parent) {
                final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                if (convertView == null) {
                    holder = new ViewHolder();
                    convertView = inflater.inflate(R.layout.dialog_reconnect_wifi, null);
                    holder.waitBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
                    holder.monitor = (TextView) convertView.findViewById(R.id.monitor);
                    convertView.setTag(holder);
                } else {
                    holder = (ViewHolder) convertView.getTag();
                }
                return convertView;
            }
        };
    }
    public AlertDialog      create() {
        this.instance = builderDialog.create();
        return this.instance;
    }
    public   void           addTimeToWaitBar(final int progress) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                holder.waitBar.setMax(101);
                holder.waitBar.setProgress(holder.waitBar.getProgress() + progress);
                holder.monitor.setText(holder.waitBar.getProgress() + "%");
                adapter.notifyDataSetChanged();
            }
        });
    }
    public void             dismiss() {
        this.instance.dismiss();
    }
}
