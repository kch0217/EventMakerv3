//COMP4521  Kwok Chung Hin   20111831   chkwokad@ust.hk
//COMP4521  Kwok Tsz Ting 20119118  ttkwok@ust.hk
//COMP4521  Li Lok Him  20103470    lhliab@ust.hk
package hk.ust.cse.comp4521.eventmaker.restRelationship;

import java.net.ConnectException;
import java.net.SocketTimeoutException;

import retrofit.RetrofitError;

/**
 * Created by User on 5/21/2015.
 */
public class relationshipErrorHandler implements  retrofit.ErrorHandler{
    @Override
    public Throwable handleError(RetrofitError cause) {

//        if (cause.isNetworkError()) {
//            if(cause.getMessage().contains("authentication")){
//                //401 errors
//                return  new Exception("Invalid credentials. Please verify login info.");
//            }else if (cause.getCause() instanceof SocketTimeoutException) {
//                //Socket Timeout
//                return new SocketTimeoutException("Connection Timeout. " +
//                        "Please verify your internet connection.");
//            } else {
//                //No Connection
//                return new ConnectException("No Connection. " +
//                        "Please verify your internet connection.");
//            }
//        } else {
//
            return cause;
        }
//    }
}
