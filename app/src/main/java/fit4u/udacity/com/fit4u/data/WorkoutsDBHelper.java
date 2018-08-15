package fit4u.udacity.com.fit4u.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WorkoutsDBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "workouts.db";

    private static final int VERSION = 28;

    WorkoutsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String CREATE_TABLE = "CREATE TABLE " + WorkoutsContract.WorkoutEntry.TABLE_NAME + " (" +
                WorkoutsContract.WorkoutEntry._ID + " INTEGER PRIMARY KEY, " +
                WorkoutsContract.WorkoutEntry.COLUMN_WORKOUT_NAME + " TEXT NOT NULL, " +
                WorkoutsContract.WorkoutEntry.COLUMN_WORKOUT_POSTER_URL + " TEXT);";

        db.execSQL(CREATE_TABLE);

        final String CREATE_TABLE_EXERCISE = "CREATE TABLE " + WorkoutsContract.ExercisetEntry.TABLE_NAME + " (" +
                WorkoutsContract.ExercisetEntry._ID + " INTEGER PRIMARY KEY, " +
                WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_NAME + " TEXT NOT NULL, " +
                WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_URL + " TEXT, " +
                WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_IMG_URL + " TEXT, " +
                WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_STEPS + " TEXT, " +
                WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_PARENT_NAME + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE_EXERCISE);

        final String CREATE_TABLE_NEW_WORKOUTS = "CREATE TABLE " + WorkoutsContract.NewWorkoutEntry.TABLE_NAME + " (" +
                WorkoutsContract.NewWorkoutEntry._ID + " INTEGER PRIMARY KEY, " +
                WorkoutsContract.NewWorkoutEntry.COLUMN_NEW_WORKOUT_NAME + " TEXT unique);";

        db.execSQL(CREATE_TABLE_NEW_WORKOUTS);

        final String CREATE_TABLE_CUSTOM_EXERCISES = "CREATE TABLE " + WorkoutsContract.CustomExercisesEntry.TABLE_NAME + " (" +
                WorkoutsContract.CustomExercisesEntry._ID + " INTEGER PRIMARY KEY, " +
                WorkoutsContract.CustomExercisesEntry.COLUMN_NEW_WORKOUT_NAME + " TEXT NOT NULL, " +
                WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_NAME + " TEXT NOT NULL, " +
                WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_URL + " TEXT, " +
                WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_IMG_URL + " TEXT, " +
                WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_STEPS + " TEXT, " +
                WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_PARENT_NAME + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE_CUSTOM_EXERCISES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WorkoutsContract.WorkoutEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WorkoutsContract.ExercisetEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WorkoutsContract.NewWorkoutEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + WorkoutsContract.CustomExercisesEntry.TABLE_NAME);
        onCreate(db);
    }
}
