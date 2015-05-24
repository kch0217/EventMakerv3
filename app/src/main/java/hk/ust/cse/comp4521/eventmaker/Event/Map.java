//COMP4521  Kwok Chung Hin   20111831   chkwokad@ust.hk
//COMP4521  Kwok Tsz Ting 20119118  ttkwok@ust.hk
//COMP4521  Li Lok Him  20103470    lhliab@ust.hk
package hk.ust.cse.comp4521.eventmaker.Event;

import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.Timestamp;

import hk.ust.cse.comp4521.eventmaker.Constants;
import hk.ust.cse.comp4521.eventmaker.Helper.ServerConnection;
import hk.ust.cse.comp4521.eventmaker.PostEvent.EventMenu;
import hk.ust.cse.comp4521.eventmaker.R;
import hk.ust.cse.comp4521.eventmaker.User.UserServer;

public class Map extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener{
    private MapFragment mMapFragment;
    private String TAG="MAP";
    private final int ballcount=1;
    private Marker marker;
    private int num=0;
    Button but;
    Button back;
    private double lat;
    private double lon;
    private double orilat;
    private double orilon;
    private String interest;
    private int mode;//100 =from hin 200=from him
    private ProgressDialog pd;
    private boolean click;

    public Handler handle = new Handler(){
        @Override
        public void handleMessage(Message inputMessage) {
            if (inputMessage.what == Constants.ConnectionError) {
                finish();
            }

        }
    };
    //make use of Google Map v.2 for implementation of map
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "map starting");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //set the type of google map
        GoogleMapOptions options=new GoogleMapOptions();
        options.mapType(GoogleMap.MAP_TYPE_NORMAL)
                .compassEnabled(true);


        mMapFragment = MapFragment.newInstance(options);
        FragmentTransaction fragmentTransaction =
                getFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
        but= (Button) findViewById(R.id.submit);
        but.setVisibility(View.INVISIBLE);
        back= (Button) findViewById(R.id.back);
        back.setVisibility(View.INVISIBLE);

        Intent receive=getIntent();
        if(receive!=null){
            //if intent is received, there should be at least three attributes
            //latitude,longitude and the mode denoting where the intent is from
            mode=receive.getExtras().getInt(Constants.eventCode,0);
            orilat=receive.getExtras().getDouble("lat", 0);
            orilon=receive.getExtras().getDouble("lon",0);
            interest = receive.getExtras().getString("Interest", "");
            if(mode==100){
                //the map is used to create new event
                interest=receive.getExtras().getString("Interest", null);
                Log.i(TAG,"from search");
                // 1. Instantiate an AlertDialog.Builder with its constructor
                AlertDialog.Builder builder = new AlertDialog.Builder(Map.this);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage("A new event has been created for you. Please select a location for it by putting a marker on the map.")
                        .setTitle("Instruction")
                        .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {


                            }
                        });

                // 3. Get the AlertDialog from create()
                builder.create().show();
            }
            else if(mode==200){
                //from event menu for users to check the location on the map
                Log.i(TAG, "receive from event");

            }
            else{
                //basically should not happen
                Log.i(TAG,"something goes wrong");
            }
        }
        click = false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        //things could only be done after the map is ready
        Log.i(TAG,"map ready");
        googleMap.setMyLocationEnabled(true);
        if(mode==100){
            //submit button is available
            but.setVisibility(View.VISIBLE);
        }
        //set the map center as the location provided. Location could be
        //user's location or event's location
        LatLng mylocation=new LatLng(orilat,orilon);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 16));
        if(mode==200){
            googleMap.addMarker(new MarkerOptions()
                            .position(mylocation)
                            .title("destination")
            );
            back.setVisibility(View.VISIBLE);
            //back button is available for going back to event menu
        }

        but.setOnClickListener(this);
        back.setOnClickListener(this);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(mode==100) {
                    Log.i(TAG, "clicked");
                    //only allow one marker for event location
                    if (num < ballcount) {
                        num++;
                        marker = googleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title("destination")
                        );
                        lat = latLng.latitude;
                        lon = latLng.longitude;
                    } else {
                        marker.remove();
                        marker = googleMap.addMarker(new MarkerOptions()
                                        .position(latLng)
                                        .title("destination")
                        );
                        lat = latLng.latitude;
                        lon = latLng.longitude;
                    }
                }
                else{
                    Log.i(TAG,"clciked in mode 200");
                }
                click = true;
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.submit){
            Event2 eventToSubmit=new Event2();
            if (click == false && mode ==100) {
                //user has not chosen the event location
                Toast.makeText(Map.this, "Please select a venue for the event to take place!", Toast.LENGTH_SHORT).show();
                return;
            }
            eventToSubmit._ownerid= UserServer.returnInfo._id;
            Log.i(TAG,eventToSubmit._ownerid);
            eventToSubmit.latitude=lat;
            eventToSubmit.longitude=lon;
            Timestamp eventtime=eventToSubmit.currentTimestamp;
            eventToSubmit.interest = interest;
            pd = ProgressDialog.show(Map.this,"Network Access", "Connecting to the server", true);
            ServerConnection serverConn = new ServerConnection(Map.this, handle);
            serverConn.run(); //test network connection
            pd.dismiss();

            while (UserServer.connectionState ==null){

            }

            if (UserServer.connectionState == false){
                Toast.makeText(Map.this, "No Internet access", Toast.LENGTH_SHORT);
                return;
            }
            Event_T eventhelper=new Event_T();
            eventhelper.createEvent(eventToSubmit);
            boolean find=false;
            //getting back the id just created
            String _id=null;
            ProgressDialog dialog=ProgressDialog.show(Map.this,"loading","Please wait...",true);
            Log.i(TAG,"the id");
            while(find!=true){
                for(int i=0;i<Event_T.test.size();i++){
                    int year = eventtime.getYear();
                    int month = eventtime.getMonth();
                    int date = eventtime.getDate();
                    int hour = eventtime.getHours();
                    int min = eventtime.getMinutes();
                    int second=eventtime.getSeconds();
                    if (year==Event_T.test.get(i).currentTimestamp.getYear() && month == Event_T.test.get(i).currentTimestamp.getMonth()&&
                            date == Event_T.test.get(i).currentTimestamp.getDate()&& hour==Event_T.test.get(i).currentTimestamp.getHours()&&
                            min==Event_T.test.get(i).currentTimestamp.getMinutes() && (second==Event_T.test.get(i).currentTimestamp.getSeconds()
                    ||second==Event_T.test.get(i).currentTimestamp.getSeconds()+1 ||second==Event_T.test.get(i).currentTimestamp.getSeconds()-1)){
                        find=true;
                        dialog.dismiss();
                        _id=Event_T.test.get(i)._id;
                    }

//                    if(eventtime.compareTo(Event_T.test.get(i).currentTimestamp)==0){
//                        find=true;
//                        dialog.dismiss();
//                        _id=Event_T.test.get(i)._id;
//                    }
                }
            }

            Log.i(TAG, "pass through id"+ _id);
            Intent startEvent=new Intent(Map.this,EventMenu.class);
            startEvent.putExtra(Constants.eventId, _id);
            startEvent.putExtra(Constants.reconnect,200);
            //used uckor reconnection only
            Log.i(TAG, "Going to event menu");
            startEvent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startEvent);
            finish();
        }
        else if(v.getId()==R.id.back){
            finish();
        }
    }
}
