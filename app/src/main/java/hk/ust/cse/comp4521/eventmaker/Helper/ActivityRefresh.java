package hk.ust.cse.comp4521.eventmaker.Helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Timer;
import java.util.TimerTask;

import hk.ust.cse.comp4521.eventmaker.Constants;
import hk.ust.cse.comp4521.eventmaker.Event.Event_T;
import hk.ust.cse.comp4521.eventmaker.Relationship.Relahelper;
import hk.ust.cse.comp4521.eventmaker.User.UserServer;

public class ActivityRefresh extends Service {

    //use new threads to execute
    private boolean connected;
    public ActivityRefresh() {
        connected = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        networkAccess();
                    }
                }, 1000, 30000);
            }
        });


        networkAccess();
        return super.onStartCommand(intent, flags, startId);


    }

    public void networkAccess(){
        ServerConnection server = new ServerConnection(null, null);
        if (UserServer.connectionState == false){
            connected = false;
            Intent i = new Intent(Constants.signaling).putExtra("Signal", Constants.ConnectionError);
            this.sendBroadcast(i);
        }
        else {
            connected = true;

            Event_T eventserver = new Event_T();
            eventserver.getAllEvent();

            Relahelper relationshiphelper = new Relahelper();
            relationshiphelper.getAllRelationship();
        }

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }


}
