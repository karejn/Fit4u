package fit4u.udacity.com.fit4u.sync;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import fit4u.udacity.com.fit4u.WorkoutsWidgetProvider;
import fit4u.udacity.com.fit4u.data.Workouts;

public class WorkoutsWidgetIntentService extends IntentService {

    public static final String ACTION_CHANGE_WORKOUT = "udacity.com.fit4u.action.change_workout";
    public static final String ACTION_CHANGE_WORKOUT_WIDGET = "udacity.com.fit4u.action.change_workout_widget";
    Workouts workout;

    public WorkoutsWidgetIntentService() {
        super("WorkoutsWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHANGE_WORKOUT.equals(action)) {
                handleActionChangeWorkout();
            } else if (ACTION_CHANGE_WORKOUT_WIDGET.equals(action)) {
                Bundle bundle;
                bundle = intent.getExtras();
                workout = (Workouts) bundle.getSerializable("WorkoutName");
                handleActionUpdateWorkoutWidgets(workout);
            }
        }
    }

    private void handleActionChangeWorkout() {

    }

    private void handleActionUpdateWorkoutWidgets(Workouts workoutName) {

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, WorkoutsWidgetProvider.class));
        WorkoutsWidgetProvider.updateWorkoutWidgets(this, appWidgetManager, workoutName, appWidgetIds);

    }

    public static void startActionUpdateWorkoutWidgets(Context context, Workouts workoutName) {
        Bundle b = new Bundle();
        b.putSerializable("WorkoutName", workoutName);
        Intent intent = new Intent(context, WorkoutsWidgetIntentService.class);
        intent.setAction(ACTION_CHANGE_WORKOUT_WIDGET);
        intent.putExtras(b);
        context.startService(intent);
    }
}
