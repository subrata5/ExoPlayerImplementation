package com.example.subrata.alexatestthree;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.mp4.Track;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.ButterKnife;

public class VideoPlayActivity extends Activity implements Player.EventListener {

    private String TAG = VideoPlayActivity.class.getSimpleName();
    private static final String KEY_VIDEO_URI = "video_uri";


    PlayerView videoFullScreenPlayer;
    ProgressBar spinnerVideoDetails;
    ImageView imageViewExit;

    String videoUri;
    SimpleExoPlayer player;
    Handler mHandler;
    Runnable mRunnable;

    public static Intent getStartIntent(Context context, String videoUri){
        Intent intent = new Intent(context, VideoPlayActivity.class);
        intent.putExtra(KEY_VIDEO_URI, videoUri);
        return intent;
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_video_play);

        ButterKnife.bind(this);

        videoFullScreenPlayer = findViewById(R.id.videoFullScreenPlayer);
        spinnerVideoDetails = findViewById(R.id.spinnerVideoDetails);
        imageViewExit = findViewById(R.id.imageViewExit);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        //getSupportActionBar().hide();

        if(getIntent().hasExtra(KEY_VIDEO_URI)){
            videoUri = getIntent().getStringExtra(KEY_VIDEO_URI);
        }
        
        setUp();

        imageViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setUp() {
        initializePlayer();
        if(videoUri == null){
            return;
        }
        buildMediaSource(Uri.parse(videoUri));
    }

    private void initializePlayer(){
        if(player == null){
            //1. Create a default TrackSelector
            LoadControl loadControl = new DefaultLoadControl(
                    new DefaultAllocator(true, 16),
                    5000,
                    5000,
                    1500,
                    5000,
                    -1,
                    true
            );

            BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
            TrackSelection.Factory videoTrackSelectionFactory =
                    new AdaptiveTrackSelection.Factory(bandwidthMeter);
            TrackSelector trackSelector =
                    new DefaultTrackSelector(videoTrackSelectionFactory);


            //2. Create the player
            player = ExoPlayerFactory.newSimpleInstance(new DefaultRenderersFactory(VideoPlayActivity.this), trackSelector);
            videoFullScreenPlayer.setPlayer(player);
        }
    }

    private void buildMediaSource(Uri mUri) {

        //Measure playback during playback
        DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        //produces data source instances
        DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(this,
                Util.getUserAgent(this, "ExoPlayerApp"), bandwidthMeter);

        //Media source representing the media
        MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                .createMediaSource(mUri);

        //prepare the player with the source
        player.prepare(videoSource);
        player.setPlayWhenReady(true);
        player.addListener(this);
    }


    private void releasePlayer(){
        if(player != null){
            player.release();
            player = null;
        }
    }

    private void pausePlayer(){
        if(player != null){
            player.setPlayWhenReady(false);
            player.getPlaybackState();
        }
    }


    private void resumePlayer(){
        if(player != null){
            player.setPlayWhenReady(true);
            player.getPlaybackState();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        pausePlayer();
        if(mRunnable != null){
            mHandler.removeCallbacks(mRunnable);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();

        resumePlayer();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releasePlayer();
    }

    @Override
    public void onTimelineChanged(Timeline timeline, @Nullable Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        switch (playbackState){

            case  Player.STATE_BUFFERING:
                spinnerVideoDetails.setVisibility(View.VISIBLE);
                break;

            case Player.STATE_ENDED:
                //Activate the force enable
                break;

            case Player.STATE_IDLE:
                break;

            case Player.STATE_READY:
                spinnerVideoDetails.setVisibility(View.GONE);
                break;

                default:
                    //Status = Playback.IDLE
                    break;

        }
    }


    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
}
