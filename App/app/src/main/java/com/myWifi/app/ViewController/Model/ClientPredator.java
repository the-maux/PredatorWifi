package com.myWifi.app.ViewController.Model;

import java.util.ArrayList;

public class            ClientPredator {
    private String      macAddres = null;
    private String      IP = null;
    private String      nameDevice = "Device unknow";
    private String      SSID = "Hidden";
    private boolean     isProbe;
    private String      time = null;
    private String      TAG = "ClientObj";
    private boolean     error = false;
    private ArrayList   records;
    private int         dhcp = 0, http = 0, ssid = 0, dns = 0;

    public              ClientPredator(String rcvStr) {
        try {
            if (rcvStr.contains("#") && rcvStr.contains("*") && rcvStr.contains(";") && rcvStr.contains("_")) {
                setProbe(true);
                setTime(rcvStr.substring(0, rcvStr.indexOf("#")));
                setNameDevice(rcvStr.substring(rcvStr.indexOf("*") + 1, rcvStr.indexOf(";")));
                setSSID(rcvStr.substring(rcvStr.indexOf(";") + 1, rcvStr.indexOf("_")));
                setMacAddres(rcvStr.substring(rcvStr.indexOf("_") + 1, rcvStr.length()));
                return ;
            }
        } catch (StringIndexOutOfBoundsException e) {
            error = true;
        }
        error = true;
    }
    public Object       removeM(int offsett, Record.recordType type, ArrayList records) {
        Object tmp = records.get(offsett);
        for (; offsett < records.size(); offsett++) {
            if (((Record)records.get(offsett)).getRecordType() == type) {
                tmp = records.get(offsett);
                break;
            }
        }
        return records.remove(tmp);
    }
    public              ClientPredator(String rcvStr, int osef) {
        try {
            if (rcvStr.contains("*") && rcvStr.contains(";")) {
                setProbe(false);
                setIP(rcvStr.substring(0, rcvStr.indexOf("*")));
                setNameDevice(rcvStr.substring(rcvStr.indexOf("*") + 1, rcvStr.indexOf(";")));
                setMacAddres(rcvStr.substring(rcvStr.indexOf(";") + 1, rcvStr.length()));
                records = new ArrayList<Record>();
                return ;
            }
        } catch (StringIndexOutOfBoundsException e) {
            error = true;
        }
        error = true;
    }
    public String       getMacAddres() {
        return macAddres;
    }
    public void         setMacAddres(String macAddres) {
        this.macAddres = macAddres;
    }
    public String       getIP() {
        return IP;
    }
    public void         setIP(String IP) {
        this.IP = IP;
    }
    public String       getNameDevice() {
        return nameDevice;
    }
    public void         setNameDevice(String nameDevice) {
        if (!nameDevice.matches("^.*[^a-zA-Z0-9 ].*$")) this.nameDevice = "Inconnu";
        else this.nameDevice = nameDevice;
    }
    public String       getSSID() {
        return SSID;
    }
    public void         setSSID(String SSID) {
        if (SSID == null)
            return ;
        if (SSID.matches("^.*[^a-zA-Z0-9 ].*$")) this.nameDevice = "Hidden";
        else this.SSID = SSID;
    }
    public boolean      isProbe() {
        return isProbe;
    }
    public void         setProbe(boolean probe) {
        isProbe = probe;
    }
    public String       getTime() {
        return time;
    }
    public void         setTime(String time) {
        this.time = time;
    }
    public boolean      isError() {
        return error;
    }
    public void         setError(boolean error) {
        this.error = error;
    }
    private boolean     isAlreadyOnit(String record) {
        for (Object line : records) {
            if (((Record)line).getRecord().contains(record))
                return true;
        }
        return false;
    }
    public void         addDhcpLog(String newRecord) {
        if (isAlreadyOnit(newRecord))
            return ;
        records.add(new Record(newRecord, Record.recordType.DHCP));
        if (dhcp > 100)
            removeM(99, Record.recordType.DHCP, records);
        else
            dhcp++;
    }
    public void         addDnsLog(String newRecord) {
        if (isAlreadyOnit(newRecord))
            return ;
        records.add(new Record(newRecord, Record.recordType.DNS));
        if (dns > 100)
            removeM(99, Record.recordType.DNS, records);
        else
            dns++;
    }
    public void         addSsidLog(String newRecord) {
        if (isAlreadyOnit(newRecord))
            return ;
        records.add(new Record(newRecord, Record.recordType.SSID));
        if (ssid > 100)
            removeM(99, Record.recordType.SSID, records);
        else
            ssid++;
    }
    public void         addHttpLog(Record.recordType typeHTTP, String hostname, String path, String param[]) {
        records.add(new Record(typeHTTP, hostname, path, param));
        if (http > 100)
            removeM(99, Record.recordType.HttpGET, records);
        else
            http++;
    }
    public void         addHttpLog(String newRecord) {
        if (isAlreadyOnit(newRecord))
            return ;
        records.add(new Record(newRecord, Record.recordType.HttpCredit));
        if (http > 100)
            removeM(99, Record.recordType.HttpGET, records);
        else
            http++;
    }
    public int          getDhcp() {
        return dhcp;
    }
    public int          getHttp() {
        return http;
    }
    public int          getDns() {
        return dns;
    }
    public int          getSsid() {
        return ssid;
    }
    public ArrayList<Record> getRecords() { return records; }

}
