package hk.ust.cse.comp4521.eventmaker.Event;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.comp4521.eventmaker.Constants;
import hk.ust.cse.comp4521.eventmaker.Helper.ActivityRefresh;
import hk.ust.cse.comp4521.eventmaker.Helper.ServerConnection;
import hk.ust.cse.comp4521.eventmaker.R;
import hk.ust.cse.comp4521.eventmaker.Relationship.Relahelper;
import hk.ust.cse.comp4521.eventmaker.Relationship.Relationship;
import hk.ust.cse.comp4521.eventmaker.Relationship.Relationship2;
import hk.ust.cse.comp4521.eventmaker.User.UserInfo;
import hk.ust.cse.comp4521.eventmaker.User.UserModel;
import hk.ust.cse.comp4521.eventmaker.User.UserServer;

/**
 * Created by LiLokHim on 14/5/15.
 */
public class EventMenu extends Activity {
    private String TAG = "eventMenu";
    private Event event = null;
    private String event_id;
    private int check_interval=30000;
    private List<Relationship> roommemlist;

    /*********UI***************/
    private TextView event_name;
    private Button user1_button;
    private Button user2_button;
    private Button user3_button;
    private Button user4_button;
    private TextView location_text;
    private TextView time_text;
    private Button location_button;
    private Button setting;

    private BroadcastReceiver mReceiver;
    private ServiceConnection serverConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        /************************UI init*******************/
        event_name =(TextView)findViewById(R.id.event_name);
        location_text =(TextView)findViewById(R.id.location_text);
        time_text =(TextView)findViewById(R.id.time_text);
        user1_button=(Button)findViewById(R.id.user1_button);
        user2_button=(Button)findViewById(R.id.user2_button);
        user3_button=(Button)findViewById(R.id.user3_button);
        user4_button=(Button)findViewById(R.id.user4_button);
        location_button=(Button)findViewById(R.id.location_button);
        setting=(Button)findViewById(R.id.setting);
        user2_button.setVisibility(View.INVISIBLE);
        user3_button.setVisibility(View.INVISIBLE);
        user4_button.setVisibility(View.INVISIBLE);
        user1_button.setOnClickListener(new pressButton());
        user2_button.setOnClickListener(new pressButton());
        user3_button.setOnClickListener(new pressButton());
        user4_button.setOnClickListener(new pressButton());
        location_button.setOnClickListener(new pressButton());
        /************************UI init*******************/



        Intent intent = getIntent();
        event_id = intent.getStringExtra(Constants.eventId);
        UserModel usm=UserModel.getUserModel();
        usm.saveEventId(event_id);

        Intent refresh = new Intent(EventMenu.this, ActivityRefresh.class);
        serverConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(refresh, serverConnection, Context.BIND_AUTO_CREATE);
        //deal with relationship
        Relahelper relahelper=new Relahelper();
        relahelper.getAllRelationship();
        Relationship2 newrela=new Relationship2();
        newrela.roomId=event_id;
        newrela.userId=UserServer.returnInfo._id;

        relahelper.createRelationship(newrela);

        Thread get_event_thread=new get_my_event();
        get_event_thread.start();

    }


    //update the info of UI
    public void update_info()  {

        if(event!=null)
        {
            event_name.setText(event.interest);
            time_text.setText(event.starting+"-"+event.ending);
            location_text.setText(event.locationName);
            List<UserInfo> event_users=get_users_in_event();

            for(int i=0;i<event_users.size();i++)
            {
                user1_button.setVisibility(View.INVISIBLE);
                user2_button.setVisibility(View.INVISIBLE);
                user3_button.setVisibility(View.INVISIBLE);
                user4_button.setVisibility(View.INVISIBLE);


                if(i==0)
                {
                    user1_button.setVisibility(View.VISIBLE);
                    user1_button.setText(event_users.get(i).Name);
                }
                else if(i==1)
                {
                    user2_button.setVisibility(View.VISIBLE);

                    user2_button.setText(event_users.get(i).Name);

                }
                else if(i==2)
                {
                    user3_button.setVisibility(View.VISIBLE);

                    user3_button.setText(event_users.get(i).Name);

                }
                else if(i==3)
                {
                    user4_button.setVisibility(View.VISIBLE);

                    user4_button.setText(event_users.get(i).Name);

                }
            }
        }
    }


    //Get all the users from the event
    public List<UserInfo> get_users_in_event()
    {
        List<UserInfo> allUsers= UserServer.UserInfoArrayList;
        List<UserInfo> ret_list=new ArrayList<UserInfo>();

        //find the owner info
        for(UserInfo user:allUsers) {
            if (event._ownerid.equals(user._id))
            {
                ret_list.add(user);
                break;
            }
        }
        //find the others


        return ret_list;

    }

    //check the event from test and update the event periodically, also call update_info every time
    public class get_my_event extends Thread {
        public void run() {
            List<Event> allEvents=Event_T.test;

            for(Event events:allEvents)
            {
                if(events._id.equals(event_id)){
                event=events;
                    update_info();
                    break;
                }

            }
            try {
                Thread.sleep(check_interval);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public class update_rela implements Runnable{

        @Override
        public void run() {

        }
    }


    public class pressButton implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            if (view.getId() == R.id.user1_button) {
            }
            else if(view.getId()==R.id.user2_button){

            }
            else if(view.getId()==R.id.user3_button){

            }
            else if(view.getId()==R.id.user4_button){

            }
            else if(view.getId()==R.id.location_button){
                Intent intent = new Intent(getApplicationContext(), Map.class);
                intent.putExtra("lat",event.latitude);
                intent.putExtra("lon",event.longitude);
                intent.putExtra(Constants.eventCode,200);
                startActivity(intent);

            }
        }
    }




    @Override
    protected void onResume() {
        super.onResume();

        IntentFilter intentFilter = new IntentFilter(Constants.signaling);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getIntExtra("Signal", -1) ==Constants.ConnectionError){
                    finish();
                }
                else if (intent.getIntExtra("Signal", -1) ==Constants.EventDeleted){
                    finish();
                }

            }
        };

        this.registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregister our receiver
        this.unregisterReceiver(this.mReceiver);
        unbindService(serverConnection);
    }


}