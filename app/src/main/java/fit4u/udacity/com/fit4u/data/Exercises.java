package fit4u.udacity.com.fit4u.data;

import java.io.Serializable;

public class Exercises implements Serializable {

    private String exercise_name;
    private String exercise_url;
    private String exercise_step;
    private String exercise_img_url;
    private String exercise_parent_name;
    private String exercise_parent_custom_name;

    public Exercises(String exercise_name, String exercise_url, String exercise_step,String exercise_img_url, String exercise_parent_name ,String exercise_parent_custom_name ) {
        this.exercise_name = exercise_name;
        this.exercise_url = exercise_url;
        this.exercise_step = exercise_step;
        this.exercise_img_url = exercise_img_url;
        this.exercise_parent_name = exercise_parent_name;
        this.exercise_parent_custom_name = exercise_parent_custom_name;
    }

    public String getExercise_parent_custom_name() {
        return exercise_parent_custom_name;
    }

    public String getExercise_parent_name() {
        return exercise_parent_name;
    }

    public String getExercise_img_url() {
        return exercise_img_url;
    }

    public String getExersice_name() {
        return exercise_name;
    }

    public String getExersice_url() {
        return exercise_url;
    }

    public String getExercise_step() {
        return exercise_step;
    }


}
