package fit4u.udacity.com.fit4u.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import fit4u.udacity.com.fit4u.R;

public class WorkoutsContentProvider extends ContentProvider {

    public static final int CODE_WORKOUTS = 100;
    public static final int CODE_EXERCISES = 200;
    public static final int CODE_SELECTED_EXERCISES = 300;
    public static final int CODE_NEW_WORKOUTS = 400;
    public static final int CODE_NEW_SELECTED_WORKOUTS = 401;
    public static final int CODE_CUSTOM_EXERCISES = 500;
    public static final int CODE_SELECTED_CUSTOM_EXERCISES = 600;
    public static final int CODE_SELECT_ALL_CUSTOM_EXERCISES = 700;
    private WorkoutsDBHelper workoutsDBHelper;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WorkoutsContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, WorkoutsContract.PATH_WORKOUTS, CODE_WORKOUTS);
        matcher.addURI(authority, WorkoutsContract.PATH_EXERCISES, CODE_EXERCISES);
        matcher.addURI(authority, WorkoutsContract.PATH_EXERCISES + "/*", CODE_SELECTED_EXERCISES);
        matcher.addURI(authority, WorkoutsContract.PATH_NEW_WORKOUTS, CODE_NEW_WORKOUTS);
        matcher.addURI(authority, WorkoutsContract.PATH_NEW_WORKOUTS + "/*", CODE_NEW_SELECTED_WORKOUTS);
        matcher.addURI(authority, WorkoutsContract.PATH_CUSTOM_EXERCISES, CODE_CUSTOM_EXERCISES);
        matcher.addURI(authority, WorkoutsContract.PATH_CUSTOM_EXERCISES + "/*" + "/*", CODE_SELECTED_CUSTOM_EXERCISES);
        matcher.addURI(authority, WorkoutsContract.PATH_CUSTOM_EXERCISES + "/*", CODE_SELECT_ALL_CUSTOM_EXERCISES);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        workoutsDBHelper = new WorkoutsDBHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor cursor;
        switch (sUriMatcher.match(uri)) {
            case CODE_WORKOUTS: {
                cursor = workoutsDBHelper.getReadableDatabase().query(
                        WorkoutsContract.WorkoutEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_EXERCISES: {
                cursor = workoutsDBHelper.getReadableDatabase().query(
                        WorkoutsContract.ExercisetEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_SELECTED_EXERCISES: {
                String exerciseParent = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{exerciseParent};

                cursor = workoutsDBHelper.getReadableDatabase().query(
                        WorkoutsContract.ExercisetEntry.TABLE_NAME,
                        null,
                        WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_PARENT_NAME + " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_NEW_WORKOUTS: {
                cursor = workoutsDBHelper.getReadableDatabase().query(
                        WorkoutsContract.NewWorkoutEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_CUSTOM_EXERCISES: {
                cursor = workoutsDBHelper.getReadableDatabase().query(
                        WorkoutsContract.CustomExercisesEntry.TABLE_NAME,
                        null,
                        null,
                        null,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_SELECTED_CUSTOM_EXERCISES: {
                String workoutCustomName = selectionArgs[0];
                String exerciseParent = selectionArgs[1];
                String[] selectionArguments = new String[]{exerciseParent, workoutCustomName};

                cursor = workoutsDBHelper.getReadableDatabase().query(
                        WorkoutsContract.CustomExercisesEntry.TABLE_NAME,
                        null,
                        WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_NAME + " = ? and " + WorkoutsContract.CustomExercisesEntry.COLUMN_NEW_WORKOUT_NAME + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            case CODE_SELECT_ALL_CUSTOM_EXERCISES: {
                String exerciseParent = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{exerciseParent};

                cursor = workoutsDBHelper.getReadableDatabase().query(
                        WorkoutsContract.CustomExercisesEntry.TABLE_NAME,
                        null,
                        WorkoutsContract.CustomExercisesEntry.COLUMN_NEW_WORKOUT_NAME + " = ?",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException(getContext().getResources().getString(R.string.unknownUri) + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int numRowsDeleted;

        if (null == selection) selection = "1";

        switch (sUriMatcher.match(uri)) {

            case CODE_WORKOUTS:
                numRowsDeleted = workoutsDBHelper.getWritableDatabase().delete(
                        WorkoutsContract.WorkoutEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_EXERCISES:
                numRowsDeleted = workoutsDBHelper.getWritableDatabase().delete(
                        WorkoutsContract.ExercisetEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_NEW_WORKOUTS:
                numRowsDeleted = workoutsDBHelper.getWritableDatabase().delete(
                        WorkoutsContract.NewWorkoutEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_CUSTOM_EXERCISES:
                numRowsDeleted = workoutsDBHelper.getWritableDatabase().delete(
                        WorkoutsContract.CustomExercisesEntry.TABLE_NAME,
                        selection,
                        selectionArgs);
                break;
            case CODE_SELECTED_CUSTOM_EXERCISES: {
                numRowsDeleted = workoutsDBHelper.getWritableDatabase().delete(
                        WorkoutsContract.CustomExercisesEntry.TABLE_NAME,
                        WorkoutsContract.CustomExercisesEntry.COLUMN_NEW_WORKOUT_NAME + " = ? and " + WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_NAME + " = ? ",
                        selectionArgs);
                break;
            }
            case CODE_NEW_SELECTED_WORKOUTS: {
                numRowsDeleted = workoutsDBHelper.getWritableDatabase().delete(
                        WorkoutsContract.NewWorkoutEntry.TABLE_NAME,
                        WorkoutsContract.NewWorkoutEntry.COLUMN_NEW_WORKOUT_NAME + " = ?",
                        selectionArgs);
                break;
            }
            case CODE_SELECT_ALL_CUSTOM_EXERCISES: {
                numRowsDeleted = workoutsDBHelper.getWritableDatabase().delete(
                        WorkoutsContract.CustomExercisesEntry.TABLE_NAME,
                        WorkoutsContract.CustomExercisesEntry.COLUMN_NEW_WORKOUT_NAME + " = ?",
                        selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException(getContext().getResources().getString(R.string.unknownUri) + uri);
        }

        if (numRowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numRowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return 0;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {

        final SQLiteDatabase db = workoutsDBHelper.getWritableDatabase();

        switch (sUriMatcher.match(uri)) {
            case CODE_WORKOUTS:
                db.beginTransaction();
                int rowsInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(WorkoutsContract.WorkoutEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsInserted;
            case CODE_EXERCISES:
                db.beginTransaction();
                int rowsExerciseInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(WorkoutsContract.ExercisetEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsExerciseInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsExerciseInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsExerciseInserted;
            case CODE_NEW_WORKOUTS:
                db.beginTransaction();
                int rowsNewWorkoutInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(WorkoutsContract.NewWorkoutEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsNewWorkoutInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsNewWorkoutInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsNewWorkoutInserted;
            case CODE_CUSTOM_EXERCISES:
                db.beginTransaction();
                int rowsCustomInserted = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(WorkoutsContract.CustomExercisesEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            rowsCustomInserted++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }

                if (rowsCustomInserted > 0) {
                    getContext().getContentResolver().notifyChange(uri, null);
                }

                return rowsCustomInserted;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        workoutsDBHelper.close();
        super.shutdown();
    }
}
