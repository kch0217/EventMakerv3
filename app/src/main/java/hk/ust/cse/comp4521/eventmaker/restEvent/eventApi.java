//COMP4521  Kwok Chung Hin   20111831   chkwokad@ust.hk
//COMP4521  Kwok Tsz Ting 20119118  ttkwok@ust.hk
//COMP4521  Li Lok Him  20103470    lhliab@ust.hk
package hk.ust.cse.comp4521.eventmaker.restEvent;

import java.util.ArrayList;

import hk.ust.cse.comp4521.eventmaker.Event.Event;
import hk.ust.cse.comp4521.eventmaker.Event.Event2;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.DELETE;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Path;

/**
 * Created by User on 5/4/2015.
 */
public interface eventApi {
    //api for Retrofit
    //
    @GET("/events")
    void getEvents(Callback<ArrayList<Event>> callback);

    //
    @GET("/events/{id}")
    void getEvent(@Path("id") String id, Callback<Event> callback);

    //
    @DELETE("/events/{id}")
    void deleteEvent(@Path("id") String id, Callback<Response> callback);

    //
    @POST("/events")
    void addEvent(@Body Event2 event, Callback<Response> callback);

    //
    @PUT("/events/{id}")
    void updateEvent(@Body Event2 event, @Path("id") String id, Callback<Response> callback);
}
