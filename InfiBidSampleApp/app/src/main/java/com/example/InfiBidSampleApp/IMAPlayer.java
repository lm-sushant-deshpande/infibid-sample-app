package com.example.InfiBidSampleApp;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.ads.interactivemedia.v3.api.AdDisplayContainer;
import com.google.ads.interactivemedia.v3.api.AdEvent;
import com.google.ads.interactivemedia.v3.api.AdsLoader;
import com.google.ads.interactivemedia.v3.api.AdsManager;
import com.google.ads.interactivemedia.v3.api.AdsRenderingSettings;
import com.google.ads.interactivemedia.v3.api.AdsRequest;
import com.google.ads.interactivemedia.v3.api.ImaSdkFactory;
import com.google.ads.interactivemedia.v3.api.ImaSdkSettings;
import com.google.ads.interactivemedia.v3.api.player.VideoProgressUpdate;

import java.util.Arrays;

public class IMAPlayer extends AppCompatActivity {

    public static final String EXTRA_VAST_TAG_URL = "com.example.InfiBidSampleApp.EXTRA_VAST_TAG_URL";
    private static final String LOGTAG = "IMABasicSample";
    private static final String SAMPLE_VIDEO_URL =
            "https://storage.googleapis.com/gvabox/media/samples/stock.mp4";


    private ImaSdkFactory sdkFactory;
    private AdsLoader adsLoader;
    private AdsManager adsManager;
    private int savedPosition = 0;
    private VideoView videoPlayer;
    private MediaController mediaController;
    private View playButton;
    private VideoAdPlayerAdapter videoAdPlayerAdapter;
    private String vastTagUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my); // Ensure this matches your layout

        // Retrieve the VAST tag URL from the Intent
        vastTagUrl = getIntent().getStringExtra(EXTRA_VAST_TAG_URL);

        mediaController = new MediaController(this);
        videoPlayer = findViewById(R.id.videoView);
        mediaController.setAnchorView(videoPlayer);
        videoPlayer.setMediaController(mediaController);

        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        videoAdPlayerAdapter = new VideoAdPlayerAdapter(videoPlayer, audioManager);

        sdkFactory = ImaSdkFactory.getInstance();

        AdDisplayContainer adDisplayContainer =
                ImaSdkFactory.createAdDisplayContainer(
                        findViewById(R.id.videoPlayerContainer), videoAdPlayerAdapter);

        ImaSdkSettings settings = sdkFactory.createImaSdkSettings();
        adsLoader = sdkFactory.createAdsLoader(this, settings, adDisplayContainer);

        adsLoader.addAdErrorListener(
                adErrorEvent -> {
                    Log.i(LOGTAG, "Ad Error: " + adErrorEvent.getError().getMessage());
                    resumeContent();
                });
        adsLoader.addAdsLoadedListener(
                adsManagerLoadedEvent -> {
                    adsManager = adsManagerLoadedEvent.getAdsManager();
                    adsManager.addAdErrorListener(
                            adErrorEvent -> {
                                Log.e(LOGTAG, "Ad Error: " + adErrorEvent.getError().getMessage());
                                String universalAdIds =
                                        Arrays.toString(adsManager.getCurrentAd().getUniversalAdIds());
                                Log.i(LOGTAG, "Discarding the current ad break with universal "
                                        + "ad Ids: " + universalAdIds);
                                adsManager.discardAdBreak();
                            });
                    adsManager.addAdEventListener(
                            adEvent -> {
                                if (adEvent.getType() != AdEvent.AdEventType.AD_PROGRESS) {
                                    Log.i(LOGTAG, "Event: " + adEvent.getType());
                                }
                                switch (adEvent.getType()) {
                                    case LOADED:
                                        adsManager.start();
                                        break;
                                    case CONTENT_PAUSE_REQUESTED:
                                        pauseContentForAds();
                                        break;
                                    case CONTENT_RESUME_REQUESTED:
                                        resumeContent();
                                        break;
                                    case ALL_ADS_COMPLETED:
                                        adsManager.destroy();
                                        adsManager = null;
                                        break;
                                    case CLICKED:
                                        // Handle ad click events if needed
                                        break;
                                    default:
                                        break;
                                }
                            });
                    AdsRenderingSettings adsRenderingSettings =
                            ImaSdkFactory.getInstance().createAdsRenderingSettings();
                    adsManager.init(adsRenderingSettings);
                });

        playButton = findViewById(R.id.playButton);
        playButton.setOnClickListener(
                view -> {
                    videoPlayer.setVideoPath(SAMPLE_VIDEO_URL);
                    requestAds(vastTagUrl);
                    view.setVisibility(View.GONE);
                });
        updateVideoDescriptionVisibility();
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration configuration) {
        super.onConfigurationChanged(configuration);
        updateVideoDescriptionVisibility();
    }

    private void updateVideoDescriptionVisibility() {
        int orientation = getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
            findViewById(R.id.videoDescription).setVisibility(View.GONE);
        } else {
            findViewById(R.id.videoDescription).setVisibility(View.VISIBLE);
        }
    }

    private void pauseContentForAds() {
        Log.i(LOGTAG, "pauseContentForAds");
        savedPosition = videoPlayer.getCurrentPosition();
        videoPlayer.stopPlayback();
        videoPlayer.setMediaController(null);
    }

    private void resumeContent() {
        Log.i(LOGTAG, "resumeContent");
        videoPlayer.setVideoPath(SAMPLE_VIDEO_URL);
        videoPlayer.setMediaController(mediaController);
        videoPlayer.setOnPreparedListener(
                mediaPlayer -> {
                    if (savedPosition > 0) {
                        mediaPlayer.seekTo(savedPosition);
                    }
                    mediaPlayer.start();
                });
        videoPlayer.setOnCompletionListener(
                mediaPlayer -> videoAdPlayerAdapter.notifyImaOnContentCompleted());
    }

    private void requestAds(@NonNull String adTagUrl) {
        AdsRequest request = sdkFactory.createAdsRequest();
        request.setAdTagUrl(adTagUrl);
        request.setContentProgressProvider(
                () -> {
                    if (videoPlayer.getDuration() <= 0) {
                        return VideoProgressUpdate.VIDEO_TIME_NOT_READY;
                    }
                    return new VideoProgressUpdate(
                            videoPlayer.getCurrentPosition(), videoPlayer.getDuration());
                });
        adsLoader.requestAds(request);
    }
}
