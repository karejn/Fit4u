package fit4u.udacity.com.fit4u.utilities;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

import fit4u.udacity.com.fit4u.data.WorkoutsContract;
import fit4u.udacity.com.fit4u.sync.WorkoutsSyncIntentService;

public class WorkoutsSyncUtils {

    private static boolean sInitialized;

    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, WorkoutsSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }

    synchronized public static void initialize(@NonNull final Context context) {

        if (sInitialized) return;

        sInitialized = true;

        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {
                Uri QueryUri = WorkoutsContract.WorkoutEntry.CONTENT_URI;
                String[] projectionColumns = {WorkoutsContract.WorkoutEntry._ID};
                Cursor cursor = context.getContentResolver().query(
                        QueryUri,
                        projectionColumns,
                        null,
                        null,
                        null);

                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                if (cursor != null)
                    cursor.close();
            }
        });

        checkForEmpty.start();
    }

}
