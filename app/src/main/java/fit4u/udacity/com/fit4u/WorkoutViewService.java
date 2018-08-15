package fit4u.udacity.com.fit4u;

import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.util.ArrayList;

import fit4u.udacity.com.fit4u.data.Exercises;
import fit4u.udacity.com.fit4u.data.Workouts;

public class WorkoutViewService extends RemoteViewsService {

    public WorkoutViewFactory onGetViewFactory(Intent intent) {
        return new WorkoutViewFactory(this.getApplicationContext());
    }

}

class WorkoutViewFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ArrayList<Exercises> exercises;

    public WorkoutViewFactory(Context mContext) {
        this.mContext = mContext;

    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        Workouts workout = WorkoutsWidgetProvider.workout;
        this.exercises = workout.getExercises();
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        if (exercises == null)
            return 0;
        return exercises.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.exercise_text_view_widget_layout);
        String st = exercises.get(position).getExersice_name();
        views.setTextViewText(R.id.text_view_workout_widget, st);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtra("Exercise", exercises.get(position));
        views.setOnClickFillInIntent(R.id.widget_layout_item, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
