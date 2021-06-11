package com.galactik.notifications;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.galactik.notifications.ApiUtil.MyPREFERENCES;


public class AddVisitorId extends Application {
    String deviceToken = "";
    String visitorId;
    Context context;
    SharedPreferences sharedpreferences;

    public AddVisitorId(Application application) {
        this.context = application;
    }

    public void setVisitorId(String visitorId) {
//        Log.wtf("visitorId", visitorId);
//        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedpreferences.edit();
//        editor.putString("visitorId", visitorId);
//        editor.apply();
//        final Handler handler = new Handler(Looper.getMainLooper());
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                deviceToken = ApiUtil.getToken(context);
//                Log.wtf("deviceToken", deviceToken);
//
//                if (!deviceToken.equals("")) {
//                    Log.wtf("deviceToken", deviceToken);
//                    sendPost();
//                } else {
//                    Log.wtf("deviceToken", "empty hai ");
//                }
//            }
//        }, 2000);
    }

    public void registerDevice(String deviceId, String userId, String visitorId, String loginStatus) {
        Log.wtf("visitorId", visitorId);
        sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString("visitorId", visitorId);
        editor.apply();
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                deviceToken = ApiUtil.getToken(context);
                Log.wtf("deviceToken", deviceToken);
                if (!deviceToken.equals("")) {
                    Log.wtf("deviceToken", deviceToken);
                    sendPost(); // Api calling for visitorId
                    NetworkService service = ApiUtil.getAPIService();
                    DeviceTokenRequest deviceTokenRequest = new DeviceTokenRequest(deviceId, deviceToken, userId, loginStatus);
                    service.deviceTokenRegistration(deviceTokenRequest).enqueue(new Callback<ApiResponse>() {
                        @Override
                        public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                            if (response.isSuccessful()) {
                                //showResponse(response.body().toString());
                                Log.wtf("post submitted to API.", response.body().toString());
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiResponse> call, Throwable t) {
                            Log.wtf("failure", "Unable to submit post to API.");
                        }
                    });
                } else {
                    Log.wtf("deviceToken", "empty hai ");
                }
            }
        }, 5000);
    }

    public void sendPost() {
        deviceToken = ApiUtil.getToken(context);
        visitorId = ApiUtil.getVisitorId(context);
        Log.wtf("deviceToken", deviceToken);
        Log.wtf("visitorId", visitorId);
       NetworkService service = ApiUtil.getAPIService();
        Subscription subscription = new Subscription(deviceToken);
        DeviceSubscribe deviceSubscribe = new DeviceSubscribe(visitorId, subscription, "android");
        service.savePost(deviceSubscribe).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    //showResponse(response.body().toString());
                    Log.wtf("post submitted to API.", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Log.wtf("failure", "Unable to submit post to API.");
            }
        });
    }
}
