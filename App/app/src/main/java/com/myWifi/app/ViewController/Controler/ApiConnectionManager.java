package com.myWifi.app.ViewController.Controler;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.ViewController.Model.StackClientProbe;
import com.myWifi.app.ViewController.Model.StackClientSniffed;
import com.myWifi.app.ViewController.View.Dialog.DialogDetailReconnectWifi;
import com.myWifi.app.ViewController.View.FragmentPredatorProbe;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class                                ApiConnectionManager {
    private static Context                  context;
    private static ApiConnectionManager     ourInstance = null;
    private static ApiParsingThreaded       apiLink;
    private static Socket                   socket;
    private static String                   TAG = "ApiConnectionManager";
    private static boolean                  isApiLinked = false;
    private static boolean                  HasListClient = false;
    private static StackClientSniffed       listClientSniff = null;
    private static StackClientProbe         listClientProbe = null;
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
    public void                             connectApi(final android.support.v4.app.Fragment fragment, final boolean wichFragment) {
        (new Thread() {
            @Override
            public void run() {
                try {
                    socket = new Socket(Ip, Port);
                    outPutSocket = new PrintStream(socket.getOutputStream());
                    apiLink =
                            new ApiParsingThreaded(
                                    listClientSniff,
                                    listClientProbe,
                                    activity,
                                    socket.getInputStream(), outPutSocket, wichFragment);
                    isApiLinked = true;
                    apiLink.execute();
                    if (wichFragment) {
                        apiLink.setOnlyProbe();
                        fragment.getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ((FragmentPredatorProbe)fragment).onApiConnected();
                            };
                    });
                    }
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
        listClientSniff = new StackClientSniffed((MainActivityToFragment) activity);
        listClientProbe = new StackClientProbe((MainActivityToFragment) activity);
        HasListClient = true;
    }
    public void                             linkadapterSniffClients(ArrayAdapter adapter) {
        listClientSniff.setAdapter(adapter);
    }
    public void                             linkAdapterProbeClients(ArrayAdapter adapter) {
        listClientProbe.setAdapter(adapter);
    }
    public StackClientSniffed               getListClientsSniff() {
        return listClientSniff;
    }
    public StackClientProbe                 getListClientProbe() { return listClientProbe; }
    public void                             startProbeMonitor() {
        outPutSocket.print("/start ProbeMonitor\n");
    }
    public void                             stopProbeMonitor() {
        Log.d(TAG, "/stop ProbeMonitor sended");
        outPutSocket.print("/stop ProbeMonitor\n");
        apiLink.avoidOnlyProbe();
    }
    public void                             changeApName(String ApName) {
        Log.d(TAG, "changing ApName by :" + ApName);
        ((MainActivityToFragment) activity).getManagerWifi().setSsid(ApName);
        outPutSocket.print("/ApName " + ApName + "\n");
    }
    public void                             stopServer() {
        Log.d(TAG, "stop server");
        apiLink.cancel(true);
        outPutSocket.print("/restart ApServer\n");
        outPutSocket.close();
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isApiLinked = false;
        HasListClient = false;
        Log.d(TAG, "all closed");
    }
    public void                             reconnectToserverProbe(final FragmentPredatorProbe fragment
                                                                   ) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                stopServer();
                ((MainActivityToFragment) activity).getManagerWifi().reconnectToServerProbe(fragment);

            }
            });
    }
}
