package hk.ust.cse.comp4521.eventmaker.Helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import hk.ust.cse.comp4521.eventmaker.Event.Event_T;
import hk.ust.cse.comp4521.eventmaker.User.UserServer;

public class ActivityRefresh extends Service {

    private boolean connected;
    public ActivityRefresh() {
        connected = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    public void networkAccess(){
        ServerConnection server = new ServerConnection(null, null);
        if (UserServer.connectionState == false){
            connected = false;
        }
        Event_T eventserver =new Event_T();
        eventserver.getAllEvent();

    }



    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }


}
