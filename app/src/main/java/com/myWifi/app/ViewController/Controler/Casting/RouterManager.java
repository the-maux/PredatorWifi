package com.myWifi.app.ViewController.Controler.Casting;


import android.content.Context;
import android.os.Bundle;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import com.google.android.gms.cast.Cast;
import com.google.android.gms.cast.CastDevice;
import com.google.android.gms.common.api.GoogleApiClient;

import java.io.IOException;

class                               RouterManager extends MediaRouter.Callback {
    private String                  TAG = "RouterManager";
    protected boolean               mApplicationStarted = false;
    protected boolean               videoIsLoaded;
    private Cast.Listener           mCastClientListener;
    private CastDevice              mSelectedDevice;
    private CastingManager          manager;
    private Context                 Ctx;
    protected GoogleApiClient       mApiClient;

    public                          RouterManager(CastingManager manager, Context Ctx) {
        this.manager = manager;
    }
    @Override
    public void                     onRouteSelected(MediaRouter router, MediaRouter.RouteInfo info) {
        initCastClientListener();
        mSelectedDevice = CastDevice.getFromBundle( info.getExtras() );
        launchReceiver();
    }
    @Override
    public void                     onRouteUnselected( MediaRouter router, MediaRouter.RouteInfo info ) {
        disconnectEverything();
        mSelectedDevice = null;
        //mButton.setText("http://techslides.com/demos/sample-videos/small.webm");
        videoIsLoaded = false;
    }

    private void                    initCastClientListener() {
        mCastClientListener = new Cast.Listener() {
            @Override
            public void onApplicationStatusChanged() {
            }

            @Override
            public void onVolumeChanged() {
            }

            @Override
            public void onApplicationDisconnected( int statusCode ) {
                disconnectEverything();
            }
        };
    }
    private void                    launchReceiver() {
        Cast.CastOptions.Builder apiOptionsBuilder = Cast.CastOptions
                .builder( mSelectedDevice, mCastClientListener );
        ConnectionFailedListener mConnectionFailedListener = new ConnectionFailedListener(this);
        mApiClient = new GoogleApiClient.Builder(Ctx)
                .addApi( Cast.API, apiOptionsBuilder.build() )
                .addConnectionCallbacks( new ConnectionCallbacks(this, mApiClient) )
                .addOnConnectionFailedListener( mConnectionFailedListener )
                .build();

        mApiClient.connect();
    }
    public void                     disconnectEverything() {
        if(mApiClient != null) {
            if(mApplicationStarted) {
                try {
                    Cast.CastApi.stopApplication(mApiClient);
                    if(manager.remoteMediaPlayer != null) {
                        Cast.CastApi.removeMessageReceivedCallbacks(mApiClient, manager.remoteMediaPlayer.getNamespace());
                        manager.remoteMediaPlayer = null;
                    }
                } catch(IOException e) {
                    //Log.e( TAG, "Exception while removing application " + e );
                }
                mApplicationStarted = false;
            }
            if(mApiClient.isConnected())
                mApiClient.disconnect();
            mApiClient = null;
        }
        mSelectedDevice = null;
        videoIsLoaded = false;
    }
    public void                     reconnectChannels(Bundle hint) {
        if( ( hint != null ) && hint.getBoolean( Cast.EXTRA_APP_NO_LONGER_RUNNING ) ) {
            Log.e( TAG, "App is no longer running" );
            disconnectEverything();
        } else {
            try {
                Cast.CastApi.setMessageReceivedCallbacks(mApiClient,
                                                            manager.remoteMediaPlayer.getNamespace(),
                                                            manager.remoteMediaPlayer);
            } catch( IOException e ) {
                Log.e( TAG, "Exception while creating media channel ", e );
            } catch( NullPointerException e ) {
                Log.e( TAG, "Something wasn't reinitialized for reconnectChannels" );
            }
        }
    }
    public boolean                  isVideoLoaded() {
        return videoIsLoaded;
    }
}