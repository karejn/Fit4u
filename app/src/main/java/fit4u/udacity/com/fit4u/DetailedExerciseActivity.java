package fit4u.udacity.com.fit4u;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

import fit4u.udacity.com.fit4u.Fragments.DetailedExerciseFragment;
import fit4u.udacity.com.fit4u.data.Exercises;
import fit4u.udacity.com.fit4u.data.Workouts;
import fit4u.udacity.com.fit4u.data.WorkoutsContract;
import fit4u.udacity.com.fit4u.sync.WorkoutsWidgetIntentService;

public class DetailedExerciseActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = "DetailedExerciseActivity";
    Exercises exercise;
    Bundle bundle = new Bundle();
    FloatingActionButton fabFavorite;
    private FloatingActionButton mFabShare;
    ArrayList<Integer> customWorkouts;
    String[] listWorkouts;
    Activity act;
    private CoordinatorLayout coordinatorLayout;
    private static final int ID_CUSTOM_WORKOUT_LOADER = 101;
    boolean[] checkedWorkouts;

    public static final String[] MAIN_NEW_WORKOUT_PROJECTION = {
            WorkoutsContract.NewWorkoutEntry.COLUMN_NEW_WORKOUT_NAME
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_exercise);
        customWorkouts = new ArrayList<>();
        act = this;
        fabFavorite = findViewById(R.id.fab_favorite);
        mFabShare = findViewById(R.id.fab_share);

        ActionBar toolbar = getSupportActionBar();
        toolbar.setDisplayHomeAsUpEnabled(true);
        coordinatorLayout = findViewById(R.id.coordinator_layout);
        fabFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (listWorkouts != null) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailedExerciseActivity.this);
                    mBuilder.setTitle(getResources().getString(R.string.selectCustomSet));
                    mBuilder.setMultiChoiceItems(listWorkouts, checkedWorkouts, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            checkedWorkouts[which] = isChecked;
                        }
                    });
                    mBuilder.setCancelable(true);
                    mBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            StringBuilder st = new StringBuilder();

                            for (int i = 0; i < listWorkouts.length; i++) {
                                if (checkedWorkouts[i]) {
                                    String workout = listWorkouts[i];
                                    ContentResolver newWorkoutsContentResolver = getContentResolver();
                                    Boolean isFound = isAddedToFavorite(newWorkoutsContentResolver, exercise.getExersice_name(), workout);

                                    if (!isFound) {
                                        ContentValues[] newWorkoutContentValues = new ContentValues[1];
                                        ContentValues newWorkoutValues = new ContentValues();
                                        newWorkoutValues.put(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_IMG_URL, exercise.getExercise_img_url());
                                        newWorkoutValues.put(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_NAME, exercise.getExersice_name());
                                        newWorkoutValues.put(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_PARENT_NAME, exercise.getExercise_parent_name());
                                        newWorkoutValues.put(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_STEPS, exercise.getExercise_step());
                                        newWorkoutValues.put(WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_URL, exercise.getExersice_url());
                                        newWorkoutValues.put(WorkoutsContract.CustomExercisesEntry.COLUMN_NEW_WORKOUT_NAME, workout);
                                        newWorkoutContentValues[0] = newWorkoutValues;

                                        newWorkoutsContentResolver.bulkInsert(
                                                WorkoutsContract.CustomExercisesEntry.CONTENT_URI,
                                                newWorkoutContentValues);

                                        st.append(getResources().getString(R.string.exerciseAdded) + workout);
                                        st.append("\n");

                                        updateWidget(workout, exercise);
                                    } else {
                                        st.append(getResources().getString(R.string.exerciseAlreadyAdded) + workout + getResources().getString(R.string.folder));
                                        st.append("\n");
                                    }
                                }

                            }

                            if (!TextUtils.isEmpty(st)) {
                                Snackbar snackbar = Snackbar
                                        .make(coordinatorLayout, st, Snackbar.LENGTH_LONG);
                                snackbar.setActionTextColor(Color.YELLOW);
                                snackbar.show();
                            }
                        }
                    });
                    mBuilder.show();
                } else {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(DetailedExerciseActivity.this);
                    mBuilder.setTitle("");
                    mBuilder.setMessage(getResources().getString(R.string.pleaseAddCustom));
                    mBuilder.setCancelable(false);
                    mBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    }).show();
                }

            }
        });

        NestedScrollView scrollView = findViewById(R.id.nested);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY - oldScrollY > 0) {
                    fabFavorite.hide();
                    mFabShare.hide();
                } else {
                    fabFavorite.show();
                    mFabShare.show();
                }
            }
        });

        mFabShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onShare();
            }
        });

        FragmentManager fragmentManager = getSupportFragmentManager();

        try {
            bundle = getIntent().getExtras();
            exercise = (Exercises) bundle.getSerializable("Exercise");
        } catch (Throwable e) {
            e.printStackTrace();
        }


        if (savedInstanceState == null) {
            DetailedExerciseFragment exerciseNewFragment = new DetailedExerciseFragment();
            exerciseNewFragment.setOneExercise(exercise);

            fragmentManager.beginTransaction()
                    .add(R.id.player_container, exerciseNewFragment)
                    .commit();
        }

        setTitle(exercise.getExersice_name());
        LoaderManager loaderManager = this.getSupportLoaderManager();
        loaderManager.initLoader(ID_CUSTOM_WORKOUT_LOADER, null, this);
    }

    private void updateWidget(String workoutName, Exercises exercise) {
        Workouts widgetWorkout = WorkoutsWidgetProvider.workout;
        if (widgetWorkout != null) {
            if (widgetWorkout.getWorkout_name().equals(workoutName)) {
                widgetWorkout.addExercise(exercise);
                WorkoutsWidgetIntentService.startActionUpdateWorkoutWidgets(this, widgetWorkout);
            }
        }
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

    @Override
    public void onSaveInstanceState(Bundle currentState) {
        super.onSaveInstanceState(currentState);
        currentState.putSerializable("Exercise", exercise);

    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, @Nullable Bundle args) {
        switch (loaderId) {
            case ID_CUSTOM_WORKOUT_LOADER:
                Uri workoutQueryUri = WorkoutsContract.NewWorkoutEntry.CONTENT_URI;
                String selection = WorkoutsContract.NewWorkoutEntry.getSqlSelectNewWorkouts();
                return new CursorLoader(this,
                        workoutQueryUri,
                        MAIN_NEW_WORKOUT_PROJECTION,
                        selection,
                        null,
                        null);
            default:
                throw new RuntimeException(getResources().getString(R.string.loaderNotImplemented) + loaderId);
        }
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        if (data.getCount() != 0) {
            listWorkouts = new String[data.getCount()];
            checkedWorkouts = new boolean[data.getCount()];
            while (data.moveToNext()) {
                String custom = data.getString(data.getColumnIndex(WorkoutsContract.NewWorkoutEntry.COLUMN_NEW_WORKOUT_NAME));
                listWorkouts[data.getPosition()] = custom;
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }

    private boolean isAddedToFavorite(ContentResolver favoriteContentResolver, String exerciseName, String customName) {
        Uri uriForMovieClicked = WorkoutsContract.CustomExercisesEntry.buildExerciseUriWithName(customName, exerciseName);
        String[] argument = {customName, exerciseName};
        String[] projectionColumns = {WorkoutsContract.CustomExercisesEntry.COLUMN_NEW_WORKOUT_NAME, WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_NAME};
        final Cursor cursorReviews = favoriteContentResolver.query(
                uriForMovieClicked,
                projectionColumns,
                null,
                argument,
                null);
        int found = cursorReviews.getCount();

        cursorReviews.close();

        if (found == 1)
            return true;
        else
            return false;
    }

    private void onShare() {

        String[] separated = exercise.getExercise_step().split("//");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < separated.length; i++) {
            sb.append(separated[i]);
            sb.append("\n\n");
        }

        String title = exercise.getExersice_name();
        String text = sb.toString();

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TITLE, title);
        shareIntent.putExtra(Intent.EXTRA_TEXT, text);

        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_using)));
        } else {
            Log.e(TAG, getResources().getString(R.string.noAvailableIntent));
            Toast.makeText(this, getResources().getString(R.string.no_app_available_to_share_content), Toast.LENGTH_LONG).show();
        }
    }
}
