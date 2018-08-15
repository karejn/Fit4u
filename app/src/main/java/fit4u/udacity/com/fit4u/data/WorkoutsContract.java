package fit4u.udacity.com.fit4u.data;

import android.net.Uri;
import android.provider.BaseColumns;

public class WorkoutsContract {

    public static final String CONTENT_AUTHORITY = "udacity.com.fit4u";
    public static final String PATH_WORKOUTS = "workouts";
    public static final String PATH_EXERCISES = "exercises";
    public static final String PATH_NEW_WORKOUTS = "customworkouts";
    public static final String PATH_CUSTOM_EXERCISES = "customexercises";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final class WorkoutEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WORKOUTS)
                .build();

        public static final String TABLE_NAME = "workouts";

        public static final String COLUMN_ID = "id";
        public static final String COLUMN_WORKOUT_NAME = "workout_name";
        public static final String COLUMN_WORKOUT_POSTER_URL = "poster_url";

        public static String getSqlSelectWorkouts() {
            return WorkoutsContract.WorkoutEntry.COLUMN_WORKOUT_NAME;
        }

    }

    public static final class ExercisetEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_EXERCISES)
                .build();

        public static final String TABLE_NAME = "exercises";

        public static final String COLUMN_EXERCISE_PARENT_NAME = "exercise_parent_name";
        public static final String COLUMN_EXERCISE_NAME = "exercise_name";
        public static final String COLUMN_EXERCISE_URL = "exercise_url";
        public static final String COLUMN_EXERCISE_IMG_URL = "exercise_img_url";
        public static final String COLUMN_EXERCISE_STEPS = "exercise_steps";

        public static Uri buildExerciseUriWithName(String workoutName) {
            return CONTENT_URI.buildUpon()
                    .appendPath(workoutName)
                    .build();
        }

    }

    public static final class NewWorkoutEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_NEW_WORKOUTS)
                .build();

        public static final String TABLE_NAME = "new_workout";
        public static final String COLUMN_NEW_WORKOUT_NAME = "new_workout_name";

        public static String getSqlSelectNewWorkouts() {
            return WorkoutsContract.NewWorkoutEntry.COLUMN_NEW_WORKOUT_NAME;
        }

        public static Uri buildExerciseUriWithName(String customName) {
            return CONTENT_URI.buildUpon()
                    .appendPath(customName)
                    .build();
        }

    }

    public static final class CustomExercisesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_CUSTOM_EXERCISES)
                .build();

        public static final String TABLE_NAME = "custom_exercise";
        public static final String COLUMN_EXERCISE_PARENT_NAME = "exercise_parent_name";
        public static final String COLUMN_EXERCISE_NAME = "exercise_name";
        public static final String COLUMN_EXERCISE_URL = "exercise_url";
        public static final String COLUMN_EXERCISE_IMG_URL = "exercise_img_url";
        public static final String COLUMN_EXERCISE_STEPS = "exercise_steps";
        public static final String COLUMN_NEW_WORKOUT_NAME = "new_workout";

        public static String getSqlSelectCustomExercises() {
            return WorkoutsContract.CustomExercisesEntry.COLUMN_EXERCISE_NAME;
        }

        public static Uri buildExerciseUriWithName(String customName, String exerciseName) {
            return CONTENT_URI.buildUpon()
                    .appendPath(customName)
                    .appendPath(exerciseName)
                    .build();
        }


        public static Uri buildExerciseUriWithCustomName(String customName) {
            return CONTENT_URI.buildUpon()
                    .appendPath(customName)
                    .build();
        }

    }
}
