package hk.ust.cse.comp4521.eventmaker.Event;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import hk.ust.cse.comp4521.eventmaker.R;

/**
 * Created by LiLokHim on 14/5/15.
 */
public class EventMenu extends Activity {
    private String TAG = "eventMenu";
    private Event event = null;
    private String event_id;
    private int check_interval=30000;

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

        /************************UI init*******************/



        Intent intent = getIntent();
//        event_id = intent.getExtras().getString();
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

        }
    }

//    public List<UserInfo> get_users_in_event()
//    {
//        List<UserInfo> allUsers= UserServer.UserInfoArrayList;
//
//
//
//    }

    //check the event from test and update the event periodically, also call update_info every time
    public class get_my_event extends Thread {
        public void run() {
            List<Event> allEvents=Event_T.test;

            for(Event events:allEvents)
            {
                if(events._id==event_id){
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


}