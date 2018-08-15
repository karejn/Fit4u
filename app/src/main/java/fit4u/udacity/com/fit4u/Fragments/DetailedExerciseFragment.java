package fit4u.udacity.com.fit4u.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import fit4u.udacity.com.fit4u.R;
import fit4u.udacity.com.fit4u.data.Exercises;

public class DetailedExerciseFragment extends Fragment implements ExoPlayer.EventListener {

    private SimpleExoPlayer mExoPlayer;
    private SimpleExoPlayerView mPlayerView;
    private static MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private static final String TAG = "DetailedExerciseFragment";
    ImageView noVideoImage;
    TextView stepDesc;
    View rootView;
    Long positionPlayer;
    Boolean playWhenReady;
    Exercises exercise;

    public DetailedExerciseFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.detailed_exercise_fragment, container, false);
        noVideoImage = rootView.findViewById(R.id.no_video_img);
        stepDesc = rootView.findViewById(R.id.Step_text_desc);
        mPlayerView = rootView.findViewById(R.id.playerView);

        initializeMediaSession();

        if (savedInstanceState != null) {
            exercise = (Exercises) savedInstanceState.getSerializable("Exercise");
            playWhenReady = savedInstanceState.getBoolean("ExoPlayerReady");
            positionPlayer = savedInstanceState.getLong("ExoPlayerPosition");
        }

        if (!TextUtils.isEmpty(exercise.getExersice_url())) {
            noVideoImage.setVisibility(rootView.INVISIBLE);
            mPlayerView.setFocusable(true);
            initializePlayer(Uri.parse(exercise.getExersice_url()));
        } else {
            noVideoImage.setImageResource(R.drawable.fitnessdefault);
            noVideoImage.setVisibility(rootView.VISIBLE);
        }

        String[] separated = exercise.getExercise_step().split("//");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < separated.length; i++) {
            sb.append(separated[i]);
            sb.append("\n\n");
        }
        stepDesc.setText(sb);
        return rootView;
    }

    public void setOneExercise(Exercises exercise) {
        this.exercise = exercise;
    }

    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {
    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
    }

    @Override
    public void onLoadingChanged(boolean isLoading) {
    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if ((playbackState == ExoPlayer.STATE_READY) && playWhenReady) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if ((playbackState == ExoPlayer.STATE_READY)) {
            mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        mMediaSession.setPlaybackState(mStateBuilder.build());
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            mPlayerView.setPlayer(mExoPlayer);
            //mPlayerView.requestFocus();
            mExoPlayer.addListener(this);

            String userAgent = Util.getUserAgent(getContext(), "StepVideo");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);

        }
    }

    private void initializeMediaSession() {

        mMediaSession = new MediaSessionCompat(getContext(), TAG);

        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

        mMediaSession.setMediaButtonReceiver(null);

        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE);

        mMediaSession.setPlaybackState(mStateBuilder.build());

        mMediaSession.setCallback(new MySessionCallback());

        mMediaSession.setActive(true);

    }

    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null) {

            positionPlayer = mExoPlayer.getCurrentPosition();
            playWhenReady = mExoPlayer.getPlayWhenReady();
            releasePlayer();
        }

        mMediaSession.setActive(false);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mExoPlayer != null)
            releasePlayer();

        mMediaSession.setActive(false);
    }

    private class MySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putSerializable("Exercise", exercise);
        if (!TextUtils.isEmpty(exercise.getExersice_url())) {
            currentState.putBoolean("ExoPlayerReady", playWhenReady);
            currentState.putLong("ExoPlayerPosition", positionPlayer);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mExoPlayer != null) {
            if (playWhenReady != null) {
                mExoPlayer.setPlayWhenReady(playWhenReady);
                mExoPlayer.seekTo(positionPlayer);
            }
        } else {
            initializeMediaSession();

            if (!TextUtils.isEmpty(exercise.getExersice_url())) {
                noVideoImage.setVisibility(rootView.INVISIBLE);
                mPlayerView.setFocusable(true);
                initializePlayer(Uri.parse(exercise.getExersice_url()));
                mExoPlayer.setPlayWhenReady(playWhenReady);
                mExoPlayer.seekTo(positionPlayer);
            } else {
                noVideoImage.setImageResource(R.drawable.fitnessdefault);
                noVideoImage.setVisibility(rootView.VISIBLE);
            }

        }
    }
}
