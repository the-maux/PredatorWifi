package com.myWifi.app.ViewController.Model;


public class                Record {
    private String          record;
    private recordType      recordType;
    private String          host;
    private String          httpRecordTmp;
    private String          path;
    private String          param[];



    public enum recordType { HttpPost, HttpGET, HttpCredit, DNS, DHCP, SSID}

    public Record(recordType typeHTTP, String hostname, String path, String[] param) {
        recordType = typeHTTP;
        host = hostname;
        this.path = path;
        this.param = param;
        record = recordType + ": " + hostname + path;
    }

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
    public String           getHost() {
        return host;
    }
    public String           getPath() {
        return path;
    }
    public String[]         getParam() {
        return param;
    }
}
