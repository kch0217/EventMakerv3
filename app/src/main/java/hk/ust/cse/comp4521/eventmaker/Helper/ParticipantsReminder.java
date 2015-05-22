package hk.ust.cse.comp4521.eventmaker.Helper;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import hk.ust.cse.comp4521.eventmaker.PostEvent.EventMenu;
import hk.ust.cse.comp4521.eventmaker.Event.Event_T;
import hk.ust.cse.comp4521.eventmaker.R;
import hk.ust.cse.comp4521.eventmaker.Relationship.Relahelper;
import hk.ust.cse.comp4521.eventmaker.Relationship.Relationship;
import hk.ust.cse.comp4521.eventmaker.SearchFrag;
import hk.ust.cse.comp4521.eventmaker.User.UserModel;
import hk.ust.cse.comp4521.eventmaker.User.UserServer;

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
    private Timer timer;
    private BroadcastReceiver mReceiver;

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
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Log.i("ParticipantReminder", "TimeTask");
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        checkeventUserExist();
                        if (foundEvent) {
                            checkNewParticipants();
                            notifyMenu();
                        }


                    }
                });
                thread.start();

            }
        }, 6000, 5000);


        IntentFilter intentFilter = new IntentFilter(Constants.signaling);
        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getIntExtra("Signal", -1) ==Constants.ConnectionError){
                    //stop participants service

                    stopSelf();
                }


            }
        };


        this.registerReceiver(mReceiver, intentFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Stopping the service.");
        timer.cancel();
        mNotificationManager.cancel(noteId);
        this.unregisterReceiver(this.mReceiver);
        super.onDestroy();
    }

    private void checkeventUserExist() {
        foundEvent = false;
        for (int i = 0; i< Relahelper.relas.size(); i++){
            if (Relahelper.relas.get(i).userId.equals(UserServer.returnInfo._id)){
                foundEvent = true;
                break;
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
            Relationship newRe = new Relationship();
            newRe.roomId = origin.get(i).roomId;
            newRe.userId = origin.get(i).userId;
            newRe._id = origin.get(i)._id;
            newList.add(newRe);
        }
    }



    private void checkNewParticipants(){
        LinkedList<Relationship> newRelate = new LinkedList<>();
        copyList(newRelate, Relahelper.relas);
        Log.i("Checkingparticipants", "New size: "+newRelate.size());
        Log.i("Checkingparticipants+",  "Old size: " + oldlist.size());
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

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder mBuilder =
                new Notification.Builder(this)
                        .setSmallIcon(R.drawable.playing)
                        .setLargeIcon(largeIcon.createScaledBitmap(largeIcon,72,72,false))
                        .setOngoing(false)
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
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);


        mBuilder.setContentIntent(resultPendingIntent);

        Log.i(TAG, "Service: putNotification()");

        // noteId allows you to update the notification later on.
        // set the service as a foreground service
        mNotificationManager.notify(noteId, mBuilder.build());

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
                        .setOngoing(false)
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
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_CANCEL_CURRENT);

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
