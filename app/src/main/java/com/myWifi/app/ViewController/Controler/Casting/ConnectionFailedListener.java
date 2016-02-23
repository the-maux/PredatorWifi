package com.myWifi.app.ViewController.Controler.Casting;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;

class                               ConnectionFailedListener implements GoogleApiClient.OnConnectionFailedListener {
    private RouterManager manager;

    public                          ConnectionFailedListener(RouterManager manager) {
        this.manager = manager;
    }
    @Override
    public void onConnectionFailed( ConnectionResult connectionResult ) {
        manager.disconnectEverything();
    }
}
