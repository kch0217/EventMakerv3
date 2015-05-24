package hk.ust.cse.comp4521.eventmaker.restRelationship;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import hk.ust.cse.comp4521.eventmaker.Constants;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by User on 5/21/2015.
 */
public class restRelationship {
    private static relationshipApi restClient;
    static{
        setupRestClient();
    }

    private restRelationship(){}
    public static  relationshipApi get(){return restClient;}
    private static void setupRestClient(){
        // create http client and set timeout
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(3, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(3, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(3, TimeUnit.SECONDS);

        RestAdapter.Builder builder=new RestAdapter.Builder()
                .setEndpoint(Constants.KSERVER_URL)
                .setErrorHandler(new relationshipErrorHandler())
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setExecutors(new eventExecutor(), new eventExecutor());

        RestAdapter restAdapter=builder.build();
        restClient=restAdapter.create(relationshipApi.class);
    }
    //run restful api on other threads
    private static class eventExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            new Thread(command).start();
        }
    }
}
