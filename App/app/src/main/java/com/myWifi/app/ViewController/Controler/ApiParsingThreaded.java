package com.myWifi.app.ViewController.Controler;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import com.myWifi.app.ViewController.Model.Client;
import com.myWifi.app.ViewController.Model.Record;
import com.myWifi.app.ViewController.Model.StackClientProbe;
import com.myWifi.app.ViewController.Model.StackClientSniffed;

import java.io.*;
import java.net.Socket;

public class ApiParsingThreaded extends AsyncTask<Void, Void, Void> {
    private String              TAG = "ApiParsingThreaded";
    private String              response = "";
    private StackClientSniffed  listSniffedTarget;
    private StackClientProbe    listTargetProbe;
    private int                 nbrClient = 0;
    private int                 nbrClientTarget = 0;
    private Activity            MainActivity;
    private Socket              socket = null;
    private InputStream         inputStream;
    private PrintStream         outputStream;
    private Client lastClientUpdated = null;
    private String              lastHostnameSniffed = null;
    private String              lastPathSniffed = null;
    private boolean             onlyProbeRequest = false;

    public ApiParsingThreaded(StackClientSniffed listClientFrag, StackClientProbe listClientProbe, Activity activity,
                              InputStream inputStream, PrintStream outputStream, boolean probe) {
        this.listSniffedTarget = listClientFrag;
        MainActivity = activity;
        this.inputStream = inputStream;
        this.outputStream = outputStream;
        this.listTargetProbe = listClientProbe;
    }
    public void                 updateClient(Client client) {
  //      Log.d(TAG, "Updating Client : " + client.getMacAddres());
        if (!this.listTargetProbe.isEmpty()) {
            for (Object clientTmp : this.listTargetProbe)
                if (client.getMacAddres().contains(((Client) clientTmp).getMacAddres())) {
                    if (!client.getSSID().contains("Hidden")) {
                        ((Client) clientTmp).addSsidLog(((Client) clientTmp).getSSID());
                        ((Client) clientTmp).setSSID(client.getSSID());
                    }
                    if (((Client) clientTmp).getNameDevice().contains("Device unknow") && !client.getNameDevice().contains("Device unknow"))
                        ((Client) clientTmp).setNameDevice(client.getNameDevice());
                    return;
                }
        }
    }
    private void                addHistorySsid(Client client) {
        Log.d(TAG,
                "Adding Ssid" + client.getSsid() +
                        " to history at client :" + client.getMacAddres());
        for (int i = 0; i < this.listTargetProbe.size(); i++) {
            if (((Client)this.listTargetProbe.get(i)).getMacAddres().contains(client.getMacAddres()))
                client.addSsidLog(((Client)this.listTargetProbe.get(i)).getSSID());
        }
    }
    private void                parseDataClientProbe(final String line) {
        MainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Client client = new Client(line);
                if (!client.isError() && !client.getSSID().contains("Hidden")){
                    if (!listTargetProbe.isEmpty() && listTargetProbe.contains(client)) {
                        updateClient(client);
                    } else {
//                        Log.d(TAG, "Adding client :[" + client.getMacAddres() + "] To the list");
                        listTargetProbe.add(client);
                    }
                } else {
/*                    Log.d(TAG, "Error : client isError : " + client.isError() +
                            " with SSID: " + client.getSSID());*/
                }
            }
        });
    }
    private void                parseNewTargetConnection(final String line) {
        MainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Client clientTmp = new Client(line, 0);
                if (!clientTmp.isError()) {
                    if (nbrClientTarget++ > 0 && listSniffedTarget.contains(clientTmp)) {
                        updateClient(clientTmp);
                    } else{
                        addHistorySsid(clientTmp);
                        listSniffedTarget.add(clientTmp);
                    }
                }
             }
        });
    }
    private void                parseHttpCredidential(final String line) {
        //HTTP-Credidential:[10.0.0.20:55146 > 91.216.107.211:80] [93mHTTP password: pwd=password[0m
        //print_str = '[%s > %s] %s%s%s' % (src_ip_port, dst_ip_port, T, msg, W)
        MainActivity.runOnUiThread(new Runnable() {
            @Override public void run() {
                String Ip = line.substring(line.indexOf("[")+1, line.indexOf(":")-1);
                lastClientUpdated = getClientByIp(Ip);
                if (lastClientUpdated != null) {
                    String Log = "[" + line.substring(line.indexOf(">") + 2, line.length());
                    lastClientUpdated.addHttpLog(Log);
                }
            }});
    }
    private void                parseHttpUrl(final String httpRecord) {
        //HTTP-Url:[10.0.0.20] GET wwww.root-me.org/local/cache-vignettes/L150xH150/rwhiteGrand-048ea.png?1465816508
        //HTTP-Url:[10.0.0.20] GET maximum-motors.com/wp-login.php?redirect_to=http%3A%2F%2Fmaximum-motors.com%2Fwp-admin%2F&reauth=1
        //HTTP-Url:[10.0.0.20] POST load: log=Afmin&pwd=password&wp-submit=Se+connecter&redirect_to=http%3A%2F%2Fmaximum-motors.com%2Fwp-admin%2F&testcookie=1
        MainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String Ip, HttpType, httpRecordTmp, param = "";
                Record.recordType typeHTTP = Record.recordType.HttpGET;

                Ip = httpRecord.substring(1, httpRecord.indexOf("]"));
                httpRecordTmp = httpRecord.substring(httpRecord.indexOf("]")+2, httpRecord.length());
                HttpType = httpRecordTmp.substring(0, httpRecordTmp.indexOf(" "));
                httpRecordTmp = httpRecordTmp.substring(httpRecordTmp.indexOf(" "), httpRecordTmp.length());
                if (HttpType.contains("GET")) {
                    typeHTTP = Record.recordType.HttpGET;
                    lastHostnameSniffed = httpRecordTmp.substring(1, httpRecordTmp.indexOf("/"));
                    lastPathSniffed = httpRecordTmp.substring(httpRecordTmp.indexOf("/"), httpRecordTmp.length());
                } else if (HttpType.contains("POST")) {
                    typeHTTP = Record.recordType.HttpPost;
                    if (httpRecordTmp.contains("load:"))
                        httpRecordTmp = httpRecordTmp.substring(httpRecordTmp.indexOf("load: "), httpRecordTmp.length());
                    param = httpRecordTmp;
                } else {
                    Log.d(TAG, "unknow HTTP TYPE : " + HttpType);
                }
                lastClientUpdated = getClientByIp(Ip);
                if (lastClientUpdated != null)
                    lastClientUpdated.addHttpLog(typeHTTP, lastHostnameSniffed, lastPathSniffed, param);
            }});
    }
    private void                parseDhcpRecord(final String dhcpRecord) {
        //DHCPREQUEST for 10.0.0.20 from 10:68:3f:7a:65:ef via wlan1
        //DHCPACK on 10.0.0.20 to 10:68:3f:7a:65:ef (android-8b005558e83e4ecf) via wlan1
        MainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String DHCPRecord;
                //TODO:seeking client by ip not by last taken
                if (lastClientUpdated != null) {
                    lastClientUpdated.addDhcpLog(dhcpRecord);
                }
                Log.w(TAG, "DHCP: " + dhcpRecord);
            }});
    }
    private void                parseDnsService(final String request) {
        //DnsService-Request:10.0.0.20#cdn1.smartadserver.com. IN A
        MainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    String dnsRecord;
                    if (request.contains(".com") || request.contains(".org") || request.contains(".eu") ||
                            request.contains(".fr") || request.contains(".bz") || request.contains(".info") ||
                            request.contains(".net") || request.contains(".tv")) {

                        String Ip = request.substring(0, request.indexOf("#"));

                        if (request.contains(" "))
                            dnsRecord = request.substring(request.indexOf("#")+1, request.indexOf(" ")-1);
                        else
                            dnsRecord = request.substring(request.indexOf("#")+1, request.length());
                        Client clientTmp = getClientByIp(Ip);
                        if (clientTmp != null) {
                            Log.d(TAG, "dnsRequest:" + dnsRecord);
                            lastClientUpdated = clientTmp;
                            clientTmp.addDnsService(dnsRecord);
                        }
                    }
            }});
    }
    private void                parseDnsSSLStrip(final String record) {
        //DnsService-SSLStrip:webatout.email-match.com#atout.email-match.com
        MainActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (record.contains(".com") || record.contains(".org") || record.contains(".eu") ||
                        record.contains(".fr") || record.contains(".bz") || record.contains(".info") ||
                        record.contains(".net") ) {
                    String realHost = "SSLStrip: https://" +
                            record.substring(record.indexOf("#")+1, record.length());
                    String newHost = "http://" +
                            record.substring(0, record.indexOf("#"));
                    if (lastClientUpdated != null) {
                        Log.d(TAG, "DnsStrip: " + realHost + " to " + newHost);
                        lastClientUpdated.addDnsStrip(realHost, newHost);
                    }
                }
            }
        });
    }
    private Client              getClientByIp(String ip) {
        for (Object client : listSniffedTarget) {
            if (((Client) client).getIP() != null && ip != null &&
                    ((Client) client).getIP().contains(ip))
                return (Client) client;
        }
        return null;
    }
    private void                parseAndAddFifo(String[] data){
        for (String line : data) {
            if (MainActivity == null)
                break;
            if (!line.contains("connectivitycheck.android.com")) {
                if (line.contains("TargetConnection") && !onlyProbeRequest) {
                    parseNewTargetConnection(line.substring("TargetConnection#".length(), line.length()));
                } else if (line.contains("HTTP-Credidential:") && !onlyProbeRequest) {
                    parseHttpCredidential(line.substring("HTTP-Credidential:".length(), line.length()));
                } else if (line.contains("HTTP-Url:") && !onlyProbeRequest) {
                    parseHttpUrl(line.substring("HTTP-Url:".length(), line.length()));
                } else if (line.contains("DHCP:") && !onlyProbeRequest) {
                    parseDhcpRecord(line.substring("DHCP:".length(), line.length()));
                } else if (line.contains("DNS-Request:") && !onlyProbeRequest) {
                    parseDnsService(line.substring("DNS-Request:".length(), line.length()));
                } else if (line.contains("DNS-SSLStrip:") && !onlyProbeRequest) {
                    parseDnsSSLStrip(line.substring("DNS-SSLStrip:".length(), line.length()));
                } else if (line.contains("Probe:") && onlyProbeRequest) {
                    parseDataClientProbe(line.substring("Probe:".length()));
                }
            }
        }
    }
    @Override
    protected Void              doInBackground(Void... arg0)  {
        int                     bytesRead;
        byte[]                  buffer = new byte[1024];
        String                  bufferTmp;
        ByteArrayOutputStream   byteArrayOutputStream = new ByteArrayOutputStream(1024);

        try {
            while (!isCancelled() && (bytesRead = inputStream.read(buffer)) != -1) {//* inputStream.read() will block if no data return
                byteArrayOutputStream.write(buffer, 0, bytesRead);
                try {
                    bufferTmp = byteArrayOutputStream.toString("UTF-8");
                    response += bufferTmp;
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                parseAndAddFifo(response.split(System.getProperty("line.separator")));
                byteArrayOutputStream.reset();
            }
        } catch (IOException e) {

        }
    return null;
    }
    @Override
    protected void              onPostExecute(Void result) {
        if (outputStream != null)
            outputStream.close();
        super.onPostExecute(result);
    }
    public void                 setOnlyProbe() {
        onlyProbeRequest = true;
    }
    public void                 avoidOnlyProbe() {
        onlyProbeRequest = false;
    }
}

