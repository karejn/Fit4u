package fit4u.udacity.com.fit4u;

import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import fit4u.udacity.com.fit4u.data.Exercises;
import fit4u.udacity.com.fit4u.data.WorkoutsContract;
import fit4u.udacity.com.fit4u.databinding.ActivityExercisesBinding;

public class ExercisesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        ExercisesAdapter.ExercisesAdapterOnClickHandler {

    private ExercisesAdapter mAdapter;
    ActivityExercisesBinding mBinding;
    private Uri mUri;
    private static final int ID_EXERCISES_LOADER = 123;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private int mPosition = RecyclerView.NO_POSITION;

    public static final String[] EXERCISES_DETAIL_PROJECTION = {
            WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_NAME,
            WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_URL,
            WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_IMG_URL,
            WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_STEPS,
            WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_PARENT_NAME
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercises);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_exercises);
        mRecyclerView = mBinding.recyclerviewExercises;
        mLoadingIndicator = mBinding.pbLoadingIndicator;

        LinearLayoutManager layoutManagerExercises =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManagerExercises);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new ExercisesAdapter(this, this);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mAdapter);

        mUri = getIntent().getData();
        if (mUri == null)
            throw new NullPointerException(getResources().getString(R.string.uriCannotBeNull));

        setTitle(mUri.getLastPathSegment());

        getSupportLoaderManager().initLoader(ID_EXERCISES_LOADER, null, this);
    }

    private void showWorkoutsDataView() {
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showLoading() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {

            case ID_EXERCISES_LOADER:
                showLoading();
                return new CursorLoader(this,
                        mUri,
                        EXERCISES_DETAIL_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException(getResources().getString(R.string.loaderNotImplemented) + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        if (data.getCount() != 0)
            showWorkoutsDataView();
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    @Override
    public void onClick(Exercises exercise) {
        Intent exerciseDetailIntent = new Intent(this, DetailedExerciseActivity.class);
        exerciseDetailIntent.putExtra("Exercise", exercise);
        startActivity(exerciseDetailIntent);
    }
}
