package fit4u.udacity.com.fit4u.data;

import java.io.Serializable;
import java.util.ArrayList;

public class Workouts implements Serializable {

    private String set_name;
    private String poster_url;
    private ArrayList<Exercises> Exercises;

    public Workouts(String workout_name, String poster_url, ArrayList<Exercises> exercises) {
        this.set_name = workout_name;
        this.poster_url = poster_url;
        Exercises = exercises;
    }

    public String getPoster_url() {
        return poster_url;
    }

    public String getWorkout_name() {
        return set_name;
    }

    public ArrayList getExercises() {
        return Exercises;
    }

    public void setExercises(ArrayList<Exercises> exercises) {
        Exercises = exercises;
    }

    public void addExercise(Exercises exercise) {
        Exercises.add(exercise);
    }

    public void deleteExercise(Exercises exercise) {
        for (int i = 0; i < Exercises.size(); i++) {
            if (exercise.getExersice_name().equals(this.Exercises.get(i).getExersice_name())) {
                Exercises.remove(i);
            }
        }
    }
}
