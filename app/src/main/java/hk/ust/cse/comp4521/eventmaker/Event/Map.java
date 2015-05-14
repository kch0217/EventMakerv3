package hk.ust.cse.comp4521.eventmaker.Event;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import hk.ust.cse.comp4521.eventmaker.Constants;
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
        but.setVisibility(View.VISIBLE);
        but.setOnClickListener(this);
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
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
        });
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.submit){
            Event2 eventToSubmit=new Event2();
            eventToSubmit._ownerid= UserServer.returnInfo._id;
            eventToSubmit.latitude=lat;
            eventToSubmit.longitude=lon;
            Event_T eventhelper=new Event_T();
            eventhelper.createEvent(eventToSubmit);
            Intent startEvent=new Intent(Map.this,EventMenu.class);

            startActivity(startEvent);
            finish();
        }
    }
}
