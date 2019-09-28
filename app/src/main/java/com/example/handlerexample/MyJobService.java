package com.example.handlerexample;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.SystemClock;
import android.util.Log;

public class MyJobService extends JobService {
    private static final String TAG = "MyJobService";
    private boolean jobCancelled = false;
    @Override
    public boolean onStartJob(JobParameters params) {
        //create a background thread here to do long-running task
        Log.d(TAG, "onStartJob: ");
        doBackgroundWork(params);
        return true;
    }

    private void doBackgroundWork(final JobParameters params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //todo
                for (int i = 0; i < 5; i++) {
                    if(jobCancelled){
                        return;
                    }
                    Log.d(TAG, "run: scheduled job "+i);
                    SystemClock.sleep(1000);
                }
                Log.d(TAG, "job finished");
                jobFinished(params,false);
            }
        }).start();
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        Log.d(TAG, "onStopJob: job cancelled");
        jobCancelled = true;
        return false;
    }
}
