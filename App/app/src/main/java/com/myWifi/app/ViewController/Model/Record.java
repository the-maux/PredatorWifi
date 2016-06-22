package com.myWifi.app.ViewController.Model;


public class                Record {
    private String          record;
    private recordType      typeRecord;
    private String          host;
    private String          httpRecordTmp;
    private String          path;
    private String          param;

    public enum recordType { HttpPost, HttpGET, HttpCredit, DnsService, DnsStrip, DHCP, SSID}

    /** DNS Strip **/
    public          Record(String realHost, String newHost, recordType dnsStrip) {
        typeRecord = dnsStrip;
        path = realHost;
        param = newHost;
    }
    /** HTTP**/
    public          Record(recordType typeHTTP, String hostname, String path, String param) {
        typeRecord = typeHTTP;
        host = hostname;
        this.path = path;
        this.param = param;
        record = typeRecord + ": " + hostname + path;
    }
    /** BASIC : SSID DHCP **/
    public          Record(String record, recordType type) {
        this.record = record;
        this.typeRecord = type;

    }
    public String           getRecord() {
        return record;
    }
    public recordType       getTypeRecord() {
        return typeRecord;
    }
    public String           getHost() {
        return host;
    }
    public String           getPath() {
        return path;
    }
    public String           getParam() {
        return param;
    }
}
