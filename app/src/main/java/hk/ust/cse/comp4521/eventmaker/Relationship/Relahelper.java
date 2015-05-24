//COMP4521  Kwok Chung Hin   20111831   chkwokad@ust.hk
//COMP4521  Kwok Tsz Ting 20119118  ttkwok@ust.hk
//COMP4521  Li Lok Him  20103470    lhliab@ust.hk
package hk.ust.cse.comp4521.eventmaker.Relationship;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import hk.ust.cse.comp4521.eventmaker.restRelationship.restRelationship;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by User on 5/21/2015.
 */
public class Relahelper {
    String TAG="RELAHELPER";
    public static List<Relationship> relas;
    public static Relationship2 relcreated;
    public static Relationship returnrel;

    public static Object lock;
    public static boolean locker = false;

    //RESTFUL api communcation between server and application for relationship using Retrofit
    public void createRelationship(final Relationship2 rel){
        restRelationship.get().addRelationship(rel, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.i(TAG,"create rel success");
                relcreated=rel;
                getAllRelationship();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG,"create rel fails"+retrofitError.getKind().name());
            }
        });
        getAllRelationship();
    }

    public void getAllRelationship(){
        restRelationship.get().getRelationships(new Callback<ArrayList<Relationship>>() {
            @Override
            public void success(ArrayList<Relationship> relationships, Response response) {
                relas = relationships;
                if (relas != null) {
                    for (int i = 0; i < relas.size(); i++) {
                        Log.i(TAG, relas.get(i).roomId + " " + relas.get(i).userId+"curry"+relas.get(i)._id);
                    }
                } else {
                    relas = new ArrayList<Relationship>();
                }
                Log.i(TAG, "fetched all relationship");
                if (locker){
                    synchronized (lock){
                        lock.notify();
                    }
                    locker = false;
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG, "why!" + retrofitError.getKind().name());
                relas = new ArrayList<Relationship>();
                if (locker){
                    synchronized (lock){
                        lock.notify();
                    }
                    locker = false;
                }
            }
        });
    }

    public void updateRelationship(Relationship2 rel, final String _id){
        restRelationship.get().updateRelationship(rel, _id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.i(TAG,"update rel successfully"+_id);
                getAllRelationship();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG,"fail rel update"+retrofitError.getKind().name());
            }
        });
        getAllRelationship();
    }

    public void deleteRelationship(final String id){
        restRelationship.get().deleteRelationship(id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Log.i(TAG, "delete rel successfully");
                getAllRelationship();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG, "delete rel fail"+retrofitError.getKind().name()+id);
            }
        });
        getAllRelationship();
    }

    public void getRelationship(String id){
        restRelationship.get().getRelationship(id, new Callback<Relationship>() {
            @Override
            public void success(Relationship relationship, Response response) {
                Log.i(TAG,"get rel success");
                returnrel=relationship;
                getAllRelationship();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG, "fail rel get"+retrofitError.getKind().name());
            }
        });
        getAllRelationship();
    }
}
