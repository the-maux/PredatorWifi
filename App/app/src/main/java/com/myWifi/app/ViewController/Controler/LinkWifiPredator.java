package com.myWifi.app.ViewController.Controler;

import android.os.AsyncTask;
import android.util.Log;
import com.myWifi.app.ViewController.Model.ClientPredator;
import com.myWifi.app.ViewController.Model.Record;
import com.myWifi.app.ViewController.View.FragmentWifiPredator;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class                    LinkWifiPredator extends AsyncTask<Void, Void, Void> {
    private String              TAG = "LinkWifiPredator";
    private String              dstAddress;
    private String              response = "";
    private int                 dstPort;
    private ArrayList           listClient;
    private int                 nbrClient = 0;
    private int                 nbrClientTarget = 0;
    private FragmentWifiPredator instance;
    private Socket              socket = null;
    private PrintStream         outputStream;
    private ClientPredator      lastClientUpdated = null;
    private String              lastHostnameSniffed = null;
    private String              lastPathSniffed = null;

    public                      LinkWifiPredator(String addr, int port, ArrayList listClientFrag,
                                                 FragmentWifiPredator instance){
        dstAddress = addr;
        dstPort = port;
        this.listClient = listClientFrag;
        this.instance = instance;
    }
    public void                 closeconnection() {
        this.cancel(true);
        try {
            socket.close();
        } catch (IOException e) {
            Log.w(TAG, "close error");
            e.printStackTrace();

        }
    }
    public void                 updateClient(ClientPredator clientPredator) {
        if (!this.listClient.isEmpty()) {
            for (Object clientTmp : this.listClient)
                if (clientPredator.getMacAddres().contains(((ClientPredator) clientTmp).getMacAddres())) {
                    if (((ClientPredator) clientTmp).getSSID().contains("Hidden") && !clientPredator.getSSID().contains("Hidden"))
                        ((ClientPredator) clientTmp).setSSID(clientPredator.getSSID());
                    if (((ClientPredator) clientTmp).getNameDevice().contains("Device unknow") && !clientPredator.getNameDevice().contains("Device unknow"))
                        ((ClientPredator) clientTmp).setNameDevice(clientPredator.getNameDevice());
                    return;
                }
        }
    }
    private void                addHistorySsid(ClientPredator clientPredator) {
        for (int i = 0; i < this.listClient.size(); i++) {
            if (((ClientPredator)this.listClient.get(i)).getMacAddres().contains(clientPredator.getMacAddres()))
                clientPredator.addSsidLog(((ClientPredator)this.listClient.get(i)).getSSID());
        }
    }
    private void                parseDataClientProbe(final String line) {
        this.instance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ClientPredator clientPredatorTmp = new ClientPredator(line);
                if (!clientPredatorTmp.isError() && !clientPredatorTmp.getSSID().contains("Hidden")){
                    if (nbrClient++ > 0 && listClient.contains(clientPredatorTmp)) {
                        updateClient(clientPredatorTmp);
                    } else {
                        listClient.add(clientPredatorTmp);
                    }
                }
            }
        });
    }
    private void                parseNewTargetConnection(final String line) {
        this.instance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ClientPredator clientPredatorTmp = new ClientPredator(line, 0);
                if (!clientPredatorTmp.isError()) {
                    if (nbrClientTarget++ > 0 && listClient.contains(clientPredatorTmp)) {
                        updateClient(clientPredatorTmp);
                    } else{
                        addHistorySsid(clientPredatorTmp);
                        listClient.add(clientPredatorTmp);
                    }
                }
             }
        });
    }
    private void                parseHttpCredidential(final String line) {
        //HTTP-Credidential:[10.0.0.20:55146 > 91.216.107.211:80] [93mHTTP password: pwd=password[0m
        //print_str = '[%s > %s] %s%s%s' % (src_ip_port, dst_ip_port, T, msg, W)
        this.instance.getActivity().runOnUiThread(new Runnable() {
            @Override public void run() {
                String Ip = line.substring(line.indexOf("[")+1, line.indexOf(":")-1);
                ClientPredator clientPredatorTmp = getClientByIp(Ip);
                if (clientPredatorTmp != null) {
                    String Log = "[" + line.substring(line.indexOf(">") + 2, line.length());
                    clientPredatorTmp.addHttpLog(Log);
                }
            }});
    }
    private void                parseHttpUrl(final String httpRecord) {
        //HTTP-Url:[10.0.0.20] GET wwww.root-me.org/local/cache-vignettes/L150xH150/rwhiteGrand-048ea.png?1465816508
        //HTTP-Url:[10.0.0.20] GET maximum-motors.com/wp-login.php?redirect_to=http%3A%2F%2Fmaximum-motors.com%2Fwp-admin%2F&reauth=1
        //HTTP-Url:[10.0.0.20] POST load: log=Afmin&pwd=password&wp-submit=Se+connecter&redirect_to=http%3A%2F%2Fmaximum-motors.com%2Fwp-admin%2F&testcookie=1
        this.instance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "HttpRequest:" + httpRecord);
                String Ip = "", HttpType = "", httpRecordTmp = "", param[] = null;
                Record.recordType typeHTTP = Record.recordType.HttpGET;

                Ip = httpRecord.substring(1, httpRecord.indexOf("]"));
                httpRecordTmp = httpRecord.substring(httpRecord.indexOf("]")+2, httpRecord.length());
                HttpType = httpRecordTmp.substring(0, httpRecordTmp.indexOf(" "));
                httpRecordTmp = httpRecordTmp.substring(httpRecordTmp.indexOf(" "), httpRecordTmp.length());
                if (HttpType.contains("GET")) {
                    typeHTTP = Record.recordType.HttpGET;
                    lastHostnameSniffed = httpRecordTmp.substring(1, httpRecordTmp.indexOf("/"));
                    lastPathSniffed = httpRecordTmp.substring(httpRecordTmp.indexOf("/"), httpRecordTmp.length());
                    Log.d(TAG, "Record: Ip:[" + Ip +
                            "] typeHTTP:[" + typeHTTP +
                            "] Hostname:[" + lastHostnameSniffed +
                            "] Path:[" + lastPathSniffed + "]");
                } else if (HttpType.contains("POST")) {
                    typeHTTP = Record.recordType.HttpPost;
                    if (httpRecordTmp.contains("load:"))
                        httpRecordTmp = httpRecordTmp.substring(httpRecordTmp.indexOf("load: "), httpRecordTmp.length());
                    param = httpRecordTmp.split("&");
                    Log.d(TAG, "Record: Ip:[" + Ip +
                            "] typeHTTP:[" + typeHTTP +
                            "] Hostname:[" + lastHostnameSniffed +
                            "] Path:[" + lastPathSniffed + "] param[" + param.toString()+ "]");
                } else {
                    Log.d(TAG, "unknow HTTP TYPE : " + HttpType);
                }
                ClientPredator clientPredatorTmp = getClientByIp(Ip);
                if (clientPredatorTmp != null)
                    clientPredatorTmp.addHttpLog(typeHTTP, lastHostnameSniffed, lastPathSniffed, param);
            }});
    }
    private void                parseDhcpRecord(final String dhcpRecord) {
        //DHCPREQUEST for 10.0.0.20 from 10:68:3f:7a:65:ef via wlan1
        //DHCPACK on 10.0.0.20 to 10:68:3f:7a:65:ef (android-8b005558e83e4ecf) via wlan1
        this.instance.getActivity().runOnUiThread(new Runnable() {
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
    private void                parseDnsRequest(final String request) {
        //DNS-Request:10.0.0.20#cdn1.smartadserver.com. IN A
        this.instance.getActivity().runOnUiThread(new Runnable() {
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
                        ClientPredator clientPredatorTmp = getClientByIp(Ip);
                        if (clientPredatorTmp != null) {
                            Log.d(TAG, "dnsRequest:" + dnsRecord);
                            lastClientUpdated = clientPredatorTmp;
                            clientPredatorTmp.addDnsLog(dnsRecord);
                        }
                    }
            }});
    }
    private void                parseDnsSSLStrip(final String record) {
        //DNS-SSLStrip:webatout.email-match.com#atout.email-match.com
        this.instance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (record.contains(".com") || record.contains(".org") || record.contains(".eu") ||
                        record.contains(".fr") || record.contains(".bz") || record.contains(".info") ||
                        record.contains(".net") ) {
                    String dnsRecord = "SSLStrip: https://" +
                             record.substring(record.indexOf("#")+1, record.length()) +
                                "  to http://" +
                            record.substring(0, record.indexOf("#"));
                    if (lastClientUpdated != null) {
                        Log.d(TAG, dnsRecord);
                        lastClientUpdated.addDnsLog(dnsRecord);
                    }
                }
            }
        });
    }
    private ClientPredator      getClientByIp(String ip) {
        for (Object client : listClient) {
            if (((ClientPredator) client).getIP() != null && ip != null &&
                    ((ClientPredator) client).getIP().contains(ip))
                return (ClientPredator) client;
        }
        return null;
    }
    private void                parseAndAddFifo(String[] data){
        for (String line : data) {
            if (instance == null || instance.getActivity() == null)
                break;
            if (!line.contains("connectivitycheck.android.com")) {
                if (line.contains("TargetConnection")) {
                    parseNewTargetConnection(line.substring("TargetConnection#".length(), line.length()));
                } else if (line.contains("HTTP-Credidential:")) {
                    parseHttpCredidential(line.substring("HTTP-Credidential:".length(), line.length()));
                } else if (line.contains("HTTP-Url:")) {
                    parseHttpUrl(line.substring("HTTP-Url:".length(), line.length()));
                } else if (line.contains("DHCP:")) {
                    parseDhcpRecord(line.substring("DHCP:".length(), line.length()));
                } else if (line.contains("DNS-Request:")) {
                    parseDnsRequest(line.substring("DNS-Request:".length(), line.length()));
                } else if (line.contains("DNS-SSLStrip:")) {
                    parseDnsSSLStrip(line.substring("DNS-SSLStrip:".length(), line.length()));
                } else {
                    parseDataClientProbe(line);
                }
            }
        }
    }
    private void                sendCallBackToServer(PrintStream outputStream) {
        outputStream.print("Ack\n");
    }
    private void                myListen(InputStream inputStream, PrintStream outputStream) throws IOException {
        int                     bytesRead;
        byte[]                  buffer = new byte[1024];
        String                  bufferTmp;
        ByteArrayOutputStream   byteArrayOutputStream = new ByteArrayOutputStream(1024);

        this.outputStream = outputStream;
        outputStream.print("Ack\n");
        while ((bytesRead = inputStream.read(buffer)) != -1) {//* inputStream.read() will block if no data return
            byteArrayOutputStream.write(buffer, 0, bytesRead);
            bufferTmp = byteArrayOutputStream.toString("UTF-8");
            response += bufferTmp;
            parseAndAddFifo(response.split(System.getProperty("line.separator")));
            byteArrayOutputStream.reset();
            sendCallBackToServer(outputStream);
        }

    }

    @Override
    protected Void              doInBackground(Void... arg0) {
        Log.w(TAG, "doInBackground");
        try {
            socket = new Socket("10.0.0.1", 1234);
            myListen(socket.getInputStream(), new PrintStream(socket.getOutputStream()));
        } catch (UnknownHostException e) {
            e.printStackTrace();
            response = "UnknownHostException: " + e.toString();
        }  catch (ConnectException e) {
            Log.w(TAG, "ConnectException");
            this.instance.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    instance.errorConnection();
                }
            });
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            response = "IOException: " + e.toString();
        } finally {
            try {
                 if(socket != null) socket.close();
            } catch (IOException e) {
                 e.printStackTrace();
            }
        }
        Log.w(TAG, "leaving socket");
        this.instance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                instance.errorConnection();
            }
        });
        return null;
    }
    @Override
    protected void              onPostExecute(Void result) {
        if (outputStream != null)
            outputStream.close();
        super.onPostExecute(result);
    }
}

