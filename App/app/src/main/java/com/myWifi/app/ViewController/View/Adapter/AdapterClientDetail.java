package com.myWifi.app.ViewController.View.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Model.Record;
import com.myWifi.app.ViewController.Model.ClientPredator;
import com.myWifi.app.ViewController.Model.StackClientPredator;
import com.myWifi.app.ViewController.View.Dialog.DialogDetailFactory;

import java.util.ArrayList;


public class AdapterClientDetail extends ArrayAdapter<Record> {
    private ArrayList<Record>   records;
    private StackClientPredator clientStack;
    private ClientPredator      clientPredator;

    public                      AdapterClientDetail(Context context, ClientPredator clientPredator,
                                    StackClientPredator clientStack) {
        super(context, 0,  clientPredator.getRecords());
        this.records = clientPredator.getRecords();
        this.clientStack = clientStack;
        this.clientPredator = clientPredator;
    }
    private View.OnClickListener onClickRecordDetail(final Record record) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                (new DialogDetailFactory(getContext()))
                        .DialogDetailDetailClient(record).show();
            }
        };
    }
    private  void               initViewDnsStrip(TextView typeBtn, TextView path, TextView param,
                                                 TextView hostname, Record record) {
        typeBtn.setTextColor(Color.GREEN);
        typeBtn.setText("DnsStrip");
        path.setText(record.getPath());
        param.setText(record.getParam());
        hostname.setVisibility(View.INVISIBLE);
    }
    private  void               initViewGlobal(View convertView, Record record) {
        ImageView alertGG = (ImageView) convertView.findViewById(R.id.alertImg) ;
        TextView typeBtn = (TextView) convertView.findViewById(R.id.typeBtn);
        TextView httpType = (TextView) convertView.findViewById(R.id.httpType);
        TextView hostname = (TextView) convertView.findViewById(R.id.hostname);
        TextView path = (TextView) convertView.findViewById(R.id.path);
        TextView param = (TextView) convertView.findViewById(R.id.param);
        RelativeLayout RelLayoutDetailClienPreda = (RelativeLayout)
                convertView.findViewById(R.id.RelLayoutDetailClienPreda);

        Record.recordType recordType = record.getTypeRecord();
        if (recordType == Record.recordType.HttpCredit ||
                recordType == Record.recordType.HttpGET ||
                recordType == Record.recordType.HttpPost) {
            initViewHttp(recordType, record, typeBtn, httpType,
                            hostname, path, param, alertGG);
            RelLayoutDetailClienPreda.setOnClickListener(onClickRecordDetail(record));
            return ;
        }
        else if (recordType == Record.recordType.DnsStrip) {
            initViewDnsStrip(typeBtn, path, param, hostname, record);
            return ;
        }
        else if (recordType == Record.recordType.DnsService) {
            typeBtn.setTextColor(Color.GREEN);
            typeBtn.setText("DnsService");
        }
        else if (recordType == Record.recordType.DHCP) {
            typeBtn.setTextColor(Color.YELLOW);
            typeBtn.setText("DHCP");
        }
        else if (recordType == Record.recordType.SSID) {
            typeBtn.setTextColor(Color.RED);
            typeBtn.setText("SSID");
        }
        path.setText(record.getRecord());
        hostname.setVisibility(View.INVISIBLE);
        param.setVisibility(View.INVISIBLE);
    }
    private void                initViewHttpGet(TextView param, TextView httpType,
                                                String pathString, ImageView alertGG) {
        param.setVisibility(View.INVISIBLE);
        httpType.setText("Get");
        if (pathString.contains("pass") || pathString.contains("key") ||
                pathString.contains("admin") || pathString.contains("login") ||
                pathString.contains("user") || pathString.contains("log") ||
                pathString.contains("pwd") || pathString.contains("nickname") ||
                pathString.contains("id"))
            alertGG.setVisibility(View.VISIBLE);
    }
    private void                initViewHttpPost(TextView httpType, Record record,
                                                 ImageView alertGG, TextView param) {
        String paramString = record.getParam();
        httpType.setText("Post");
        param.setText(paramString);
        if (paramString.contains("pass") || paramString.contains("key") ||
                paramString.contains("admin") || paramString.contains("login") ||
                paramString.contains("user") || paramString.contains("log") ||
                paramString.contains("pwd") || paramString.contains("nickname") ||
                paramString.contains("id"))
            alertGG.setVisibility(View.VISIBLE);
    }
    private void                initViewHttp(Record.recordType type, Record record,
                                             TextView typeBtn, TextView httpType,
                                             TextView hostname, TextView path,
                                             TextView param, ImageView alertGG) {
        String pathString = record.getPath();
        typeBtn.setTextColor(Color.BLUE);
        typeBtn.setText("HTTP");
        httpType.setVisibility(View.VISIBLE);
        if (type == Record.recordType.HttpCredit) {
            param.setVisibility(View.INVISIBLE);
            httpType.setText("Credidential");
        }
        else if (type == Record.recordType.HttpGET)
            initViewHttpGet(param, httpType, pathString, alertGG);
        else if (type == Record.recordType.HttpPost)
            initViewHttpPost(httpType, record, alertGG, param);
        hostname.setText(record.getHost());
        path.setText(record.getPath());
    }
    @Override
    public View                 getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_adapter_client_detail_record, parent, false);
        }
        for (int rcx = position; rcx < this.records.size(); rcx++) {
            if (this.clientStack.isAllowed(this.records.get(rcx).getTypeRecord())) {
                initViewGlobal(convertView, this.records.get(rcx));
                break;
            }
        }
        return convertView;
    }
    @Override
    public int                  getCount() {
        int retMe = 0;
        if (clientStack.isAllowed(Record.recordType.HttpPost))
            retMe += clientPredator.getHttp();
        if (clientStack.isAllowed(Record.recordType.SSID))
            retMe += clientPredator.getSsid();
        if (clientStack.isAllowed(Record.recordType.DnsService))
            retMe += clientPredator.getDns();
        if (clientStack.isAllowed(Record.recordType.DHCP))
            retMe += clientPredator.getDhcp();
        return retMe;
    }
    @Override
    public int                  getPosition(Record item) {
        return super.getPosition(item);
    }
}
