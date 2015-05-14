package hk.ust.cse.comp4521.eventmaker.Event;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.SyncStateContract;
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
import hk.ust.cse.comp4521.eventmaker.PassiveSearch.SearchHelper;
import hk.ust.cse.comp4521.eventmaker.R;
import hk.ust.cse.comp4521.eventmaker.User.UserServer;

public class Map extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener{
    private MapFragment mMapFragment;
    private String TAG="MAP";
    private final int ballcount=1;
    private Marker marker;
    private int num=0;
    Button but;

    private double lat;
    private double lon;
    private double orilat;
    private double orilon;
    private String interest;
    private int mode;//100 =from hin 200=from him

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
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
        Intent receive=getIntent();

        if(receive!=null){
            mode=receive.getExtras().getInt(Constants.eventCode,0);
            orilat=receive.getExtras().getDouble("lat",0);
            orilon=receive.getExtras().getDouble("lon",0);
            if(mode==100){
                interest=receive.getExtras().getString("Interest", null);
                Log.i(TAG,"from search");
            }
            else if(mode==200){
                Log.i(TAG,"receive from event");
            }
            else{
                Log.i(TAG,"something goes wrong");
            }
        }
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        if(mode==100){
            but.setVisibility(View.VISIBLE);
        }
        LatLng mylocation=new LatLng(orilat,orilon);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(mylocation, 16));
        if(mode==200){
            googleMap.addMarker(new MarkerOptions()
                            .position(mylocation)
                            .title("destination")
            );
        }

        but.setOnClickListener(this);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(mode==100) {
                    Log.i(TAG, "clicked");
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

                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.submit){
            Event2 eventToSubmit=new Event2();
            eventToSubmit._ownerid= UserServer.returnInfo._id;
            eventToSubmit.latitude=lat;
            eventToSubmit.longitude=lon;
            Timestamp eventtime=eventToSubmit.currentTimestamp;
            Event_T eventhelper=new Event_T();
            eventhelper.createEvent(eventToSubmit);
            boolean find=false;
            //id
            String _id=null;
            ProgressDialog dialog=ProgressDialog.show(Map.this,"loading","Please wait...",true);

            while(find!=true){
                for(int i=0;i<Event_T.test.size();i++){
                    if(eventtime.compareTo(Event_T.test.get(i).currentTimestamp)==0){
                        find=true;
                        dialog.dismiss();
                        _id=Event_T.test.get(i)._id;
                    }
                }
            }
            Log.i(TAG,"pass through fucking id");
            Intent startEvent=new Intent(Map.this,EventMenu.class);
            startActivity(startEvent);
            finish();
        }
    }
}
