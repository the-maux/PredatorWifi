package com.myWifi.app.ViewController.Model.Predator;


public class                Record {
    private String          record;
    private recordType      recordType;
    public enum recordType { HTTP, DNS, DHCP, SSID}

    public                  Record(String record, recordType type) {
        this.record = record;
        this.recordType = type;
    }
    public String           getRecord() {
        return record;
    }
    public recordType       getRecordType() {
        return recordType;
    }
}
