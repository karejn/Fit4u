package fit4u.udacity.com.fit4u;

import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import fit4u.udacity.com.fit4u.data.Exercises;
import fit4u.udacity.com.fit4u.data.WorkoutsContract;
import fit4u.udacity.com.fit4u.utilities.CircleTransform;

public class ExercisesAdapter extends RecyclerView.Adapter<ExercisesAdapter.ExercisesAdapterViewHolder> {

    private Cursor mCursor;
    private final Context mContext;
    final private ExercisesAdapterOnClickHandler mClickHandler;
    private final static int FADE_DURATION = 1000; //FADE_DURATION in milliseconds

    public ExercisesAdapter(@NonNull Context context, ExercisesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public ExercisesAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        int layoutId;
        layoutId = R.layout.exercise_item;
        View view = LayoutInflater.from(mContext).inflate(layoutId, viewGroup, false);
        view.setFocusable(true);

        return new ExercisesAdapter.ExercisesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExercisesAdapter.ExercisesAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String exercise_name = mCursor.getString(mCursor.getColumnIndex(WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_NAME));
        String exercise_image_url = mCursor.getString(mCursor.getColumnIndex(WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_IMG_URL));
        holder.exercise_name.setText(exercise_name);
        if (exercise_image_url != null)
            if (!TextUtils.isEmpty(exercise_image_url))
                Picasso.with(mContext).load(exercise_image_url).transform(new CircleTransform()).placeholder(R.drawable.defaultimg).into(holder.exercise_image);

        setFadeAnimation(holder.itemView);
    }

    private void setFadeAnimation(View view) {
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(FADE_DURATION);
        view.startAnimation(anim);
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

    public interface ExercisesAdapterOnClickHandler {
        void onClick(Exercises exercise);
    }

    class ExercisesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        final TextView exercise_name;
        final ImageView exercise_image;

        ExercisesAdapterViewHolder(View view) {
            super(view);
            exercise_name = view.findViewById(R.id.exercise_name);
            exercise_image = view.findViewById(R.id.exercise_image);

            view.setOnClickListener(this);
            view.setOnLongClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String exerciseParentName = mCursor.getString(mCursor.getColumnIndex(WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_PARENT_NAME));
            String exerciseName = mCursor.getString(mCursor.getColumnIndex(WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_NAME));
            String exerciseVideoUrl = mCursor.getString(mCursor.getColumnIndex(WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_URL));
            String exerciseSteps = mCursor.getString(mCursor.getColumnIndex(WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_STEPS));
            String exerciseImgUrl = mCursor.getString(mCursor.getColumnIndex(WorkoutsContract.ExercisetEntry.COLUMN_EXERCISE_IMG_URL));
            Exercises exercise = new Exercises(exerciseName, exerciseVideoUrl, exerciseSteps, exerciseImgUrl, exerciseParentName, null);
            mClickHandler.onClick(exercise);
        }

        @Override
        public boolean onLongClick(View v) {
            return false;
        }
    }
}
