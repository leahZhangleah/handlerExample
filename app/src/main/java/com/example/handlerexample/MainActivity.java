package com.example.handlerexample;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    Button firstThreadBtn, secondRunnableBtn,mainThreadBtn,handlerThread,scheduleJobBtn,cancelJobBtn;
    FirstThread firstThread;
    Thread thread;
    MyHandlerThread myHandlerThread;
    Handler mainHandler,threadHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate: ");
        //view initialisation
        mainThreadBtn = findViewById(R.id.main_thread);
        firstThreadBtn = findViewById(R.id.first_thread);
        secondRunnableBtn = findViewById(R.id.second_runnable);
        handlerThread = findViewById(R.id.handler_thread);
        scheduleJobBtn = findViewById(R.id.schedule_job);
        cancelJobBtn = findViewById(R.id.cancel_job);

        //get handler of ui thread
        mainHandler = new Handler();

        //start handlerThread and create handler for this thread
        myHandlerThread = new MyHandlerThread();
        myHandlerThread.start();
        //needs to start thread first before having handler
        threadHandler = myHandlerThread.getHandler();


        mainThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,SeconActivity.class);
                startActivity(intent);
            }
        });
        firstThreadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firstThread = new FirstThread();
                firstThread.start();
            }
        });
        secondRunnableBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                thread = new Thread(new SecondRunnable());
                thread.start();
            }
        });
        handlerThread.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                we usually have HandlerThread in service which doesn't follow activity's lifecycle
                since handlerThread has looper, it will be alive as long as the messagequeue associated with it is not empty
                IntentService works on the same concept
                 */
                //HandlerThread can be only started once, or it will crash the app

                //if we create runnable like, the runnable will keep a reference to our activity, if it's not done,
                //our activity can't be GCed, therefore, it's better to do as SecondRunnable
                threadHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i <10 ; i++) {
                            Log.d(TAG, "handler thread run: "+i);
                            SystemClock.sleep(1000);
                        }
                    }
                });
            }
        });

        scheduleJobBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scheduleJob(v);
            }
        });

       cancelJobBtn.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               cancelJob(v);
           }
       });
    }

    /*
    schedule a job
     */

    public void scheduleJob(View v){
        ComponentName componentName = new ComponentName(this, MyJobService.class);
        JobInfo info = new JobInfo.Builder(100,componentName)
                .setRequiresCharging(true)
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                .setPersisted(true)
                .setPeriodic(10*60*1000)
                .build();
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        int resultCode = scheduler.schedule(info);
        if(resultCode == JobScheduler.RESULT_SUCCESS){
            Log.d(TAG, "scheduled Job: ");
        }else{
            Log.d(TAG, "failed Job schedule: ");
        }
    }

    public void cancelJob(View v){
        JobScheduler scheduler = (JobScheduler) getSystemService(JOB_SCHEDULER_SERVICE);
        scheduler.cancel(100);
        Log.d(TAG, "cancelJob: ");
    }

    private class FirstThread extends Thread{
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                Log.d(TAG, "first run: "+i);
                if(i==3){
                    /*mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            firstThreadBtn.setText("50%");
                        }
                    });*/
                    //same as above
                    firstThreadBtn.post(new Runnable() {
                        @Override
                        public void run() {
                            firstThreadBtn.setText("50%");
                        }
                    });

                    //or call runOnUiThread
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class SecondRunnable implements Runnable {
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                Log.d(TAG, "second run: "+i);
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: ");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        checkThreadAlive(firstThread);
        checkThreadAlive(thread);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        checkThreadAlive(firstThread);
        checkThreadAlive(thread);
        myHandlerThread.quit();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart: ");
    }

    private void checkThreadAlive(Thread thd){
        if(thd!=null && thd.isAlive()&&!thd.isInterrupted()){
            Log.d(TAG, thd + "checkThreadAlive: true");

        }
    }


}
