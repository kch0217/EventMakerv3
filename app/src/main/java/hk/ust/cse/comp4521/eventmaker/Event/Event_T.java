//COMP4521  Kwok Chung Hin   20111831   chkwokad@ust.hk
//COMP4521  Kwok Tsz Ting 20119118  ttkwok@ust.hk
//COMP4521  Li Lok Him  20103470    lhliab@ust.hk
package hk.ust.cse.comp4521.eventmaker.Event;


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

    public static Object lock;
    public static boolean locker = false;


    //RESTFUL api communication between server and application for events using retrofit
    public void createEvent(final Event2 evt){
        restClientEvent.get().addEvent(evt, new Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                Log.i(TAG, "Add Response is " + response.getBody().toString());
                eventCreated = evt;
                getAllEvent();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG, "cannot conect" + retrofitError.getKind().name());
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
                Log.i(TAG,test.size()+"");
                if (locker)
                synchronized (lock){
                    lock.notify();
                }
            }
            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG, "youknowwhat"+retrofitError.getKind().name());
                test=new ArrayList<Event>();
                if (locker)
                    synchronized (lock){
                        lock.notify();
                    }
            }
        });
    }

    public void updateEvent(Event2 evt, final String id){
        restClientEvent.get().updateEvent(evt, id, new Callback<retrofit.client.Response>() {
            @Override
            public void success(retrofit.client.Response response, retrofit.client.Response response2) {
                Log.i(TAG, "update successfully on" + id);
                getAllEvent();
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
                getAllEvent();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG, "delete fail"+retrofitError.getKind().name());
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
                getAllEvent();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG,"FAIL get"+retrofitError.getKind().name());
            }
        });
        getAllEvent();
    }


}
