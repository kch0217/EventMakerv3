package hk.ust.cse.comp4521.eventmaker.Helper;

import android.app.Service;
import android.content.Intent;
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
    private boolean connected;
    private boolean binded;
    private String eventID;
    private Timer timer;
    private int counter;

    public ActivityRefresh() {
        connected = false;
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
        super.onDestroy();

    }

    public void networkAccess(){
        ServerConnection server = new ServerConnection(null, null);
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
