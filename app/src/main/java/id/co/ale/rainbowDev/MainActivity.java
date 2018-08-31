package id.co.ale.rainbowDev;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.ale.infra.application.RainbowIntent;
import com.ale.listener.SigninResponseListener;
import com.ale.listener.StartResponseListener;
import com.ale.rainbowsdk.Connection;
import com.ale.rainbowsdk.RainbowSdk;
import com.ale.service.RainbowService;

import id.co.ale.rainbowDev.Helpers.Util;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String[] perms = {
                "android.permission.CAMERA",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.RECORD_AUDIO"
        };
        int permsRequestCode = 200;



        ActivityCompat.requestPermissions(this, perms, permsRequestCode);

        TextView versionName = (TextView) findViewById(R.id.version_name);
        try {
            PackageInfo packageInfo = this.getPackageManager().getPackageInfo(getPackageName(),0);
            versionName.setText(packageInfo.versionName);
        }catch (Exception e){}

        Util.blockedwords = getResources().getStringArray(R.array.blocked_words);
    }

    private void showLogin(){
        Intent intent = new Intent(RainbowSdk.instance().getContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void showFrontPage()
    {
        Intent intent = new Intent(RainbowSdk.instance().getContext(), FrontPageActivity.class);
        startActivity(intent);
        finish();
    }

    private void showIM(){
        Intent intent = new Intent(RainbowSdk.instance().getContext(), ImsgActivity.class);
        startActivity(intent);
        finish();
    }

    private StartResponseListener startResponseListener = new StartResponseListener() {
        @Override
        public void onStartSucceeded() {
            SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);

            Util.AUTO_TRANSLATE = sp.getString("AUTO_TRANSLATE", "off");

            String username = sp.getString(Util.USERNAME_CODE,"");
            String password = sp.getString(Util.PASSWORD_CODE,"");
            if(username.length() > 0 && password.length() > 0){
                RainbowSdk.instance().connection().signin(
                        username, password, "openrainbow.com", signinResponseListener
                );
            }else{
                showLogin();
            }
        }

        @Override
        public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String s) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Util.toast("Gagal menghubungi server", getApplicationContext());
                }
            });
        }
    };

    private SigninResponseListener signinResponseListener = new SigninResponseListener() {
        @Override
        public void onSigninSucceeded() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    NotificationCompat.Builder notificationBuilder = RainbowSdk.instance().getNotificationBuilder();
                    notificationBuilder.setContentText(getResources().getString(R.string.notif_connected));
                    PendingIntent contentIntent = PendingIntent.getActivity(RainbowSdk.instance().getContext(), 0, RainbowIntent.getLauncherIntent(getApplicationContext(), TukangDagang.class), 0);
                    notificationBuilder.setContentIntent(contentIntent);

                    NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.notify(RainbowService.EVENT_NOTIFICATION, notificationBuilder.build());
                    mNotificationManager.cancel(RainbowService.EVENT_NOTIFICATION);

                    MessengerService.getInstance().startService();
                }
            });

            showIM();
        }

        @Override
        public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String s) {
            showFrontPage();
        }
    };

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults) {
        Connection connection = RainbowSdk.instance().connection();

        if(connection.isConnected()){
            showIM();
        }else{
            connection.start(this.startResponseListener);
        }
    }
}