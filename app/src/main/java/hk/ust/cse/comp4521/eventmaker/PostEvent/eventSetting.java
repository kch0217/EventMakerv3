package hk.ust.cse.comp4521.eventmaker.PostEvent;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

import hk.ust.cse.comp4521.eventmaker.Constants;
import hk.ust.cse.comp4521.eventmaker.Event.Event;
import hk.ust.cse.comp4521.eventmaker.Event.Event2;
import hk.ust.cse.comp4521.eventmaker.Event.Event_T;
import hk.ust.cse.comp4521.eventmaker.R;

public class eventSetting extends Activity implements View.OnClickListener{
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
    private EditText editText;
    private TextView title;
    private int mode;//100=owner 200=non

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
        //suppose no need to check if event exists
        evt_id=getIntent().getExtras().getString(Constants.eventSetting);
        for(Event ev: Event_T.test){
            if(ev._id.equals(evt_id)){
                evt=ev;
            }
        }
        title= (TextView) findViewById(R.id.title);
        title.setText(evt.interest+" event");
        editText=new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);
        partno.setText(Integer.toString(evt.numOfPart));
        startTimeToBeViewed.setText(evt.starting);
        endTimeToBeViewed.setText(evt.ending);

        changeStart= (Button) findViewById(R.id.buttonStart);
        changeStart.setOnClickListener(this);
        changeEnd= (Button) findViewById(R.id.buttonEnd);
        changeEnd.setOnClickListener(this);
        changePart.setOnClickListener(this);
        mode=getIntent().getExtras().getInt(Constants.eventSettingType,0);
        //100=owner 200=non owner
        if(mode==200){
            //only the owner could change the setting
            changePart.setVisibility(View.INVISIBLE);
            changeEnd.setVisibility(View.INVISIBLE);
            changeStart.setVisibility(View.INVISIBLE);
            Log.i(TAG,"hiding the buttons");
        }
        else if(mode==100){
            Log.i(TAG,"ownertype");
        }
        else{
            Log.i(TAG,"neither owner/non-owner, fail to fetch value");
        }
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
            //allow user to choose time on timepickerdialog
            TimePickerDialog start=new TimePickerDialog(eventSetting.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String texttobeset=hourOfDay+":"+minute;
                    startTimeToBeViewed.setText(hourOfDay+":"+minute);
                    evt.starting=texttobeset;
                    Log.i(TAG, "start" + evt.starting);
                    Log.i(TAG,"end"+evt.ending);
                    Log.i(TAG,"part"+evt.numOfPart);
                    Event2 tobeupload=copyFromEvent(evt);
                    tobeupload.starting=texttobeset;
                    eventhelper.updateEvent(tobeupload,evt_id);
                    //update the event on database whenever changes are made
                }
            },hour,minute,false);
            start.setTitle("starting time");
            start.show();
        }
        else if(v.getId()==R.id.buttonEnd){
            //allow user to choose time on timepickerdialog
            TimePickerDialog end=new TimePickerDialog(eventSetting.this, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    String texttobeset=hourOfDay+":"+minute;
                    endTimeToBeViewed.setText(texttobeset);
                    evt.ending=texttobeset;
                    Log.i(TAG,"start"+evt.starting);
                    Log.i(TAG,"end"+evt.ending);
                    Log.i(TAG,"part"+evt.numOfPart);
                    Event2 tobeupload=copyFromEvent(evt);
                    tobeupload.ending=texttobeset;
                    eventhelper.updateEvent(tobeupload,evt_id);
                    //update the event on database whenever changes are made
                }
            },hour,minute,false);
            end.setTitle("ending time");
            end.show();
        }
        else if(v.getId()==R.id.buttonPart){
            //create an alertdialog which uses an edittext as view
            AlertDialog.Builder builder=new AlertDialog.Builder(eventSetting.this);
            Log.i(TAG,"trying to set the builder");
            final EditText edt=new EditText(this);
            edt.setInputType(InputType.TYPE_CLASS_NUMBER);
            builder.setTitle("participants")
                    .setView(edt)
                    .setMessage("enter number of participants")
                    .setPositiveButton("done", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            partno.setText(edt.getText().toString());
                            evt.numOfPart=Integer.parseInt(edt.getText().toString());
                            Log.i(TAG,"start"+evt.starting);
                            Log.i(TAG, "end" + evt.ending);
                            Log.i(TAG,"part"+evt.numOfPart);
                            Event2 et=copyFromEvent(evt);
                            et.numOfPart=Integer.parseInt(edt.getText().toString());
                            eventhelper.updateEvent(et,evt_id);
                            //update the event on database whenever changes are made
                            dialog.dismiss();

                        }
                    })
                    .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            dialog.dismiss();
                            ViewGroup test= (ViewGroup) edt.getParent();
                            test.removeView(edt);

                        }
                    });
            final AlertDialog alertDialog=builder.create();
            alertDialog.show();
//            builder.show();

        }

    }

    public Event2 copyFromEvent(Event evt){
        //helper function
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
