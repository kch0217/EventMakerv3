//COMP4521  Kwok Chung Hin   20111831   chkwokad@ust.hk
//COMP4521  Kwok Tsz Ting 20119118  ttkwok@ust.hk
//COMP4521  Li Lok Him  20103470    lhliab@ust.hk
package hk.ust.cse.comp4521.eventmaker.restForUser;

import android.util.Log;

import com.squareup.okhttp.OkHttpClient;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import hk.ust.cse.comp4521.eventmaker.Constants;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Ken on 25/4/2015.
 */
public class RestClient {
    private static userServerAPI restClient;


    static {
        setupRestClient();
    }

    public static void restart(){
        restClient = null;
        setupRestClient();
    }

    private RestClient() {}

    public static userServerAPI get() {
        return restClient;
    }

    private static void setupRestClient() {

        // create http client and set timeout
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(2, TimeUnit.SECONDS);
        okHttpClient.setConnectTimeout(2, TimeUnit.SECONDS);
        okHttpClient.setWriteTimeout(2, TimeUnit.SECONDS);

        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(Constants.SERVER_URL)
                .setErrorHandler(new RetrofitErrorHandler())
                .setClient(new OkClient(okHttpClient))
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setExecutors(new executeRest(), new executeRest()) ; //use new thread to execute the network operations

        RestAdapter restAdapter = builder.build();
        restClient = restAdapter.create(userServerAPI.class);
    }

    private static class executeRest implements Executor{

        @Override
        public void execute(Runnable runnable) {

            Log.i("RestClient", "Using new thread to execute network...");
            new Thread(runnable).start();
        }
    }


}
