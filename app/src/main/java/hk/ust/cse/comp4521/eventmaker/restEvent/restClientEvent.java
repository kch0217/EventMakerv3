package hk.ust.cse.comp4521.eventmaker.restEvent;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.Executor;

import hk.ust.cse.comp4521.eventmaker.Constants;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by User on 5/4/2015.
 */
public class restClientEvent {
    private static eventApi restClient;
    static{
        setupRestClient();
    }

    private restClientEvent(){}
    public static  eventApi get(){return restClient;}

    private static void setupRestClient(){
        RestAdapter.Builder builder=new RestAdapter.Builder()
                .setEndpoint(Constants.KSERVER_URL)
                .setErrorHandler(new retErrorHandler())
                .setClient(new OkClient(new OkHttpClient()))
                .setLogLevel(RestAdapter.LogLevel.FULL);

        RestAdapter restAdapter=builder.build();
        restClient=restAdapter.create(eventApi.class);
    }

    private static class eventExecutor implements Executor {
        @Override
        public void execute(Runnable command) {

        }
    }
}
