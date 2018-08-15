package fit4u.udacity.com.fit4u.utilities;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

import fit4u.udacity.com.fit4u.data.Exercises;
import fit4u.udacity.com.fit4u.data.Workouts;
import fit4u.udacity.com.fit4u.data.WorkoutsContract;

public class OpenWorkoutsJsonUtils {

    public static ContentValues[] getWorkoutsContentValues(Context context, List<Workouts> workouts) {
        ContentValues[] workoutsContentValues = new ContentValues[workouts.size()];

        for (int i = 0; i < workouts.size(); i++) {
            String workout_name = workouts.get(i).getWorkout_name();
            String poster_url = workouts.get(i).getPoster_url();

            ContentValues workoutsValues = new ContentValues();
            workoutsValues.put(WorkoutsContract.WorkoutEntry.COLUMN_WORKOUT_NAME, workout_name);
            workoutsValues.put(WorkoutsContract.WorkoutEntry.COLUMN_WORKOUT_POSTER_URL, poster_url);

            workoutsContentValues[i] = workoutsValues;
        }

        return workoutsContentValues;
    }

    public static ContentValues[] getExercisesContentValues(Context context, List<Workouts> workouts) {
        int counter = 0;
        int indexer = 0;
        ArrayList<Exercises> exercisesList;

        for (int i = 0; i < workouts.size(); i++) {
            exercisesList = workouts.get(i).getExercises();
            for (int j = 0; j < exercisesList.size(); j++) {
                counter++;
            }
        }
        ContentValues[] exercisesContentValues = new ContentValues[counter];

        for (int i = 0; i < workouts.size(); i++) {
            String workout_name = workouts.get(i).getWorkout_name();
            exercisesList = workouts.get(i).getExercises();

            for (int j = 0; j < exercisesList.size(); j++) {
                String exercise_name = exercisesList.get(j).getExersice_name();
                String exercise_img_url = exercisesList.get(j).getExercise_img_url();
                String exercise_video_url = exercisesList.get(j).getExersice_url();
                String exercise_steps = exercisesList.get(j).getExercise_step();

                ContentValues exercisesValues = new ContentValues();
                exercisesValues.put(WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_NAME, exercise_name);
                exercisesValues.put(WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_URL, exercise_video_url);
                exercisesValues.put(WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_IMG_URL, exercise_img_url);
                exercisesValues.put(WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_STEPS, exercise_steps);
                exercisesValues.put(WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_PARENT_NAME, workout_name);

                exercisesContentValues[indexer] = exercisesValues;
                indexer++;
            }

        }

        return exercisesContentValues;
    }

}
