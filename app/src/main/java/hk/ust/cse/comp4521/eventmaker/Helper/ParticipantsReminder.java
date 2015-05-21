package hk.ust.cse.comp4521.eventmaker.Helper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import hk.ust.cse.comp4521.eventmaker.Constants;
import hk.ust.cse.comp4521.eventmaker.Relationship.Relahelper;
import hk.ust.cse.comp4521.eventmaker.Relationship.Relationship;

public class ParticipantsReminder extends Service {

    private String eventID;
    private LinkedList<Relationship> oldlist;
    private Relationship relate;
    private List<String> id_delete;
    private List<String> id_add;

    public ParticipantsReminder() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        eventID = intent.getStringExtra(Constants.eventId);
//        Object lock = new Object();
//        Relahelper.lock = lock;
//        Relahelper.locker = true;
        while (Relahelper.relas == null){



        }

        oldlist = new LinkedList<>();
        copyList(oldlist, Relahelper.relas);

        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i("ParticipantReminder", "TimeTask");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        checkNewParticipants();
                    }
                });
                thread.start();

            }
        }, 3000, 20000);
        return super.onStartCommand(intent, flags, startId);
    }

    private void copyList(LinkedList<Relationship> newList, List<Relationship> origin) {
        for (int i = 0 ; i<origin.size(); i++){
            newList.add(origin.get(i));
        }
    }

    private void checkNewParticipants(){
        LinkedList<Relationship> newRelate = new LinkedList<>();
        copyList(newRelate, Relahelper.relas);

        while (!newRelate.isEmpty() && !oldlist.isEmpty()){
            if (newRelate.contains(oldlist.getFirst())){
                
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
