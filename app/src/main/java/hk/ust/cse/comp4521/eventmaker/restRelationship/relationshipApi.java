package hk.ust.cse.comp4521.eventmaker.restRelationship;

import java.util.ArrayList;

import hk.ust.cse.comp4521.eventmaker.Event.Event;
import hk.ust.cse.comp4521.eventmaker.Event.Event2;
import hk.ust.cse.comp4521.eventmaker.Relationship.Relationship;
import hk.ust.cse.comp4521.eventmaker.Relationship.Relationship2;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by User on 5/21/2015.
 */
public interface relationshipApi {
    @GET("/relationship")
    void getRelationships(Callback<ArrayList<Relationship>> callback);

    //
    @GET("/relationship/{id}")
    void getRelationship(@Path("id") String id, Callback<Relationship> callback);

    //
    @DELETE("/relationship/{id}")
    void deleteRelationship(@Path("id") String id, Callback<Response> callback);

    //
    @POST("/relationship")
    void addRelationship(@Body Relationship2 event, Callback<Response> callback);

    //
    @PUT("/relationship/{id}")
    void updateRelationship(@Body Relationship2 event, @Path("id") String id, Callback<Response> callback);

}
