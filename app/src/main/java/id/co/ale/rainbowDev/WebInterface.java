package id.co.ale.rainbowDev;

import es.dmoral.toasty.Toasty;

import com.ale.infra.application.RainbowIntent;
import com.ale.infra.contact.Contact;
import com.ale.infra.contact.IContact;
import com.ale.infra.contact.IRainbowContact;
import com.ale.infra.contact.RainbowPresence;
import com.ale.infra.http.GetFileResponse;
import com.ale.infra.http.adapter.concurrent.RainbowServiceException;
import com.ale.infra.list.ArrayItemList;
import com.ale.infra.list.IItemListChangeListener;
import com.ale.infra.manager.Conversation;
import com.ale.infra.manager.IMMessage;
import com.ale.infra.manager.fileserver.IFileProxy;
import com.ale.infra.manager.fileserver.RainbowFileDescriptor;
import com.ale.infra.proxy.conversation.IRainbowConversation;
import com.ale.infra.proxy.users.IUserProxy;
import com.ale.infra.xmpp.xep.IMamNotification;
import com.ale.listener.IRainbowContactsSearchListener;
import com.ale.listener.IRainbowGetConversationListener;
import com.ale.listener.SignoutResponseListener;
import com.ale.rainbowsdk.RainbowSdk;
import com.ale.service.RainbowService;
import com.amulyakhare.textdrawable.TextDrawable;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import com.ale.infra.contact.Contact;
import com.ale.rainbowsdk.RainbowSdk;

import java.util.ArrayList;
import java.util.List;

import id.co.ale.rainbowDev.Helpers.Util;

import static android.content.Context.NOTIFICATION_SERVICE;

/**
 * Created by Jib Ridwan on 2/24/2017.
 */

public class WebInterface extends AppCompatActivity {
    Context mContext;
    public Contact contact;
    String cid ;
    String jid = "eaa0dd9146164d23a2f739295c7ed7f1@openrainbow.com";
    WebInterface(Context c) {
        mContext = c;

    }

    SignoutResponseListener signoutResponseListener = new SignoutResponseListener() {
        @Override
        public void onSignoutSucceeded() {


            try{


            MessengerService.getInstance().stopService();

            SharedPreferences sp = getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            Log.d("trysetyo",Util.USERNAME_CODE);
            editor.remove(Util.USERNAME_CODE);
            editor.remove(Util.PASSWORD_CODE);
            editor.commit();
            }catch (Exception err){
                Log.d("trysetyo",err.toString());
                Log.d("trysetyo",Util.USERNAME_CODE);
                Log.d("trysetyo",Util.PASSWORD_CODE);
            }


//            NotificationCompat.Builder notificationBuilder = RainbowSdk.instance().getNotificationBuilder();
//            notificationBuilder.setContentText(getResources().getString(R.string.notif_connecting));
//            PendingIntent contentIntent = PendingIntent.getActivity(RainbowSdk.instance().getContext(), 0, RainbowIntent.getLauncherIntent(getApplicationContext(), MainActivity.class), 0);
//            notificationBuilder.setContentIntent(contentIntent);
//
//            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            mNotificationManager.notify(RainbowService.EVENT_NOTIFICATION, notificationBuilder.build());

           // Intent intent = new Intent(getBaseContext(), TukangDagang.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
           // startActivity(intent);
           // finish();
        }

//        @Override
//        public void onRequestFailed(RainbowSdk.ErrorCode errorCode, String s) {
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Util.toast("Terjadi kesalahan saat logout",getBaseContext());
//                }
//            });
//        }
    };


    @JavascriptInterface
    public void showLogin() {

        Intent i = new Intent(mContext, MainActivity.class);
        mContext.startActivity(i);
    }

    boolean isUserClickButton = false;
    @JavascriptInterface
    public void exitAp() {
        if(isUserClickButton==false){
            Toast.makeText(mContext,"Jika menekan sekali lagi, app akan ditutup",Toast.LENGTH_LONG).show();
            isUserClickButton = true;
        }else {

            finish();
            System.exit(0);
        }
        new CountDownTimer(3000,1000){
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                isUserClickButton = false;
            }
        }.start();

    }

    @JavascriptInterface
    public void logoutRainbow() {
        Log.d("trysetyo", "logoutRainbow: ");
        RainbowSdk.instance().connection().signout(signoutResponseListener);


    }

    @JavascriptInterface
    public void showChat(String jid2) {
    try {


        IRainbowContact x = RainbowSdk.instance().contacts().getContactFromJabberId(jid2);
        this.contact = (Contact) x;
        Util.tempContact = this.contact;

        Intent intentCall = new Intent(this.mContext, ChatActivity.class);
        mContext.startActivity(intentCall);
    }catch (Exception err){
        Log.d("trysetyo", err.toString());
    }
    }

    @JavascriptInterface
     public void showTampil(String jid2) {
        Log.d("nais1","ok");
        //Intent i = new Intent(mContext, MainActivity.class);
        //
//        mContext.startActivity(i);



        IRainbowContact x = RainbowSdk.instance().contacts().getContactFromJabberId(jid2);
//        Toasty.error(this.mContext,contact.toString());
            this.contact = (Contact) x;


//        this.contact = (Contact) contacts.get(2);
            //      if (this.contact!=null) {
            String idd = RainbowSdk.instance().myProfile().getConnectedUser().getImJabberId();
            Log.d("nais1", idd);
            Log.d("nais1", this.contact.toString());
            //com.ale.infra.contact.Contact@6
        // f307b61
            RainbowSdk.instance().webRTC().makeCall((Contact) this.contact, true);
            // Log.d("nais", this.contact.toString());
            Intent intentCall = new Intent(this.mContext, VoiceCallActivity.class);
            mContext.startActivity(intentCall);





        //showLogin();

//        contact.setLocalContact("583a2496");


            //..      List<IRainbowContact> list = new ArrayList<IRainbowContact>();
//        list.add(contact.)
//contacts.get(2).getImJabberId();

       // List<IRainbowContact> contacts = RainbowSdk.instance().contacts().getContactFromJabberId("33");
//        Toasty.error(this.mContext,"ok");

    //    }
//        else{
///

        }




    }


