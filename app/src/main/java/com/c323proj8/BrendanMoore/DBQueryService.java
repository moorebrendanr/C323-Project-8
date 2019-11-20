package com.c323proj8.BrendanMoore;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import java.util.Timer;
import java.util.TimerTask;

public class DBQueryService extends Service {
    Handler toastHandler;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        MessagesDatabase db = new MessagesDatabase(this);
        TimerTask task = new Task(db, db.getCount());
        Timer timer = new Timer("DBQueryTimer");
        timer.scheduleAtFixedRate(task, 0, 5000);
        return START_STICKY;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        toastHandler = new Handler();
    }

    private class Task extends TimerTask {
        MessagesDatabase db;
        int dbSize;

        Task(MessagesDatabase db, int dbSize) {
            this.db = db;
            this.dbSize = dbSize;
        }

        @Override
        public void run() {
            int currentSize = db.getCount();
            if (currentSize > dbSize) {
                dbSize = currentSize;
                toastHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(DBQueryService.this, "There is a new Message in the Database!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
}
