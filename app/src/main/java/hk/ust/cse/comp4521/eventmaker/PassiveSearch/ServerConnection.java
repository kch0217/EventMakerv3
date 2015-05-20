package hk.ust.cse.comp4521.eventmaker.PassiveSearch;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;


import java.util.Objects;

import hk.ust.cse.comp4521.eventmaker.Constants;
import hk.ust.cse.comp4521.eventmaker.User.UserServer;
import hk.ust.cse.comp4521.eventmaker.restForUser.RestClient;

/**
 * Created by Ken on 20/5/2015.
 */
public class ServerConnection implements Runnable{

    Context currentStage;
    Handler handle;
    Object lock;

    public ServerConnection(Context in, Handler h) {
        currentStage = in;
        handle = h;
    }

//    public ServerConnection() {
//        currentStage = null;
//    }

    @Override
    public void run() {


        final UserServer userServer = new UserServer();
        lock = new Object();
        userServer.lock = lock;
        userServer.connectionState = null;

        userServer.updateInternalState();



        while (userServer.connectionState ==null){
            synchronized (lock){
                try {
                    Log.i("ConnectionWait", "Waiting");
                    lock.wait();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        if (userServer.connectionState != null && userServer.connectionState == false){
            // 1. Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(currentStage);

            // 2. Chain together various setter methods to set the dialog characteristics
            builder.setMessage("Cannot connect to the server!!")
                    .setTitle("Error")
                    .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if (handle != null) {
                                Message msg = new Message();
                                msg.what = Constants.ConnectionError;
                                handle.sendMessage(msg);
                            }

                        }
                    });

            // 3. Get the AlertDialog from create()
            builder.create().show();
        }

    }
}
