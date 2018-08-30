package id.co.ale.rainbowDev;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;
import android.support.v4.content.ContextCompat;

import com.ale.rainbowsdk.RainbowSdk;

import id.co.ale.rainbowDev.Service.RainbowService;

public class MessengerService extends Application {
    private static MessengerService mInstance;

    @Override
    protected void attachBaseContext(Context base){
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate(){
        super.onCreate();

        mInstance = this;
        RainbowSdk.instance().setNotificationBuilder(
                getApplicationContext(),
                MainActivity.class,
                R.drawable.ic_notification,
                getString(R.string.app_name),
                getResources().getString(R.string.notif_connecting),
                ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary)
        );

        if(!RainbowSdk.instance().isInitialized()){
            RainbowSdk.instance().initialize(null, null);
        }
    }

    public static synchronized MessengerService getInstance() {
        return mInstance;
    }

    public void startService(){
        Intent mainService = new Intent(getApplicationContext(), RainbowService.class);
        startService(mainService);
    }

    public void stopService() {
        Intent mainService = new Intent(getApplicationContext(), RainbowService.class);
        stopService(mainService);
    }
}