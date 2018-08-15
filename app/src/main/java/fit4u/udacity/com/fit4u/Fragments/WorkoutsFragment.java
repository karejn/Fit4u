package fit4u.udacity.com.fit4u.Fragments;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import fit4u.udacity.com.fit4u.ExercisesActivity;
import fit4u.udacity.com.fit4u.R;
import fit4u.udacity.com.fit4u.WorkoutsAdapter;
import fit4u.udacity.com.fit4u.data.WorkoutsContract;
import fit4u.udacity.com.fit4u.sync.WorkoutsSyncIntentService;
import fit4u.udacity.com.fit4u.utilities.WorkoutsSyncUtils;

public class WorkoutsFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor>,
        WorkoutsAdapter.WorkoutAdapterOnClickHandler,
        SwipeRefreshLayout.OnRefreshListener {

    private RecyclerView mRecyclerView;
    private WorkoutsAdapter tAdapter;
    private static final int ID_WORKOUT_LOADER = 99;
    private ProgressBar mLoadingIndicator;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private int mPosition = RecyclerView.NO_POSITION;

    public static final String[] MAIN_WORKOUT_PROJECTION = {
            WorkoutsContract.WorkoutEntry.COLUMN_WORKOUT_NAME,
            WorkoutsContract.WorkoutEntry.COLUMN_WORKOUT_POSTER_URL,
            WorkoutsContract.WorkoutEntry.COLUMN_ID
    };

    public WorkoutsFragment() {
        super();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflator = inflater.inflate(R.layout.sets_fragment, container, false);
        mRecyclerView = inflator.findViewById(R.id.recyclerview_workouts);
        mLoadingIndicator = inflator.findViewById(R.id.pb_loading_indicator);
        mSwipeRefreshLayout = inflator.findViewById(R.id.swipe_refresh_layout);

        LinearLayoutManager layoutManagerWorkouts =
                new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManagerWorkouts);
        mRecyclerView.setHasFixedSize(true);
        tAdapter = new WorkoutsAdapter(getContext(), this);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(tAdapter);

        showLoading();
        LoaderManager loaderManager = getActivity().getSupportLoaderManager();
        loaderManager.initLoader(ID_WORKOUT_LOADER, null, this);
        WorkoutsSyncUtils.initialize(getContext());
        mSwipeRefreshLayout.setOnRefreshListener(this);
        return inflator;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void showWorkoutsDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case ID_WORKOUT_LOADER:
                showLoading();
                Uri workoutQueryUri = WorkoutsContract.WorkoutEntry.CONTENT_URI;
                String selection = WorkoutsContract.WorkoutEntry.getSqlSelectWorkouts();
                return new CursorLoader(getContext(),
                        workoutQueryUri,
                        MAIN_WORKOUT_PROJECTION,
                        selection,
                        null,
                        null);
            default:
                throw new RuntimeException(getResources().getString(R.string.loaderNotImplemented) + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        tAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0) showWorkoutsDataView();

        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        tAdapter.swapCursor(null);
    }

    @Override
    public void onClick(String exerciseName) {
        Intent exerciseDetailIntent = new Intent(getActivity(), ExercisesActivity.class);
        Uri uriForWorkoutClicked = WorkoutsContract.ExercisetEntry.buildExerciseUriWithName(exerciseName);
        exerciseDetailIntent.setData(uriForWorkoutClicked);
        startActivity(exerciseDetailIntent);
    }


    @Override
    public void onRefresh() {
        Intent intentToSyncImmediately = new Intent(getContext(), WorkoutsSyncIntentService.class);
        getContext().startService(intentToSyncImmediately);
    }

}
