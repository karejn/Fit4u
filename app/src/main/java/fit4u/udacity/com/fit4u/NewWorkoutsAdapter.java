package fit4u.udacity.com.fit4u;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.TextView;

import java.util.ArrayList;

import fit4u.udacity.com.fit4u.data.Exercises;
import fit4u.udacity.com.fit4u.data.Workouts;
import fit4u.udacity.com.fit4u.data.WorkoutsContract;
import fit4u.udacity.com.fit4u.sync.WorkoutsWidgetIntentService;

public class NewWorkoutsAdapter  extends RecyclerView.Adapter<NewWorkoutsAdapter.NewWorkoutAdapterViewHolder>{

    private Cursor mCursor;
    private final Context mContext;
    final private NewWorkoutsAdapter.NewWorkoutAdapterOnClickHandler mClickHandler;
    private final static int FADE_DURATION = 1000;

    public NewWorkoutsAdapter(@NonNull Context context , NewWorkoutAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public NewWorkoutAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        int layoutId;
        layoutId = R.layout.new_set_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);

        return new NewWorkoutsAdapter.NewWorkoutAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewWorkoutAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String new_workout_name = mCursor.getString(mCursor.getColumnIndex(WorkoutsContract.NewWorkoutEntry.COLUMN_NEW_WORKOUT_NAME));
        holder.workout_name.setText(new_workout_name);

        setFadeAnimation(holder.itemView);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    public interface NewWorkoutAdapterOnClickHandler {
        void onClick(String newWorkoutName);
    }

    class NewWorkoutAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView workout_name;

        NewWorkoutAdapterViewHolder(View view)
        {
            super(view);
            workout_name = view.findViewById(R.id.new_workout_name);
            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String newWorkoutName = mCursor.getString(mCursor.getColumnIndex(WorkoutsContract.NewWorkoutEntry.COLUMN_NEW_WORKOUT_NAME));
            mClickHandler.onClick(newWorkoutName);
        }

        @Override
        public boolean onLongClick(View v) {


            android.support.v7.app.AlertDialog.Builder mBuilder = new android.support.v7.app.AlertDialog.Builder(mContext);
            mBuilder.setTitle(mContext.getResources().getString(R.string.areYouSureToRemoveSet));
            mBuilder.setPositiveButton(mContext.getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    int adapterPosition = getAdapterPosition();
                    mCursor.moveToPosition(adapterPosition);
                    String newWorkoutName = mCursor.getString(mCursor.getColumnIndex(WorkoutsContract.NewWorkoutEntry.COLUMN_NEW_WORKOUT_NAME));
                    ContentResolver newWorkoutsContentResolver = mContext.getContentResolver();
                    String[] selectionArguments = new String[]{newWorkoutName};
                    Uri uriForMovieClicked = WorkoutsContract.NewWorkoutEntry.buildExerciseUriWithName(newWorkoutName);
                    newWorkoutsContentResolver.delete(uriForMovieClicked,
                            WorkoutsContract.NewWorkoutEntry.COLUMN_NEW_WORKOUT_NAME + " = ?",
                            selectionArguments);
                    uriForMovieClicked = WorkoutsContract.CustomExercisesEntry.buildExerciseUriWithCustomName(newWorkoutName);

                    newWorkoutsContentResolver.delete(uriForMovieClicked,
                            WorkoutsContract.CustomExercisesEntry.COLUMN_NEW_WORKOUT_NAME + " = ?",
                            selectionArguments);

                    String workoutName =null;
                    if(WorkoutsWidgetProvider.workout != null)
                        workoutName = WorkoutsWidgetProvider.workout.getWorkout_name();

                    if(workoutName!= null && newWorkoutName.equals(workoutName))
                    {
                        ArrayList<Exercises> Exercises = new ArrayList<>() ;
                        Workouts customWorkout = new Workouts(mContext.getResources().getString(R.string.selectSet) , null ,Exercises );
                        WorkoutsWidgetIntentService.startActionUpdateWorkoutWidgets(mContext,customWorkout);
                    }
                }
            });
            mBuilder.setNegativeButton(mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            mBuilder.show();

            return true;
        }
    }
}
