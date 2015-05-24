package hk.ust.cse.comp4521.eventmaker.Helper;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import hk.ust.cse.comp4521.eventmaker.Constants;
import hk.ust.cse.comp4521.eventmaker.Event.Event_T;
import hk.ust.cse.comp4521.eventmaker.Relationship.Relahelper;
import hk.ust.cse.comp4521.eventmaker.User.UserServer;

public class ActivityRefresh extends Service {

    //use new threads to execute
    public static boolean connected;
    private boolean binded;
    private String eventID;
    private Timer timer;
    private int counter;
    private BroadcastReceiver mReceiver;


    public ActivityRefresh() {
        connected = false;
    }

    @Override
    public void onCreate() {

        IntentFilter intentFilter = new IntentFilter(Constants.signaling);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getIntExtra("Signal", -1) ==Constants.ConnectionError){
                    //stop participants service

                    stopSelf();
                }
                if (intent.getIntExtra("Signal", -1) == Constants.allserviceStopped) {
                    Log.i("ActivityRefresh", "Received force close");
                    stopSelf();
                }

            }
        };


        this.registerReceiver(mReceiver, intentFilter);
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer = new Timer(true);


        timer.schedule(new TimerTask() {
            @Override
            public void run() {

                Log.i("ActivityRefresh", "TimeTask");
                networkAccess();


            }
        }, 3000, 10000);

        if (intent == null)
            stopSelf();
        eventID = intent.getStringExtra(Constants.eventId);




        return super.onStartCommand(intent, flags, startId);


    }

    @Override
    public void onDestroy() {
        Log.i("ActivityRefresh", "Stopping Refreshing the network.");
        timer.cancel();
        this.unregisterReceiver(this.mReceiver);
        super.onDestroy();

    }

    public void networkAccess(){
        ServerConnection server = new ServerConnection(null, null);
        server.run();
        if (UserServer.connectionState == false){
            connected = false;
            Intent i = new Intent(Constants.signaling).putExtra("Signal", Constants.ConnectionError);
            this.sendBroadcast(i);
            stopSelf();
        }
        else {
            connected = true;

            Event_T eventserver = new Event_T();
            eventserver.getAllEvent();
            Object lock = new Object();
            Relahelper.relas = null;
            Relahelper.locker = true;
            Relahelper.lock = lock;


            Relahelper relationshiphelper = new Relahelper();
            relationshiphelper.getAllRelationship();
            while (Relahelper.relas == null){
                synchronized (lock){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            checkeventExist();
        }

    }

    private void checkeventExist() {
        boolean foundEvent = false;
        for (int i = 0; i< Relahelper.relas.size(); i++){
            if (Relahelper.relas.get(i).userId.equals(UserServer.returnInfo._id)){
                foundEvent = true;
                break;
            }
        }
        Log.i("ActivityRefresh", "Result is "+ foundEvent);
        if (!foundEvent){
//            Intent i = new Intent(Constants.signaling).putExtra("Signal", Constants.EventDeleted);
//            this.sendBroadcast(i);

            stopSelf();
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
//        Timer timer = new Timer(true);
//        timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                Log.i("ActivityRefresh", "TimeTask");
//                Thread thread = new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        networkAccess();
//                    }
//                });
//                thread.start();
//
//            }
//        }, 1000, 20000);
        binded = true;
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        binded = false;
        return super.onUnbind(intent);
    }


}
