package fit4u.udacity.com.fit4u.sync;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import fit4u.udacity.com.fit4u.data.Exercises;
import fit4u.udacity.com.fit4u.data.Workouts;
import fit4u.udacity.com.fit4u.data.WorkoutsContract;
import fit4u.udacity.com.fit4u.utilities.OpenWorkoutsJsonUtils;

public class WorkoutsSyncTask {

    synchronized public static void syncWorkouts(final Context context) {

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();

        mDatabase.child("Workouts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Object key = dataSnapshot.getValue();
                Map workouts = (HashMap) key;
                List<Workouts> workoutsList = new ArrayList<>();

                Iterator it = workouts.entrySet().iterator();
                while (it.hasNext()) {
                    ArrayList<Exercises> exercisesList = new ArrayList<>();
                    Map.Entry entry = (Map.Entry) it.next(); //current entry in a loop
                    Object workout_entry = entry.getValue();
                    Map workout_entry_map = (HashMap) workout_entry;
                    String workout_name = workout_entry_map.get("workout_name").toString();
                    String poster_url = workout_entry_map.get("poster_url").toString();
                    Object exercises_object = workout_entry_map.get("exercises");
                    Map exercises = (HashMap) exercises_object;

                    Iterator it2 = exercises.entrySet().iterator();
                    while (it2.hasNext()) {
                        Map.Entry entry2 = (Map.Entry) it2.next(); //current entry in a loop
                        Object exercise_entry = entry2.getValue();
                        Map exercise = (HashMap) exercise_entry;
                        Exercises newExercise = new Exercises(exercise.get("exercise_name").toString(),
                                exercise.get("exercise_url").toString(),
                                exercise.get("exercise_steps").toString(),
                                exercise.get("exercise_img_url").toString(),
                                workout_name, null);
                        exercisesList.add(newExercise);
                    }
                    Workouts newWorkout = new Workouts(workout_name, poster_url, exercisesList);
                    workoutsList.add(newWorkout);
                }

                ContentValues[] workoutsValues = OpenWorkoutsJsonUtils.getWorkoutsContentValues(context, workoutsList);
                ContentValues[] exercisesValues = OpenWorkoutsJsonUtils.getExercisesContentValues(context, workoutsList);

                if (workoutsValues != null && workoutsValues.length != 0) {
                    ContentResolver workoutsContentResolver = context.getContentResolver();

                    workoutsContentResolver.delete(WorkoutsContract.WorkoutEntry.CONTENT_URI,
                            null,
                            null);

                    workoutsContentResolver.bulkInsert(
                            WorkoutsContract.WorkoutEntry.CONTENT_URI,
                            workoutsValues);

                    workoutsContentResolver.delete(WorkoutsContract.ExercisetEntry.CONTENT_URI,
                            null,
                            null);

                    workoutsContentResolver.bulkInsert(
                            WorkoutsContract.ExercisetEntry.CONTENT_URI,
                            exercisesValues);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
