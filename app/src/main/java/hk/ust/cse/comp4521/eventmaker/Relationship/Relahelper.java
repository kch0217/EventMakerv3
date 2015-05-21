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
    public static Relationship relcreated;

    public void createRelationship(final Relationship rel){
        restRelationship.get().addRelationship(rel, new Callback<Relationship>() {
            @Override
            public void success(Relationship relationship, Response response) {
                Log.i(TAG, "Add Response is " + response.getBody().toString());
                relcreated=rel;
                getAllRelationship();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG,"createion fails"+retrofitError.getKind().name());
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
                        Log.i(TAG, relas.get(i).eventId + " " + relas.get(i).userId);
                    }
                } else {
                    relas = new ArrayList<Relationship>();
                }
                Log.i(TAG, "fetched all relationship");
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                Log.i(TAG, "why!" + retrofitError.getKind().name());
            }
        });
    }

}
