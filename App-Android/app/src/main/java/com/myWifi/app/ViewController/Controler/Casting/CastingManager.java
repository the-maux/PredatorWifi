package com.myWifi.app.ViewController.Controler.Casting;



import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.media.MediaRouteSelector;
import android.support.v7.media.MediaRouter;
import android.util.Log;
import com.google.android.gms.cast.*;
import com.google.android.gms.common.api.ResultCallback;

import com.myWifi.app.MainActivityToFragment;
import com.myWifi.app.R;


public class                        CastingManager {
    private String                  TAG = "CastingManager";
    private MediaRouter             mMediaRouter;
    private MediaRouteSelector      mMediaRouteSelector;
    private RouterManager           routerManager;
    private String                  urlVideo = "http://techslides.com/demos/sample-videos/small.webm";
    private String                  mediaType = "video/mp4";
    protected RemoteMediaPlayer     remoteMediaPlayer;
    protected boolean               mIsPlaying;

    public                          CastingManager(Context context, Fragment fragment) {
        mMediaRouter = MediaRouter.getInstance(context);
        mMediaRouteSelector = new MediaRouteSelector.Builder()
                .addControlCategory(
                        CastMediaControlIntent.categoryForCast(fragment.getString(R.string.app_id)))
                .build();
        ((MainActivityToFragment)fragment.getActivity()).initMenuForAirplay(mMediaRouteSelector);
        routerManager = new RouterManager(this, context);
        initRemoteMediaPlayer();
    }

    public void                     action() {
        if(!routerManager.isVideoLoaded())
            startVideo();
        else
            controlVideo();
    }

    private void                    startVideo() {
        MediaMetadata mediaMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MOVIE);
        mediaMetadata.putString(MediaMetadata.KEY_TITLE, "TITRE DE LA VIDEO");
        MediaInfo mediaInfo = new MediaInfo.Builder(urlVideo)
                .setContentType(mediaType)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setMetadata(mediaMetadata)
                .build();
        try {
            remoteMediaPlayer.load(routerManager.mApiClient, mediaInfo, true)
                    .setResultCallback(new ResultCallback<RemoteMediaPlayer.MediaChannelResult>() {
                        @Override
                        public void onResult(RemoteMediaPlayer.MediaChannelResult mediaChannelResult) {
                            if(mediaChannelResult.getStatus().isSuccess()) {
                                routerManager.videoIsLoaded = true;
                            }
                        }
                    } );
        } catch(Exception e) {
            e.printStackTrace();
            Log.e(TAG, "Error in start Video");
        }
    }

    private void                    controlVideo() {
        if(remoteMediaPlayer == null || !routerManager.videoIsLoaded)
            return;
        if(mIsPlaying) {
            remoteMediaPlayer.pause(routerManager.mApiClient);
        } else {
            remoteMediaPlayer.play(routerManager.mApiClient);
        }
    }
    private void                    initRemoteMediaPlayer() {
        remoteMediaPlayer = new RemoteMediaPlayer();
        remoteMediaPlayer.setOnStatusUpdatedListener(new RemoteMediaPlayer.OnStatusUpdatedListener() {
            @Override
            public void onStatusUpdated() {
                MediaStatus mediaStatus = remoteMediaPlayer.getMediaStatus();
                mIsPlaying = mediaStatus.getPlayerState() == MediaStatus.PLAYER_STATE_PLAYING;
            }
        });

        remoteMediaPlayer.setOnMetadataUpdatedListener(new RemoteMediaPlayer.OnMetadataUpdatedListener() {
            @Override
            public void onMetadataUpdated() {
            }
        });
    }

    public MediaRouter              getmMediaRouter() {
        return mMediaRouter;
    }
}
