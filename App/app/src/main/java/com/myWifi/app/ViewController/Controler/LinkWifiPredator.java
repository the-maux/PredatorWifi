package com.myWifi.app.ViewController.Controler;

import android.os.AsyncTask;
import android.util.Log;
import com.myWifi.app.ViewController.Model.ClientPredator;
import com.myWifi.app.ViewController.View.FragmentLinkWifiPredator;

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
    private FragmentLinkWifiPredator instance;
    private Socket              socket = null;
    private PrintStream         outputStream;

    public                      LinkWifiPredator(String addr, int port, ArrayList listClientFrag,
                                                 FragmentLinkWifiPredator instance){
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
    private void                parseHTTPRecord(final String line) {
        this.instance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                String          ip = line.substring(0, line.indexOf("}"));
                String          msg = line.substring(line.indexOf("}")+1, line.length());

                if (ip.contains(":"))
                    ip = ip.substring(0, ip.indexOf(":"));
                ClientPredator clientPredatorTmp = getClientByIp(ip);
                if (clientPredatorTmp != null) {
                    msg = msg.substring(0, msg.indexOf("DNS"));
                    if (!msg.contains("connectivitycheck.android.com/generate_204"))
                        clientPredatorTmp.addHttpLog(msg);
                    Log.w(TAG, "HTTP:(" + ip + ") " + msg);
                }
        }});
        }
    private void                parseDhcpRecord(final String line) {
        this.instance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //Log.w(TAG, "DHCP: " + line);
            }});
    }
    private void                parseDnsRecord(final String line) {
        this.instance.getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                    if (line.contains(".com") || line.contains(".org") || line.contains(".eu") ||
                            line.contains(".fr") || line.contains(".bz") || line.contains(".info")) {
                        String Ip = line.substring(0, line.indexOf("#"));
                        String dnsRecord = line.substring(line.indexOf("#")+1, line.length());
                        ClientPredator clientPredatorTmp = getClientByIp(Ip);
                        if (clientPredatorTmp != null) {
                            clientPredatorTmp.addDnsLog(dnsRecord);
                        }
                    }
            }});
    }
    private ClientPredator getClientByIp(String ip) {
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
            if (line.contains("TargetConnection")) {
                parseNewTargetConnection(line.substring("TargetConnection#".length(), line.length()));
            } else if (line.contains("HTTP:")) {
                parseHTTPRecord(line.substring("HTTP:".length(), line.length()));
            } else if (line.contains("DHCP:")) {
                parseDhcpRecord(line.substring("DHCP:".length(), line.length()));
            } else if (line.contains("DNS:")) {
                parseDnsRecord(line.substring("DNS:".length(), line.length()));
            } else {
                parseDataClientProbe(line);
            }
        }
    }
    private void                sendCallBackToServer(PrintStream outputStream) {
        outputStream.print("0\n");
        //printStream.close();
    }
    private void                myListen(InputStream inputStream, PrintStream outputStream) throws IOException {
        int                     bytesRead;
        byte[]                  buffer = new byte[1024];
        String                  bufferTmp;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
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
            socket = new Socket(dstAddress, dstPort);
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

