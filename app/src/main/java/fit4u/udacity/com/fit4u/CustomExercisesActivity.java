package fit4u.udacity.com.fit4u;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import java.util.ArrayList;

import fit4u.udacity.com.fit4u.data.Exercises;
import fit4u.udacity.com.fit4u.data.Workouts;
import fit4u.udacity.com.fit4u.data.WorkoutsContract;
import fit4u.udacity.com.fit4u.sync.WorkoutsWidgetIntentService;
import fit4u.udacity.com.fit4u.utilities.RecyclerItemTouchHelper;

public class CustomExercisesActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>,
        CustomExercisesAdapter.CustomExercisesAdapterOnClickHandler,
        RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    private CustomExercisesAdapter mAdapter;
    private Uri mUri;
    private static final int ID_EXERCISES_LOADER = 555;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout coordinatorLayout;
    private int mPosition = RecyclerView.NO_POSITION;
    private Workouts customWorkout;

    public static final String[] CUSTOM_EXERCISES_DETAIL_PROJECTION = {
            WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_NAME,
            WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_URL,
            WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_IMG_URL,
            WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_STEPS,
            WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_PARENT_NAME
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_exercises);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.recyclerview_custom_exercises);
        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);
        coordinatorLayout = findViewById(R.id.coordinator_layout);

        LinearLayoutManager layoutManagerExercises =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        mRecyclerView.setLayoutManager(layoutManagerExercises);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new CustomExercisesAdapter(this, this);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new RecyclerItemTouchHelper(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, this);
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(mRecyclerView);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback1 = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        new ItemTouchHelper(itemTouchHelperCallback1).attachToRecyclerView(mRecyclerView);

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
                        CUSTOM_EXERCISES_DETAIL_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException(getResources().getString(R.string.loaderNotImplemented) + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        ArrayList<Exercises> Exercises = new ArrayList<>();

        mAdapter.swapCursor(data);
        if (mPosition == RecyclerView.NO_POSITION) mPosition = 0;
        mRecyclerView.smoothScrollToPosition(mPosition);
        showWorkoutsDataView();

        if (data.getCount() != 0) {
            data.moveToPosition(-1);
            while (data.moveToNext()) {

                String exerciseParentName = data.getString(data.getColumnIndex(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_PARENT_NAME));
                String exerciseName = data.getString(data.getColumnIndex(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_NAME));
                String exerciseVideoUrl = data.getString(data.getColumnIndex(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_URL));
                String exerciseSteps = data.getString(data.getColumnIndex(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_STEPS));
                String exerciseImgUrl = data.getString(data.getColumnIndex(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_IMG_URL));
                String exerciseParentCustomName = data.getString(data.getColumnIndex(WorkoutsContract.CustomExercisesEntry.COLUMN_NEW_WORKOUT_NAME));
                Exercises exercise = new Exercises(exerciseName, exerciseVideoUrl, exerciseSteps, exerciseImgUrl, exerciseParentName, exerciseParentCustomName);
                Exercises.add(exercise);
            }

            customWorkout = new Workouts(mUri.getLastPathSegment(), null, Exercises);
            WorkoutsWidgetIntentService.startActionUpdateWorkoutWidgets(this, customWorkout);
        } else {
            Exercises = new ArrayList<>();
            customWorkout = new Workouts(mUri.getLastPathSegment(), null, Exercises);
            WorkoutsWidgetIntentService.startActionUpdateWorkoutWidgets(this, customWorkout);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putSerializable("Workout", customWorkout);

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

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position) {
        if (viewHolder instanceof CustomExercisesAdapter.CustomExercisesAdapterViewHolder) {

            final Exercises ExerciseSelected = ((CustomExercisesAdapter.CustomExercisesAdapterViewHolder) viewHolder).getExercise(position);
            final ContentResolver newWorkoutsContentResolver = getContentResolver();
            String[] selectionArguments = new String[]{ExerciseSelected.getExercise_parent_custom_name(), ExerciseSelected.getExersice_name()};
            Uri uriForMovieClicked = WorkoutsContract.CustomExercisesEntry.buildExerciseUriWithName(ExerciseSelected.getExercise_parent_custom_name(), ExerciseSelected.getExersice_name());

            newWorkoutsContentResolver.delete(uriForMovieClicked,
                    null,
                    selectionArguments);

            updateWidget(ExerciseSelected.getExercise_parent_custom_name(), ExerciseSelected);

            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, ExerciseSelected.getExersice_name() + getResources().getString(R.string.removeFromSets), Snackbar.LENGTH_LONG);
            snackbar.setAction(getResources().getString(R.string.undo), new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    ContentValues[] newWorkoutContentValues = new ContentValues[1];
                    ContentValues newWorkoutValues = new ContentValues();
                    newWorkoutValues.put(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_IMG_URL, ExerciseSelected.getExercise_img_url());
                    newWorkoutValues.put(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_NAME, ExerciseSelected.getExersice_name());
                    newWorkoutValues.put(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_PARENT_NAME, ExerciseSelected.getExercise_parent_name());
                    newWorkoutValues.put(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_STEPS, ExerciseSelected.getExercise_step());
                    newWorkoutValues.put(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_URL, ExerciseSelected.getExersice_url());
                    newWorkoutValues.put(WorkoutsContract.CustomExercisesEntry.COLUMN_NEW_WORKOUT_NAME, ExerciseSelected.getExercise_parent_custom_name());
                    newWorkoutContentValues[0] = newWorkoutValues;

                    newWorkoutsContentResolver.bulkInsert(
                            WorkoutsContract.CustomExercisesEntry.CONTENT_URI,
                            newWorkoutContentValues);

                    updateRestoreToWidget(ExerciseSelected.getExercise_parent_custom_name(), ExerciseSelected);
                }
            });
            snackbar.setActionTextColor(Color.YELLOW);
            snackbar.show();
        }
    }

    private void updateRestoreToWidget(String workoutName, Exercises exercise) {
        Workouts widgetWorkout = WorkoutsWidgetProvider.workout;
        if (widgetWorkout != null) {
            if (widgetWorkout.getWorkout_name().equals(workoutName)) {
                widgetWorkout.addExercise(exercise);
                WorkoutsWidgetIntentService.startActionUpdateWorkoutWidgets(this, widgetWorkout);
            }
        }
    }

    private void updateWidget(String workoutName, Exercises exerciseName) {
        Workouts widgetWorkout = WorkoutsWidgetProvider.workout;
        if (widgetWorkout != null) {
            if (widgetWorkout.getWorkout_name().equals(workoutName)) {
                widgetWorkout.deleteExercise(exerciseName);
                WorkoutsWidgetIntentService.startActionUpdateWorkoutWidgets(this, widgetWorkout);
            }
        }
    }
}
