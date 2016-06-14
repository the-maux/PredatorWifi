package com.myWifi.app.ViewController.View.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.myWifi.app.R;
import com.myWifi.app.ViewController.Model.Record;
import com.myWifi.app.ViewController.Model.ClientPredator;
import com.myWifi.app.ViewController.Model.StackClientPredator;

import java.util.ArrayList;


public class                    recordClientAdapter extends ArrayAdapter<Record> {
    private ArrayList<Record>   records;
    private StackClientPredator clientStack;
    private ClientPredator clientPredator;

    public                      recordClientAdapter(Context context, ClientPredator clientPredator,
                                                    StackClientPredator clientStack) {
        super(context, 0,  clientPredator.getRecords());
        this.records = clientPredator.getRecords();
        this.clientStack = clientStack;
        this.clientPredator = clientPredator;
    }
    private  void               initView(View convertView, Record record) {
        TextView recordTxtView = (TextView) convertView.findViewById(R.id.idrecordTxt);
        recordTxtView.setText(record.getRecord());
        if (record.getRecordType() == Record.recordType.HTTP) recordTxtView.setTextColor(Color.WHITE);
        else if (record.getRecordType() == Record.recordType.DNS) recordTxtView.setTextColor(Color.GREEN);
        else if (record.getRecordType() == Record.recordType.DHCP) recordTxtView.setTextColor(Color.YELLOW);
        else if (record.getRecordType() == Record.recordType.SSID) recordTxtView.setTextColor(Color.RED);
    }
    @Override
    public View                 getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lv_adapter_client_detail_record, parent, false);
        }
        for (int rcx = position; rcx < this.records.size(); rcx++) {
            if (this.clientStack.isAllowed(this.records.get(rcx).getRecordType())) {
                initView(convertView, this.records.get(rcx));
                break;
            }
        }
        return convertView;
    }
    @Override
    public int                  getCount() {
        int retMe = 0;
        if (clientStack.isAllowed(Record.recordType.HTTP))
            retMe += clientPredator.getHttp();
        if (clientStack.isAllowed(Record.recordType.SSID))
            retMe += clientPredator.getSsid();
        if (clientStack.isAllowed(Record.recordType.DNS))
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
