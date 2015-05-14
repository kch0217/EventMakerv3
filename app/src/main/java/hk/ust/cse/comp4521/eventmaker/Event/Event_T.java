package hk.ust.cse.comp4521.eventmaker.Event;

import android.location.Location;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.comp4521.eventmaker.restEvent.restClientEvent;
import retrofit.Callback;
import retrofit.RetrofitError;

/**
 * Created by User on 5/4/2015.
 */
public class Event_T{
    String TAG="VZXYZ";
    public static List<Event> test;
    public static Event returnevt;
    public static String testid;
    public static Event2 eventCreated;
    public Event_T(){}

    public static double locationDetection(Location one, Location two){
        double latdif=one.getLatitude()-two.getLatitude();
        double longdif=one.getLongitude()-two.getLongitude();
        double result=Math.sqrt(latdif*latdif+longdif*longdif);
        return result;
    };
    public void createEvent(final Event2 evt){
        restClientEvent.get().addEvent(evt, new Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                Log.i(TAG, "Add Response is " + response.getBody().toString());
                eventCreated=evt;
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG,"cannot conect"+retrofitError.getKind().name());
            }
        });
        getAllEvent();
    }

    public void getAllEvent(){

        restClientEvent.get().getEvents(new Callback<ArrayList<Event>>() {
            @Override
            public void success(ArrayList<Event> events, retrofit.client.Response response) {
                test = events;
                if (test!=null) {
                    for (int i = 0; i < test.size(); i++) {
                        Log.i(TAG, test.get(i)._id);
                    }
                }
                else{
                    test=new ArrayList<Event>();
                }
            }
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG, "youknowwhat"+retrofitError.getKind().name());
                test=new ArrayList<Event>();
            }
        });
    }

    public void updateEvent(Event2 evt, final String id){
        restClientEvent.get().updateEvent(evt, id, new Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                Log.i(TAG, "update successfully on" + id);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG, "failed update");
            }
        });
        getAllEvent();
    }

    public void deleteEvent(String id){
        restClientEvent.get().deleteEvent(id, new Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                Log.i(TAG, "delete ok");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG, "delete fail");
            }
        });
        getAllEvent();
    }

    public void getEvent(String id){
        restClientEvent.get().getEvent(id, new Callback<Event>() {
            @Override
            public void success(Event event, retrofit.client.Response response) {
                Log.i(TAG,"get event ok"+event._id);
                returnevt=event;
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG,"FAIL get");
            }
        });
        getAllEvent();
    }


}
