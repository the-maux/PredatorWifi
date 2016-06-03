package com.myWifi.app.ViewController.Controler.Casting;

import android.os.Bundle;
import com.google.android.gms.cast.ApplicationMetadata;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

class                   ConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {
    private boolean     mWaitingForReconnect = false;
    private RouterManager manager;
    private GoogleApiClient mApiClient;

    public              ConnectionCallbacks(RouterManager manager, GoogleApiClient mApiClient) {
        this.manager = manager;
        this.mApiClient = mApiClient;
    }
    @Override
    public void         onConnected(Bundle hint) {
        if( mWaitingForReconnect ) {
            mWaitingForReconnect = false;
            manager.reconnectChannels(hint);
        } else {
            try {
                Cast.CastApi.launchApplication(mApiClient, "Wifi Predator", false)
                        .setResultCallback(
                                new ResultCallback<Cast.ApplicationConnectionResult>() {
                                    @Override
                                    public void onResult(
                                            Cast.ApplicationConnectionResult applicationConnectionResult) {
                                        Status status = applicationConnectionResult.getStatus();
                                        if( status.isSuccess() ) {
                                            //Values that can be useful for storing/logic
                                            ApplicationMetadata applicationMetadata =
                                                    applicationConnectionResult.getApplicationMetadata();
                                            String sessionId =
                                                    applicationConnectionResult.getSessionId();
                                            String applicationStatus =
                                                    applicationConnectionResult.getApplicationStatus();
                                            boolean wasLaunched =
                                                    applicationConnectionResult.getWasLaunched();

                                            manager.mApplicationStarted = true;
                                            manager.reconnectChannels(null);
                                        }
                                    }
                                }
                        );
            } catch ( Exception e ) {

            }
        }
    }

    @Override
    public void         onConnectionSuspended(int i) {
        mWaitingForReconnect = true;
    }
}