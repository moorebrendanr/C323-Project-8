package com.c323proj8.BrendanMoore;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

/**
 * This class runs database queries in the background.
 */
public class DBQueryService extends Service {
    Handler toastHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * Start the Service
     * @param intent the intent
     * @param flags flags
     * @param startId the start ID
     * @return START_STICKY
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MessagesDatabase db = new MessagesDatabase(this);
        TimerTask task = new Task(db, db.getCount());
        Timer timer = new Timer("DBQueryTimer");
        timer.scheduleAtFixedRate(task, 0, 5000);
        return START_STICKY;
    }

    /**
     * Create the Service
     */
    @Override
    public void onCreate() {
        super.onCreate();
        // Create Handler in UI thread
        toastHandler = new Handler();
    }

    /**
     * A task that periodically queries the database.
     */
    private class Task extends TimerTask {
        MessagesDatabase db;
        int dbSize;

        /**
         * Construct a Task
         * @param db the databse
         * @param dbSize the database size
         */
        Task(MessagesDatabase db, int dbSize) {
            this.db = db;
            this.dbSize = dbSize;
        }

        /**
         * If database size increases, update size and send a toast.
         */
        @Override
        public void run() {
            int currentSize = db.getCount();
            if (currentSize > dbSize) {
                dbSize = currentSize;
                toastHandler.post(new Runnable() {
                    /**
                     * Send a toast in the UI thread.
                     */
                    @Override
                    public void run() {
                        Toast.makeText(DBQueryService.this, "There is a new Message in the Database!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
