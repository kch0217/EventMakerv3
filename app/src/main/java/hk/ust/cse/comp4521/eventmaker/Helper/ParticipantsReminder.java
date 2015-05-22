package hk.ust.cse.comp4521.eventmaker.Helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import hk.ust.cse.comp4521.eventmaker.Constants;
import hk.ust.cse.comp4521.eventmaker.Event.Event;
import hk.ust.cse.comp4521.eventmaker.Event.EventMenu;
import hk.ust.cse.comp4521.eventmaker.Event.Event_T;
import hk.ust.cse.comp4521.eventmaker.R;
import hk.ust.cse.comp4521.eventmaker.Relationship.Relahelper;
import hk.ust.cse.comp4521.eventmaker.Relationship.Relationship;
import hk.ust.cse.comp4521.eventmaker.SearchFrag;

public class ParticipantsReminder extends Service {

    private String eventID;
    private LinkedList<Relationship> oldlist;
    private Relationship relate;
    private List<String> id_delete;
    private List<String> id_add;
    private boolean firstNotification ;
    private boolean foundEvent;
    private String interest;

    private String TAG = "Participants Reminder Service";

    private NotificationManager mNotificationManager;
    private int noteId = 2;

    public ParticipantsReminder() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "started");
        eventID = intent.getStringExtra(Constants.eventId);
        Object lock = new Object();
        Relahelper.lock = lock;
        Relahelper.locker = true;

        while (Relahelper.relas == null){
            synchronized (lock) {
                Log.i("TAG", "Waiting");
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        oldlist = new LinkedList<>();
        copyList(oldlist, Relahelper.relas);
        firstNotification = true;
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Timer timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i("ParticipantReminder", "TimeTask");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        checkeventExist();
                        if (foundEvent) {
                            checkNewParticipants();
                            notifyMenu();
                        }


                    }
                });
                thread.start();

            }
        }, 3000, 20000);
        return super.onStartCommand(intent, flags, startId);
    }

    private void checkeventExist() {
        foundEvent = false;
        for (int i = 0; i< Event_T.test.size(); i++){
            if (Event_T.test.get(i)._id.equals(eventID)){
                foundEvent = true;
                interest = Event_T.test.get(i).interest;
            }
        }
        if (!foundEvent){
            Intent i = new Intent(Constants.signaling).putExtra("Signal", Constants.EventDeleted);
            this.sendBroadcast(i);
            stopSelf();
        }
    }

    private void copyList(LinkedList<Relationship> newList, List<Relationship> origin) {
        for (int i = 0 ; i<origin.size(); i++){
            newList.add(origin.get(i));
        }
    }

    private void checkNewParticipants(){
        LinkedList<Relationship> newRelate = new LinkedList<>();
        copyList(newRelate, Relahelper.relas);
        boolean doneChecking = false;
        id_add = new ArrayList<>();
        id_delete = new ArrayList<>();
        while (!newRelate.isEmpty() && !oldlist.isEmpty() && !doneChecking){
            if (newRelate.contains(oldlist.getFirst())){
                newRelate.remove(oldlist.getFirst());
                oldlist.removeFirst();
            }
            else{
                doneChecking = true;
            }
        }
        if (!newRelate.isEmpty()){
            for (int i = 0; i< newRelate.size(); i++){
                if (newRelate.get(i).roomId.equals(eventID)){
                    id_add.add(newRelate.get(i).userId);
                }
            }
        }
        if (!oldlist.isEmpty()){
            for (int i = 0; i< oldlist.size(); i++){
                if (oldlist.get(i).roomId.equals(eventID)){
                    id_delete.add(oldlist.get(i).userId);
                }
            }
        }
        oldlist.clear();
        copyList(oldlist, Relahelper.relas);


    }

    private void notifyMenu(){
        if (!id_delete.isEmpty() || !id_add.isEmpty()) {
            Intent i = new Intent(Constants.signaling).putExtra("Signal", Constants.personnelChanges);
            this.sendBroadcast(i);
            if (firstNotification){
                firstNotification = false;
                putNotification();
            }
            else
                updateNotification();
        }
    }

    private void putNotification(){

        Bitmap largeIcon;

        largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.playing);

        String textmessage;
        if (!id_add.isEmpty() && !id_delete.isEmpty()){
            textmessage = "There is a personnel change.";
        }
        else if (!id_add.isEmpty()){
            textmessage = "Some peoeple have joined the event.";
        }
        else
            textmessage = "Some people have left the event.";



        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.playing)
                        .setLargeIcon(largeIcon.createScaledBitmap(largeIcon,72,72,false))
                        .setOngoing(true)
                        .setContentTitle(interest)
                        .setContentText(textmessage);

        // Creates an explicit intent for the Activity
        Intent resultIntent = new Intent(this, EventMenu.class);
        resultIntent.putExtra(Constants.eventId, eventID);
        resultIntent.putExtra(Constants.reconnect, 100);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(SearchFrag.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);


        mBuilder.setContentIntent(resultPendingIntent);

        Log.i(TAG, "Service: putNotification()");

        // noteId allows you to update the notification later on.
        // set the service as a foreground service
        startForeground(noteId, mBuilder.build());

    }

    private void updateNotification(){
        Bitmap largeIcon;

        largeIcon = BitmapFactory.decodeResource(getResources(), R.drawable.playing);

        String textmessage;
        if (!id_add.isEmpty() && !id_delete.isEmpty()){
            textmessage = "There is a personnel change.";
        }
        else if (!id_add.isEmpty()){
            textmessage = "Some peoeple have joined the event.";
        }
        else
            textmessage = "Some people have left the event.";



        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.playing)
                        .setLargeIcon(largeIcon.createScaledBitmap(largeIcon, 72, 72, false))
                        .setOngoing(true)
                        .setContentTitle(interest)
                        .setContentText(textmessage)
                        .setSound(alarmSound);

        // Creates an explicit intent for the Activity
        Intent resultIntent = new Intent(this, EventMenu.class);
        resultIntent.putExtra(Constants.eventId, eventID);
        resultIntent.putExtra(Constants.reconnect, 100);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(SearchFrag.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(resultPendingIntent);

        Log.i(TAG, "Service: updateNotification()");

        // noteId allows you to update the notification later on.
        mNotificationManager.notify(noteId, mBuilder.build());

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
