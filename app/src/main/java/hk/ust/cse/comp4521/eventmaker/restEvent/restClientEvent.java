//COMP4521  Kwok Chung Hin   20111831   chkwokad@ust.hk
//COMP4521  Kwok Tsz Ting 20119118  ttkwok@ust.hk
//COMP4521  Li Lok Him  20103470    lhliab@ust.hk
package hk.ust.cse.comp4521.eventmaker.restEvent;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

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
        // create http client and set timeout
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(3, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(3, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(3, TimeUnit.SECONDS);

        RestAdapter.Builder builder=new RestAdapter.Builder()
                .setEndpoint(Constants.KSERVER_URL)
                .setErrorHandler(new retErrorHandler())
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setExecutors(new eventExecutor(),new eventExecutor());

        RestAdapter restAdapter=builder.build();
        restClient=restAdapter.create(eventApi.class);
    }
       //run restful api on other threads
    private static class eventExecutor implements Executor {
        @Override
        public void execute(Runnable command) {
            new Thread(command).start();
        }
    }
}
