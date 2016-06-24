package com.myWifi.app.ViewController.Controler;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.ViewController.Model.StackClientPredator;
import com.myWifi.app.ViewController.View.Dialog.DialogDetailReconnectWifi;
import com.myWifi.app.ViewController.View.FragmentPredatorProbe;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ApiConnectionManager {
    private static Context                  context;
    private static ApiConnectionManager     ourInstance = null;
    private static ApiParsingThreaded       apiLink;
    private static Socket                   socket;
    private static String                   TAG = "ApiConnectionManager";
    private static boolean                  isApiLinked = false;
    private static boolean                  HasListClient = false;
    private static StackClientPredator      listClient = null;
    private static Activity                 activity = null;
    private static String                   Ip = "10.0.0.1";
    private static int                      Port = 1234;
    private static PrintStream              outPutSocket;

    private ApiConnectionManager(Context context) {
        this.context = context;
    }
    public static synchronized ApiConnectionManager getInstance(Context context) {
        if (ourInstance == null) {
            ourInstance = new ApiConnectionManager(context);
        }
        return ourInstance;
    }
    public static synchronized ApiConnectionManager getInstance(Context context, Activity activityF) {
        if (ourInstance == null) {
            ourInstance = new ApiConnectionManager(context);
        }
        activity = activityF;
        return ourInstance;
    }

    public  boolean                         isApiLinked() {
        return isApiLinked;
    }
    private void                            closeAPI() throws IOException {
        Log.w(TAG, "leaving socket");
        isApiLinked = false;
        apiLink.cancel(true);
        socket.close();
        try {
            if(socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void                             connectApi() {
        (new Thread() {
            @Override
            public void run() {
                try {
                    socket = new Socket(Ip, Port);
                    outPutSocket = new PrintStream(socket.getOutputStream());
                    apiLink =
                            new ApiParsingThreaded(
                                    listClient,
                                    activity,
                                    socket.getInputStream(), outPutSocket);
                    isApiLinked = true;
                    apiLink.execute();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
    public boolean                          HasClients() {
        return HasListClient;
    }
    public void                             createListClient() {
        listClient = new StackClientPredator((MainActivityToFragment) activity);
        HasListClient = true;
    }
    public void                             linkAdapterListClient(ArrayAdapter adapter) {
        listClient.setAdapter(adapter);
    }
    public StackClientPredator              getListClients() {
        return listClient;
    }

    public void                             startProbeMonitor() {
        outPutSocket.print("/start ProbeMonitor\n");
        apiLink.setOnlyProbe();
    }
    public void                             stopProbeMonitor() {
        outPutSocket.print("/stop ProbeMonitor\n");
        apiLink.avoidOnlyProbe();
    }
    public void                             changeApName(String ApName) {
        ((MainActivityToFragment) activity).getManagerWifi().setSsid(ApName);
        outPutSocket.print("/ApName " + ApName + "\n");
    }
    public void                             stopServer() {
        outPutSocket.print("/stop ApServer\n");
        outPutSocket.close();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isApiLinked = false;
        HasListClient = false;
    }
    public void                             reconnectToserverProbe(FragmentPredatorProbe fragment, DialogDetailReconnectWifi alert) {
        stopServer();
        ((MainActivityToFragment) activity).getManagerWifi().reconnectToServerProbe(fragment, alert);
    }
}
