package hk.ust.cse.comp4521.eventmaker.PostEvent;

import android.app.TimePickerDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import hk.ust.cse.comp4521.eventmaker.Constants;
import hk.ust.cse.comp4521.eventmaker.Event.Event;
import hk.ust.cse.comp4521.eventmaker.Event.Event2;
import hk.ust.cse.comp4521.eventmaker.Event.Event_T;
import hk.ust.cse.comp4521.eventmaker.R;

public class eventSetting extends ActionBarActivity implements View.OnClickListener{
    private TextView startText;
    private TextView endText;
    private TextView startTimeToBeViewed;
    private TextView endTimeToBeViewed;
    private TextView partText;
    private TextView partno;
    private Button changeStart;
    private Button changeEnd;
    private Button changePart;
    private Event evt;
    private String evt_id;
    private String TAG="EVSTE";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_setting2);
        startText= (TextView) findViewById(R.id.startT);
        endText= (TextView) findViewById(R.id.endT);
        startTimeToBeViewed= (TextView) findViewById(R.id.startTime);
        endTimeToBeViewed= (TextView) findViewById(R.id.endTime);
        partText= (TextView) findViewById(R.id.part);
        partno= (TextView) findViewById(R.id.partno);
        changePart= (Button) findViewById(R.id.buttonPart);
        //event menu has to send event id to this activity
        //suppose no nedd to check if event exists
        evt_id=getIntent().getExtras().getString(Constants.eventSetting);
        for(Event ev: Event_T.test){
            if(ev._id.equals(evt_id)){
                evt=ev;
            }
        }
        partno.setText(evt.numOfPart);
        startTimeToBeViewed.setText(evt.starting);
        endTimeToBeViewed.setText(evt.ending);

        changeStart= (Button) findViewById(R.id.buttonStart);
        changeStart.setOnClickListener(this);
        changeEnd= (Button) findViewById(R.id.buttonEnd);
        changeEnd.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_event_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        Calendar mcurrentTime=Calendar.getInstance();
        int hour=mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute=mcurrentTime.get(Calendar.MINUTE);
        final Event_T eventhelper=new Event_T();
        if(v.getId()==R.id.buttonStart){
            TimePickerDialog start=new TimePickerDialog(eventSetting.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String texttobeset=hourOfDay+":"+minute;
                    startTimeToBeViewed.setText(hourOfDay+":"+minute);
                    eventhelper.updateEvent(copyFromEvent(evt),evt_id);
                }
            },hour,minute,false);
            start.setTitle("starting time");
            start.show();
        }
        else if(v.getId()==R.id.buttonEnd){
            TimePickerDialog end=new TimePickerDialog(eventSetting.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String texttobeset=hourOfDay+":"+minute;
                    endTimeToBeViewed.setText(texttobeset);
                    eventhelper.updateEvent(copyFromEvent(evt),evt_id);
                }
            },hour,minute,false);
            end.setTitle("ending time");
            end.show();
        }
        else if(v.getId()==R.id.buttonPart){

        }

    }

    public Event2 copyFromEvent(Event evt){
        Event2 et=new Event2();
        et._ownerid=evt._ownerid;
        et.interest=evt.interest;
        et.longitude=evt.longitude;
        et.latitude=evt.latitude;
        et.numOfPart=evt.numOfPart;
        et.currentTimestamp=evt.currentTimestamp;
        et.locationName=evt.locationName;
        et.starting=evt.starting;
        et.ending=evt.ending;
        Log.i(TAG, "copy all details to event2");
        return et;
    }
}
